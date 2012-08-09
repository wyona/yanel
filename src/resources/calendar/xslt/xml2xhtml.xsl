<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"  
  xmlns:cal="http://..."
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="cal"
>

<xsl:output method="xhtml" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>Calendar</title>
    </head>      
    <body>
      <h2>Calendar</h2>
      <table border="1">
      <xsl:apply-templates select="/calendar/cal:event"/>
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template match="cal:event">  
  <tr bgcolor="#CCCCCC">
    <td><h3>EVENT</h3></td><td/><td/><td/>
  </tr>
  <tr>
    <xsl:apply-templates select="cal:summary"/>
    <xsl:apply-templates select="cal:location"/>
  </tr>
  <tr>
    <td><h3>Start Date:</h3></td>
    <xsl:apply-templates select="cal:dtstart"/>
    <td><h3>End Date:</h3></td>
    <xsl:apply-templates select="cal:dtend"/>
  </tr>
  <tr>
    <td>(Time Zone) </td>
    <td><xsl:value-of select="substring-before(cal:dtstart/@tzid,':')"/></td>
    <td>(Time Zone) </td>
    <td><xsl:value-of select="substring-before(cal:dtend/@tzid,':')"/></td>    
  </tr>
  <tr>
    <xsl:apply-templates select="@categories"/> 
    <xsl:apply-templates select="@class"/>     
  </tr>
  <tr>
  <td><h3>Created:</h3></td>
  <td>
    <xsl:variable name="created">
    <xsl:call-template name="compute-date-v1">
      <xsl:with-param name="date" select="@created"/>
    </xsl:call-template>  
    </xsl:variable>    
    <xsl:value-of select="$created"/>      
  </td>
  <td><h3>Last-Modified:</h3></td>
  <td>
    <xsl:variable name="last-modified">
    <xsl:call-template name="compute-date-v1">
      <xsl:with-param name="date" select="@last-modified"/>
    </xsl:call-template>  
    </xsl:variable>    
    <xsl:value-of select="$last-modified"/> 
  </td>
  </tr>
  <tr>
  <td><h3>DtStamp:</h3></td>
  <td>
    <xsl:variable name="dtstamp">
    <xsl:call-template name="compute-date-v1">
      <xsl:with-param name="date" select="@dtstamp"/>
    </xsl:call-template>  
    </xsl:variable>    
    <xsl:value-of select="$dtstamp"/> 
  </td>
  <td><h3>UID:</h3></td>
  <td><xsl:value-of select="@uid"/></td>
  </tr> 
</xsl:template>

<xsl:template match="cal:dtstart">   
   <td>
   <xsl:variable name="start-date">
    <xsl:call-template name="compute-date-v2">
      <xsl:with-param name="tzid" select="@tzid"/>
    </xsl:call-template>  
    </xsl:variable>    
    <xsl:value-of select="$start-date"/> 
   </td>
</xsl:template>

<xsl:template match="cal:dtend">
  <td> 
    <xsl:variable name="end-date">
    <xsl:call-template name="compute-date-v2">
      <xsl:with-param name="tzid" select="@tzid"/>
    </xsl:call-template>  
    </xsl:variable>    
    <xsl:value-of select="$end-date"/> 
  </td>
</xsl:template>

<xsl:template match="cal:location">
  <td><h3>Location:</h3></td>
  <td><xsl:value-of select="."/></td>
</xsl:template>

<xsl:template match="cal:summary">
  <td><h3>Summary:</h3></td>
  <td><xsl:value-of select="."/></td>
</xsl:template>

<xsl:template match="@class">
  <td><h3>Class:</h3></td>
  <td><xsl:value-of select="."/></td>
</xsl:template>

<xsl:template match="@categories">
  <td><h3>Categories:</h3></td>
  <td><xsl:value-of select="."/></td>
</xsl:template>

<xsl:template name="compute-date-v1">
  <xsl:param name="date"/>
  <xsl:variable name="year"><xsl:value-of select="substring($date,1,4)"/></xsl:variable>
  <xsl:variable name="monthNumber"><xsl:value-of select="substring($date,5,2)"/></xsl:variable>
  <xsl:variable name="dayNumber"><xsl:value-of select="substring($date,7,2)"/></xsl:variable>
  <xsl:variable name="time"><xsl:value-of select="substring($date,10,6)"/></xsl:variable>

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

  <xsl:variable name="timeString">
    <xsl:call-template name="get-time">
      <xsl:with-param name="time" select="$time"/>
    </xsl:call-template>
  </xsl:variable>  

  
  <xsl:value-of select="concat($dayName,',',' ',$dayNumber,' ',$monthName,' ',$year,' ',$timeString,' +00:00')"/>   
</xsl:template>

<xsl:template name="compute-date-v2">
  <xsl:param name="tzid"/>
  <xsl:variable name="timeZone"><xsl:value-of select="substring-before($tzid,':')"/></xsl:variable>
  <xsl:variable name="date"><xsl:value-of select="substring-after($tzid,':')"/></xsl:variable>
  <xsl:variable name="year"><xsl:value-of select="substring($date,1,4)"/></xsl:variable>
  <xsl:variable name="monthNumber"><xsl:value-of select="substring($date,5,2)"/></xsl:variable>
  <xsl:variable name="dayNumber"><xsl:value-of select="substring($date,7,2)"/></xsl:variable>
  <xsl:variable name="time"><xsl:value-of select="substring($date,10,6)"/></xsl:variable>

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

  <xsl:variable name="timeString">
    <xsl:call-template name="get-time">
      <xsl:with-param name="time" select="$time"/>
    </xsl:call-template>
  </xsl:variable>  
  
  <xsl:choose>
    <xsl:when test="starts-with($timeString, '::')">
     
     
          <xsl:value-of select="concat($dayName,',',' ',$dayNumber,' ',$monthName,' ',$year)"/>
    
         
   
     
    </xsl:when>
    <xsl:otherwise>
      
      
      <xsl:value-of select="concat($dayName,',',' ',$dayNumber,' ',$monthName,' ',$year,' ',$timeString,' +00:00')"/>     
     
     
    </xsl:otherwise>
  </xsl:choose>
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

<xsl:template name="get-time">
 <xsl:param name="time"/>
 <xsl:variable name="hour"><xsl:value-of select="substring($time,1,2)"/></xsl:variable>
 <xsl:variable name="minutes"><xsl:value-of select="substring($time,3,2)"/></xsl:variable>
 <xsl:variable name="seconds"><xsl:value-of select="substring($time,5,2)"/></xsl:variable>
 <xsl:value-of select="concat($hour,':',$minutes,':',$seconds)"/> 
</xsl:template>


</xsl:stylesheet>
