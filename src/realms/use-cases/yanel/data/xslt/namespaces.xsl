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
    <yanel:root xmlns:yanel="http://www.wyona.org/yanel/1.0">
      <xsl:copy-of select="*"/>
      <yanel:hello>world</yanel:hello>
    </yanel:root>
  </xsl:template>
  
</xsl:stylesheet>
