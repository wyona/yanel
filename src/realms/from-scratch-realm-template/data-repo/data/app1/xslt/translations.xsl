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

  <xsl:template match="yanel:translations">
    <span id="translations" class="language_choice">
      <xsl:if test="count(yanel:translation[@exists='true']) > 1">
        <xsl:apply-templates select="yanel:translation[@exists='true']"/>
      </xsl:if>
    </span>
  </xsl:template>

  <xsl:template match="yanel:translation">
    <xsl:choose>
      <xsl:when test="@current='true'">
        <span><xsl:call-template name="label"/></span>
      </xsl:when>
      <xsl:otherwise>
        <span><a href="{$yarep.back2realm}{substring-after(@path, '/')}"><xsl:call-template name="label"/></a></span>
      </xsl:otherwise>
    </xsl:choose>
<!--
    <xsl:if test="position() != last()">
      <xsl:text> | </xsl:text>
    </xsl:if>
-->
  </xsl:template>
  
  <!-- Maybe one should just use English, Deutsch ... -->
  <xsl:template name="label">
    <xsl:choose>
      <xsl:when test="@language = 'de'">
        Deutsch
      </xsl:when>
      <xsl:when test="@language = 'en'">
        English
      </xsl:when>
<!--
      <xsl:when test="@language = 'de' and @current = 'true'">
        Deutsch
      </xsl:when>
      <xsl:when test="@language = 'de' and not(@current)">
        German
      </xsl:when>
      <xsl:when test="@language = 'en' and @current = 'true'">
        English
      </xsl:when>
      <xsl:when test="@language = 'en' and not(@current)">
        Englisch
      </xsl:when>
-->
      <xsl:otherwise>
    <xsl:value-of select="translate(@language, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
