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

  <xsl:template match="yanel:node">
    <xsl:choose>
      <xsl:when test="descendant-or-self::yanel:node[concat('/', @href) = $currentPath]">
        <div class="menublock-selected-{count(ancestor-or-self::yanel:node)}">
          <xsl:call-template name="item-selected"/>
          <xsl:apply-templates select="yanel:node"/>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <div class="menublock-{count(ancestor-or-self::yanel:node)}">
          <xsl:call-template name="item-default"/>
          <xsl:apply-templates select="yanel:node"/>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="item-default">
    <xsl:variable name="style">
      <xsl:call-template name="getstyle">
        <xsl:with-param name="href" select="@href"/>
      </xsl:call-template>
    </xsl:variable>
    <div class="menuitem-{count(ancestor-or-self::yanel:node)}">
      <a href="{$yarep.back2realm}{@href}" class="{$style}"><xsl:apply-templates select="yanel:name"/></a>
    </div>
  </xsl:template>

  <xsl:template name="item-selected">
    <xsl:variable name="style">
      <xsl:call-template name="getstyle">
        <xsl:with-param name="href" select="@href"/>
      </xsl:call-template>
    </xsl:variable>
    <div class="menuitem-selected-{count(ancestor-or-self::yanel:node)}-{$style}">
      <xsl:choose>
        <xsl:when test="concat('/', @href) = $currentPath">
          <xsl:apply-templates select="yanel:name"/>
        </xsl:when>
        <xsl:otherwise>
          <a href="{$yarep.back2realm}{@href}" class="active"><xsl:apply-templates select="yanel:name"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template name="getstyle">
    <xsl:param name="href"/>
    <xsl:choose>
      <xsl:when test="contains($href, 'Wind')">wind</xsl:when>
      <xsl:when test="contains($href, 'AerospaceAutomotiveMassTransportation')">transport</xsl:when>
      <xsl:when test="contains($href, 'MarineSportCivilEngineering')">technical</xsl:when>
      <xsl:otherwise>normal</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="yanel:name">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
