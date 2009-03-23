<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:param name="host" select="'localhost'"/>
  <xsl:param name="ssl-port" select="'NULL_SSL_PORT'"/>
  <xsl:param name="http-port" select="'NULL_HTTP_PORT'"/>
  
  <xsl:template match="/">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>
  
  <xsl:template match="Connector[(not(@protocol)) and (not(@secure) or @secure != 'true')]">
    <xsl:copy> 
      <xsl:copy-of select="@*"/>
    <!--xsl:if test="../Engine[1]"-->
    <xsl:if test="../Engine[1]//Host[@name = $host]">
      <xsl:attribute name="port">
        <xsl:value-of select="$http-port"/>
      </xsl:attribute>
    </xsl:if>
      <xsl:copy-of select="node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="Connector[@secure = 'true']">
    <xsl:copy> 
      <xsl:copy-of select="@*"/>
    <xsl:if test="../Engine[1]//Host[@name = $host]">
      <xsl:attribute name="port">
        <xsl:value-of select="$ssl-port"/>
      </xsl:attribute>
    </xsl:if>
      <xsl:copy-of select="node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
