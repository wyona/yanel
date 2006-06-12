<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:output method="html"/>

<xsl:variable name="prefix" select="/xhtml:html/xhtml:head/xhtml:path/@prefix"/>
<xsl:variable name="language" select="/xhtml:html/xhtml:head/xhtml:languages/@this"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<xsl:comment>
WARNING: This file has been generated automatically. All changes will be lost.
         Please edit only *.xhtml" files and re-generate the "*.html" pages by
         using page2xhtml.xsl
</xsl:comment>

<head>
  <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/> - WYONA PICTURES</title>
  <link href="{$prefix}css/general.css" type="text/css" rel="stylesheet"/>
  <xsl:choose>
    <xsl:when test="/xhtml:html/xhtml:body/xhtml:div[@id='rightcolumn']">
      <link href="{$prefix}css/positioning-with-rc.css" type="text/css" rel="stylesheet"/>
    </xsl:when>
    <xsl:otherwise>
      <link href="{$prefix}css/positioning-without-rc.css" type="text/css" rel="stylesheet"/>
    </xsl:otherwise>
  </xsl:choose>
</head>

<body>
  <div id="head">
    <div id="logo">
      <a href="{$prefix}index.html"><img src="{$prefix}images/wyona-pictures-logo.png" border="0"/></a>
    </div>
    <div id="navigator">
      <a href="{$prefix}{$language}/sitemap.html">Sitemap</a> | <xsl:choose><xsl:when test="$language='de'"><a href="{$prefix}de/suche.html">Suche</a></xsl:when><xsl:when test="$language='fr'"><a href="{$prefix}fr/recherche.html">Recherche</a></xsl:when><xsl:when test="$language='en'"><a href="{$prefix}en/search.html">Search</a></xsl:when><xsl:otherwise>The language <xsl:value-of select="$language"/> has not been implemented yet!</xsl:otherwise></xsl:choose> | <a href="{$prefix}{$language}/topicmap.html">Topic Map</a>
    </div>
    <div id="languages">
      <xsl:choose><xsl:when test="$language='en'"><span id="selected-lang">EN</span></xsl:when><xsl:otherwise><a href="{/xhtml:html/xhtml:head/xhtml:languages/xhtml:en/@path}">EN</a></xsl:otherwise></xsl:choose> | <xsl:choose><xsl:when test="$language='de'"><span id="selected-lang">DE</span></xsl:when><xsl:otherwise><a href="{/xhtml:html/xhtml:head/xhtml:languages/xhtml:de/@path}">DE</a></xsl:otherwise></xsl:choose> | <xsl:choose><xsl:when test="$language='fr'"><span id="selected-lang">FR</span></xsl:when><xsl:otherwise><a href="{/xhtml:html/xhtml:head/xhtml:languages/xhtml:fr/@path}">FR</a></xsl:otherwise></xsl:choose>
    </div>
  </div>

  <div id="separator">
    &#160;
  </div>

  <div id="content">
    <div id="pagetitle"><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/></div>

    <xsl:copy-of select="/xhtml:html/xhtml:body/xhtml:div[@id='content']/*"/>
  </div>

  <xsl:if test="/xhtml:html/xhtml:body/xhtml:div[@id='rightcolumn']">
  <div id="rightcolumn">
    <xsl:copy-of select="/xhtml:html/xhtml:body/xhtml:div[@id='rightcolumn']/*"/>
  </div>
  </xsl:if>

  <div id="footer">
    Copyright &#169; 2006 Wyona Pictures - <a href="{$prefix}en/contact.html">Contact</a>
  </div>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
