<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:yh="http://yanel.wyona.org/homepage/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:rm="http://yanel.wyona.org/roadmap/1.0"
>

<xsl:output method="xhtml" encoding="UTF-8" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

<xsl:param name="os" select="'OS_NULL'"/>
<xsl:param name="client" select="'CLIENT_NULL'"/>
<xsl:param name="language" select="'LANGUAGE_NULL'"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
  <title>Roadmap</title>  
<!--
  <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-homepage.xml"/>
-->
</head>
<body>
<h3>Roadmap</h3>
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

<xsl:template match="rm:release">
<h4>Release <xsl:value-of select="@version"/></h4>
<table width="100%" border="1">
<tbody>
<tr>
<th width="55%">Feature</th><th width="15%">Status</th><th width="15%">Due Date</th><th width="15%">Assigned To</th><th>Task Tracker</th>
</tr>
<xsl:apply-templates/>
</tbody>
</table>
</xsl:template>

<xsl:template match="rm:feature">
<tr>
  <xsl:copy-of select="rm:td[1]"/>
  <td><xsl:value-of select="rm:td[2]"/></td>
  <td><xsl:value-of select="rm:td[3]"/></td>
  <td><xsl:value-of select="rm:td[4]"/></td>
  <td><xsl:choose><xsl:when test="rm:td[5] and rm:td[5] != '-'"><xsl:choose><xsl:when test="rm:td[2] = 'done'"><strike><a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id={rm:td[5]}"><xsl:value-of select="rm:td[5]"/></a></strike></xsl:when><xsl:otherwise><a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id={rm:td[5]}"><xsl:value-of select="rm:td[5]"/></a></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise>-</xsl:otherwise></xsl:choose></td>
</tr>
</xsl:template>

</xsl:stylesheet>
