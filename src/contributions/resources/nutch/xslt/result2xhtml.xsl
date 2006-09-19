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
  
  <xsl:template match="/">
    
    <xhtml:html>
    
    <xhtml:head>
      <xhtml:title>Results for query: [<xsl:value-of select="$query"/>]</xhtml:title>
    </xhtml:head>
    
    <xhtml:body>
    <xhtml:form>
    <xhtml:p>
      <xhtml:input type="text" name="query" value="{$query}"/>
      <xhtml:input type="submit" value="Search"/>
    </xhtml:p>
    </xhtml:form>
    
    <xhtml:p>
      <xsl:apply-templates/>
    </xhtml:p>
    </xhtml:body>
    </xhtml:html>
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
                <xhtml:b><xsl:value-of select="number(@yanel:end + 1)"/></xhtml:b> of about 
                <xhtml:b><xsl:value-of select="number(@yanel:totalHits)"/></xhtml:b> for 
                <xhtml:b><xsl:value-of select="$query"/></xhtml:b><xhtml:br/>
                <xhtml:a href="?query={$query}&amp;yanel.resource.viewid=source">view results as XML</xhtml:a>
              </xhtml:font>
            </xhtml:td>
          </xhtml:tr>
          
          <xsl:for-each select="yanel:result">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
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
      <!--
      <xhtml:tr>
        <xhtml:td>fetched Date</xhtml:td>
        <xhtml:td><i18n:date-time src-pattern="long" pattern="yyyy-MM-dd hh:mm:ss"><xsl:value-of select="yanel:fetchedDate"/></i18n:date-time></xhtml:td>
      </xhtml:tr>
      <xhtml:tr>
        <xhtml:td>segment</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:segment"/></xhtml:td>
      </xhtml:tr>
      <xhtml:tr>
        <xhtml:td>digest</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:digest"/></xhtml:td>
      </xhtml:tr>
      
      <xhtml:tr>
        <xhtml:td>Hit index document number</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:hitIndexDocNo"/></xhtml:td>
      </xhtml:tr>
      <xhtml:tr>
        <xhtml:td>deduplicate value</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:hitDedupValue"/></xhtml:td>
      </xhtml:tr>
      <xhtml:tr>
        <xhtml:td>Hit index number</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:hitIndexNo"/></xhtml:td>
      </xhtml:tr>
      -->
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
