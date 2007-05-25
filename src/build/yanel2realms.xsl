<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL_servlet_context_prefix'"/>
<xsl:param name="yanel.source.version" select="'NULL_yanel_source_version'"/>

<xsl:template match="/">

<xsl:comment> NOTE: This file has been generated automatically from conf/realms.xml resp. conf/local/local.realms.xml! </xsl:comment>
<project name="realms" default="init">

  <target name="init">
    <echo>Init ...</echo>
    <dirname property="build.dir" file="${{ant.file}}"/>
  </target>

  <target name="build-realms" description="Build realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <echo>Build realm with id "<xsl:value-of select="@id"/>"</echo>
<!--
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
    <ant inheritAll="false" antfile="{@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
        </xsl:when>
        <xsl:otherwise>
    <ant inheritAll="false" antfile="${{build.dir}}/{@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
        </xsl:otherwise>
      </xsl:choose>
-->
</xsl:for-each>
  </target>

</project>

</xsl:template>

</xsl:stylesheet>
