<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  >

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yarep.parent" select="'YAREPPARENT_IS_NULL'"/>
  <xsl:param name="yanel.globalHtdocsPath" select="'YANELHTDOCS_IS_NULL'"/>

  <xsl:template match="/">
<!-- TODO: Also include sub-directories
    <xsl:apply-templates select="/dir:directory/*"/>
-->
[
<xsl:for-each select="/dir:directory/dir:file">
{
"name":"<xsl:value-of select="@name"/>",
"date":"<xsl:value-of select="@date"/>",
"workflowState":"<xsl:value-of select="@workflow-state"/>"
}<xsl:if test="position() != last()">,</xsl:if>
</xsl:for-each>
]
  </xsl:template>

  <xsl:template match="dir:directory">
    <tr class="collection">
      <td class="type" sorttable_customkey="Collection"><i18n:text key="collection"/> </td>
      <td class="name">
        <a href="{$yarep.back2realm}.{@path}/" title="directory">
          <xsl:value-of select="@name"/>
        </a>
      </td>
      <td class="lastmodifier"> - </td>
      <td class="size" sorttable_customkey="0"> - </td>
    </tr>
  </xsl:template>

  <xsl:template match="dir:file">
    <tr class="file">
      <td class="type" sorttable_customkey="Resource"><i18n:text key="resource"/></td>
      <td class="name">
        <a href="{$yarep.back2realm}.{@path}" title="file">
          <xsl:value-of select="@name"/>
        </a>
      </td>
      <td class="lastmodifier">
        <xsl:value-of select="@date"/>
      </td>
      <td class="size" sorttable_customkey="{@size}">
        <xsl:call-template name="humanbyte">
        <xsl:with-param name="bytes">
          <xsl:value-of select="@size"/>
        </xsl:with-param>
      </xsl:call-template>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="humanbyte">
    <xsl:param name="bytes"/>
    <xsl:choose>
      <!-- giga -->
      <xsl:when test="$bytes &gt;= 1073741824">
        <xsl:value-of select="round($bytes div 1073741824)"/> GB
      </xsl:when>
      <!-- mega -->
      <xsl:when test="$bytes &gt;= 1048576">
        <xsl:value-of select="round($bytes div 1048576)"/> MB
      </xsl:when>
      <!-- kilo -->
      <xsl:when test="$bytes &gt;= 1024">
        <xsl:value-of select="round($bytes div 1024)"/> KB
      </xsl:when>
      <!-- bytes -->
      <xsl:otherwise>
        <xsl:value-of select="$bytes"/> Bytes
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
