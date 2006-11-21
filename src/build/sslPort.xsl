<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:webApp="http://java.sun.com/xml/ns/j2ee"
     xmlns="http://java.sun.com/xml/ns/j2ee">
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:param name="portNumber"/>

  <xsl:template match="@*" priority="-1">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates />
    </xsl:copy>
    
  </xsl:template>

  <xsl:template match="webApp:servlet">
    <servlet>
      <xsl:copy-of select="webApp:servlet-name"/>
      <xsl:copy-of select="webApp:servlet-class"/>
      <xsl:for-each select="webApp:init-param">
        <xsl:variable name="pName" select="webApp:param-name"/>
        <xsl:variable name="pValue" select="webApp:param-value"/>
        <xsl:if test="$pName = 'ssl-port'">
          <init-param>
            <param-name>ssl-port</param-name>
            <param-value><xsl:value-of select="$portNumber"/></param-value>
          </init-param>
        </xsl:if>
      </xsl:for-each>
    </servlet>
  </xsl:template>

</xsl:stylesheet>
