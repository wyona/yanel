<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  >

  <xsl:output method="xhtml"/>

  <!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
  <!--
<xsl:output method="html"/>
-->

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yarep.parent" select="'YAREPPARENT_IS_NULL'"/>
  <xsl:param name="yanel.htdocs" select="'YANELHTDOCS_IS_NULL'"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><i18n:message key="collection"/>: <xsl:value-of select="/dir:directory/@yanel:path"/>
        </title>
        <script src="{$yanel.htdocs}yanel-js/sorttable.js" type="text/javascript"/>
      </head>
      <body>
        <div>
          <p>
            <b><i18n:message key="yanelPathTitle"/>:</b>&#160;<xsl:value-of select="/dir:directory/@yanel:path"/>
          </p>
          <p>
            <b><i18n:message key="collectionPathTitle"/>:</b>&#160;<xsl:value-of
            select="/dir:directory/@dir:path"/> (<a
              href="?yanel.resource.viewid=source">XML <i18n:message key="view"/></a>)</p>
          <p>
            <table class="sortable">
              <thead>
                <tr>
                  <th><i18n:message key="type"/></th>
                  <th class="sorttable_alpha"><i18n:message key="name"/></th>
                  <th><i18n:message key="lastmodified"/></th>
                  <th><i18n:message key="size"/></th>
                </tr>
              </thead>
              <tbody>
                <tr class="parent">
                  <td class="type" sorttable_customkey="1"><i18n:message key="parent"/> </td>
                  <td class="name">
                    <a href="{$yarep.parent}" title="directory">..</a>
                  </td>
                  <td class="lastmodifier"> - </td>
                  <td class="size"> - </td>
                </tr>
                <xsl:apply-templates select="/dir:directory/*"/>
              </tbody>
            </table>
          </p>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="dir:directory">
    <tr class="collection">
      <td class="type" sorttable_customkey="Collection"><i18n:message key="collection"/> </td>
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
      <td class="type" sorttable_customkey="Resource"><i18n:message key="resource"/></td>
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
