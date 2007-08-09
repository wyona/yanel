<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rm="http://yanel.wyona.org/roadmap/1.0"
>

<!--
<xsl:output method="xml"/>
-->

<xsl:template match="/">
<data>
<xsl:apply-templates select="/rm:roadmap/rm:release"/>
<xsl:apply-templates select="/rm:roadmap/rm:release/rm:feature"/>
</data>
</xsl:template>

<xsl:template match="rm:release">
    <event
        start="Nov 29 2006 00:00:00 GMT"
        end="Aug 01 2007 00:00:00 GMT"
        isDuration="true"
        title="Release {@version}"
        image="http://simile.mit.edu/images/csail-logo.gif"
        >
        See &lt;a href="http://yanel.wyona.org/roadmap.html"&gt;Roadmap&lt;/a&gt; for more details.
    </event>
</xsl:template>

<xsl:template match="rm:feature">
    <xsl:variable name="date" select="rm:td[3]"/>
    <xsl:variable name="year" select="substring-before($date, '.')"/>
    <xsl:variable name="month-day" select="substring-after($date, '.')"/>
    <xsl:variable name="month" select="substring-before($month-day, '.')"/>
    <xsl:variable name="day" select="substring-after($month-day, '.')"/>
    <event
        start="{$month} {$day} {$year} 00:00:00 GMT"
        title="{rm:td[1]}"
        link="http://yanel.wyona.org/specification/access-control-user-interface.html"
        >
        <xsl:value-of select="rm:td[1]"/>
    </event>

<!--
<tr>
  <xsl:copy-of select="rm:td[1]"/>
  <td><xsl:value-of select="rm:td[2]"/></td>
  <td><xsl:value-of select="rm:td[3]"/></td>
  <td><xsl:value-of select="rm:td[4]"/></td>
  <td><xsl:choose><xsl:when test="rm:td[5] and rm:td[5] != '-'"><xsl:choose><xsl:when test="rm:td[2] = 'done'"><strike><a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id={rm:td[5]}"><xsl:value-of select="rm:td[5]"/></a></strike></xsl:when><xsl:otherwise><a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id={rm:td[5]}"><xsl:value-of select="rm:td[5]"/></a></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise>-</xsl:otherwise></xsl:choose></td>
</tr>
-->
</xsl:template>

</xsl:stylesheet>
