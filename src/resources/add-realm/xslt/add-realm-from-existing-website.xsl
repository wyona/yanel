<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="yanel.back2context" select="''" />
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Add Realm From Existing Website</title>
      </head>
      <body>
      <h1>Add Realm From Existing Website</h1>
      <a href="?yanel.resource.viewid=xml">Show XML</a>

      <xsl:choose>
        <xsl:when test="/yanel:add-realm/yanel:from-existing-website/yanel:realm-created">
          <xsl:choose>
          <xsl:when test="/yanel:add-realm/yanel:from-existing-website/yanel:crawler-running">
	    <p>Realm has been created and registered, but the Crawler is still running ...</p>
            <form>
              <input type="submit" name="check-crawler-status" value="Check Crawler Status"/>
            </form>
          </xsl:when>
          <xsl:otherwise>
	    <p>Realm has been created, registered and all downloaded pages have been imported :-)</p>
          </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="/yanel:add-realm/yanel:from-existing-website/yanel:downloadevents">
            <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:isdone"/>
            <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:nofdownloads"/>
            <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:downloadevents"/>
            <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:errorevents"/>
        </xsl:when>
        <xsl:when test="/yanel:add-realm/yanel:from-existing-website/yanel:no-event-log">
          <p>No crawler seems to be running ...</p>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:not-valid"/>
          <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:valid"/>
      <form>
        <xsl:if test="/yanel:add-realm/yanel:from-existing-website/yanel:valid">
          <input type="hidden" name="confirm" value="true"/>
	</xsl:if>
        <table>
        <xsl:apply-templates select="/yanel:add-realm/yanel:from-existing-website/yanel:parameter"/>
        <xsl:choose>
        <xsl:when test="/yanel:add-realm/yanel:from-existing-website/yanel:valid">
          <tr><td colspan="2" align="right"><input type="submit" name="submit-from-existing-website" value="Yes, Add Realm"/></td></tr>
        </xsl:when>
        <xsl:otherwise>
          <tr><td colspan="2" align="right"><input type="submit" name="submit-from-existing-website" value="Add Realm"/></td></tr>
        </xsl:otherwise>
        </xsl:choose>
        </table>
      </form>
        </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="yanel:parameter">
    <xsl:apply-templates select="@yanel:exception"/>
    <xsl:choose>
      <xsl:when test="@yanel:configuration-value">
    <tr><td><b><xsl:value-of select="@yanel:name"/></b></td><td><xsl:value-of select="@yanel:configuration-value"/></td></tr>
      </xsl:when>
      <xsl:otherwise>
        <tr><td><b><xsl:value-of select="@yanel:name"/></b></td><td><input type="text" name="{@yanel:name}" value="{@yanel:value}"/> <xsl:apply-templates select="@yanel:required"/></td></tr>
        <tr><td>&#160;</td><td align="right">(i.e. <xsl:value-of select="@yanel:sample-value"/>)</td></tr>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@yanel:exception">
    <tr><td colspan="2"><font color="red">Exception:</font>&#160;<xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="yanel:not-valid">
    <p><b>Something is wrong with one or more input parameters. Please see below for more details.</b></p>
  </xsl:template>

  <xsl:template match="yanel:valid">
    <p>All input parameters are valid. Do you want to create a new realm from scratch with the parameters below?</p>
  </xsl:template>

  <xsl:template match="@yanel:required">
    <xsl:if test=". = 'true'">
      *
    </xsl:if>
  </xsl:template>

  <xsl:template match="yanel:isdone">
    <p>
      <xsl:choose>
        <xsl:when test=". = 'false'">
          Still crawling resp. importing ... (Please reload this page to check on status)
	</xsl:when>
        <xsl:when test=". = 'true'">
          Crawling resp. importing has been completed :-) Please see new realm <a href="{../yanel:realm-id}/"><xsl:value-of select="../yanel:realm-name"/></a>.
	</xsl:when>
	<xsl:otherwise>
          Exception: <xsl:value-of select="."/>
	</xsl:otherwise>
      </xsl:choose>
    </p>
  </xsl:template>

  <xsl:template match="yanel:nofdownloads">
    <p><xsl:value-of select="."/> number of "pages" downloaded.</p>
  </xsl:template>

  <xsl:template match="yanel:downloadevents">
    <h2>Downloaded pages</h2>
    <p><xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="yanel:errorevents">
    <h2>Failed downloads</h2>
    <p><xsl:value-of select="."/></p>
  </xsl:template>
   
</xsl:stylesheet>
