<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml">

  <!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.meta.language" select="'en'"/>

  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <xsl:comment> WARNING: This content has been generated dynamically. All
        changes will be lost.</xsl:comment>
      <head>
        <xsl:comment>Name: <xsl:value-of select="$yanel.path.name"/> (without
          suffix: <xsl:value-of select="$name-without-suffix"/>), Path:
            <xsl:value-of select="$yanel.path"/>, Back 2 Realm: <xsl:value-of
          select="$yarep.back2realm"/>, Back 2 Context: <xsl:value-of select="$yanel.back2context"/>
        </xsl:comment>
        <title>
          <xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/> - Yanel</title>
        <!-- This is needed such that Microsoft Internet Explorer displays characters such as &nbsp; correctly (also see xsl:output, whereas I (Michi) am not sure if the encoding is really needed there) -->
        <meta content="application/xhtml+xml; charset=UTF-8" http-equiv="Content-Type"/>
        <meta name="generator" content="Wyona Yanel"/>
        <!-- http://dublincore.org/documents/2001/04/12/usageguide/simple-html.shtml -->
        <meta name="DC.Creator" content="TODO"/>
        <meta name="DC.Language" content="TODO"/>
        <link rel="stylesheet" href="{$yarep.back2realm}css/global.css" type="text/css"/>
        <xsl:copy-of select="/xhtml:html/xhtml:head/*[name(.) != 'title']"/>
        <xsl:choose>
          <xsl:when test="/xhtml:html/xhtml:head/xhtml:link/@rel='neutron-introspection'">
            <!-- NOTE: Use the one from the copy above in case one exists! -->
          </xsl:when>
          <xsl:otherwise>
            <!-- TODO: Maybe one should better not cut-off the suffix! -->
            <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
          </xsl:otherwise>
        </xsl:choose>
      </head>
      <body>
        <div id="container">
          <div id="header">
            <a href="{$yarep.back2realm}index.html">
              <img src="{$yarep.back2realm}img/yanel.gif"/>
            </a>
          </div>
          <div id="outer">
            <div id="inner">
              <div id="navigation">
                <xsl:call-template name="navigation"/>
              </div>
              <div id="content">
                <div id="test">
                  <xsl:copy-of select="/xhtml:html/xhtml:body/*"/>
                </div>
              </div>
            </div>
          </div>
          <div id="footer">
            <font size="-1">
              <i18n:message key="poweredBy"/>
              <a href="http://yanel.wyona.org">Wyona Yanel</a>, <a
                href="https://svn.wyona.org/repos/public/tomcat-cluster/">Wyona
              Balancer</a>, <a href="http://tomcat.apache.org">Apache
              Tomcat</a>, <a href="http://httpd.apache.org">Apache HTTP Server</a>
            </font>
            <br/> Copyright &#169; 2006 <a
            href="http://www.wyona.com">Wyona</a>. <i18n:message
            key="allRightsReserved"/>. - <a href="?yanel.resource.meta">
              <i18n:message key="pageInfo"/>
            </a>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="navigation">
    <b><a href="{$yarep.back2realm}">Testing</a></b>
    <br/> &#160;<a href="{$yarep.back2realm}testing-control.html">Testing&#160;Control</a>
    <br/> &#160;<a href="{$yarep.back2realm}test-results-archive/">Test&#160;Results&#160;Archive</a>
    <br/> &#160;<a href="{$yarep.back2realm}testing-times.html">Time&#160;Analyze</a>
    <br/> <b><a href="{$yarep.back2realm}use-cases/">Use&#160;Cases</a></b>
    <br/>
  </xsl:template>

</xsl:stylesheet>
