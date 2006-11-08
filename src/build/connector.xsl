<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:param name="portNumber"/>
  <xsl:param name="keystoreFile"/>
  <xsl:param name="keystorePass"/>
  <xsl:param name="defaultPort"/>

  <xsl:template match="@*" priority="-1">
    <xsl:copy/>
  </xsl:template>

  <xsl:template match="node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="Service[@name='Catalina']">
    <Service name="Catalina">
      <xsl:for-each select="Connector">
        <xsl:variable name="URIEncoding"><xsl:value-of select="@URIEncoding"/></xsl:variable>
        <Connector>
          <xsl:for-each select="@*">
            <xsl:variable name="aName" select="local-name(.)"/>
            <xsl:variable name="aValue" select="."/>
            <xsl:choose>
              <xsl:when test="$aName = 'port' and $aValue = $defaultPort">
                <xsl:attribute name="{$aName}"><xsl:value-of select="$aValue"/></xsl:attribute>
                <xsl:if test="$URIEncoding = ''">
                  <xsl:attribute name="URIEncoding"><xsl:text>UTF-8</xsl:text></xsl:attribute>
                </xsl:if>
              </xsl:when>
              <!-- do we have to redirect everything to our configured redirect port -->
              <xsl:when test="$aName = 'redirectPort'"><xsl:attribute name="{$aName}"><xsl:value-of select="$portNumber"/></xsl:attribute></xsl:when>
              <xsl:otherwise><xsl:attribute name="{$aName}"><xsl:value-of select="$aValue"/></xsl:attribute></xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </Connector>  
      </xsl:for-each>
      
      <xsl:if test="not(count(//Connector[@port = $portNumber]))">
        <Connector port="{$portNumber}" URIEncoding="UTF-8" maxHttpHeaderSize="8192" 
                   maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
                   enableLookups="false" disableUploadTimeout="true"
                   acceptCount="100" scheme="https" secure="true"
                   clientAuth="false" sslProtocol="TLS"
                   keystoreFile="{$keystoreFile}" keystorePass="{$keystorePass}" />
      </xsl:if>
      <xsl:copy-of select="Engine"/>
    </Service>
  </xsl:template>

</xsl:stylesheet>
