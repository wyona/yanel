<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
    xmlns="http://www.w3.org/1999/xhtml">

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.meta.language" select="'en'"/>

  <xsl:template match="xhtml:p">
    <p>This line has been inserted by a second XSLT</p>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
