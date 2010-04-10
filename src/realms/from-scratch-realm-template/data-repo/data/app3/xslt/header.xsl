<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="xhtml dc">

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.toolbar-status" select="'TOOLBAR-STATUS_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVED-PREFIX_IS_NULL'"/>
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  <xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
  <xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
  <xsl:param name="languages" select="'LANGUAGES_IS_NULL'"/>

<xsl:template name="header">
<div id="header">
  <div class="add-nav-box">
    <form action="http://www.google.com/search" class="header-forms">
      <fieldset>
        <div class="text">
          <input type="text" value="" name="q"/>
        </div>
        <input type="hidden" name="domains" value="wyona.com"/>
        <input type="hidden" name="sitesearch" value="wyona.com"/>
        <input type="submit" value="Search" class="submit" />
      </fieldset>
    </form>

<ul class="add-nav">
  <li><a href="#">Contact</a></li>
  <li><a href="#">Register</a></li>
  <li><a href="#">Extranet</a></li>
  <li><a href="#">RSS</a></li>
  <li><a href="#">Follow us</a></li>

<!-- TODO: Also see /data-repo/data/app1/xslt/translations.xsl -->
<xsl:choose>
  <xsl:when test="$content-language = 'en'">
  <li><a href="{$yarep.back2realm}de/index.html">DE</a></li>
  </xsl:when>
  <xsl:otherwise>
  <li><a href="{$yarep.back2realm}en/index.html">EN</a></li>
  </xsl:otherwise>
</xsl:choose>
</ul>
</div>

<div class="header-box">
<strong class="logo"><a href="{$yarep.back2realm}">wyona</a></strong>

<ul id="nav">
<!-- TODO: Use i18n (What about the links?) -->
<xsl:choose>
  <xsl:when test="$content-language = 'de'">
    <li><a href="{$yarep.back2realm}de/produkte.html">Produkte</a></li>
    <li><a href="{$yarep.back2realm}de/dienstleistungen.html">Dienstleistungen</a></li>
    <li><a href="{$yarep.back2realm}de/loesungen.html">Lösungen</a></li>
    <li><a href="{$yarep.back2realm}de/resultate.html">Resultate</a></li>
    <li><a href="{$yarep.back2realm}de/blog_und_news.html">Blog/News</a></li>
    <li><a href="{$yarep.back2realm}de/unterlagen.html">Unterlagen</a></li>
    <li><a href="{$yarep.back2realm}de/ueber.html">&#220;ber uns</a></li>
<!--
    <li><a href="{$yarep.back2realm}de/ueber_uns.html">&#220;ber uns</a></li>
-->
  </xsl:when>
  <xsl:otherwise>
    <li><a href="{$yarep.back2realm}en/products.html">Products</a></li>
    <li><a href="{$yarep.back2realm}en/services.html">Services</a></li>
    <li><a href="{$yarep.back2realm}en/solutions.html">Solutions</a></li>
    <li><a href="{$yarep.back2realm}en/impact.html">Impact</a></li>
    <li><a href="{$yarep.back2realm}en/blog_news.html">Blog/News</a></li>
    <li><a href="{$yarep.back2realm}en/resources.html">Resources</a></li>
    <li><a href="{$yarep.back2realm}en/about.html">About us</a></li>
<!--
    <li><a href="{$yarep.back2realm}en/about_us.html">About us</a></li>
-->
  </xsl:otherwise>
</xsl:choose>
</ul>
</div>
</div>
</xsl:template>

</xsl:stylesheet>
