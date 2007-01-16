<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="company" select="''"/>
  <xsl:param name="firstName" select="''"/>
  <xsl:param name="lastName" select="''"/>
  <xsl:param name="email" select="''"/>
  <xsl:param name="address" select="''"/>
  <xsl:param name="zipCity" select="''"/>
  <xsl:param name="message" select="''"/>
  <xsl:param name="error" select="''"/>
  
  <xsl:param name="sent" select="'false'"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><i18n:message key="contact"/></title>
      </head>
      
      <body>
        <h3><i18n:message key="contact"/></h3>
<!--
        <p><i18n:message key="generalInquiriesPleaseContact"/><a href="mailto:contact@wyona.com">contact@wyona.com</a>.</p>
-->
        <p><i18n:message key="mailingAddressCanBeFound"/><a href="http://www.wyona.com/contact.html"><i18n:message key="corporateContactPage"/></a>.</p>
        <div id="contenBody">
        
          <xsl:choose>
            <xsl:when test="$error != ''">
              <xsl:apply-templates select="form" mode="error"/>
            </xsl:when>
            <xsl:when test="$sent = 'true'">
              <xsl:apply-templates select="form" mode="thankYou"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:apply-templates select="form" mode="init"/>
            </xsl:otherwise>
          </xsl:choose>
        
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="form" mode="init">
    <form name="contact-form" method="post" action="#">
      <table cellpadding="0" cellspacing="0" border="0">
        <xsl:for-each select="inputfields/input">
          <xsl:variable name="inputName"><xsl:value-of select="."/></xsl:variable>
          <tr>
            <td align="right" valign="top" class="contentfield"><i18n:message><xsl:attribute name="key"><xsl:value-of select="$inputName"/></xsl:attribute></i18n:message>:&#0160;</td>
            <td>
              <input type="text" name="{$inputName}" class="box" size="40"/>
            </td>
            <td><xsl:if test="@required = 'true'">*</xsl:if></td>
          </tr>
        </xsl:for-each>
        
        <xsl:for-each select="textAreas">
          <xsl:variable name="inputName"><xsl:value-of select="textArea"/></xsl:variable>
          <tr>
            <td align="right" valign="top" class="contentfield"><i18n:message><xsl:attribute name="key"><xsl:value-of select="$inputName"/></xsl:attribute></i18n:message>:&#0160;</td>
            <td>
              <textarea rows="8" name="{$inputName}" cols="30" class="box"></textarea>
            </td>
            <td><xsl:if test="@required = 'true'">*</xsl:if></td>
          </tr>
        </xsl:for-each>
        
        <tr>
          <td colspan="2">&#160;
          </td>
          <td>
            <input type="submit" name="submit" value="i18n:attr key=send"/>
          </td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield" colspan="3"><br/><i18n:message key="requiredFields"/></td>
        </tr>
      </table>
    </form>
  </xsl:template>

  <xsl:template match="form" mode="error">
    <form name="contact-form" method="post" action="#">
      <table cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td colspan="3" align="left" valign="top" class="contentfield"><font color="red"><i18n:message key="{$error}"/></font></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="company"/>&#0160;</td>
          <td>
            <input type="text" name="company" class="box" size="40" value="{$company}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="firstName"/>:&#0160;</td>
          <td>
            <input type="text" name="firstName" class="box" size="40" value="{$firstName}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="lastName"/>:&#0160;</td>
          <td>
            <input type="text" name="lastName" class="box" size="40" value="{$lastName}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="address"/>:&#0160;</td>
          <td>
            <input type="text" name="address" class="box" size="40" value="{$address}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="zipCity"/>:&#0160;</td>
          <td>
            <input type="text" name="zipCity" class="box" size="40" value="{$zipCity}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield">
            <xsl:if test="starts-with($error, 'email')">
              <font color="#FF0000"><i18n:message key="email"/>:&#0160;</font>
            </xsl:if>
            <xsl:if test="not(starts-with($error, 'email'))">
              <i18n:message key="email"/>:&#0160;
            </xsl:if>
          </td>
          <td>
            <input type="text" name="email" class="box" size="40" value="{$email}"/>
          </td>
          <td>*</td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="message"/>:&#0160;</td>
          <td>
            <textarea rows="8" name="message" cols="30" class="box"><xsl:value-of select="$message"/></textarea>
          </td>
          <td></td>
        </tr>
        <tr>
          <td colspan="2">&#160;
          </td>
          <td>
            <input type="submit" name="submit" value="i18n:attr key=send"/>
          </td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield" colspan="3"><br/><i18n:message key="requiredFields"/></td>
        </tr>
      </table>
    </form>
  </xsl:template>
  
  <xsl:template match="form" mode="thankYou">
    <table>
      <tr>
        <td colspan="3" align="right" valign="top" class="contentfield"><i18n:message key="thankYou"/></td>
      </tr>
      <tr>
        <td colspan="3" align="right" valign="top" class="contentfield"><i18n:message key="inquiryContent"/></td>
      </tr>
      
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="company"/>:&#0160;</td>
        <td><xsl:value-of select="$company"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="firstName"/>:&#0160;</td>
        <td><xsl:value-of select="$firstName"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="lastName"/>:&#0160;</td>
        <td><xsl:value-of select="$lastName"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="address"/>:&#0160;</td>
        <td><xsl:value-of select="$address"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="zipCity"/>:&#0160;</td>
        <td><xsl:value-of select="$zipCity"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="email"/>:&#0160;</td>
        <td><xsl:value-of select="$email"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="message"/>:&#0160;</td>
        <td><xsl:value-of select="$message"/></td>
        <td></td>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>
