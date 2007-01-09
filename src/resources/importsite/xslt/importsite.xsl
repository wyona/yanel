<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="error" select="''"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><i18n:message key="importsite"/></title>
        <style>
            .samplevalue {
              padding-bottom:10px;
            }
        </style>
      </head>
      
      <body>
        <h3><i18n:message key="importsite"/></h3>
        
        <div id="contentBody">
        
          <form method="post">
              <table cellpadding="0" cellspacing="0" border="0">
                <xsl:for-each select="form/inputfields/input">
                  <tr>
                    <td align="right" valign="top" class="contentfield" width="150">
                      <xsl:choose>
                        <xsl:when test="$error != ''"><font color="red"><i18n:message key="{$error}"/></font></xsl:when>
                        <xsl:otherwise><i18n:message key="{.}"/>:&#0160;</xsl:otherwise>
                      </xsl:choose>
                    </td>
                    <td>
                      <input type="text" name="{.}" class="box" size="30"/>
                    </td>
                    <td><xsl:if test="@required = 'true'">*</xsl:if></td>
                  </tr>
                  <tr>
                    <td>&#160;</td>
                    <td class="samplevalue">
                      (i.e. <xsl:value-of select="@samplevalue"/>)
                    </td>
                    <td>&#160;</td>
                  </tr>
                </xsl:for-each>
                
                <tr>
                  <td>&#160;
                  </td>
                  <td align="right">
                    <input type="submit" name="submit" value="i18n:attr key=importsite"/>
                  </td>
                </tr>
        
                <tr>
                  <td align="right" valign="top" class="contentfield" colspan="2"><br/><i18n:message key="requiredFields"/></td>
                </tr>
              </table>
          </form>
        
        </div>
      </body>
    </html>
  </xsl:template>
    
</xsl:stylesheet>
