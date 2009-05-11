<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:param name="servlet.context.prefix" select="'NULL_servlet_context_prefix'"/>
<xsl:param name="yanel.source.version" select="'NULL_yanel_source_version'"/>
<xsl:param name="maven.url" select="'NULL_maven_url'"/>
<xsl:param name="yanel.source.home" select="'NULL_yanel_source_home'"/>
<xsl:param name="copy.resource-type-configs.to.webapp" select="'NULL_copy_resource-type-configs_to_webapp'"/>

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
    <xsl:when test="not(@src)">
      <echo>INFO: Do not compile, because no 'src' attribute specified (package: <xsl:value-of select="@package"/>)</echo>
    </xsl:when>
    <xsl:when test="not(@compile)">
      <echo>INFO: Do not compile: <xsl:value-of select="@src"/></echo>
    </xsl:when>
    <xsl:when test="@compile='false'">
      <echo>INFO: Do not compile: <xsl:value-of select="@src"/></echo>
    </xsl:when>
    <xsl:otherwise>
      <echo>INFO: Start compiling resource <xsl:value-of select="@src"/></echo>
    <xsl:variable name="RT-ant-file">
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
          <xsl:value-of select="concat(@src, '/build.xml')"/>
        </xsl:when>
        <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
          <xsl:value-of select="concat(@src, '/build.xml')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('${build.dir}/', @src, '/build.xml')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <ant inheritAll="false" antfile="{$RT-ant-file}" target="compile">
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
      <property name="yanel.source.home" value="{$yanel.source.home}"/>
    </ant>
      <echo>INFO: End compiling resource <xsl:value-of select="@src"/></echo>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>

  <xsl:comment>INFO: Deploy libs of resources</xsl:comment>
  <target name="deploy-resources" description="Deploy resources" depends="init">
    <mkdir dir="${{build.dir}}/webapps/{$servlet.context.prefix}/resources"/>
<xsl:for-each select="/yanel:resource-types/yanel:resource-type">

    <xsl:if test="@copy-dir-name and $copy.resource-type-configs.to.webapp = 'true'">
      <xsl:comment>Copy sources of resource type '<xsl:value-of select="@src"/>' to Yanel webapp folder ...</xsl:comment>
      <xsl:variable name="RT-home-dir">
        <xsl:choose>
          <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
            <xsl:value-of select="@src"/>
          </xsl:when>
          <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
            <xsl:value-of select="@src"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="concat('${build.dir}/', @src, '/build.xml')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/resources/{@copy-dir-name}">
        <fileset dir="{$RT-home-dir}" excludes="build/**, src/java/**, src/build/**, build.xml"/>
      </copy>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="@src">
  <xsl:variable name="RT-lib-dir">
    <xsl:choose>
      <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
        <xsl:value-of select="concat(@src, '/build/lib')"/>
      </xsl:when>
      <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
        <xsl:value-of select="concat(@src, '/build/lib')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat('${build.dir}/', @src, '/build/lib')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
    <copy todir="${{build.dir}}/webapps/{$servlet.context.prefix}/WEB-INF/lib">
      <fileset dir="{$RT-lib-dir}" excludes="pom.xml, *-sources.jar"/>
    </copy>
      </xsl:when>
      <xsl:otherwise>
        <echo>WARN: No 'src' attribute specified (package: <xsl:value-of select="@package"/>)</echo>
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
    <xsl:when test="not(@src)">
      <echo>INFO: Do not clean, because no 'src' attribute specified (package: <xsl:value-of select="@package"/>)</echo>
    </xsl:when>
    <xsl:otherwise>
    <xsl:variable name="RT-build-dir">
      <xsl:choose>
        <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
          <xsl:value-of select="concat(@src, '/build')"/>
        </xsl:when>
        <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
          <xsl:value-of select="concat(@src, '/build')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('${build.dir}/', @src, '/build')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
          <delete dir="{$RT-build-dir}"/>
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
    <xsl:when test="@src">
  <xsl:variable name="RT-ant-file">
    <xsl:choose>
      <xsl:when test="starts-with(@src, '/') or string-length(substring-before(@src, ':/'))='1'">
        <xsl:value-of select="concat(@src, '/build.xml')"/>
      </xsl:when>
      <xsl:when test="starts-with(@src, '@YANEL_SRC_DIR@')">
        <xsl:value-of select="concat(@src, '/build.xml')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat('${build.dir}/', @src, '/build.xml')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
    <ant inheritAll="false" antfile="{$RT-ant-file}" target="copy-dependencies">
      <property name="build.dir" value="${{build.dir}}"/>
      <property name="servlet.context.prefix" value="{$servlet.context.prefix}"/>
      <property name="yanel.source.version" value="{$yanel.source.version}"/>
      <property name="maven.url" value="{$maven.url}"/>
      <property name="yanel.source.home" value="{$yanel.source.home}"/>
    </ant>
    </xsl:when>
    <xsl:otherwise>
      <echo>WARN: No 'src' attribute specified (package: <xsl:value-of select="@package"/>)</echo>
    </xsl:otherwise>
  </xsl:choose>
</xsl:for-each>
  </target>
</project>
</xsl:template>

</xsl:stylesheet>
