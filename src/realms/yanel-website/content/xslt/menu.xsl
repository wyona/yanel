<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  exclude-result-prefixes="xhtml">
  
  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  <xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
  <xsl:param name="languages" select="'LANGUAGES_IS_NULL'"/>
  <xsl:param name="currentPath" select="'CURRENTPATH_IS_NULL'"/>

  <xsl:template match="yanel:sitetree">
    <div id="menu">
      <xsl:apply-templates select="yanel:node"/>
    </div>
  </xsl:template>

  <xsl:template match="yanel:node[@href]">
    <xsl:text>&#160;</xsl:text>
    <xsl:choose>
      <xsl:when test="@href != ''">
        <a href="{$yarep.back2realm}{@href}"><xsl:apply-templates select="yanel:name"/></a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="yanel:name"/>
      </xsl:otherwise>
    </xsl:choose>
    <br/>
  </xsl:template>

  <xsl:template match="yanel:node[not(@href)]">
    <xsl:if test="not(position()=1)">
      <br/>
    </xsl:if>
    <b><xsl:value-of select="yanel:name"/></b>
    <br/>
    <xsl:apply-templates select="yanel:node"/>
  </xsl:template>

</xsl:stylesheet>
