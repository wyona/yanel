<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="yanel.back2context" select="''" />
  
  <xsl:param name="realmid" select="''" />
  <xsl:param name="realmname" select="''" />
  <xsl:param name="url" select="''" />
  <xsl:param name="fslocation" select="''" />
  <xsl:param name="crawldepth" select="''" />
  <xsl:param name="crawlmaxpages" select="''" />

  <xsl:param name="realmid-prop-exists" select="''" />
  <xsl:param name="realmname-prop-exists" select="''" />
  <xsl:param name="url-prop-exists" select="''" />
  <xsl:param name="fslocation-prop-exists" select="''" />
  <xsl:param name="crawldepth-prop-exists" select="''" />
  <xsl:param name="crawlmaxpages-prop-exists" select="''" />
  
  <xsl:param name="downloadevents" select="''" />
  <xsl:param name="errorevents" select="''" />
  <xsl:param name="nofdownloads" select="''" />
  <xsl:param name="isdone" select="''" />

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
        <xsl:if test="$isdone = 'false'">
          <meta http-equiv="refresh" content="2"/>
        </xsl:if>
      </head>
      
      <body>
        <h3><i18n:message key="add-realm"/></h3>
        
        <div id="contentBody">
          <xsl:choose>
            <xsl:when test="$submitted != 'false'">
              <xsl:choose>
                <xsl:when test="$isdone = 'true'">
                  <h2>Import completed</h2>
                  <p>
                    <a>
                      <xsl:attribute name="href">
                        <xsl:value-of select="$yanel.back2context"/><xsl:value-of select="$realmid"/>/
                      </xsl:attribute>View <xsl:value-of select="$realmname" />
                    </a>
                  </p>
                </xsl:when>
                <xsl:otherwise>
                  <h2>Import running, please wait...</h2>
                  <form>
                    <input type="submit" value="Stop Crawl" name="stop"/>
                  </form>
                </xsl:otherwise>
              </xsl:choose>
              <p>[<xsl:value-of select="$nofdownloads"/>] pages have been imported.</p>
              <p>Downloaded Pages:</p>
              <p style="font-size: small"><pre><xsl:value-of select="$downloadevents"/></pre></p>
              <p>Errors:</p>
              <p style="font-size: small"><pre><xsl:value-of select="$errorevents"/></pre></p>
            </xsl:when>
            <xsl:otherwise>
              
<!--
              <form method="post">
                  <table cellpadding="0" cellspacing="0" border="0">
                    <xsl:apply-templates select="/yanel:form/yanel:inputfields/yanel:input"/>
                  </table>
              </form>
              <br/><br/>
