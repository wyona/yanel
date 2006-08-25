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
  </target>

  <target name="compile-core">
    <echo>Compile Core ...</echo>
  </target>

  <target name="compile-resource" description="Compile Java classes of a specific resource" depends="init, compile-core">
    <property name="resource.home.dir" value="HOME_DIR_IS_NULL"/>
    <property name="resource.name" value="NAME_IS_NULL"/>

    <property name="resource.classes.dir" value="${{resource.home.dir}}/build/classes"/>
    <property name="resource.lib.dir" value="${{resource.home.dir}}/build/lib"/>
    <property name="resource.java.dir" value="${{resource.home.dir}}/src/java"/>

    <mkdir dir="${{resource.classes.dir}}"/>
    <javac srcdir="${{resource.java.dir}}" destdir="${{resource.classes.dir}}"
      classpathref="classpath.yanel"
      debug="true"
    />
    <mkdir dir="${{resource.lib.dir}}"/>
    <jar destfile="${{resource.lib.dir}}/yanel-resource-${{resource.name}}-0.0.1-dev-r${{yanel.revision}}.jar"
      basedir="${{resource.classes.dir}}"
    />
  </target>

<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <target name="compile-{@name}-resource" description="Compile Java classes of {@name} resource" depends="init">
    <antcall target="compile-resource">
      <param name="resource.home.dir" value="{@src}"/>
      <!--
      <param name="resource.home.dir" value="${yanel.home.dir}/src/resources/tape"/>
      -->
      <param name="resource.name" value="{@name}"/>
    </antcall>
  </target>
</xsl:for-each>

<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <target name="compile-resources" description="Compile resources" depends="init">
    <antcall target="compile-{@name}-resource"/>
  </target>
</xsl:for-each>
</project>
</xsl:template>

</xsl:stylesheet>
