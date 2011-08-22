<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:comments="http://www.wyona.org/yanel/comments/1.0"
  xmlns:cmts="http://www.wyona.org/yanel/1.0"
>

<!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
<xsl:output method="xhtml" encoding="UTF-8"/>
<!--
<xsl:output method="html"/>
-->

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
<xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
<xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
<xsl:param name="do.not.track" select="'DO_NOT_TRACK_IS_NULL'"/>

<xsl:param name="yanel.meta.language" select="'en'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<xsl:comment>
WARNING: This content has been generated dynamically. All changes will be lost.
</xsl:comment>

<head>
<xsl:comment>Name: <xsl:value-of select="$yanel.path.name"/> (without suffix: <xsl:value-of select="$name-without-suffix"/>), Path: <xsl:value-of select="$yanel.path"/>, Back 2 Realm: <xsl:value-of select="$yarep.back2realm"/>, Back 2 Context: <xsl:value-of select="$yanel.back2context"/></xsl:comment>

  <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/> - Yanel</title>

  <!-- This is needed such that Microsoft Internet Explorer displays characters such as &nbsp; correctly (also see xsl:output, whereas I (Michi) am not sure if the encoding is really needed there) -->
  <meta content="application/xhtml+xml; charset=UTF-8" http-equiv="Content-Type"/>
  <meta name="generator" content="Wyona Yanel"/>

  <!-- http://dublincore.org/documents/2001/04/12/usageguide/simple-html.shtml -->
  <meta name="DC.Creator" content="TODO"/>
  <meta name="DC.Language" content="TODO"/>

  <link rel="shortcut icon" href="{$yarep.back2realm}favicon.ico" type="image/vnd.microsoft.icon" />
  <link rel="stylesheet" href="{$yarep.back2realm}css/global.css" type="text/css"/>
  <base target="_top"/>
  <xsl:copy-of select="/xhtml:html/xhtml:head/*[name(.) != 'title']"/>

  <xsl:choose>
  <xsl:when test="/xhtml:html/xhtml:head/xhtml:link/@rel='neutron-introspection'">
    <!-- NOTE: Use the one from the copy above in case one exists! -->
  </xsl:when>
  <xsl:otherwise>
    <!-- TODO: Maybe one should better not cut-off the suffix! -->
    <link rel="neutron-introspection" type="application/neutron+xml" href="?yanel.resource.usecase=introspection"/>
    <!--
    <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
    -->
  </xsl:otherwise>
  </xsl:choose>
</head>

<body>

<table id="bodytable" cellpadding="0" cellspacing="0" border="0">
<tr>
  <td colspan="2">
    <div id="header">
      <a href="{$yarep.back2realm}index.html">
        <img id="logo" src="{$yarep.back2realm}img/yanel.png"/>
      </a>
<!-- TODO: Change the slogan to:
      <span style="color: white;">&#160;&#160;everything is related content is everything<br/>&#160;</span>
-->
      <img id="lead" src="{$yarep.back2realm}img/everything_is_content.png"/>
    </div>
  </td>
</tr>

<tr>
<!-- NAVIGATION -->
<td valign="top">
<div id="navigation">
<xsl:call-template name="navigation"/>
</div>
</td>



<!-- CONTENT -->
<td valign="top" width="100%">
  <div id="content">
    <xsl:call-template name="translations"/>
    <xsl:copy-of select="/xhtml:html/xhtml:body/node()"/>

    <br/>
    <hr/>
    <div id="comments">
      <h4>Your comments are much appreciated</h4>
      <p>Is the content of this page unclear or you think it could be improved? Please <a href="{$yarep.back2realm}usecases/add-comment.html?path={$yanel.path}">add a comment</a> and we will try to improve it accordingly.</p>
      <xsl:apply-templates select="/xhtml:html/comments:has-comments"/><xsl:apply-templates select="/xhtml:html/cmts:root/cmts:comments"/>
    </div>
  </div>
</td>
</tr>
<tr>
  <td colspan="2" id="footer">

    <i18n:text key="poweredBy"/><xsl:text> </xsl:text><a href="http://yanel.wyona.org">Wyona Yanel</a> | <a href="http://svn.wyona.com/repos/public/tomcat-cluster/">Wyona Balancer</a> | <a href="http://tomcat.apache.org">Apache Tomcat</a> | <a href="http://httpd.apache.org">Apache HTTP Server</a><br/>
    Copyright &#169; 2011 <a href="http://www.wyona.com">Wyona</a>. <i18n:text key="allRightsReserved"/>. - <a href="?yanel.resource.meta"><i18n:text key="pageInfo"/></a> - <a href="?yanel.toolbar=on">Toolbar</a> - <a href="http://donottrack.us/" target="_blank">Do not track</a>: <xsl:value-of select="$do.not.track"/>

  </td>
</tr>
</table>

</body>
</html>
</xsl:template>


<xsl:template name="translations">
  <xi:include href="yanelresource:/navigation/translations.xml?path={$yanel.path}&amp;language={$content-language}"/>
</xsl:template>

<xsl:template name="navigation">
<xi:include href="yanelresource:/navigation/menu.xml?path={$yanel.path}&amp;language={$language}"/>
<br/>
<b><i18n:text key="search"/></b>
&#160;<form action="{$yarep.back2realm}en/search.html" method="GET"><input type="text" name="query" class="searchbox" size="14"/></form>
</xsl:template>

<xsl:template match="comments:has-comments">
&#160;|&#160;Display all (<xsl:value-of select="."/>) comments
</xsl:template>

<xsl:template match="cmts:comments">
<h4><xsl:value-of select="count(cmts:comment)"/> Comments</h4>
<xsl:apply-templates select="cmts:comment"/>
</xsl:template>

<xsl:template match="cmts:comment">
<p>
<span style="font-size: 8px;">Posted by anonymous on <xsl:value-of select="cmts:created"/></span><br/>
<strong><xsl:value-of select="cmts:title"/></strong>
<br/>
<xsl:value-of select="cmts:text"/>
</p>
</xsl:template>

</xsl:stylesheet>
