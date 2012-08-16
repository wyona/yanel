<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cal="http://..."
>

<xsl:output method="text"/>

<xsl:template match="/">
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//Mozilla.org/NONSGML Mozilla Calendar V1.1//EN
<xsl:apply-templates select="/calendar/cal:event"/>END:VCALENDAR
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
