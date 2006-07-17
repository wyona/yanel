<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
  <title>Collection</title>
</head>

<body>
Hello World!
Path: <xsl:value-of select="/dir:directory/@yanel:path"/>
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="dir:directory">
Collection: <xsl:value-of select="@path"/><br/>
</xsl:template>

<xsl:template match="dir:file">
Resource: <xsl:value-of select="@path"/><br/>
</xsl:template>
</xsl:stylesheet>
