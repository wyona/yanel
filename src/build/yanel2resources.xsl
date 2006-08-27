<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL'"/>

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
      <xsl:comment> Do not compile: <xsl:value-of select="@src"/> </xsl:comment>
    </xsl:when>
    <xsl:otherwise>
<!--
    <ant antfile="${{build.dir}}/{@src}build.xml" target="compile"/>
-->
    <exec executable="ant">
      <arg line="-f ${{build.dir}}/{@src}build.xml compile"/>
    </exec>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>

  <target name="deploy-resources" description="Deploy resources" depends="init">
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="${{build.dir}}/{@src}/build/lib"/>
    </copy>
</xsl:for-each>
  </target>

  <target name="clean-resources" description="Clean resources" depends="init">
<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <xsl:choose>
    <xsl:when test="@compile='false'">
      <xsl:comment> Do not clean: <xsl:value-of select="@src"/>build </xsl:comment>
    </xsl:when>
    <xsl:otherwise>
    <delete dir="${{build.dir}}/{@src}/build"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>
</project>
</xsl:template>

</xsl:stylesheet>
