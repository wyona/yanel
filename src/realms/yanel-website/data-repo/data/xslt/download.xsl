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
        <title><i18n:text key="collection"/>: <xsl:value-of select="/dir:directory/@yanel:path"/>
        </title>
        <script src="{$yanel.htdocs}yanel-js/sorttable.js" type="text/javascript"/>
      </head>
      <body>

<h1>Download (UNIX)</h1>
<p>You can select to download a source code snapshot from below, or get the latest version of Yanel through our <a href="../../download/source-repository.html">Git repository</a>. Please note that we have a strong focus on keeping the <a href="../../download/source-repository.html">Git repository</a> stable, and source code snapshots can become out of date rather quickly. If you are interested in our development methodology, see the <a href="../development/processes/index.html">page on processes</a>.</p>
<p><span style="font-size: 15px; font-weight: bold;">Source Code Snapshots</span></p>

<p>
<table border="1" cellspacing="0" cellpadding="0" bgcolor="#dddddd">
<tbody>
<tr>
<th>Package</th><th>Size</th><th>Date</th><th>Issues</th>
</tr>
<tr>
<td valign="top"><a href="../../download/source-snapshots/wyona-yanel-3.0.0-rc76b2afc69489824bc8f9c1f05ffdcffcfa07c04-src.zip">wyona-yanel-3.0.0-rc76b2afc69489824bc8f9c1f05ffdcffcfa07c04-src.zip</a></td>
<td valign="top">14MB</td>
<td valign="top">March 25, 2015</td>
<td valign="top">No issues reported so far</td>
</tr>
<xsl:for-each select="/dir:directory/dir:file">
  <xsl:if test="starts-with(@name, 'wyona-yanel')">
  <xsl:apply-templates select="." mode="hugo"/>
  </xsl:if>
</xsl:for-each>
</tbody>
</table>
</p>

<h2>Source Code from Git Repository</h2>
<p>See&#160;<a href="../../download/source-repository.html">Wyona's Git repository</a>.</p>

      </body>
    </html>
  </xsl:template>

  <xsl:template match="dir:file" mode="hugo">
    <tr class="file">
      <td class="name">
        <a href="{$yarep.back2realm}.{@path}" title="file">
          <xsl:value-of select="@name"/>
        </a>
      </td>
      <td class="size" sorttable_customkey="{@size}">
        <xsl:call-template name="humanbyte">
        <xsl:with-param name="bytes">
          <xsl:value-of select="@size"/>
        </xsl:with-param>
      </xsl:call-template>
      </td>
      <td class="lastmodifier">
        <xsl:value-of select="@date"/>
      </td>
      <td>No issues reported so far</td>
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
