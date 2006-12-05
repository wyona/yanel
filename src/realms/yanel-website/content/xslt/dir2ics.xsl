<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
>

<xsl:output method="text"/>

<xsl:template match="/">
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//Mozilla.org/NONSGML Mozilla Calendar V1.1//EN
<xsl:apply-templates select="/dir:directory/*"/>
</xsl:template>


<xsl:template match="dir:directory">
<xsl:comment>
Do NOT show collections: <xsl:value-of select="@name"/>, <xsl:value-of select="@path"/>
</xsl:comment>
</xsl:template>

<xsl:template match="dir:file">
BEGIN:VEVENT
UID:<xsl:value-of select="@name"/>
END:VEVENT
<!--
<link href="{@name}"/>
<xsl:comment>
<xsl:value-of select="@path"/>
</xsl:comment>
-->
</xsl:template>

</xsl:stylesheet>
