<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0">

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>Internal Server Error</title>
        <link rel="stylesheet" href="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-css/global.css" type="text/css"/>
      </head>
      <body>
        <table id="bodytable" cellpadding="0" cellspacing="0">
          <tr>
            <td id="title">Yanel - Internal Server Error</td>
            <td id="logo"><img src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-img/yanel_header.png"/></td> 
          </tr> 
          <tr>
            <td colspan="2" valign="top" width="100%">
              <div id="content">
                <p>
                  <a href="?yanel.format=xml">Show XML source</a>
                </p> 
                <p>
                  The server is currently unable to handle your request. Please
                  try again later. If the error persists, consider contacting the 
                  website operators.
                </p> 
                <ul>
                  <li>Servlet path: <xsl:value-of select="/yanel:yanel/yanel:request/@servlet-path"/></li>
                  <li>Request path: <xsl:value-of select="/yanel:yanel/yanel:request/@uri"/></li>
                  <li>Message: <xsl:value-of select="/yanel:yanel/yanel:exception/@message"/></li>
                </ul>
                <h4>Stacktrace</h4>
                <xsl:value-of select="/yanel:yanel/yanel:exception"/>
              </div> 
            </td> 
          </tr> 
        </table> 
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
