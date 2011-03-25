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
          <fieldset style="width: 300px; background: #eee;">
            <legend>Search ...</legend>
          <xsl:apply-templates select="/workflow-dashboard/workflow-states"/>
          <br/><br/>
          <xsl:apply-templates select="/workflow-dashboard/mime-types"/>
          <br/><br/>
          <input type="submit" value="Search"/>
          </fieldset>
        </form>

        <xsl:choose>
          <xsl:when test="/workflow-dashboard/@query">
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
          <li>
            <xsl:choose>
              <xsl:when test="revision">
                <a href="{$yarep.back2realm}{substring-after(@path, '/')}?yanel.resource.revision={revision}"><xsl:value-of select="@path"/></a> (Revision: <xsl:value-of select="revision"/>)
              </xsl:when>
              <xsl:otherwise>
                <a href="{$yarep.back2realm}{substring-after(@path, '/')}"><xsl:value-of select="@path"/></a>
              </xsl:otherwise>
            </xsl:choose>
          </li>
        </xsl:for-each>
        </ul>
          </xsl:otherwise>
        </xsl:choose>

        <p>
        <a><xsl:attribute name="href">?yanel.resource.viewid=xml&amp;workflow-state=<xsl:value-of select="/workflow-dashboard/@workflow-state"/><xsl:if test="/workflow-dashboard/@mime-type">&amp;mime-type=<xsl:value-of select="/workflow-dashboard/@mime-type"/></xsl:if></xsl:attribute>XML</a>
        </p>
          </xsl:when>
          <xsl:otherwise>
<xsl:comment>No query yet.</xsl:comment>
          </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="workflow-states">
    Workflow state:<br/>
    <select name="workflow-state">
      <xsl:for-each select="state">
        <xsl:choose>
          <xsl:when test="/workflow-dashboard/@workflow-state = .">
        <option selected="selected"><xsl:value-of select="."/></option>
          </xsl:when>
          <xsl:otherwise>
        <option><xsl:value-of select="."/></option>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </select>
  </xsl:template>

  <xsl:template match="mime-types">
    Content type:<br/>
    <select name="mime-type">
      <option value="none_selected">any</option>
      <xsl:for-each select="mime-type">
        <xsl:choose>
          <xsl:when test="/workflow-dashboard/@mime-type = .">
        <option selected="selected"><xsl:value-of select="."/></option>
          </xsl:when>
          <xsl:otherwise>
        <option><xsl:value-of select="."/></option>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </select>
  </xsl:template>

</xsl:stylesheet>
