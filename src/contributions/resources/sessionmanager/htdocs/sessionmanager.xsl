<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:s="http://www.wyona.org/yanel/1.0"
  xmlns="http://www.w3.org/1999/xhtml">

  <xsl:template match="/s:session-manager">
    <html>
      <head>
        <title>Session manager</title>
      </head>
      <body>
        <h1>Session manager</h1>
        <strong>Total number of sessions: </strong>
        <xsl:value-of select="s:number-of-sessions"/>

        <h2>All "logged-in" sessions</h2>
        <!-- Show all logged-in sessions first -->
        <ol>
          <xsl:apply-templates select="s:session[s:identities]"/>
        </ol>

        <h2>All anonymous sessions</h2>
        <!-- Show all anonymous sessions later -->
        <ol>
          <xsl:apply-templates select="s:session[s:no-identity-yet]"/>
        </ol>

        <hr/>
        <p><a href="?yanel.resource.viewid=source">XML</a> (Please make sure to configure a view called "source" within your resource configuration, e.g. see <em>src/realms/welcome-admin/yanel/rti/session-manager.html.yanel-rc</em>)</p>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="s:session">
    <li>
      <strong>Session: </strong>
      <xsl:value-of select="@id"/>
      <ul>
        <li>
          <strong>Creation time: </strong>
          <xsl:value-of select="@creation-time"/>
        </li>
        <li>
          <strong>Last accessed time: </strong>
          <xsl:value-of select="@last-accessed-time"/>
        </li>
        <xsl:if test="s:identities">
          <li>
            <strong>Identities: </strong>
            <xsl:value-of select="s:identities"/>
          </li>
        </xsl:if>

<!--
        <xsl:if test="s:identity">
          <li>
            <strong>Identity: </strong>
            <xsl:value-of select="s:identities"/>
          </li>
        </xsl:if>
        <xsl:if test="s:last-access">
          <li>
            <strong>Last accessed page: </strong>
            <xsl:value-of select="s:last-access"/>
          </li>
        </xsl:if>
-->
      </ul>
      <br/>
    </li>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
