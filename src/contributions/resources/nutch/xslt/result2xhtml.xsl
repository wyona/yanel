<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"  
>
 
  <xsl:output method="xhtml"/>
  <!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
  <!--
  <xsl:output method="html"/>
  -->
  
  <xsl:variable name="query"><xsl:value-of select="/yanel:nutch/yanel:query"/></xsl:variable>
  <xsl:variable name="totalHits" select="/yanel:nutch/yanel:results/@yanel:totalHits" />
  <xsl:variable name="hitsPerPage" select="/yanel:nutch/yanel:results/@yanel:hitsPerPage" />
  <xsl:variable name="currentPageNo" select="/yanel:nutch/yanel:results/@yanel:currentPageNo" />
  <xsl:variable name="numberOfPagesShown" select="/yanel:nutch/yanel:results/@yanel:numberOfPagesShown" />
  <xsl:variable name="range" select="number($numberOfPagesShown div 2)" />
  
  <xsl:template match="/">
    
    <html>
    
    <head>
      <title>Results for query: [<xsl:value-of select="$query"/>]</title>
      <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
    </head>
    
    <body>
    <form>
    <p>
      <input type="text" name="query" value="{$query}"/>
      <input type="hidden" name="hitsPerPage" value="{$hitsPerPage}"/>
      <input type="submit" value="Search"/>
    </p>
    </form>
    
    <p>
      <xsl:apply-templates/>
    </p>
    
    </body>
    </html>
  </xsl:template>
  
  <xsl:template match="yanel:query">
    <!-- do nothing -->
  </xsl:template>
  
  <xsl:template match="yanel:no-query">
<!--
  <xhtml:p>No query yet.</xhtml:p>
-->
  </xsl:template>
  
  <xsl:template match="yanel:results">
    <xsl:apply-templates select="yanel:exception"/>
    <xhtml:table border="0">
      <xsl:choose>
        <xsl:when test="number(@yanel:totalHits) > 0">
          <xhtml:tr>
            <xhtml:td colspan="2"><xhtml:hr/></xhtml:td>
          </xhtml:tr>
          <xhtml:tr>
            <xhtml:td width="50%"></xhtml:td>
            <xhtml:td align="right">
              <xhtml:font size="-1">
                Results <xhtml:b><xsl:value-of select="number(@yanel:start + 1)"/></xhtml:b>- 
                
                <xsl:variable name="maxHit">
                  <xsl:choose>
                    <xsl:when test="number(@yanel:start + @yanel:hitsPerPage) &gt; number(@yanel:totalHits)"><xsl:value-of select="number(@yanel:totalHits)" /></xsl:when>
                    <xsl:otherwise><xsl:value-of select="number(@yanel:start + @yanel:hitsPerPage)" /></xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xhtml:b><xsl:value-of select="$maxHit"/></xhtml:b> of about 
                <xhtml:b><xsl:value-of select="number(@yanel:totalHits)"/></xhtml:b> for 
                <xhtml:b><xsl:value-of select="$query"/></xhtml:b><xhtml:br/>
                <xhtml:a href="?query={$query}&amp;hitsPerPage={$hitsPerPage}&amp;start={number(@yanel:start)}&amp;yanel.resource.viewid=source">view results as XML</xhtml:a>
              </xhtml:font>
            </xhtml:td>
          </xhtml:tr>
          
          <xsl:for-each select="yanel:result">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          
          <xhtml:table width="100%" border="0">
            <xhtml:tr>
              <xhtml:td></xhtml:td>
     	        <xhtml:td align="center">
                <xhtml:font size="-2">
                  <xsl:call-template name="makeLinksForPagening">
     	              <xsl:with-param name="pageNo">1</xsl:with-param>
     	            </xsl:call-template>
                </xhtml:font>
     	        </xhtml:td>
     	        <xhtml:td></xhtml:td>
     	      </xhtml:tr>
          </xhtml:table>
          
        </xsl:when>
        <xsl:when test="$query = ''"><!-- if no query inserted show empty form --></xsl:when>
        <xsl:otherwise>
          <xhtml:tr>
            <xhtml:td colspan="2">Your search - <xhtml:b><xsl:value-of select="$query" /></xhtml:b> - did not match any documents.</xhtml:td>
          </xhtml:tr>
        </xsl:otherwise>
      </xsl:choose>
    </xhtml:table>
  </xsl:template>
  
  <xsl:template name="makeLinksForPagening">
  	<xsl:param name="pageNo"/>
    <xsl:variable name="minPage"><xsl:value-of select="number($currentPageNo - $range - 1)" /></xsl:variable>
    <xsl:variable name="maxPage"><xsl:value-of select="number($currentPageNo + $range)" /></xsl:variable>
    <xsl:if test="number(floor($totalHits div $hitsPerPage)) != 0">
      <xsl:if test="number($pageNo - 1) != number(ceiling($totalHits div $hitsPerPage))">
        <xsl:choose>
          <xsl:when test="number($pageNo) = number($currentPageNo)">
            <xsl:if test="number($pageNo) &gt; number($minPage)">
              <xsl:if test="number($pageNo) &lt; number($maxPage)">
                <xsl:value-of select="$pageNo" />&#0160;
              </xsl:if>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="number($pageNo) &gt; number($minPage)">
              <xsl:if test="number($pageNo) &lt; number($maxPage)">
                <a href="?query={$query}&amp;hitsPerPage={$hitsPerPage}&amp;start={number(number($pageNo - 1) * $hitsPerPage)}"><xsl:value-of select="$pageNo" /></a>&#0160;
              </xsl:if>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="makeLinksForPagening">
          <xsl:with-param name="pageNo"><xsl:value-of select="number($pageNo + 1)" /></xsl:with-param>
        </xsl:call-template>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="yanel:result">
    <xhtml:table>
    
      <xhtml:tr>
        <xhtml:td colspan="2">
          <xhtml:a>
            <xsl:attribute name="href">
              <xsl:value-of select="yanel:url"/>
            </xsl:attribute>
            <xsl:value-of select="yanel:title"/>
          </xhtml:a>
        </xhtml:td>
      </xhtml:tr>
      
      <xhtml:tr>
        <xhtml:td colspan="2">
          <xhtml:font size="-1">
            <xsl:for-each select="yanel:fragments">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </xhtml:font>
        </xhtml:td>
      </xhtml:tr>
      
      <xhtml:tr>
        <xhtml:td clospan="2"><xhtml:font size="-2" color="green"><xsl:value-of select="yanel:url"/></xhtml:font></xhtml:td>
      </xhtml:tr>
      
    </xhtml:table>
  </xsl:template>
  
  <xsl:template match="yanel:fragment">
    <xsl:choose>
      <xsl:when test="@yanel:highlight = 'true'"><xhtml:b><xsl:value-of select="."/></xhtml:b></xsl:when>
      <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="yanel:exception">
    <p>
       <div style="color: red; font-size: 24px;">Exception:</div> <xsl:value-of select="."/>
    </p>
  </xsl:template>

</xsl:stylesheet>
