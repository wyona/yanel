<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="yanel.back2context" select="''" />
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Create a new realm (aka 'website') from scratch</title>
      </head>
      <body>
      <h1>Create a new realm (aka 'website') from scratch</h1>
      <xsl:choose>
        <xsl:when test="/yanel:add-realm/yanel:from-scratch/yanel:realm-created">
          <p>Realm has been created and registered. Please see the <a href="index.html">list of registered realms</a>.</p>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="/yanel:add-realm/yanel:from-scratch/yanel:not-valid"/>
          <xsl:apply-templates select="/yanel:add-realm/yanel:from-scratch/yanel:valid"/>
          <xsl:apply-templates select="/yanel:add-realm/yanel:from-scratch/yanel:exception"/>
          <p>Please enter a realm ID (which is used as URL prefix but also as internal reference ID) and a human readable realm name:</p>
      <form>
        <xsl:if test="/yanel:add-realm/yanel:from-scratch/yanel:valid">
          <input type="hidden" name="confirm" value="true"/>
	</xsl:if>
        <table>
        <xsl:apply-templates select="/yanel:add-realm/yanel:from-scratch/yanel:parameter"/>
        <xsl:choose>
        <xsl:when test="/yanel:add-realm/yanel:from-scratch/yanel:valid">
          <tr><td colspan="2" align="right"><input type="submit" name="submit-from-scratch" value="Yes, Add Realm"/></td></tr>
        </xsl:when>
        <xsl:otherwise>
          <tr><td colspan="2" align="right"><input type="submit" name="submit-from-scratch" value="Add Realm"/></td></tr>
        </xsl:otherwise>
        </xsl:choose>
          <tr><td colspan="2" align="right"><br/><i18n:message key="requiredFields"/>fields marked with (*) are required</td></tr>
        </table>
      </form>
        </xsl:otherwise>
        </xsl:choose>
<hr/>
      <a href="?yanel.resource.viewid=xml">Show XML</a>
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
        <tr><td>&#160;</td><td align="right">(for example '<i><xsl:value-of select="@yanel:sample-value"/></i>')</td></tr>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@yanel:exception">
    <tr><td colspan="2"><font color="red">Exception:</font>&#160;<xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="yanel:exception">
    <p><font color="red">Exception:</font>&#160;<xsl:value-of select="."/></p>
  </xsl:template>

  <xsl:template match="yanel:not-valid">
    <p>Something is wrong with one or more input parameters. Please see below for more details.</p>
  </xsl:template>

  <xsl:template match="yanel:valid">
    <p>All input parameters are valid. Do you want to create a new realm from scratch with the parameters below?</p>
  </xsl:template>

  <xsl:template match="@yanel:required">
    <xsl:if test=". = 'true'">
      *
    </xsl:if>
  </xsl:template>
   
</xsl:stylesheet>
