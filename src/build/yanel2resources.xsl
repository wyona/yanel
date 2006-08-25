<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xml" indent="yes"/>

<xsl:template match="/">
<project name="resources">

<xsl:for-each select="/yanel:yanel/yanel:resources/yanel:resource">
  <target name="compile-TODO-resource" description="Compile Java classes of TODO resource" depends="init">
    <antcall target="compile-resource">
      <param name="resource.home.dir" value="{@src}"/>
      <!--
      <param name="resource.home.dir" value="${yanel.home.dir}/src/resources/tape"/>
      -->
      <param name="resource.name" value="TODO"/>
    </antcall>
  </target>
</xsl:for-each>
</project>
</xsl:template>

</xsl:stylesheet>
