<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:yanel="http://www.wyona.org/yanel/1.0">

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" href="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-css/global.css" type="text/css"/>
        <xsl:apply-templates select="/xhtml:html/xhtml:head/*"/>
      </head>
      <body>
        <table cellspacing="0" cellpadding="0" id="bodytable">
          <tr>
            <td id="title"></td>
            <td id="logo">
              <img src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-img/yanel_header.png"/>
            </td>
          </tr>
          <tr>
            <td width="100%" valign="top" colspan="2">
              <div id="content">
                <xsl:apply-templates select="/xhtml:html/xhtml:body/*"/>
              </div>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
