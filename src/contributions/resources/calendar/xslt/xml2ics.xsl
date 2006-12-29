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
SUMMARY:<xsl:value-of select="cal:summary"/>
CLASS:PUBLIC
DTSTART;TZID=/mozilla.org/20050126_1/Africa/Ceuta:20061130T110000
DTEND;TZID=/mozilla.org/20050126_1/Africa/Ceuta:20061130T130000
LOCATION:<xsl:value-of select="cal:location"/>
CATEGORIES:Customer
END:VEVENT
</xsl:template>

</xsl:stylesheet>
