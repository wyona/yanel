<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
>

<!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
<xsl:output method="xhtml" encoding="UTF-8"/>
<!--
<xsl:output method="html"/>
-->

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>

<xsl:param name="yanel.meta.language" select="'en'"/>
<xsl:param name="yanel.request.query-string" select="'No query-string specified'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<xsl:comment>
WARNING: This content has been generated dynamically. All changes will be lost.
</xsl:comment>

<head>
  <title>BasicXMLResource XSLT parameters</title>
</head>

<body>
<ul>
  <li>yanel.meta.language: <xsl:value-of select="$yanel.meta.language"/></li>
  <li>yanel.request.query-string: <xsl:value-of select="$yanel.request.query-string"/></li>
</ul>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
