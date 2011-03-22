<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="xhtml dc">

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  <xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
  <xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
  <xsl:param name="languages" select="'LANGUAGES_IS_NULL'"/>
  <xsl:param name="yanel.toolbar-status" select="'YANEL_TOOLBAR_STATUS_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVED-PREFIX_IS_NULL'"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Workflow Dashboard</title>
      </head>
      <body>
        <h1>Workflow Dashboard</h1>
        <form>
          <xsl:apply-templates select="/workflow-dashboard/workflow-states"/>
          <br/>
          <xsl:apply-templates select="/workflow-dashboard/mime-types"/>
          <br/>
          <input type="submit" value="Search"/>
        </form>

        <p>
        Query: <xsl:value-of select="/workflow-dashboard/@query"/>
        </p>

        <h2>Results</h2>
        <xsl:choose>
          <xsl:when test="/workflow-dashboard/no-nodes-found">
            <p>No documents found!</p>
          </xsl:when>
          <xsl:otherwise>
        <ul>
        <xsl:for-each select="/workflow-dashboard/node">
          <li><a href="{$yarep.back2realm}{substring-after(@path, '/')}"><xsl:value-of select="@path"/></a></li>
        </xsl:for-each>
        </ul>
          </xsl:otherwise>
        </xsl:choose>

        <p>
        <a href="?yanel.resource.viewid=xml">XML</a>
        </p>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="workflow-states">
    <select name="workflow-state">
      <xsl:for-each select="state">
        <option><xsl:value-of select="."/></option>
      </xsl:for-each>
    </select>
  </xsl:template>

  <xsl:template match="mime-types">
    <select name="mime-type">
      <xsl:for-each select="mime-type">
        <option><xsl:value-of select="."/></option>
      </xsl:for-each>
    </select>
  </xsl:template>

</xsl:stylesheet>
