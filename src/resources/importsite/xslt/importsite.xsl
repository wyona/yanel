<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="realmid" select="''" />
  <xsl:param name="realmname" select="''" />
  <xsl:param name="url" select="''" />
  <xsl:param name="fs-location" select="''" />
  <xsl:param name="crawldepth" select="''" />
  <xsl:param name="maxpages" select="''" />
  
  <xsl:param name="error-realmid" select="''" />
  <xsl:param name="error-realmname" select="''" />
  <xsl:param name="error-url" select="''" />
  <xsl:param name="error-fs-location" select="''" />
  <xsl:param name="error-crawldepth" select="''" />
  <xsl:param name="error-maxpages" select="''" />

  <xsl:param name="submitted" select="'false'" />

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
          <xsl:choose>
            <xsl:when test="$submitted != 'false'">
              <p>[X] pages have been imported.</p>
              <p>[X]% complete.</p>
            </xsl:when>
            <xsl:otherwise>
              
              <form method="post">
                  <table cellpadding="0" cellspacing="0" border="0">
                    <xsl:for-each select="form/inputfields/input">
                      <tr>
                        <td align="right" valign="top" class="contentfield" width="150">
                          <i18n:message key="{.}"/>:&#0160;
                        </td>
                        <td>
                          <xsl:choose>
                            <xsl:when test="$error-realmid != '' or $error-realmname != '' 
                                            or $error-url != '' or $error-fs-location != ''
                                            or $error-crawldepth != '' or $error-maxpages != ''">
                              <xsl:choose>
                                <xsl:when test="position()='1'">
                                  <input type="text" name="{.}" class="box" size="30" value="{$realmid}" />
                                </xsl:when>
                                <xsl:when test="position()='2'">
                                  <input type="text" name="{.}" class="box" size="30" value="{$realmname}" />
                                </xsl:when>
                                <xsl:when test="position()='3'">
                                  <input type="text" name="{.}" class="box" size="30" value="{$url}" />
                                </xsl:when>
                                <xsl:when test="position()='4'">
                                  <input type="text" name="{.}" class="box" size="30" value="{$fs-location}" />
                                </xsl:when>
                                <xsl:when test="position()='5'">
                                  <input type="text" name="{.}" class="box" size="30" value="{$crawldepth}" />
                                </xsl:when>
                                <xsl:when test="position()='6'">
                                  <input type="text" name="{.}" class="box" size="30" value="{$maxpages}" />
                                </xsl:when>
                                <xsl:otherwise/>
                              </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                              <input type="text" name="{.}" class="box" size="30" />
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                        <td><xsl:if test="@required = 'true'">*</xsl:if></td>
                        <xsl:choose>
                          <xsl:when test="position()='1' and $error-realmid != ''">
                            <td>
                              &#160;<font color="red"><i18n:message key="{$error-realmid}"/></font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='2' and $error-realmname != ''">
                            <td>
                              &#160;<font color="red"><i18n:message key="{$error-realmname}"/></font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='3' and $error-url != ''">
                            <td>
                              &#160;<font color="red"><i18n:message key="{$error-url}"/></font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='4' and $error-fs-location != ''">
                            <td>
                              &#160;<font color="red"><i18n:message key="{$error-fs-location}"/></font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='5' and $error-crawldepth != ''">
                            <td>
                              &#160;<font color="red"><i18n:message key="{$error-crawldepth}"/></font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='6' and $error-maxpages != ''">
                            <td>
                              &#160;<font color="red"><i18n:message key="{$error-maxpages}"/></font>
                            </td>
                          </xsl:when>
                          <xsl:otherwise>
                            <td>
                              &#160;
                            </td>
                          </xsl:otherwise>
                        </xsl:choose>
                        <!--<xsl:if test="$error != ''">
                          <td>
                            &#160;<font color="red"><i18n:message key="{$error}"/></font>
                          </td>
                        </xsl:if>-->
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
        
            </xsl:otherwise>
          </xsl:choose>
        </div>
      </body>
    </html>
  </xsl:template>
    
</xsl:stylesheet>
