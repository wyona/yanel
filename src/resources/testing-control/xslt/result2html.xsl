<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  
  <xsl:variable name="testsuite.list" select="//testsuite"/>
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Testing Results</title>
      </head>
      <body>
        <div id="contenBody">
          <xsl:apply-templates/>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="testsuite">
    <p>
       Name: <xsl:value-of select="@name"/>
       Errors: <xsl:value-of select="@errors"/>
       Failures: <xsl:value-of select="@failures"/>
       Packagesname: <xsl:value-of select="@package"/> 
       Execution Time: <xsl:value-of select="@time"/>
       <xsl:apply-templates/>
    </p>
  </xsl:template>
  
  <xsl:template match="system-out">
    <p>System-out: 
    <xsl:value-of select="."/>
    </p>
  </xsl:template>
  
  <xsl:template match="system-err">
    <p>System-error: 
    <xsl:value-of select="."/>
    </p>
  </xsl:template>
  
</xsl:stylesheet>

