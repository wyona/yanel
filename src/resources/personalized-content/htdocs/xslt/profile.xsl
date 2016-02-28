<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns:boost="http://www.wyona.org/yanel/boost/1.0"
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
Your <a href="http://donottrack.us/" target="_blank">DNT header parameter</a> is currently set to '<code><xsl:value-of select="$do.not.track"/></code>', which means that <xsl:choose><xsl:when test="$do.not.track = 'true'">you are currently not being tracked.</xsl:when><xsl:otherwise>you are currently being tracked!</xsl:otherwise></xsl:choose>
</p>

<p>Depending on your browser you can disable or enable the DNT header parameter. For more details see <a href="http://donottrack.us/" target="_blank">http://donottrack.us/</a>.</p>

<xsl:apply-templates select="/boost:personalized-content/boost:exception"/>

<xsl:apply-templates select="/boost:personalized-content/boost:interests"/>
<xsl:apply-templates select="/boost:personalized-content/boost:clickstream"/>

          </body>
        </html>
  </xsl:template>

  <xsl:template match="boost:exception">
    <p><span style="color: red;">Exception:</span>&#160;<xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="boost:interests">
              <h2>Interests</h2>
<xsl:choose>
  <xsl:when test="boost:interest">
<p>Based on your clickstream, which we have tracked so far, we have detected the following interests:</p>

<ul>
<xsl:for-each select="boost:interest">
  <li><xsl:value-of select="."/> (Occurrence during same session: <xsl:value-of select="@weight"/>)</li>
</xsl:for-each>
</ul>
  </xsl:when>
  <xsl:otherwise>
    <p>No interests detected yet.</p>
  </xsl:otherwise>
</xsl:choose>
  </xsl:template>

  <xsl:template match="boost:clickstream">
              <h2>Clickstream</h2>
<xsl:choose>
  <xsl:when test="boost:url">
<p>Your clickstream contains the following <xsl:value-of select="@limit"/> most recent URLs:</p>

<table>
<xsl:for-each select="boost:url">
  <xsl:sort select="position()" data-type="number" order="descending"/>
  <tr><td><xsl:value-of select="@date"/></td><td><a href="{.}"><xsl:value-of select="."/></a></td></tr>
</xsl:for-each>
</table>
  </xsl:when>
  <xsl:otherwise>
    <p>No clickstream detected yet.</p>
  </xsl:otherwise>
</xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
