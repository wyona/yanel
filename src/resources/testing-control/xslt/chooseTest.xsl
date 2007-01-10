<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/></title>
      </head>
      <body>
        <div id="contenBody">
          <h1>Testing Control</h1>
          <form method="post">
            <p><xsl:value-of select="count(//xhtml:ul[@id='htmlunit']/xhtml:li)"/> htmlunit tests are available:</p>
            <xsl:apply-templates select="//xhtml:ul[@id='htmlunit']"/>
            <hr/>
            <p><xsl:value-of select="count(//xhtml:ul[@id='junit']/xhtml:li)"/> junit tests are available:</p>
            <xsl:apply-templates select="//xhtml:ul[@id='junit']"/>
            <input type="submit" name="submit" value="Test"/>
          </form>
          <form method="post">
            <xsl:apply-templates select="//xhtml:ul" mode="all"/>
            <input type="submit" name="submit" value="execute all Tests"/>
          </form>
<!--           <form method="post">
            <br/>Archived Test-Results: <select name="archived-results">
              <option value="//test-results-archive/2006-12-20-09-39-19-tests.xml">2006-12-20-09-39-19-tests.xml</option>
            </select>
            <input type="submit" name="submit" value="Show"/>
          </form> -->
        </div>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="xhtml:li">
    <p>
      <input type="checkbox" name="testnames" value="{.}"/>
      <xsl:value-of select="substring-before(@title,'.')"/>
    </p>
  </xsl:template>

  <xsl:template match="xhtml:li" mode="all">
    <input type="hidden" name="testnames" value="{.}"/>
  </xsl:template>
  
</xsl:stylesheet>
