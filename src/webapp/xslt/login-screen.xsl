<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<!--
  <xsl:output method="xhtml" encoding="UTF-8"/>
-->

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Yanel - Login</title>
        <link rel="stylesheet" href="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-css/global.css" type="text/css"/>
      </head>
      <body>
        <table cellspacing="0" cellpadding="0" id="bodytable">
          <tr>
            <td id="title">
              Login to realm "<xsl:value-of select="/yanel:yanel-auth-screen/yanel:realm/@yanel:name"/>"
            </td>
            <td id="logo">  
               <img src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-img/yanel_header.png"/>
            </td>
          </tr>
          <tr>
            <td width="100%" valign="top"  colspan="2">
              <div id="content">

        <p>Authorization was denied. Please, enter your username and password.</p>
        <xsl:apply-templates select="/yanel:yanel-auth-screen/yanel:message"/>
          <form method="POST">
            Username:<input type="text" name="yanel.login.username"/>
            Password:<input type="password" name="yanel.login.password"/>
            <input type="submit" value="Login"/>
          </form>
        <xsl:apply-templates select="/yanel:yanel-auth-screen/yanel:ssl"/>
        <hr/>
        <p>
        <xsl:choose>
          <xsl:when test="/yanel:yanel-auth-screen/yanel:request/@yanel:qs">
            <a href="?{/yanel:yanel-auth-screen/yanel:request/@yanel:qs}&amp;yanel.format=xml">Show XML source</a>
          </xsl:when>
          <xsl:otherwise>
            <a href="?&amp;yanel.format=xml">Show XML source</a>
          </xsl:otherwise>
        </xsl:choose>
        </p>
        <xsl:apply-templates select="/yanel:yanel-auth-screen/yanel:request"/>
        <xsl:apply-templates select="/yanel:yanel-auth-screen/yanel:realm"/>
        </div>
        </td>
</tr>
</table>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="yanel:message">
      <div id="message"><xsl:value-of select="."/></div>
  </xsl:template>

  <xsl:template match="yanel:request">
    Request: <xsl:value-of select="@yanel:urlqs"/>
  </xsl:template>

  <xsl:template match="yanel:ssl">
      <xsl:choose>
        <xsl:when test="contains(@yanel:status, 'ON')">
          <p><img src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-img/ssl_on.png" alt="ssl on"/> SSL support is ON</p>
        </xsl:when>
        <xsl:otherwise>
          <h4><img src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-img/ssl_off.png" alt="ssl off"/> Warning: SSL support is OFF. Your username and password will be sent as plain text.</h4>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="yanel:realm">
    <p>
    Realm info:
       <ul>
        <li>Name: <xsl:value-of select="@yanel:name"/></li>
        <li>Mount Point: <xsl:value-of select="@yanel:mount-point"/></li>
       </ul>
    </p>
  </xsl:template>

</xsl:stylesheet>
