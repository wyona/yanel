<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dir="http://apache.org/cocoon/directory/2.0" xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0">
  <xsl:output method="xhtml"/>
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Results Archive</title>
      </head>
      <body>
          <h1>Test Results Archive:</h1>
        <p>
          <xsl:apply-templates select="//xhtml:a[@title='file']"/>
        </p>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="xhtml:a[@title='file']"> 
    Test: 
    <a href="../testing-control.html?archived-results={.}">
      <xsl:value-of select="substring-before(@href,'-test')"/>
    </a>
    <br/>
  </xsl:template>
</xsl:stylesheet>
