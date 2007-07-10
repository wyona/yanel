<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns:xi="http://www.w3.org/2001/XInclude"
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
<xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
<xsl:param name="yanel.last.modified" select="'LAST_MODIFIED_IS_NULL'"/>
<xsl:param name="yanel.username" select="'USERNAME_IS_NULL'"/>

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

<!--
  <link rel="shortcut icon" href="{$yarep.back2realm}favicon.ico" type="image/vnd.microsoft.icon" />
  <link rel="stylesheet" href="{$yarep.back2realm}css/global.css" type="text/css"/>
-->
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

<table id="bodytable" cellpadding="0" cellspacing="0" border="1">
<tr>
  <td colspan="3">
    <h1 id="header">
    HEADER
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
    <xsl:call-template name="translations"/>
    <xsl:copy-of select="/xhtml:html/xhtml:body/node()"/>
  </div>
</td>
</tr>
<tr>
  <td colspan="3" id="footer">

    <i18n:message key="poweredBy"/><xsl:text> </xsl:text><a href="http://yanel.wyona.org">Wyona Yanel</a> - Copyright &#169; 2007 <a href="http://www.wyona.com">Wyona</a>. <i18n:message key="allRightsReserved"/>. - <a href="?yanel.resource.meta"><i18n:message key="pageInfo"/></a> - <a href="?yanel.toolbar=on">Toolbar</a><br/>
<i>This page last changed on <xsl:value-of select="$yanel.last.modified"/> by SOMEBODY.</i>

  </td>
</tr>
</table>

</body>
</html>
</xsl:template>


<xsl:template name="translations">
<!--
  <xi:include href="yanelresource:/navigation/translations.xml?path={$yanel.path}&amp;language={$language}"/>
-->
</xsl:template>

<xsl:template name="navigation">
NAVIGATION
<!--
<xi:include href="yanelresource:/navigation/menu.xml?path={$yanel.path}&amp;language={$language}"/>
-->
</xsl:template>

</xsl:stylesheet>
