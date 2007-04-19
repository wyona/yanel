<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:yanel="http://www.wyona.org/yanel/1.0">

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">

    <xsl:comment> NOTE: This file has been generated automatically from conf/yanel.xml! </xsl:comment>
    <project name="test-resources">

      <target name="init">
        <dirname property="build.dir" file="${{ant.file}}"/>
      </target>

      <target name="build-test-resources" description="Build resources" depends="init">

        <javac destdir="${{junit.build.dir}}">
          <classpath refid="classpath" />
          <include name="src/test/junit/**/*.java"/>
          <xsl:apply-templates select="/yanel:resource-types/yanel:resource-type"/>
        </javac>
        
        <javac destdir="${{htmlunit.build.dir}}">
          <classpath refid="classpath" />
          <include name="src/test/htmlunit/**/*.java"/>
          <xsl:apply-templates select="/yanel:resource-types/yanel:resource-type"/>
        </javac>
        
      </target>
    </project>
  </xsl:template>
  
  <xsl:template match="yanel:resource-type[@compile='true']">
    <xsl:variable name="srcpath">
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
          <xsl:value-of select="@src"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('${build.dir}/', @src)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <src path="{$srcpath}"/>
  </xsl:template>
  
  <xsl:template match="yanel:resource-type[@compile='false']">
  </xsl:template>

</xsl:stylesheet>
