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
        <br/>
        <ul>
          <!-- Show all logged-in sessions first -->
          <xsl:apply-templates select="s:session[s:identities]"/>
          <!-- Show all anonymous sessions later -->
          <xsl:apply-templates select="s:session[s:no-identity-yet]"/>
        </ul>
        <hr/>
        <p><a href="?yanel.resource.viewid=source">XML</a> (Please make sure to configure a view called "source")</p>
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
