<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0" 
  xmlns="http://www.wyona.org/yanel/1.0"
  >
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:param name="servlet.context.prefix" select="'NULL'"/>
  
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="@src">
    <xsl:attribute name="src"><xsl:value-of select="."/>resource.xml</xsl:attribute>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
