<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml">

  <xsl:param name="testname" select="'all'"/>

  <xsl:output method="html"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>
          <xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/>
        </title>
      </head>
      <body>
        <div id="contenBody">
          <h1>Testing Results Times</h1>
          <p>Here you can see an overview of execution times of the Junit and HTMLunit test. It just considers 
          test which did run successful.</p>
          <p>
            <form>Configure how many results should be analyzed
              <select name="configNumberOfResults">
                <xsl:apply-templates select="//xhtml:ul[@id='configResultNumbers']"/>
              </select>
              <input type="submit" value="Set"/>
            </form>
          </p>
          <xsl:apply-templates select="//xhtml:ul[@id='testnames']" mode="testnames"/>
          <p>
            <a href="?showTest=all">
            All Tests</a>
          </p>
          <img src="?showImage={$testname}" alt="All Testsuites Execution Time"/>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="xhtml:li" mode="testnames">
    <p>
      <a href="?showTest={.}">
        Details of <xsl:value-of select="."/>
      </a>
    </p>
  </xsl:template>

  <xsl:template match="xhtml:li[@id='selected']">
    <option selected="selected">
      <xsl:value-of select="."/>
    </option>
  </xsl:template>

  <xsl:template match="xhtml:li">
    <option>
      <xsl:value-of select="."/>
    </option>
  </xsl:template>

</xsl:stylesheet>
