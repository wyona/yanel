<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:str="http://exslt.org/strings"
 extension-element-prefixes="str"
>


<xsl:param name="Eclipse-classpath-doc-URL" select="'empty.classpath'"/>
<xsl:param name="CLASSPATH" select="/*[1]/text()"/>
<xsl:param name="CLASSPATH-separator" select="':'"/>
<xsl:param name="source-dirs"/>
<xsl:param name="classes-dir"/>
<xsl:param name="source-filename-suffix" select="'-sources'"/>


<xsl:variable name="Eclipse-classpath-doc" select="document($Eclipse-classpath-doc-URL)"/>

<xsl:template match="/">
  <xsl:apply-templates select="$Eclipse-classpath-doc/classpath" mode="classpath"/>
</xsl:template>

<xsl:template match="classpath" mode="classpath">
  <xsl:copy>
    <xsl:apply-templates select="@*" mode="classpath"/>
    <!--xsl:comment><xsl:value-of select="$source-dirs"/></xsl:comment-->
  <xsl:if test="$source-dirs">
    <xsl:apply-templates select="str:split($source-dirs, ':')" mode="source-location"/>
  </xsl:if>
    <xsl:apply-templates select="node()[not(name() = 'classpathentry' and (@kind = 'lib' or @kind = 'var'))]" mode="classpath"/>
    <xsl:apply-templates select="str:split($CLASSPATH, $CLASSPATH-separator)" mode="classpath-location"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="*" mode="source-location">
  <classpathentry kind="src" path="{text()}"/>
<xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="*" mode="classpath-location">
  <classpathentry kind="lib" path="{text()}">
    <xsl:if test="$source-filename-suffix">
      <xsl:attribute name="sourcepath">
        <xsl:value-of select="concat(substring-before(text(), '.jar'), $source-filename-suffix, '.jar')"/>
      </xsl:attribute>
    </xsl:if>
  </classpathentry>
<xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="classpathentry[@kind = 'src' and $source-dirs]" mode="classpath"/>

<xsl:template match="classpathentry[@kind = 'output' and $classes-dir]" mode="classpath">
  <classpathentry kind="output" path="{$classes-dir}"/>
</xsl:template>

<xsl:template match="@*|node()" mode="classpath">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()" mode="classpath"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet>
