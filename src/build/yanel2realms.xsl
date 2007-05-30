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
<project name="realms" default="build-realms">

  <target name="init">
    <echo>Init ...</echo>
    <dirname property="build.dir" file="${{ant.file}}"/>
  </target>

  <target name="build-realms" description="Build realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <condition property="ant-file-of-realm-with-id-{@id}-exists">
      <available file="{yanel:config/@src}/build.xml"/>
    </condition>
    <antcall target="build-realm-with-id-{@id}"/>

<!--
      <xsl:choose>
        <xsl:when test="starts-with(yanel:config/@src, '/') or string-length(substring-before(yanel:config/@src, ':/'))='1'">
    <ant inheritAll="false" antfile="{yanel:config/@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
        </xsl:when>
        <xsl:otherwise>
    <ant inheritAll="false" antfile="${{build.dir}}/{yanel:config/@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
        </xsl:otherwise>
      </xsl:choose>
-->
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
  <target name="build-realm-with-id-{@id}" if="ant-file-of-realm-with-id-{@id}-exists">
    <echo>Build realm with id "<xsl:value-of select="@id"/>"</echo>
    <ant inheritAll="false" antfile="{yanel:config/@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
  </target>
</xsl:for-each>

  <target name="deploy-realms" description="Deploy libs of realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <condition property="lib-dir-of-realm-with-id-{@id}-exists">
      <available file="{yanel:config/@src}/build/lib" type="dir"/>
    </condition>
    <antcall target="deploy-realm-with-id-{@id}"/>
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
  <target name="deploy-realm-with-id-{@id}" description="Deploy libs of realm {@id}" depends="init" if="lib-dir-of-realm-with-id-{@id}-exists">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="{yanel:config/@src}/build/lib"/>
    </copy>
  </target>
</xsl:for-each>

  <target name="clean-realms" description="Clean realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <condition property="build-dir-of-realm-with-id-{@id}-exists">
      <available file="{yanel:config/@src}/build" type="dir"/>
    </condition>
    <antcall target="clean-realm-with-id-{@id}"/>
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
  <target name="clean-realm-with-id-{@id}" description="Clean realm {@id}" depends="init" if="build-dir-of-realm-with-id-{@id}-exists">
    <delete dir="{yanel:config/@src}/build"/>
  </target>
</xsl:for-each>

</project>

</xsl:template>

</xsl:stylesheet>
