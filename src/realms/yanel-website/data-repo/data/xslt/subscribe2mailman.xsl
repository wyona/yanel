<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:wb="http://www.imstat.org/meeting/1.0"
  xmlns:yanel="http://www.wyona.org/yanel/mailman/1.0.0"
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
  <xsl:param name="yanel.toolbar-status" select="'YANEL_TOOLBAR_STATUS_IS_NULL'"/>

  <xsl:template match="/">
    <html>
<head>
<title>Subscribe to mailing lists</title>
</head>
<body>
<h1>Subscribe to mailing lists</h1>

<xsl:if test="/yanel:subscribe-to-mailman/yanel:exception/@code = 'passwords-did-not-match'">
<p><font color="red">Passwords did not match!</font></p>
</xsl:if>

<xsl:choose>
<xsl:when test="/yanel:subscribe-to-mailman/yanel:success">
<p>
Your subscription request for e-mail address '<xsl:value-of select="/yanel:subscribe-to-mailman/@email"/>' to the following mailing list(s)
</p>

<ul>
<xsl:for-each select="/yanel:subscribe-to-mailman/yanel:list">
  <li><a href="http://lists.imstat.org/mailman/listinfo/{@name}"><xsl:value-of select="@name"/></a></li>
</xsl:for-each>
</ul>

<p>
has been received, and will soon be acted upon.
Depending on the configuration of the mailing list(s), your subscription request
may have to be first confirmed by you via email, or approved by the list
moderator.  If confirmation is required, you will soon get a confirmation
email which contains further instructions.
</p>

<xsl:if test="/yanel:subscribe-to-mailman/yanel:no-such-list">
<p><font color="red">The following lists are either not configured or something else went wrong:</font></p>
<ul>
<xsl:for-each select="/yanel:subscribe-to-mailman/yanel:no-such-list">
  <li><a href="http://lists.imstat.org/mailman/listinfo/{@name}"><xsl:value-of select="@name"/></a></li>
</xsl:for-each>
</ul>
</xsl:if>

</xsl:when>
<xsl:otherwise>

<form method="post">
  <table>
    <tr>
      <td>Your email address:</td><td><input type="text" name="email" value="{/yanel:subscribe-to-mailman/@email}"/></td>
    </tr>
    <tr>
      <td>Pick a password:</td><td><input type="password" name="password"/></td>
    </tr>
    <tr>
      <td>Reenter password to confirm:</td><td><input type="password" name="password-confirmed"/></td>
    </tr>
    <tr>
      <td>Select mailing list(s):</td>
      <td>
        <input type="checkbox" name="list-yanel-usage">Usage</input>
        <br/>
        <input type="checkbox" name="list-yanel-development">Development</input>
        <br/>
        <input type="checkbox" name="list-yanel-commits">Commit Messages</input>
        <br/>
      </td>
    </tr>
    <tr>
      <td colspan="2"><input type="submit" value="Subscribe"/></td>
    </tr>
  </table>
</form>
</xsl:otherwise>
</xsl:choose>
</body>
    </html>
  </xsl:template>
  
  <xsl:template match="wb:keywords">
    <p>Keywords: <xsl:value-of select="."/></p>
  </xsl:template>
  
  <xsl:template match="wb:valid-from">
    <p>Valid from date: <xsl:value-of select="."/></p>
  </xsl:template>
  
  <xsl:template match="wb:valid-to">
    <p>Valid to date: <xsl:value-of select="."/></p>
  </xsl:template>
  
  <xsl:template match="@is-annual-meeting">
    <p>Is annual meeting: <xsl:value-of select="."/></p>
  </xsl:template>
  
  <xsl:template match="wb:image">
    <p>Annual meeting image:<br/><img src="{$name-without-suffix}/{@id}"/></p>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
