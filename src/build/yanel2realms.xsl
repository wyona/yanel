<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL_servlet_context_prefix'"/>
<xsl:param name="yanel.source.version" select="'NULL_yanel_source_version'"/>
<xsl:param name="yanel.source.home" select="'NULL_yanel_source_home'"/>
<xsl:param name="maven.url" select="'NULL_maven_url'"/>

<xsl:template match="/">

<xsl:comment> NOTE: This file has been generated automatically from conf/realms.xml resp. conf/local/local.realms.xml! </xsl:comment>
<project name="realms" default="build-realms">

  <target name="init">
    <echo>Init ...</echo>
    <dirname property="build.dir" file="${{ant.file}}"/>
  </target>

  <target name="build-realms" description="Build realms" depends="init">

<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

    <condition property="ant-file-of-realm-with-id-{@id}-exists">
      <available file="{$realmPath}/build.xml"/>
    </condition>
    <antcall target="build-realm-with-id-{@id}"/>
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

  <target name="build-realm-with-id-{@id}" if="ant-file-of-realm-with-id-{@id}-exists">
    <echo>Build realm with id "<xsl:value-of select="@id"/>"</echo>
    <ant inheritAll="false" antfile="{$realmPath}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="yanel.source.home" value="{$yanel.source.home}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
  </target>
</xsl:for-each>

  <target name="deploy-realms" description="Deploy libs of realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

    <condition property="lib-dir-of-realm-with-id-{@id}-exists">
      <available file="{$realmPath}/build/lib" type="dir"/>
    </condition>
    <antcall target="deploy-realm-with-id-{@id}"/>
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

  <target name="deploy-realm-with-id-{@id}" description="Deploy libs of realm {@id}" depends="init" if="lib-dir-of-realm-with-id-{@id}-exists">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="{$realmPath}/build/lib"/>
    </copy>
  </target>
</xsl:for-each>

  <target name="clean-realms" description="Clean realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

    <condition property="build-dir-of-realm-with-id-{@id}-exists">
      <available file="{$realmPath}/build" type="dir"/>
    </condition>
    <antcall target="clean-realm-with-id-{@id}"/>
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

  <target name="clean-realm-with-id-{@id}" description="Clean realm {@id}" depends="init" if="build-dir-of-realm-with-id-{@id}-exists">
    <delete dir="{$realmPath}/build"/>
  </target>
</xsl:for-each>

<xsl:comment>Copy dependencies of realms</xsl:comment>
  <target name="copy-dependencies" description="Copy dependencies of realms" depends="init">
<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

    <condition property="build-dir-of-realm-with-id-{@id}-exists">
      <available file="{$realmPath}/build" type="dir"/>
    </condition>
    <antcall target="copy-dependencies-of-realm-with-id-{@id}"/>
</xsl:for-each>
  </target>

<xsl:for-each select="/yanel:realms/yanel:realm">
    <xsl:variable name="realmPath"><xsl:call-template name="get-realm-path"><xsl:with-param name="path"><xsl:value-of select="yanel:config/@src"/></xsl:with-param></xsl:call-template></xsl:variable>

<target name="copy-dependencies-of-realm-with-id-{@id}" description="Copy dependencies of realm {@id}" depends="init" if="build-dir-of-realm-with-id-{@id}-exists">
    <ant inheritAll="false" antfile="{$realmPath}/build.xml" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="yanel.source.home" value="{$yanel.source.home}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
</target>
</xsl:for-each>
</project>
</xsl:template>

<!-- INFO: Check if src ends-with xml -->
<xsl:template name="get-realm-path"><xsl:param name="path"/><xsl:choose><xsl:when test="substring($path, string-length($path) - 2) = 'xml'">TODO-SUBSTRING-BEFORE-LAST-INDEX</xsl:when><xsl:otherwise><xsl:value-of select="$path"/></xsl:otherwise></xsl:choose></xsl:template>

</xsl:stylesheet>
