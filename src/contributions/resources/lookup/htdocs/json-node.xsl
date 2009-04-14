<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<!-- See https://www.google.com/webmasters/tools/docs/en/protocol.html -->

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>
  <!-- TODO: Make the domain configurable per realm! -->
  <xsl:param name="domain" select="'DOMAIN_IS_NULL'"/>

  <xsl:template match="/">
    [
    <xsl:apply-templates/>
    ]
  </xsl:template>

  <xsl:template match="collection">
    {"id":"<xsl:value-of select="@path"/>", "text":"<xsl:value-of select="@name"/>","cls":"folder","disabled":false,"leaf":false}
    <xsl:choose>
      <xsl:when test="following-sibling::*">,</xsl:when>
      <xsl:otherwise></xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="resource">
      {"id":"<xsl:value-of select="@path"/>", "text":"<xsl:value-of select="@name"/>","cls":"file-txt","disabled":false,"leaf":true,"qtip":"Size: 1047552"}
        <xsl:if test="following-sibling::*">,</xsl:if>
  </xsl:template>

  <xsl:template match="label"></xsl:template>

  <xsl:template match="neither-resource-nor-collection"></xsl:template>
  
</xsl:stylesheet>
