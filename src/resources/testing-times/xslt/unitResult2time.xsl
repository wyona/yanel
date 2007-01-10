<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:param name="testname" select="'all'"/>
  <xsl:param name="date"/>

  <xsl:strip-space elements="*"/>
  
  <xsl:template match="/">
   <xsl:apply-templates select="testsuites"/>
  </xsl:template>
  
  <xsl:template match="testsuites">
    <xsl:copy>
      <xsl:choose>
        <xsl:when test="$testname = 'all'">
          <xsl:apply-templates />
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="testsuite[@name=$testname]"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="testsuite[@failures > 0]"/>
  <xsl:template match="testsuite[@errors > 0]"/>
  
  <xsl:template match="testsuite">
    <xsl:copy>
      <xsl:attribute name="date"><xsl:value-of select="$date"/></xsl:attribute>
      <xsl:copy-of select="@*[not(name()='id')]"/>
      <xsl:apply-templates select="testcase"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="testcase">
    <xsl:copy>
      <xsl:attribute name="date"><xsl:value-of select="$date"/></xsl:attribute>
      <xsl:copy-of select="@*"/>
    <xsl:choose>
      <xsl:when test="failure">
        <xsl:attribute name="failure">1</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="failure">0</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
        <xsl:choose>
      <xsl:when test="error">
        <xsl:attribute name="error">1</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="error">0</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
