<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:param name="ssl-port" select="'NULL_SSL_PORT'"/>
  <xsl:param name="keystoreFile" select="'NULL_KEYSTORE_FILE'"/>
  <xsl:param name="keystorePass" select="'NULL_KEYSTORE_PASS'"/>
  <xsl:param name="http-port" select="'NULL_HTTP_PORT'"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>
  
  <xsl:template match="Connector[@port = $http-port]">
    <!-- Replace current SSL port with SSL port configured within (local.)build.properties  -->
    <xsl:copy> 
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="redirectPort"><xsl:value-of select="$ssl-port"/></xsl:attribute>

<!-- UTF-8 Support -->
<!--
        <xsl:attribute name="URIEncoding">UTF-8</xsl:attribute>
-->
    </xsl:copy>
    
    <!-- Create TLS Connector if not exists, whereas use standard connector as template -->
    <xsl:if test="not(count(//Connector[@sslProtocol = 'TLS']))">
      <xsl:copy> 
        <xsl:copy-of select="@*[not(redirectPort)]"/>
        <xsl:attribute name="port"><xsl:value-of select="$ssl-port"/></xsl:attribute>
        <xsl:attribute name="keystoreFile"><xsl:value-of select="$keystoreFile"/></xsl:attribute>
        <xsl:attribute name="keystorePass"><xsl:value-of select="$keystorePass"/></xsl:attribute>
        <xsl:attribute name="secure">true</xsl:attribute>
        <xsl:attribute name="scheme">https</xsl:attribute>
        <xsl:attribute name="sslProtocol">TLS</xsl:attribute>
        <xsl:attribute name="clientAuth">false</xsl:attribute>

<!-- UTF-8 Support -->
<!--
        <xsl:attribute name="URIEncoding">UTF-8</xsl:attribute>
-->
      </xsl:copy>
    </xsl:if>
    
  </xsl:template>
  
  <!-- in case TLS is already set replace it with our settings from (local.)build.properties -->
  <xsl:template match="Connector[@sslProtocol = 'TLS']">
    <xsl:copy> 
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="port"><xsl:value-of select="$ssl-port"/></xsl:attribute>
      <xsl:attribute name="keystoreFile"><xsl:value-of select="$keystoreFile"/></xsl:attribute>
      <xsl:attribute name="keystorePass"><xsl:value-of select="$keystorePass"/></xsl:attribute>

<!-- UTF-8 Support -->
<!--
        <xsl:attribute name="URIEncoding">UTF-8</xsl:attribute>
-->
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
