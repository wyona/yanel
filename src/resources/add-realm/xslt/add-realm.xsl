<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="realmid" select="''" />
  <xsl:param name="realmname" select="''" />
  <xsl:param name="url" select="''" />
  <xsl:param name="fs-location" select="''" />
  <xsl:param name="crawldepth" select="''" />
  <xsl:param name="crawlmaxpages" select="''" />

  <xsl:param name="submitted" select="'false'" />

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><i18n:message key="add-realm"/></title>
        <style>
            .samplevalue {
              padding-bottom:10px;
            }
        </style>
      </head>
      
      <body>
        <h3><i18n:message key="add-realm"/></h3>
        
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
                            <xsl:when test="contains($realmid, 'ERROR:') or contains($realmname, 'ERROR:') 
                                            or contains($url, 'ERROR:') or contains($fs-location, 'ERROR:')
                                            or contains($crawldepth, 'ERROR:') or contains($crawlmaxpages, 'ERROR:')">
                              <xsl:choose>
                                <xsl:when test="position()='1' and not(contains($realmid, 'ERROR:'))">
                                  <input type="text" name="{.}" class="box" size="30" value="{$realmid}" />
                                </xsl:when>
                                <xsl:when test="position()='2' and not(contains($realmname, 'ERROR:'))">
                                  <input type="text" name="{.}" class="box" size="30" value="{$realmname}" />
                                </xsl:when>
                                <xsl:when test="position()='3' and not(contains($url, 'ERROR:'))">
                                  <input type="text" name="{.}" class="box" size="30" value="{$url}" />
                                </xsl:when>
                                <xsl:when test="position()='4' and not(contains($fs-location, 'ERROR:'))">
                                  <input type="text" name="{.}" class="box" size="30" value="{$fs-location}" />
                                </xsl:when>
                                <xsl:when test="position()='5' and not(contains($crawldepth, 'ERROR:'))">
                                  <input type="text" name="{.}" class="box" size="30" value="{$crawldepth}" />
                                </xsl:when>
                                <xsl:when test="position()='6' and not(contains($crawlmaxpages, 'ERROR:'))">
                                  <input type="text" name="{.}" class="box" size="30" value="{$crawlmaxpages}" />
                                </xsl:when>
                                <xsl:otherwise>
                                  <input type="text" name="{.}" class="box" size="30" value="" />
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                              <input type="text" name="{.}" class="box" size="30" />
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                        <td><xsl:if test="@required = 'true'">*</xsl:if></td>
                        <xsl:choose>
                          <xsl:when test="position()='1' and contains($realmid, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($realmid, 'ERROR:')"/>
                                  </xsl:attribute>
                                </i18n:message>
                              </font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='2' and contains($realmname, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($realmname, 'ERROR:')"/>
                                  </xsl:attribute>
                                </i18n:message>
                              </font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='3' and contains($url, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($url, 'ERROR:')"/>
                                  </xsl:attribute>
                                </i18n:message>
                              </font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='4' and contains($fs-location, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($fs-location, 'ERROR:')"/>
                                  </xsl:attribute>
                                </i18n:message>
                              </font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='5' and contains($crawldepth, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($crawldepth, 'ERROR:')"/>
                                  </xsl:attribute>
                                </i18n:message>
                              </font>
                            </td>
                          </xsl:when>
                          <xsl:when test="position()='6' and contains($crawlmaxpages, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($crawlmaxpages, 'ERROR:')"/>
                                  </xsl:attribute>
                                </i18n:message>
                              </font>
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
                        <input type="submit" name="submit" value="i18n:attr key=add-realm"/>
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