-->
              
              <form method="post">
                  <table cellpadding="0" cellspacing="0" border="0">
                    <xsl:for-each select="/yanel:form/yanel:inputfields/yanel:input">
                      <div>
                        <xsl:choose>
                          <xsl:when test="position()='1' and $realmid-prop-exists = 'true'">
                            <xsl:call-template name="style-display-none"/>
                          </xsl:when>
                          <xsl:when test="position()='2' and $realmname-prop-exists = 'true'">
                            <xsl:call-template name="style-display-none"/>
                          </xsl:when>
                          <xsl:when test="position()='3' and $url-prop-exists = 'true'">
                            <xsl:call-template name="style-display-none"/>
                          </xsl:when>
                          <xsl:when test="position()='4' and $fslocation-prop-exists = 'true'">
                            <xsl:call-template name="style-display-none"/>
                          </xsl:when>
                          <xsl:when test="position()='5' and $crawldepth-prop-exists = 'true'">
                            <xsl:call-template name="style-display-none"/>
                          </xsl:when>
                          <xsl:when test="position()='6' and $crawlmaxpages-prop-exists = 'true'">
                            <xsl:call-template name="style-display-none"/>
                          </xsl:when>
                          <xsl:otherwise/>
                        </xsl:choose>
                        
                      <tr>
                        <td align="left" valign="top" class="contentfield" width="120">
                          <i18n:message key="{.}"/>:&#0160;
                        </td>
                        <td>
                          <xsl:choose>
                            <xsl:when test="contains($realmid, 'ERROR:') or contains($realmname, 'ERROR:') 
                                            or contains($url, 'ERROR:') or contains($fslocation, 'ERROR:')
                                            or contains($crawldepth, 'ERROR:') or contains($crawlmaxpages, 'ERROR:')">
                              <input type="text" name="{.}" class="box" size="30">
                              <xsl:choose>
                                <xsl:when test="position()='1' and not(contains($realmid, 'ERROR:'))">
                                  <xsl:attribute name="value"><xsl:value-of select="$realmid" /></xsl:attribute>
                                </xsl:when>
                                <xsl:when test="position()='2' and not(contains($realmname, 'ERROR:'))">
                                  <xsl:attribute name="value"><xsl:value-of select="$realmname" /></xsl:attribute>
                                </xsl:when>
                                <xsl:when test="position()='3' and not(contains($url, 'ERROR:'))">
                                  <xsl:attribute name="value"><xsl:value-of select="$url" /></xsl:attribute>
                                </xsl:when>
                                <xsl:when test="position()='4' and not(contains($fslocation, 'ERROR:'))">
                                  <xsl:attribute name="value"><xsl:value-of select="$fslocation" /></xsl:attribute>
                                </xsl:when>
                                <xsl:when test="position()='5' and not(contains($crawldepth, 'ERROR:'))">
                                  <xsl:attribute name="value"><xsl:value-of select="$crawldepth" /></xsl:attribute>
                                </xsl:when>
                                <xsl:when test="position()='6' and not(contains($crawlmaxpages, 'ERROR:'))">
                                  <xsl:attribute name="value"><xsl:value-of select="$crawlmaxpages" /></xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:attribute name="value"></xsl:attribute>
                                </xsl:otherwise>
                              </xsl:choose>
                              </input>
                            </xsl:when>
                            <!-- Show the input fields if there are no default values set, and hide them otherwise -->
                            <xsl:otherwise>
                              <input name="{.}" class="box" size="30">
                              <xsl:choose>
                                <xsl:when test="position()='1'">
                                  <xsl:attribute name="value"><xsl:value-of select="$realmid" /></xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when test="$realmid-prop-exists != 'false'">
                                      <xsl:attribute name="type">hidden</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:attribute name="type">text</xsl:attribute>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:when test="position()='2'">
                                  <xsl:attribute name="value"><xsl:value-of select="$realmname" /></xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when test="$realmname-prop-exists != 'false'">
                                      <xsl:attribute name="type">hidden</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:attribute name="type">text</xsl:attribute>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:when test="position()='3'">
                                  <xsl:attribute name="value"><xsl:value-of select="$url" /></xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when test="$url-prop-exists != 'false'">
                                      <xsl:attribute name="type">hidden</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:attribute name="type">text</xsl:attribute>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:when test="position()='4'">
                                  <xsl:attribute name="value"><xsl:value-of select="$fslocation" /></xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when test="$fslocation-prop-exists != 'false'">
                                      <xsl:attribute name="type">hidden</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:attribute name="type">text</xsl:attribute>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:when test="position()='5'">
                                  <xsl:attribute name="value"><xsl:value-of select="$crawldepth" /></xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when test="$crawldepth-prop-exists != 'false'">
                                      <xsl:attribute name="type">hidden</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:attribute name="type">text</xsl:attribute>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:when test="position()='6'">
                                  <xsl:attribute name="value"><xsl:value-of select="$crawlmaxpages" /></xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when test="$crawlmaxpages-prop-exists != 'false'">
                                      <xsl:attribute name="type">hidden</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:attribute name="type">text</xsl:attribute>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:attribute name="value"></xsl:attribute>
                                </xsl:otherwise>
                              </xsl:choose>
                              </input>
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                        <td><xsl:if test="@yanel:required = 'true'">*</xsl:if></td>
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
                          <xsl:when test="position()='4' and contains($fslocation, 'ERROR:')">
                            <td>
                              &#160;<font color="red">
                                <i18n:message>
                                  <xsl:attribute name="key">
                                    <xsl:value-of select="substring-after($fslocation, 'ERROR:')"/>
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
                          (i.e. <xsl:value-of select="@yanel:samplevalue"/>)
                        </td>
                        <td>&#160;</td>
                      </tr>
                      </div>
                    </xsl:for-each>
                    
                    <tr>
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
  
  <xsl:template name="style-display-none">
    <xsl:attribute name="style">display:none;</xsl:attribute>
  </xsl:template>
  
  <xsl:template match="yanel:input">
    <tr><td><i18n:message key="{.}"/>:&#160;</td><td><input type="text" name="{.}"/><xsl:if test="@yanel:required='true'">*</xsl:if></td></tr>
    <tr><td>&#160;</td><td>(i.e. <xsl:value-of select="@yanel:samplevalue"/>)</td></tr>
  </xsl:template>
    
</xsl:stylesheet>
