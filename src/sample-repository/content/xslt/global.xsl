<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
<xsl:output method="xhtml"/>
<!--
<xsl:output method="html"/>
-->

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<xsl:comment>
WARNING: This file has been generated automatically. All changes will be lost.
         Please edit only *.xhtml" files and re-generate the "*.html" pages by
         using global.xsl
</xsl:comment>

<head>
  <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
<xsl:comment>Name: <xsl:value-of select="$yanel.path.name"/> (without suffix: <xsl:value-of select="$name-without-suffix"/>), Path: <xsl:value-of select="$yanel.path"/>, Back 2 Realm: <xsl:value-of select="$yarep.back2realm"/>, Back 2 Context: <xsl:value-of select="$yanel.back2context"/></xsl:comment>

  <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/> - Yanel</title>
</head>

<body>
<h1>YANEL</h1>

<table>
<tr>
<td valign="top">
<b>All You Need</b><br/>
&#160;<a href="{$yarep.back2realm}about.html">About</a><br/>
&#160;<a href="{$yarep.back2realm}download.html">Download</a><br/>
&#160;Getting Started<br/>
&#160;Features<br/>
&#160;News<br/>
&#160;<a href="{$yarep.back2realm}documentation/index.html">Documentation</a><br/>
<br/>
<b>Community/Developers</b><br/>
&#160;<a href="{$yarep.back2realm}download/source-repository.html">Get the Source</a><br/>
&#160;Mailing Lists<br/>
&#160;Task/Bug Tracker<br/>
&#160;<a href="{$yarep.back2realm}testing.html">Testing/Continuous Integration</a><br/>
&#160;Acknowledgements<br/>
<br/>
<b>Professional Services</b><br/>
&#160;Support<br/>
&#160;Consulting<br/>
&#160;Training<br/>
&#160;Solutions<br/>
</td>

<td>&#160;&#160;</td>

<td valign="top">
  <xsl:copy-of select="/xhtml:html/xhtml:body/*"/>
</td>
</tr>
</table>
  <p>
    Copyright &#169; 2006 Wyona
  </p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
