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
    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
        <xsl:apply-templates select="/sitetree/collection"/>
    </urlset>
  </xsl:template>

  <xsl:template match="collection">
    <url><loc><xsl:value-of select="$domain"/><xsl:value-of select="@path"/></loc></url>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="resource">
    <url><loc><xsl:value-of select="$domain"/><xsl:value-of select="@path"/></loc></url>
  </xsl:template>

  <xsl:template match="label"></xsl:template>

  <xsl:template match="neither-resource-nor-collection"></xsl:template>
  
</xsl:stylesheet>
