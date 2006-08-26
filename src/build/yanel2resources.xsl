<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

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

<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <target name="build-resources" description="Build resources" depends="init">
<!--
    <ant antfile="${{build.dir}}/{@src}build.xml" target="compile"/>
-->
    <exec executable="ant">
      <arg line="-f ${{build.dir}}/{@src}build.xml compile"/>
    </exec>
  </target>
</xsl:for-each>
</project>
</xsl:template>

</xsl:stylesheet>
