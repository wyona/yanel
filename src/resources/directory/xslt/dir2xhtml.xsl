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
<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>

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
Collection: <a href="{$yarep.back2realm}.{@path}/" title="directory"><xsl:value-of select="@name"/>/</a><br/>
</xsl:template>

<xsl:template match="dir:file">
Resource: <a href="{$yarep.back2realm}.{@path}" title="file"><xsl:value-of select="@name"/></a><br/>
</xsl:template>
</xsl:stylesheet>
