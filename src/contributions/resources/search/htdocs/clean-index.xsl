<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:y="http://www.wyona.org/yanel/clean-index/1.0">

  <xsl:template match="/y:clean-index">
    <html>
      <head>
        <title>Clean index of repository</title>
      </head>
      <body>
        <h1>Clean index of repository</h1>
        <xsl:apply-templates select="y:exception"/>
        <xsl:apply-templates select="y:message"/>
        <xsl:apply-templates select="y:missing-nodes"/>

<!--
        <xsl:if test="not(y:exception or y:message)">
-->
          <p>Choose the repository for which you would like to clean the index (Please note that you will be asked to confirm before the actual cleaning will start):</p>
          <ul>
            <xsl:for-each select="y:repository">
              <li>
                <a href="?repository={@id}"><xsl:value-of select="."/></a></li>
            </xsl:for-each>
          </ul>
<!--
        </xsl:if>
-->
      </body>
    </html>
  </xsl:template>

  <xsl:template match="y:exception">
    <p style="color:red;"><xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="y:message">
    <p style="color:green;"><xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="y:missing-nodes">
    <h3>Missing nodes</h3>
    <ul>
    <xsl:for-each select="y:path">
      <li><xsl:value-of select="."/></li>
    </xsl:for-each>
    </ul>
  </xsl:template>

</xsl:stylesheet>
