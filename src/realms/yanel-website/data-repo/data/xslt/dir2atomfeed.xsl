<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
>

<xsl:output method="xml"/>

<xsl:template match="/">
<feed xmlns="http://www.w3.org/2005/Atom" xml:base="http://yanel.wyona.org:9290/yanel-dev{/dir:directory/@yanel:path}">

  <title>Yanel Website News</title>
  <link rel="self" href="{/dir:directory/@yanel:path}"/>
  <updated>2003-12-13T18:30:02Z</updated>
  <author><name>Yanel</name></author>
  <id>urn:uuid:TODO</id>

<xsl:apply-templates select="/dir:directory/*"/>

</feed>
</xsl:template>


<xsl:template match="dir:directory">
<xsl:comment>
Do NOT show collections: <xsl:value-of select="@name"/>, <xsl:value-of select="@path"/>
</xsl:comment>
</xsl:template>

<xsl:template match="dir:file">
<link href="{@name}"/>
<xsl:comment>
<xsl:value-of select="@path"/>
</xsl:comment>
</xsl:template>
</xsl:stylesheet>
