<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:output method="html"/>
  
<!--
  <xsl:param name="servlet.context"/>  
-->
  
  <xsl:template match="/">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
        <title>Yanel Info</title>
      </head>
      <body>
        <h1>Yanel Info</h1>
        <h2>Registered Realms</h2>
        <xsl:apply-templates select="/yanel-info/realms"/>
        <h2>Registered Resources</h2>
        <xsl:apply-templates select="/yanel-info/resourcetypes"/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="realms">
    <ul>
      <xsl:apply-templates select="realm"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="realm">
    <li>
      <a href=".{mountpoint}">
<!--
        <xsl:attribute name="href">
          <xsl:value-of select="$servlet.context"/><xsl:value-of select="./mountpoint" />
        </xsl:attribute>
-->
        <xsl:value-of select="./name"/>
      </a> (ID: <xsl:value-of select="./id"/>)</li>
  </xsl:template>
  
  <xsl:template match="resourcetypes">
    <ul>
      <xsl:apply-templates select="resourcetype"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="resourcetype">
    <li>
      <xsl:value-of select="./localname"/>
    </li>
  </xsl:template>

</xsl:stylesheet>
