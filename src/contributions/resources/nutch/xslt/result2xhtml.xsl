<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"  
  xmlns="http://www.wyona.org/yanel/1.0"
>

  <xsl:output method="xhtml"/>
  <!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
  <!--
  <xsl:output method="html"/>
  -->
  
  <xsl:template match="/">
    <xhtml:html>
    
    <xhtml:head>
      <xhtml:title>Results for query: [<xsl:value-of select="/yanel:nutch/yanel:query"/>]</xhtml:title>
    </xhtml:head>
    
    <xhtml:body>
    <xhtml:form>
    <xhtml:p>
      <xhtml:input type="text" name="query"/>
      <xhtml:input type="submit" value="Search"/>
    </xhtml:p>
    </xhtml:form>
    
    <xhtml:p>
    <xhtml:b>Results:</xhtml:b>
      <xsl:apply-templates/>
    </xhtml:p>
    </xhtml:body>
    </xhtml:html>
  </xsl:template>
  
  <xsl:template match="yanel:results">
    <xsl:if test="count(.)">
      <xhtml:table border="0">
        <xhtml:tr>
          <xhtml:td colspan="2"><xhtml:b>Hit results: </xhtml:b>
            <xhtml:b><xsl:value-of select="number(@yanel:start + 1)"/></xhtml:b> to 
            <xhtml:b><xsl:value-of select="number(@yanel:end + 1)"/></xhtml:b> of 
            <xhtml:b><xsl:value-of select="number(@yanel:totalHits)"/></xhtml:b> total
          </xhtml:td>
        </xhtml:tr>
        <xsl:for-each select="yanel:result">
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </xhtml:table>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="yanel:result">
    <xhtml:table>
      <xhtml:tr>
        <xhtml:td height="30" colspan="2"><xhtml:hr/></xhtml:td>
      </xhtml:tr>
      <xhtml:tr>
        <xhtml:td>index:</xhtml:td>
        <xhtml:td>[<xsl:value-of select="@yanel:index"/>]</xhtml:td>
      </xhtml:tr>
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
        <xhtml:td>url</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:url"/></xhtml:td>
      </xhtml:tr>
      <xhtml:tr>
        <xhtml:td>Title</xhtml:td>
        <xhtml:td><xsl:value-of select="yanel:title"/></xhtml:td>
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
      <xhtml:tr>
        <xhtml:td>Fragments</xhtml:td>
        <xhtml:td>
          <xsl:for-each select="yanel:fragments">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </xhtml:td>
      </xhtml:tr>
    </xhtml:table>
  </xsl:template>
  
  <xsl:template match="yanel:fragment">
    <xsl:if test="@yanel:ellipsis = 'true'"><!-- ignore dots --></xsl:if>
    <xsl:choose>
      <xsl:when test="@yanel:highlight = 'true'"><xhtml:b><xsl:value-of select="."/></xhtml:b></xsl:when>
      <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
