<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
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


<table id="bodytable" cellpadding="0" cellspacing="0">
<tr>
  <td colspan="3">
    <h1 id="header">
      <a href="{$yarep.back2realm}index.html">
        <img id="logo" src="{$yarep.back2realm}img/yanel.png"/>
      </a>
      <img id="lead" src="{$yarep.back2realm}img/everything_is_content.png"/>
    </h1>
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
    <xsl:copy-of select="/xhtml:html/xhtml:body/node()"/>
  </div>
</td>
</tr>
<tr>
  <td colspan="3" id="footer">

    <i18n:message key="poweredBy"/><xsl:text> </xsl:text><a href="http://yanel.wyona.org">Wyona Yanel</a> | <a href="https://svn.wyona.org/repos/public/tomcat-cluster/">Wyona Balancer</a> | <a href="http://tomcat.apache.org">Apache Tomcat</a> | <a href="http://httpd.apache.org">Apache HTTP Server</a><br/>
    Copyright &#169; 2006 <a href="http://www.wyona.com">Wyona</a>. <i18n:message key="allRightsReserved"/>. - <a href="?yanel.resource.meta"><i18n:message key="pageInfo"/></a>

  </td>
</tr>
</table>


</body>
</html>
</xsl:template>

  <xsl:template name="navigation">
    <b><a href="{$yarep.back2realm}index.html">Testing</a></b>
    <br/> &#160;<a href="{$yarep.back2realm}testing-control.html">Testing&#160;Control</a>
    <br/> &#160;<a href="{$yarep.back2realm}test-results-archive/">Test&#160;Results&#160;Archive</a>
    <br/> &#160;<a href="{$yarep.back2realm}testing-times.html">Time&#160;Analyze</a>
    <br/> &#160;<a href="{$yarep.back2realm}jmeter/jmeter-results/">JMeter&#160;Results</a>
    <br/> <b><a href="{$yarep.back2realm}use-cases/">Use&#160;Cases</a></b>
    <br/>
  </xsl:template>

</xsl:stylesheet>
