<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:webApp="http://java.sun.com/xml/ns/j2ee"
     xmlns="http://java.sun.com/xml/ns/j2ee">
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:param name="ssl-port" select="'NULL'"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>
  
  <xsl:template match="webApp:servlet">
    <xsl:copy>
      <xsl:apply-templates/>
      <init-param>
        <param-name>ssl-port</param-name>
        <param-value><xsl:value-of select="$ssl-port"/></param-value>
      </init-param>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="webApp:init-param[webApp:param-name/text()='ssl-port']">
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
