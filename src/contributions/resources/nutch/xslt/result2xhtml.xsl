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
  
  <xsl:param name="language" select="/yanel:nutch/@yanel:language"/>
  
  <xsl:variable name="query"><xsl:value-of select="/yanel:nutch/yanel:query"/></xsl:variable>
  <xsl:variable name="totalHits" select="/yanel:nutch/yanel:results/@yanel:totalHits" />
  <xsl:variable name="hitsPerPage" select="/yanel:nutch/yanel:results/@yanel:hitsPerPage" />
  <xsl:variable name="currentPageNo" select="/yanel:nutch/yanel:results/@yanel:currentPageNo" />
  <xsl:variable name="numberOfPagesShown" select="/yanel:nutch/yanel:results/@yanel:numberOfPagesShown" />
  <xsl:variable name="range" select="number($numberOfPagesShown div 2)" />
  
  <xsl:template match="/">
    
    <html>
    
    <head>
      <title><xsl:apply-templates select="/yanel:nutch/yanel:no-query" mode="title"/><xsl:apply-templates select="/yanel:nutch/yanel:query" mode="title"/></title>
      <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
    </head>
    
    <body>
    
    <form>
    <p>
      <input type="text" name="query" value="{$query}"/>
      <input type="hidden" name="hitsPerPage" value="{$hitsPerPage}"/>
      <input type="hidden" name="language" value="{$language}"/>
      <input type="submit" value="i18n:attr key=search"/>
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
  <p>No query yet.</p>
-->
  </xsl:template>

  <xsl:template match="yanel:query" mode="title">
    <i18n:message key="resultsForQuery"/><xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="yanel:no-query" mode="title">
    <i18n:message key="resultsForQuery"/>
  </xsl:template>
  
  <xsl:template match="yanel:results">
    <xsl:apply-templates select="yanel:exception"/>
    <table border="0">
      <xsl:choose>
        <xsl:when test="number(@yanel:totalHits) > 0">
          <tr>
            <td colspan="2"><hr/></td>
          </tr>
          <tr>
            <td width="50%"></td>
            <td align="right">
              <font size="-1">
                <i18n:message key="results"/> <b><xsl:value-of select="number(@yanel:start + 1)"/></b>- 
                
                <xsl:variable name="maxHit">
                  <xsl:choose>
                    <xsl:when test="number(@yanel:start + @yanel:hitsPerPage) &gt; number(@yanel:totalHits)"><xsl:value-of select="number(@yanel:totalHits)" /></xsl:when>
                    <xsl:otherwise><xsl:value-of select="number(@yanel:start + @yanel:hitsPerPage)" /></xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <b><xsl:value-of select="$maxHit"/></b> <i18n:message key="ofAbout"/>
                <b><xsl:value-of select="number(@yanel:totalHits)"/></b> <i18n:message key="for"/> 
                <b><xsl:value-of select="$query"/></b><br/>
                <a href="?query={$query}&amp;hitsPerPage={$hitsPerPage}&amp;start={number(@yanel:start)}&amp;yanel.resource.viewid=source"><i18n:message key="viewResultsAsXml"/></a>
              </font>
            </td>
          </tr>
          
          <xsl:for-each select="yanel:result">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
          
          <table width="100%" border="0">
            <tr>
              <td></td>
     	        <td align="center">
                <font size="-2">
                  <xsl:call-template name="makeLinksForPagening">
     	              <xsl:with-param name="pageNo">1</xsl:with-param>
     	            </xsl:call-template>
                </font>
     	        </td>
     	        <td></td>
     	      </tr>
          </table>
          
        </xsl:when>
        <xsl:when test="$query = ''"><!-- if no query inserted show empty form --></xsl:when>
        <xsl:otherwise>
          <tr>
            <td colspan="2"><i18n:message key="yourSearch"/> - <b><xsl:value-of select="$query" /></b> - <i18n:message key="didNotMatchAnyDocments"/></td>
          </tr>
        </xsl:otherwise>
      </xsl:choose>
    </table>
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
    <table>
    
      <tr>
        <td colspan="2">
          <a>
            <xsl:attribute name="href">
              <xsl:value-of select="yanel:url"/>
            </xsl:attribute>
            <xsl:value-of select="yanel:title"/>
          </a>
        </td>
      </tr>
      
      <tr>
        <td colspan="2">
          <font size="-1">
            <xsl:for-each select="yanel:fragments">
              <xsl:apply-templates select="."/>
            </xsl:for-each>
          </font>
        </td>
      </tr>
      
      <tr>
        <td clospan="2"><font size="-2" color="green"><xsl:value-of select="yanel:url"/></font></td>
      </tr>
      
    </table>
  </xsl:template>
  
  <xsl:template match="yanel:fragment">
    <xsl:choose>
      <xsl:when test="@yanel:highlight = 'true'"><b><xsl:value-of select="."/></b></xsl:when>
      <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="yanel:exception">
    <p>
       <div style="color: red; font-size: 24px;"><i18n:message key="exception"/></div> <xsl:value-of select="."/>
    </p>
  </xsl:template>

</xsl:stylesheet>
