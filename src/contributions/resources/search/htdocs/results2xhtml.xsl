<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:y="http://www.wyona.org/yanel/search/1.0"
  >
<!--
  exclude-result-prefixes="xhtml dc dcterms">
-->

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  <xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
  <xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
  <xsl:param name="languages" select="'LANGUAGES_IS_NULL'"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>
          Search
        </title>
      </head>
      <body>

        <h1>Search</h1>

        <form>
          Search <input type="text" name="q" value="{/y:search/y:query}"/> with <select name="provider"><option value="google">Google</option><option value="bing">bing</option><option value="yanel" selected="true">Yanel</option><option value="yanelproxy-google">Google (Yanel Proxy)</option><option value="yanelproxy-msn">MSN (Yanel Proxy)</option></select> &#160; <input type="submit" value="Search"/>
        </form>

        <hr/>

        <xsl:apply-templates select="/y:search/y:exception"/>
        <xsl:apply-templates select="/y:search/y:no-query"/>
        <p>Search results provider: <xsl:value-of select="/y:search/y:provider"/></p>
        <xsl:apply-templates select="/y:search/y:results"/>
        <xsl:if test="not(/y:search/y:results) and not(/y:search/y:exception) and not(/y:search/y:no-query)">
          <p>Your search - <xsl:value-of select="/y:search/y:query"/> - did not match any documents</p>
        </xsl:if>

      </body>
    </html>
  </xsl:template>

  <xsl:template match="y:results">
    <h2>All Results (<a href="?q={../y:query}&amp;provider={../y:provider}&amp;yanel.resource.viewid=xml">as XML</a>)</h2>
    <ul>
    <xsl:apply-templates select="y:result"/>
    </ul>
  </xsl:template>

  <xsl:template match="y:result">
    <li>
      <a><xsl:attribute name="href"><xsl:choose><xsl:when test="starts-with(@url, 'http')"><xsl:value-of select="@url"/></xsl:when><xsl:otherwise><xsl:value-of select="$yarep.back2realm"/><xsl:value-of select="@url"/></xsl:otherwise></xsl:choose></xsl:attribute><xsl:choose><xsl:when test="y:title"><xsl:value-of select="y:title"/></xsl:when><xsl:otherwise><xsl:value-of select="@url"/></xsl:otherwise></xsl:choose></a>
    </li>
  </xsl:template>

  <xsl:template match="y:exception">
    <h2><font color="red">Exception:</font></h2>
    <p>
    <xsl:value-of select="."/>
    </p>
  </xsl:template>

  <xsl:template match="y:no-query">
    <h2><font color="red">Warning:</font></h2>
    <p>
    No query was specified! Please make sure to enter a search term.
    </p>
  </xsl:template>
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
