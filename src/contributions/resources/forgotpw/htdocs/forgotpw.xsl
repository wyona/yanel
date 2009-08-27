<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:yanel="http://www.wyona.org/yanel/1.0"
    xmlns:xalan="http://xml.apache.org/xalan">

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVEDPREFIX_IS_NULL'"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
          <head>
            <title>I forgot my password!</title>
          </head>
          <body>
            <h1>I forgot my password!</h1>

            <xsl:apply-templates select="/yanel:yanel-forgotpw/yanel:exception"/>
            <xsl:apply-templates select="/yanel:yanel-forgotpw/yanel:requestemail"/>
            <xsl:apply-templates select="/yanel:yanel-forgotpw/yanel:requestnewpw"/>
            <xsl:apply-templates select="/yanel:yanel-forgotpw/yanel:show-message"/>

          </body>
    </html>
  </xsl:template>

  <xsl:template match="yanel:exception" >
    <h2><font color="red">Exception</font></h2>
    <p><xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="yanel:requestemail" >
    <h2>Get a new password by E-Mail</h2>
    <form name="forgotpw-form" method="post" action="#">
      <p>
        <table cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td>Your E-Mail address:</td>
            <td>
              <input type="text" name="email"/>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="submit" name="submitForgotPW" value="Send E-Mail"/>
            </td>
          </tr>
        </table>
      </p>
    </form>
  </xsl:template>

<xsl:template match="yanel:requestnewpw" >
    <h2>Change Password</h2>
    <form name="forgotpw-form" method="post" action="#">
      <p>
        <table cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td>Enter new password:</td>
            <td>
              <input type="password" name="newPassword"/>
            </td>
          </tr>
          <tr>
            <td>Confirm new password:</td>
            <td>
              <input type="password" name="newPasswordConfirmation"/>
            </td>
          </tr>
          <tr>
            <td>
              <input name="guid" type="hidden"> <xsl:attribute name="value">
                <xsl:value-of select="yanel:guid" /></xsl:attribute>
              </input>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="submit" name="submitNewPW" value="Change Password"/>
            </td>
          </tr>
        </table>
      </p>
    </form>
  </xsl:template>

  <xsl:template match="yanel:show-message">
        <h4>
          <xsl:value-of select="." />
        </h4>
  </xsl:template>


</xsl:stylesheet>
