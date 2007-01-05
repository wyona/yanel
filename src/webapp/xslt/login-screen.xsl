<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>
          Yanel <i18n:text>- Login</i18n:text>
        </title>
      </head>
      <body>
        <h1>Login to realm <xsl:value-of select="child::node()/realm/@name"/></h1>
        <h4>Authorization was denied. Please, enter your username and password.</h4>
        <table>
          <tr><td><p><ul><xsl:apply-templates/></ul></p></td></tr>
          <tr><td>
            <form method="POST">
              <p>
                <table>
                  <tr>
                    <td>Username:</td>
                    <td>&#160;</td>
                    <td><input type="text" name="yanel.login.username"/></td>
                  </tr>
                  <tr>
                    <td>Password:</td>
                    <td>&#160;</td>
                    <td><input type="password" name="yanel.login.password"/></td>
                  </tr>
                  <tr>
                    <td colspan="2">&#160;</td>
                    <td align="right"><input type="submit" value="Login"/></td>
                  </tr>
                </table>
              </p>
            </form>
          </td></tr>
        </table>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="message">
    <li> Note:
      <ul><li> <xsl:value-of select="."/></li></ul>
    </li>
  </xsl:template>

  <xsl:template match="request">
    <li>Request: 
      <ul><li><xsl:value-of select="@urlqs"/></li></ul>
    </li>
  </xsl:template>

  <xsl:template match="realm">
    <li> Realm info:
      <ul> 
        <li>Name: <xsl:value-of select="@name"/></li>
        <li>Mount Point: <xsl:value-of select="@mount-point"/></li>
      </ul>
    </li>
  </xsl:template>

</xsl:stylesheet>
