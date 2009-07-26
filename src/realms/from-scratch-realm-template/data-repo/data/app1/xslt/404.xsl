<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
>

  <!-- TODO: Isn't there a more standardized i18n namespace? -->

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>
  <xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
  
  <xsl:template match="/">
        <html>
          <head>
            <title>
<xsl:choose>
<xsl:when test="$content-language = 'fr'">
La page que vous venez d'appeler n'existe pas (ou plus)
</xsl:when>
<xsl:when test="$content-language = 'en'">
Page not found
</xsl:when>
<xsl:otherwise>
Diese Seite existiert nicht (mehr)
</xsl:otherwise>
</xsl:choose>
            </title>
          </head>
          <body>
<xsl:choose>
<xsl:when test="$content-language = 'fr'">
<h2>La page que vous venez d'appeler n'existe pas (ou plus)</h2>
<p>
Veuillez utiliser la navigation pour atteindre la domaine souhaitée.
</p>
</xsl:when>
<xsl:when test="$content-language = 'en'">
<h2>Page not found</h2>
<p>
We're sorry, the page you've requested does not exist at this address.
</p>
<p>
Please use the navigation in order to find the appropriate content.
</p>
</xsl:when>
<xsl:otherwise>
<h1>Diese Seite existiert nicht (mehr)</h1>
<p>
<xsl:value-of select="/yanel:yanel/yanel:exception"/>
</p>
<p>
Bitte benutzen Sie die Navigation, um in den gewünschten Bereich zu gelangen.
</p>
</xsl:otherwise>
</xsl:choose>
          </body>
        </html>
  </xsl:template>
  
</xsl:stylesheet>
