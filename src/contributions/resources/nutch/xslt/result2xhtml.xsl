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
  
  <xsl:param name="yanel.meta.language" select="/yanel:nutch/@yanel:language"/>
  
  <xsl:param name="query" select="/yanel:nutch/yanel:query"/>
  <xsl:param name="totalHits" select="/yanel:nutch/yanel:results/@yanel:totalHits" />
  <xsl:param name="hitsPerPage" select="/yanel:nutch/yanel:results/@yanel:hitsPerPage" />
  <xsl:variable name="currentPageNo" select="/yanel:nutch/yanel:results/@yanel:currentPageNo" />
  <xsl:variable name="numberOfPagesShown" select="/yanel:nutch/yanel:results/@yanel:numberOfPagesShown" />
  <xsl:variable name="range" select="number($numberOfPagesShown div 2)" />
  
              <xsl:variable name="maxHit">
                <xsl:choose>
                  <xsl:when test="number(/yanel:nutch/yanel:results/@yanel:start + $hitsPerPage) &gt; number(/yanel:nutch/yanel:results/@yanel:totalHits)"><xsl:value-of select="number(/yanel:nutch/yanel:results/@yanel:totalHits)" /></xsl:when>
                  <xsl:otherwise><xsl:value-of select="number(/yanel:nutch/yanel:results/@yanel:start + $hitsPerPage)" /></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

  <xsl:template match="/">
    <html>
    
    <head>
      <title><xsl:apply-templates select="/yanel:nutch/yanel:no-query" mode="title"/><xsl:apply-templates select="/yanel:nutch/yanel:query" mode="title"/></title>
      <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
    </head>
    
    <body>
    <span id="pageInfo" itemsPerPage="{$hitsPerPage}"/>
    <form name="search">
      <table width="100%">
        <tr>
          <td>
            <input type="text" name="query" value="{$query}"/>
            <input type="hidden" name="yanel.meta.language" value="{$yanel.meta.language}"/>
            <input type="hidden" name="totalHits" value="{$totalHits}"/>
            <input type="submit" name="submit" value="i18n:attr key=search"/>
          </td>
          <td align="right">
            <font size="-2">
              <a href="?query={$query}&amp;hitsPerPage={$hitsPerPage}&amp;start={number(@yanel:start)}&amp;yanel.resource.viewid=source"><i18n:message key="viewResultsAsXml"/></a>
            </font>
          </td>
        </tr>
      </table>
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
    <i18n:message key="resultsForQuery"/><xsl:text> </xsl:text><xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="yanel:no-query" mode="title">
    Search
  </xsl:template>
  
  <xsl:template match="yanel:results">
    <div id="resultHits" hits="{$totalHits}"/>
    <table width="100%" id="exception">
      <tr>
        <td>
          <xsl:apply-templates select="yanel:exception"/>
        </td>
      </tr>
    </table>
    
    <xsl:if test="number(@yanel:totalHits) > 0">
      <table width="100%" id="tableHeadline">
        <tr>
          <td colspan="2"><hr/></td>
        </tr>
        <tr>
          <td align="right" colspan="2">
            <font size="-1">
              <i18n:message key="results"/>&#160;<b><xsl:value-of select="number(@yanel:start + 1)"/></b>- 
              
              <b><xsl:value-of select="$maxHit"/></b>&#160;<i18n:message key="ofAbout"/>&#160;<b><xsl:value-of select="number(@yanel:totalHits)"/></b><!--&#160;<i18n:message key="hitsFound"/>-->&#160;<i18n:message key="for"/>&#160;<b><xsl:value-of select="$query"/></b><br/>
            </font>
          </td>
        </tr>
      </table>
    </xsl:if>
    
    <table border="0" id="results">
      <xsl:choose>
        <xsl:when test="number(@yanel:totalHits) > 0">
          <tr>
            <td colspan="2">
              <xsl:for-each select="yanel:result">
                <xsl:apply-templates select="."/>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:when>
        <xsl:when test="$query = ''"></xsl:when>
        <xsl:otherwise>
          <tr>
            <td colspan="2">
              <i18n:message key="yourSearch"/> - <b><xsl:value-of select="$query" /></b> - <i18n:message key="didNotMatchAnyDocuments"/>
            </td>
          </tr>
        </xsl:otherwise>
      </xsl:choose>
    </table>
    
    <xsl:if test="number(@yanel:totalHits) > 0">
      <table width="100%" border="0" id="pagening">
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
    </xsl:if>
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
                <a href="?query={$query}&amp;hitsPerPage={$hitsPerPage}&amp;start={number(number($pageNo - 1) * $hitsPerPage)}&amp;yanel.meta.language={$yanel.meta.language}"><xsl:value-of select="$pageNo" /></a>&#0160;
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
            <b><xsl:if test="not(string(yanel:title))">No Title</xsl:if><xsl:value-of select="yanel:title"/></b>
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
      
      <xsl:variable name="idx"><xsl:value-of select="yanel:hitIndexNo"/></xsl:variable>
      <xsl:variable name="id"><xsl:value-of select="yanel:hitIndexDocNo"/></xsl:variable>
      
      <tr>
        <td clospan="2">
          <font size="-2" color="green"><xsl:value-of select="yanel:url"/></font>
          <font size="-2">
            <xsl:text>&#0160;(</xsl:text><a href="?show=cache&amp;idx={$idx}&amp;id={$id}"><i18n:message key="cached"/></a><xsl:text>)</xsl:text>
            <xsl:text>&#0160;(</xsl:text><a href="?show=explain&amp;query={$query}&amp;yanel.meta.language={$yanel.meta.language}&amp;idx={$idx}&amp;id={$id}"><i18n:message key="explain"/></a><xsl:text>)</xsl:text>
            <xsl:text>&#0160;(</xsl:text><a href="?show=anchors&amp;idx={$idx}&amp;id={$id}"><i18n:message key="anchors"/></a><xsl:text>)</xsl:text>
          </font>
        </td>
      </tr>
      
    </table>
  </xsl:template>
  
  <xsl:template match="yanel:fragment">
    <!-- <xsl:choose>
      <xsl:when test="@yanel:highlight = 'true'"><b><xsl:value-of select="."/></b></xsl:when>
      <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
    </xsl:choose> -->
  </xsl:template>
  
  <xsl:template match="yanel:exception">
    <tr>
      <td colspan="2">
        <div style="color: red; font-size: 24px;"><i18n:message key="exception"/></div>
        <xsl:variable name="message"><xsl:value-of select="substring-before(string(.), '#')"/></xsl:variable>
        <xsl:variable name="directory"><xsl:value-of select="substring-after(string(.), '#')"/></xsl:variable>
        <i18n:message key="{$message}"/><xsl:value-of select="$directory"/>
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
