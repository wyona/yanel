<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:param name="realmid" select="''"/>
  <xsl:param name="realmname" select="''"/>
  <xsl:param name="url" select="''"/>
  <xsl:param name="crawldepth" select="''"/>
  <xsl:param name="maxpages" select="''"/>
  <xsl:param name="message" select="''"/>
  <xsl:param name="error" select="''"/>
  
  <xsl:param name="sent" select="'false'"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><i18n:message key="importsite"/></title>
      </head>
      
      <body>
        <h3><i18n:message key="importsite"/></h3>
        
        <div id="contentBody">
        
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
    <form name="importsite-form" method="post" action="#">
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
          <td align="right" valign="top" class="contentfield"><i18n:message key="realmid"/>&#0160;</td>
          <td>
            <input type="text" name="realmid" class="box" size="40" value="{$realmid}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="realmname"/>:&#0160;</td>
          <td>
            <input type="text" name="realmname" class="box" size="40" value="{$realmname}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="url"/>:&#0160;</td>
          <td>
            <input type="text" name="url" class="box" size="40" value="{$url}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="crawldepth"/>:&#0160;</td>
          <td>
            <input type="text" name="crawldepth" class="box" size="40" value="{$crawldepth}"/>
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top" class="contentfield"><i18n:message key="maxpages"/>:&#0160;</td>
          <td>
            <input type="text" name="maxpages" class="box" size="40" value="{$maxpages}"/>
          </td>
          <td></td>
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
        <td align="right" valign="top" class="contentfield"><i18n:message key="realmid"/>:&#0160;</td>
        <td><xsl:value-of select="$realmid"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="realmname"/>:&#0160;</td>
        <td><xsl:value-of select="$realmname"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="url"/>:&#0160;</td>
        <td><xsl:value-of select="$url"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="crawldepth"/>:&#0160;</td>
        <td><xsl:value-of select="$crawldepth"/></td>
        <td></td>
      </tr>
      <tr>
        <td align="right" valign="top" class="contentfield"><i18n:message key="maxpages"/>:&#0160;</td>
        <td><xsl:value-of select="$maxpages"/></td>
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
