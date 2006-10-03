<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL_servlet_context_prefix'"/>
<xsl:param name="yanel.source.version" select="'NULL_yanel_source_version'"/>

<xsl:template match="/">

<xsl:comment> NOTE: This file has been generated automatically from conf/yanel.xml! </xsl:comment>
<project name="resources">

  <target name="init">
    <echo>Init ...</echo>
    <dirname property="build.dir" file="${{ant.file}}"/>
  </target>

  <target name="compile-core">
    <echo>Compile Core ...</echo>
  </target>

  <target name="build-resources" description="Build resources" depends="init">
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <xsl:choose>
    <xsl:when test="@compile='false'">
      <echo>INFO: Do not compile: <xsl:value-of select="@src"/></echo>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/')">
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
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>

  <target name="deploy-resources" description="Deploy resources" depends="init">
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
    <xsl:choose>
      <xsl:when test="starts-with(@src, '/')">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="{@src}/build/lib"/>
    </copy>
      </xsl:when>
      <xsl:otherwise>
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="${{build.dir}}/{@src}/build/lib"/>
    </copy>
      </xsl:otherwise>
    </xsl:choose>
</xsl:for-each>
  </target>

  <target name="clean-resources" description="Clean resources" depends="init">
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <xsl:choose>
    <xsl:when test="@compile='false'">
      <echo>INFO: Do not clean: <xsl:value-of select="@src"/>build</echo>
    </xsl:when>
    <xsl:otherwise>
    <delete dir="${{build.dir}}/{@src}/build"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>

  <target name="copy-resources-dependencies" description="Copy dependencies of resources" depends="init">
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
    <xsl:choose>
      <xsl:when test="starts-with(@src, '/')">
    <ant inheritAll="false" antfile="{@src}/build.xml" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
      </xsl:when>
      <xsl:otherwise>
    <ant inheritAll="false" antfile="${{build.dir}}/{@src}/build.xml" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
    </ant>
      </xsl:otherwise>
    </xsl:choose>
</xsl:for-each>
  </target>
</project>
</xsl:template>

</xsl:stylesheet>
