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
          <p>'<i>Cleaning the index</i>' means, that all entries inside the index, but which don't exist anymore inside the data repository, will be removed.</p>
          <p>Select the repository for which you would like to clean the index. Please note, that by selecting a repository, you will receive a list of all entries inside the index, but which don't exist anymore inside the repository, but these entries won't be removed. Only when you confirm this list, then the entries will be removed from the index for good:</p>
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
    <xsl:if test="not(@deleted='true')">
    <h3>Missing nodes inside repository: <xsl:value-of select="/y:clean-index/y:repository"/></h3>
    </xsl:if>
    <ul>
    <xsl:for-each select="y:path">
      <li><xsl:value-of select="."/></li>
    </xsl:for-each>
    </ul>

    <xsl:if test="not(@deleted='true')">
    <a href="?repository={/y:clean-index/y:repository/@id}&amp;delete=true">Clean index</a>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
