<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
>

<!-- NOTE: This XSLT is copied during the build process into the jar file! -->

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>
  <xsl:param name="yanel.toolbar-status" select="'TOOLBAR-STATUS_IS_NULL'"/>
  <xsl:param name="do.not.track" select="'DO_NOT_TRACK_IS_NULL'"/>
  
  <xsl:template match="/">
        <html>
          <head>
            <title>My Profile</title>
            <link rel="stylesheet" href="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-css/global.css" type="text/css"/>
          </head>
          <body>

<xsl:comment>
Back 2 Realm: <xsl:value-of select="$yanel.back2realm"/>,
Yanel reserved prefix: <xsl:value-of select="$yanel.reservedPrefix"/>
</xsl:comment>

              <h1>My Profile</h1>

              <p><a href="?yanel.resource.viewid=xml">Show XML source</a> </p>

<p>
The DNT header parameter (see <a href="http://donottrack.us/" target="_blank">http://donottrack.us/</a>) is currently set to: <code><xsl:value-of select="$do.not.track"/></code>
</p>

          </body>
        </html>
  </xsl:template>
  
</xsl:stylesheet>
