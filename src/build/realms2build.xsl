<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
  exclude-result-prefixes="yanel"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="local-build-realms-file"/>

<xsl:template match="/">

<xsl:comment> NOTE: This file has been generated automatically from conf/realms.xml resp. conf/local/local.realms.xml! </xsl:comment>
<project name="realms" default="build-realms">
  <property name="yanel.source.home" location=".."/><!--XXX HACK-->

  <import file="${{yanel.source.home}}/src/build/realms/realms.build.xml"/>

  <path id="realms.path">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="absolute-directory-location">
      <xsl:choose>
        <xsl:when test="starts-with(yanel:config/@src, '/') or string-length(substring-before(yanel:config/@src, ':/'))='1'">
          <xsl:value-of select="yanel:config/@src"/>
        </xsl:when>
        <xsl:when test="starts-with(yanel:config/@src, '@REALMS_DIR@')">
          <xsl:value-of select="concat('${yanel.source.home}/src/realms/', substring-after(yanel:config/@src, '@REALMS_DIR@'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat($local-build-realms-file, '/../', yanel:config/@src)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <pathelement location="{$absolute-directory-location}"/>
  <xsl:message>found realm with id "<xsl:value-of select="@id"/>" located in <xsl:value-of select="$absolute-directory-location"/></xsl:message>
</xsl:for-each>
  </path>
</project>

</xsl:template>

</xsl:stylesheet>
