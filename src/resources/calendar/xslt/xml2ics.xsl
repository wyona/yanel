<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cal="http://..."
>

<!-- See specification at http://www.ietf.org/rfc/rfc2445.txt -->

<xsl:output method="text"/>

<xsl:template match="/">BEGIN:VCALENDAR
PRODID:-//Mozilla.org/NONSGML Mozilla Calendar V1.1//EN
VERSION:2.0
BEGIN:VTIMEZONE
TZID:Europe/Zurich
X-LIC-LOCATION:Europe/Zurich
BEGIN:DAYLIGHT
TZOFFSETFROM:+0100
TZOFFSETTO:+0200
TZNAME:CEST
DTSTART:19700329T020000
RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3
END:DAYLIGHT
BEGIN:STANDARD
TZOFFSETFROM:+0200
TZOFFSETTO:+0100
TZNAME:CET
DTSTART:19701025T030000
RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10
END:STANDARD
END:VTIMEZONE
BEGIN:VTIMEZONE
TZID:W. Europe Standard Time
BEGIN:STANDARD
DTSTART:16010101T030000
TZOFFSETFROM:+0200
TZOFFSETTO:+0100
RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10
END:STANDARD
BEGIN:DAYLIGHT
DTSTART:16010101T020000
TZOFFSETFROM:+0100
TZOFFSETTO:+0200
RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3
END:DAYLIGHT
END:VTIMEZONE
BEGIN:VTIMEZONE
TZID:GMT Standard Time
BEGIN:STANDARD
DTSTART:16010101T020000
TZOFFSETFROM:+0100
TZOFFSETTO:+0000
RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10
END:STANDARD
BEGIN:DAYLIGHT
DTSTART:16010101T010000
TZOFFSETFROM:+0000
TZOFFSETTO:+0100
RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3
END:DAYLIGHT
END:VTIMEZONE
<!--
<xsl:apply-templates select="/calendar/cal:event"/>END:VCALENDAR
-->
<xsl:for-each select="/calendar/cal:event">
<xsl:sort select="cal:dtstart/@tzid"/>
<xsl:apply-templates select="."/>
</xsl:for-each>END:VCALENDAR
</xsl:template>

<xsl:template match="cal:event">BEGIN:VEVENT
CREATED:<xsl:value-of select="@created"/>
LAST-MODIFIED:<xsl:value-of select="@last-modified"/>
DTSTAMP:<xsl:value-of select="@dtstamp"/>
UID:<xsl:value-of select="@uid"/>
<xsl:apply-templates select="cal:summary"/><xsl:apply-templates select="@class"/><xsl:apply-templates select="cal:dtstart"/><xsl:apply-templates select="cal:dtend"/><xsl:apply-templates select="cal:location"/><xsl:apply-templates select="@categories"/>
END:VEVENT
</xsl:template>

<xsl:template match="cal:dtstart">
DTSTART;TZID=<xsl:value-of select="@tzid"/></xsl:template>

<xsl:template match="cal:dtend">
DTEND;TZID=<xsl:value-of select="@tzid"/></xsl:template>

<xsl:template match="cal:location">
LOCATION:<xsl:value-of select="."/></xsl:template>

<xsl:template match="cal:summary">
SUMMARY:<xsl:value-of select="."/></xsl:template>

<xsl:template match="@class">
CLASS:<xsl:value-of select="."/></xsl:template>

<xsl:template match="@categories">
CATEGORIES:<xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>
