<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="text" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL'"/>

<xsl:template match="/">

# NOTE: This file has been generated automatically from conf/yanel.xml!

resources=
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <xsl:value-of select="@src"/>,
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
