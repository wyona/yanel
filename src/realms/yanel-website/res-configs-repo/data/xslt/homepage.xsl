<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:yh="http://yanel.wyona.org/homepage/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
>

<xsl:output method="xhtml" encoding="UTF-8" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

<xsl:param name="os" select="'OS_NULL'"/>
<xsl:param name="client" select="'CLIENT_NULL'"/>
<xsl:param name="language" select="'LANGUAGE_NULL'"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
  
  <xsl:choose>
  <xsl:when test="$language = 'es'">
    <title>Bienvenido a Yanel</title>  
  </xsl:when>
  <xsl:otherwise>
    <title>Welcome to Yanel</title>  
  </xsl:otherwise>
  </xsl:choose>
    <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-homepage.xml"/>
  
  <link rel="shortcut icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
  <link rel="stylesheet" href="css/global.css" type="text/css"/>
  <base target="_top"/>
  <style>
#body {
  text-align:left;
}
#download-download {
  color: #000;
  font-weight: bold; 
  font-size: 14px;
}
#download-yanel-version {
  color: #fff;
  font-weight: bold; 
  font-size: 14px;
}
#download-for-os {
  color: #000;
  font-weight: bold; 
  font-size: 12px;
}
#downloadlink{
  text-decoration:none;
}
  </style>
</head>
<body>
<!--
<p>
DEBUG:<br/>
Operating System: <xsl:value-of select="$os"/><br/>
Client: <xsl:value-of select="$client"/><br/>
Language: <xsl:value-of select="$language"/>
</p>
-->
<div id="body">
<xsl:copy-of select="/yh:homepage/yh:header/xhtml:body/*"/>

<p>
<!--
<table background="img/download-star.gif" width="189px" height="187px">
-->
<table background="img/download-flower.gif" width="185px" height="185px" style="margin-left:100px">
<tr>
<td>
<center>
<a id="downloadlink">
<xsl:attribute name="href">
<xsl:choose>
<xsl:when test="$os = 'OS_NULL'">
en/download/index.html
</xsl:when>
<xsl:otherwise>
en/download/<xsl:value-of select="$os"/>.html
</xsl:otherwise>
</xsl:choose>
</xsl:attribute>
<span id="download-download">NOW AVAILABLE</span>
<br/>
<span id="download-yanel-version">Yanel<br/>
<xsl:choose>
<xsl:when test="$os = 'windows'">
<xsl:value-of select="/yh:homepage/yh:download/yh:version-windows"/>
</xsl:when>
<!--
<xsl:when test="$os = 'unix'">
-->
<xsl:otherwise>
<xsl:value-of select="/yh:homepage/yh:download/yh:version-unix"/>
</xsl:otherwise>
</xsl:choose>
</span>
<br/>
<xsl:choose>
<xsl:when test="$os = 'unix'">
<span id="download-for-os">for "UNIX"</span><br/>
</xsl:when>
<xsl:when test="$os = 'windows'">
<span id="download-for-os">for "Windows"</span><br/>
</xsl:when>
<xsl:otherwise>
<!--
<span id="download-for-os">for <xsl:value-of select="$os"/></span><br/>
-->
</xsl:otherwise>
</xsl:choose>
<!--
English (5.0 MB)
-->
</a>
</center>
</td>
</tr>
</table>
</p>

<xsl:copy-of select="/yh:homepage/yh:footer/xhtml:body/node()"/>

</div>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
