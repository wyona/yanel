<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
  <y:resource-types xmlns:y="http://www.wyona.org/yanel/1.0" version="{/yanel:resource-types/@version}">
  <xsl:for-each select="/yanel:resource-types/yanel:resource-type">
    <xsl:choose>
      <xsl:when test="@package">
    <y:resource-type package="{@package}"/>
      </xsl:when>
      <xsl:when test="@copy-dir-name">
    <y:resource-type src="../../resources/{@copy-dir-name}"/>
      </xsl:when>
      <xsl:otherwise>
    <y:resource-type src="{@src}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  </y:resource-types>
</xsl:template>

</xsl:stylesheet>
