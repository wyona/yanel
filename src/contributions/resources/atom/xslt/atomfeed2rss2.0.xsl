<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:atom="http://www.w3.org/2005/Atom"
  exclude-result-prefixes="atom"
>

<xsl:output method="xml" encoding="UTF-8" indent="yes" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

<xsl:template match="/">
  <rss version="2.0">
    <channel>
      <title><xsl:value-of select="/atom:feed/atom:title"/></title>
      <description>
        <xsl:value-of select="/atom:feed/atom:subtitle"/>
      </description>
      <link><xsl:value-of select="/atom:feed/atom:link/@href"/></link>     

      <xsl:apply-templates select="/atom:feed/atom:entry"/>
    </channel>
  </rss>
</xsl:template>

<xsl:template match="atom:entry">
  <item>
    <title><xsl:value-of select="atom:title"/></title>    
    <description>
      <xsl:apply-templates select="atom:summary"/>
      &lt;br/&gt;&lt;br/&gt;
      <xsl:apply-templates select="atom:content/*"/>
    </description>

    <pubDate>
      <xsl:apply-templates select="atom:published"/>
    </pubDate>
    <guid isPermaLink="false"><xsl:value-of select="atom:id"/></guid>
  </item>
</xsl:template>

<xsl:template match="*[namespace-uri()='http://www.w3.org/1999/xhtml']">
&lt;<xsl:value-of select="local-name()"/>&gt;
<xsl:apply-templates/>
&lt;/<xsl:value-of select="local-name()"/>&gt;
</xsl:template>

<xsl:template match="atom:published">
  <xsl:variable name="publishedDate"><xsl:value-of select="."/></xsl:variable>
  <xsl:variable name="year"><xsl:value-of select="substring($publishedDate,1,4)"/></xsl:variable>
  <xsl:variable name="monthNumber"><xsl:value-of select="substring($publishedDate,6,2)"/></xsl:variable>
  <xsl:variable name="dayNumber"><xsl:value-of select="substring($publishedDate,9,2)"/></xsl:variable>
  <xsl:variable name="time"><xsl:value-of select="substring($publishedDate,12,8)"/></xsl:variable>

  <xsl:variable name="dayName">
    <xsl:call-template name="get-day-of-the-week-name">
      <xsl:with-param name="day-of-the-week">
        <xsl:call-template name="compute-day-of-the-week">
          <xsl:with-param name="year" select="$year"/>
          <xsl:with-param name="month" select="$monthNumber"/>
          <xsl:with-param name="day" select="$dayNumber"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="monthName">
    <xsl:call-template name="get-month-name">
      <xsl:with-param name="month" select="$monthNumber"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:value-of select="concat($dayName,',',' ',$dayNumber,' ',$monthName,' ',$year,' ',$time,' +0000')"/>   
</xsl:template>

<xsl:template name="compute-day-of-the-week">
  <xsl:param name="year"/>
  <xsl:param name="month"/>
  <xsl:param name="day"/>

  <xsl:variable name="a" select="floor((14 - $month) div 12)"/>
  <xsl:variable name="y" select="$year - $a"/>
  <xsl:variable name="m" select="$month + 12 * $a - 2"/>

  <xsl:value-of select="($day + $y + floor($y div 4) - floor($y div 100) + floor($y div 400) + floor((31 * $m) div 12)) mod 7"/>
</xsl:template>

<xsl:template name="get-day-of-the-week-name">
  <xsl:param name="day-of-the-week"/>

  <xsl:choose>
    <xsl:when test="$day-of-the-week = 0">Sun</xsl:when>
    <xsl:when test="$day-of-the-week = 1">Mon</xsl:when>
    <xsl:when test="$day-of-the-week = 2">Tue</xsl:when>
    <xsl:when test="$day-of-the-week = 3">Wed</xsl:when>
    <xsl:when test="$day-of-the-week = 4">Thu</xsl:when>
    <xsl:when test="$day-of-the-week = 5">Fri</xsl:when>
    <xsl:when test="$day-of-the-week = 6">Sat</xsl:when>
    <xsl:otherwise>error: <xsl:value-of select="$day-of-the-week"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="get-month-name">
  <xsl:param name="month"/>
  
  <xsl:choose>
    <xsl:when test="$month = 1">Jan</xsl:when>
    <xsl:when test="$month = 2">Feb</xsl:when>
    <xsl:when test="$month = 3">Mar</xsl:when>
    <xsl:when test="$month = 4">Apr</xsl:when>
    <xsl:when test="$month = 5">May</xsl:when>
    <xsl:when test="$month = 6">Jun</xsl:when>
    <xsl:when test="$month = 7">Jul</xsl:when>
    <xsl:when test="$month = 8">Aug</xsl:when>
    <xsl:when test="$month = 9">Sep</xsl:when>
    <xsl:when test="$month = 10">Oct</xsl:when>
    <xsl:when test="$month = 11">Nov</xsl:when>
    <xsl:when test="$month = 12">Dec</xsl:when>
    <xsl:otherwise>error: <xsl:value-of select="$month"/></xsl:otherwise>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
