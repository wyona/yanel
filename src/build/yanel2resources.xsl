<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL_servlet_context_prefix'"/>
<xsl:param name="yanel.source.version" select="'NULL_yanel_source_version'"/>
<xsl:param name="maven.url" select="'NULL_maven_url'"/>

<xsl:template match="/">

<xsl:comment> NOTE: This file has been generated automatically from conf/resource-types.xml resp. conf/local/local.resource-types.xml! </xsl:comment>
<project name="resources">

  <target name="init">
    <echo>Init ...</echo>
    <dirname property="build.dir" file="${{ant.file}}"/>
  </target>

  <target name="compile-core">
    <echo>Compile Core ...</echo>
  </target>

  <target name="build-resources" description="Build resources" depends="init">
<xsl:for-each select="/yanel:resource-types/yanel:resource-type">
  <xsl:choose>
    <xsl:when test="not(@compile)">
      <echo>INFO: Do not compile: <xsl:value-of select="@src"/></echo>
    </xsl:when>
    <xsl:when test="@compile='false'">
      <echo>INFO: Do not compile: <xsl:value-of select="@src"/></echo>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
    <ant inheritAll="false" antfile="{@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
        </xsl:when>
        <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
    <ant inheritAll="false" antfile="{@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
        </xsl:when>
        <xsl:otherwise>
    <ant inheritAll="false" antfile="${{build.dir}}/{@src}/build.xml" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>

  <xsl:comment>INFO: Deploy libs of resources</xsl:comment>
  <target name="deploy-resources" description="Deploy resources" depends="init">
    <mkdir dir="${{build.dir}}/webapps/{$servlet.context.prefix}/resources"/>
<xsl:for-each select="/yanel:resource-types/yanel:resource-type">

    <xsl:if test="@copy-dir-name">
      <xsl:comment>TODO: Copy sources ...</xsl:comment>
    <xsl:choose>
      <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/resources/{@copy-dir-name}">
      <fileset dir="{@src}" excludes="build/**, src/java/**, src/build/**, build.xml"/>
    </copy>
      </xsl:when>
      <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/resources/{@copy-dir-name}">
      <fileset dir="{@src}" excludes="build/**, src/java/**, src/build/**, build.xml"/>
    </copy>
      </xsl:when>
      <xsl:otherwise>
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/resources/{@copy-dir-name}">
      <fileset dir="${{build.dir}}/{@src}" excludes="build/**, src/java/**, src/build/**, build.xml"/>
    </copy>
      </xsl:otherwise>
    </xsl:choose>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="{@src}/build/lib"/>
    </copy>
      </xsl:when>
      <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
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
<xsl:for-each select="/yanel:resource-types/yanel:resource-type">
  <xsl:choose>
    <xsl:when test="@compile='false'">
      <echo>INFO: Do not clean: <xsl:value-of select="@src"/>build</echo>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <!-- TODO: What about Windows, e.g. D:/foo/bar ...! -->
        <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
          <delete dir="{@src}/build"/>
        </xsl:when>
        <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
          <delete dir="{@src}/build"/>
        </xsl:when>
        <xsl:otherwise>
          <delete dir="${{build.dir}}/{@src}/build"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>

  <xsl:comment>Copy dependencies of resources</xsl:comment>
  <target name="copy-resources-dependencies" description="Copy dependencies of resources" depends="init">
<xsl:for-each select="/yanel:resource-types/yanel:resource-type">

  <xsl:choose>
    <xsl:when test="@copy">
      <xsl:comment>TODO!!!</xsl:comment>
    </xsl:when>
    <xsl:otherwise>
    </xsl:otherwise>
  </xsl:choose>

    <xsl:choose>
      <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
    <ant inheritAll="false" antfile="{@src}/build.xml" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
      </xsl:when>
      <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
    <ant inheritAll="false" antfile="{@src}/build.xml" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
      </xsl:when>
      <xsl:otherwise>
    <ant inheritAll="false" antfile="${{build.dir}}/{@src}/build.xml" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
    </ant>
      </xsl:otherwise>
    </xsl:choose>
</xsl:for-each>
  </target>
</project>
</xsl:template>

</xsl:stylesheet>
