<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
>

<xsl:output method="xhtml"/>
<!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
<!--
<xsl:output method="html"/>
-->

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <title>Collection: <xsl:value-of select="/dir:directory/@yanel:path"/></title>
</head>

<body>
<p>
<b>Yanel Path:</b> <xsl:value-of select="/dir:directory/@yanel:path"/>
</p>

<p>
<b>Collection Path:</b> <xsl:value-of select="/dir:directory/@dir:path"/> (<a href="?yanel.resource.viewid=source">XML view</a>)
</p>

<p>
<b>Parent:</b> <a href="../">..</a>
</p>

<p>
<b>Children:</b><br/>
<xsl:apply-templates select="/dir:directory/*"/>
</p>
</body>
</html>
</xsl:template>

<xsl:template match="dir:directory">
Collection: <a href="{@name}/" title="directory"><xsl:value-of select="@path"/>/</a><br/>
</xsl:template>

<xsl:template match="dir:file">
Resource: <a href="{@name}" title="file"><xsl:value-of select="@path"/></a><br/>
</xsl:template>
</xsl:stylesheet>
