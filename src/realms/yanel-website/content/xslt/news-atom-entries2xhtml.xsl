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
  <link rel="introspection" href="introspection-atom.xml" type="application/atomserv+xml"/>

  <link rel="alternate" title="Yanel News" href="news-atom-entries/?yanel.resource.viewid=atom" type="application/atom+xml"/>

  <title>News</title>
</head>
<body>

<h2>Yanel News</h2>


<xsl:apply-templates select="/atom:feed/atom:entry"/>


<p>
<a href="http://www.feedvalidator.org/check.cgi?url=http%3A//yanel.wyona.org/news/news-atom-entries/?yanel.resource.viewid=atom"><img src="../img/valid-atom.png" alt="[Valid Atom 1.0]" title="Validate my Atom 1.0 feed" border="0"/></a>

<a href="http://www.feedvalidator.org/check.cgi?url=http%3A//yanel.wyona.org/news/news-atom-entries/?yanel.resource.viewid=rss2.0"><img src="../img/valid-rss.png" alt="[Valid RSS 2.0]" title="Validate my RSS 2.0 feed" border="0"/></a>

&#160;
<a href="news-atom-entries/?yanel.resource.viewid=rss2.0"><img src="../img/feed-icon-16x16.png" alt="[RSS 2.0]" border="0"/></a>

</p>

</body>
</html>
</xsl:template>

<xsl:template match="atom:entry">
  <h3><xsl:value-of select="atom:title"/></h3>
  <xsl:copy-of select="atom:content/*"/>
  <br/>
  <font size="-1">Updated: <xsl:value-of select="atom:updated"/></font>
<br/>
<font size="-1">Published: <xsl:value-of select="atom:published"/></font>
</xsl:template>

</xsl:stylesheet>
