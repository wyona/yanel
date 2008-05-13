<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<!--
  <xsl:output method="xhtml" encoding="UTF-8"/>
-->

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="resource-types/http://www.wyona.org/yanel/resource/1.0::data-repo-sitetree/js/js/ext/resources/css/ext-all.css"/>
        <script type="text/javascript" src="resource-types/http://www.wyona.org/yanel/resource/1.0::data-repo-sitetree/js/js/ext/adapter/yui/yui-utilities.js"></script>
        <script type="text/javascript" src="resource-types/http://www.wyona.org/yanel/resource/1.0::data-repo-sitetree/js/js/ext/adapter/yui/ext-yui-adapter.js"></script>
        <script type="text/javascript" src="resource-types/http://www.wyona.org/yanel/resource/1.0::data-repo-sitetree/js/js/ext/ext-all.js"></script>
        <title>Sitetree - Yanel</title>
        <link rel="stylesheet" href="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-css/global.css" type="text/css"/>
      </head>
      <body>
        <h1>Sitetree</h1>
        <a href="?yanel.resource.viewid=xml">XML</a> (TODO: show-all-subnodes)
<br/>
        <a href="?yanel.resource.viewid=google-sitemap">"Google" sitemap</a> (TODO: show-all-subnodes)
        <!-- <xsl:apply-templates select="/sitetree/collection"/> -->
        
        <script language="javascript" src="resource-types/http://www.wyona.org/yanel/resource/1.0::data-repo-sitetree/js/org.wyona.yanel.navigation.gwt.navigationtree.NavigationTree.nocache.js"></script>
        <div id="navigation-tree-hook"/>
        <iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="collection">
    <li><xsl:value-of select="@name"/></li>
    <ul>
    <xsl:apply-templates/>
    </ul>
  </xsl:template>

  <xsl:template match="resource">
    <li><a href="..{@path}.html"><xsl:value-of select="@name"/></a></li>
  </xsl:template>
  
</xsl:stylesheet>
