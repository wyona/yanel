<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:atom="http://www.w3.org/2005/Atom"
>

<xsl:output method="xml" encoding="UTF-8" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html>
<head>
<!--
  <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
-->
<!--
  <link rel="introspection" type="application/atomserv+xml" href="introspection-{$name-without-suffix}.xml"/>
-->
  <meta content="application/xhtml+xml; charset=UTF-8" http-equiv="Content-Type"/>
  <title><xsl:value-of select="/atom:feed/atom:title"/> - Ulysses Demo</title>
</head>
<body>
<h1><xsl:value-of select="/atom:feed/atom:title"/></h1>

<p>
Author: <xsl:value-of select="/atom:feed/atom:author"/>
<br/>
Update: <xsl:value-of select="/atom:feed/atom:updated"/>
<br/>
Link: <xsl:value-of select="/atom:feed/atom:link/@href"/>
<br/>
ID: <xsl:value-of select="/atom:feed/atom:id"/>
</p>

<h2>Entries</h2>
<xsl:apply-templates select="/atom:feed/atom:entry"/>

<!--
<a href="http://www.feedvalidator.org/check.cgi?url=http%3A//ulysses.wyona.org/demo/atom.xml"><img src="valid-atom.png" alt="[Valid Atom 1.0]" title="Validate my Atom 1.0 feed" border="0"/></a>
-->

<hr/>

<ul>
  <li>This Feed as <a href="?yanel.resource.viewid=source">application/xml</a></li>
  <li>This Feed as <a href="?yanel.resource.viewid=atom">application/atom+xml</a></li>
</ul>
</body>
</html>
</xsl:template>

<xsl:template match="atom:entry">
<xsl:choose>
  <xsl:when test="atom:link/@href">
    <h3><a href="{atom:link/@href}"><xsl:value-of select="atom:title"/></a></h3>
  </xsl:when>
  <xsl:otherwise>
    <h3><xsl:value-of select="atom:title"/></h3>
  </xsl:otherwise>
</xsl:choose>

<p>
<xsl:value-of select="atom:summary"/>
</p>

<xsl:copy-of select="atom:content/*"/>

<br/>

<font size="-1">Last Published (Updated): <xsl:value-of select="atom:updated"/></font>
<br/>
<font size="-1">First Published: <xsl:value-of select="atom:published"/></font>
</xsl:template>

</xsl:stylesheet>
