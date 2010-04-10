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

<xsl:template name="footer">
<div id="footer">
  <ul class="footer-nav">
<xsl:choose>
  <xsl:when test="$content-language = 'de'">
    <li><a href="#">Site Map</a></li>
    <li><a href="#">Impressum</a></li>
    <li><a href="#">Datenschutz</a></li>
    <li class="contact"><a href="">Kontakt: <address>Wyona AG, Hardstrasse 219, CH-8005 Zurich, +41 44 272 91 61</address></a></li>
    <li class="copyright">Copyright &#169; 2010 Wyona</li>
  </xsl:when>
  <xsl:otherwise>
    <li><a href="#">Site Map</a></li>
    <li><a href="#">Impressum/Legal</a></li>
    <li><a href="#">Privacy Policy</a></li>
    <li class="contact"><a href="">Contact: <address>Wyona AG, Hardstrasse 219, CH-8005 Zurich, +41 44 272 91 61</address></a></li>
    <li class="copyright">Copyright &#169; 2010 Wyona</li>
  </xsl:otherwise>
</xsl:choose>
  </ul>
</div>
</xsl:template>

</xsl:stylesheet>
