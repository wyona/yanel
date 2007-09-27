<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
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
        <title>Collection: <xsl:value-of select="/dir:directory/@yanel:path"/>
        </title>
        <script src="{$yanel.htdocs}yanel-js/sorttable.js" type="text/javascript"/>
      </head>
      <body>
        <div>
          <p>
            <b>Yanel Path (with regard to realm):</b>&#160;<xsl:value-of select="/dir:directory/@yanel:path"/>
          </p>
          <p>
            <b>Collection Path:</b>&#160;<xsl:value-of
            select="/dir:directory/@dir:path"/> (<a
              href="?yanel.resource.viewid=source">XML view</a>)</p>
          <p>
            <table class="sortable">
              <thead>
                <tr>
                  <th>Type</th>
                  <th class="sorttable_alpha">Name</th>
                  <th>Lastmodified</th>
                  <th>Size</th>
                </tr>
              </thead>
              <tbody>
                <tr class="parent">
                  <td class="type" sorttable_customkey="1">Parent </td>
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
      <td class="type" sorttable_customkey="Collection">Collection </td>
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
      <td class="type" sorttable_customkey="Resource">Resource</td>
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
