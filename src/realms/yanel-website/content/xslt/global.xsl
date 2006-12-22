<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

<!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
<xsl:output method="xhtml" encoding="UTF-8"/>
<!--
<xsl:output method="html"/>
-->

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>

<xsl:param name="yanel.meta.language" select="'en'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<xsl:comment>
WARNING: This content has been generated dynamically. All changes will be lost.
</xsl:comment>

<head>
<xsl:comment>Name: <xsl:value-of select="$yanel.path.name"/> (without suffix: <xsl:value-of select="$name-without-suffix"/>), Path: <xsl:value-of select="$yanel.path"/>, Back 2 Realm: <xsl:value-of select="$yarep.back2realm"/>, Back 2 Context: <xsl:value-of select="$yanel.back2context"/></xsl:comment>

  <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/> - Yanel</title>

  <!-- This is needed such that Microsoft Internet Explorer displays characters such as &nbsp; correctly (also see xsl:output, whereas I (Michi) am not sure if the encoding is really needed there) -->
  <meta content="application/xhtml+xml; charset=UTF-8" http-equiv="Content-Type"/>
  <meta name="generator" content="Wyona Yanel"/>

  <!-- http://dublincore.org/documents/2001/04/12/usageguide/simple-html.shtml -->
  <meta name="DC.Creator" content="TODO"/>
  <meta name="DC.Language" content="TODO"/>

  <link rel="stylesheet" href="{$yarep.back2realm}css/global.css" type="text/css"/>
  <xsl:copy-of select="/xhtml:html/xhtml:head/*[name(.) != 'title']"/>

  <xsl:choose>
  <xsl:when test="/xhtml:html/xhtml:head/xhtml:link/@rel='neutron-introspection'">
    <!-- NOTE: Use the one from the copy above in case one exists! -->
  </xsl:when>
  <xsl:otherwise>
    <!-- TODO: Maybe one should better not cut-off the suffix! -->
    <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
  </xsl:otherwise>
  </xsl:choose>
</head>

<body bgcolor="#ffffff">

<center>
<table width="800" border="0" bgcolor="#f7f7f7" cellpadding="0" cellspacing="0">
<tr>
  <td colspan="3">
<h1 id="header"><a href="{$yarep.back2realm}index.html" style="text-decoration: none; color: #ffffff"><img src="{$yarep.back2realm}img/yanel.gif" border="0"/></a></h1>
  </td>
</tr>

<tr>
<!-- NAVIGATION -->
<td valign="top">
<div id="navigation">
<xsl:call-template name="navigation"/>
</div>
</td>

<!-- SEPARATOR -->
<td>&#160;&#160;</td>

<!-- CONTENT -->
<td valign="top" width="100%">
  <div id="content">
    <xsl:copy-of select="/xhtml:html/xhtml:body/*"/>
  </div>
</td>
</tr>
<tr>
  <td colspan="3" id="footer">
  <p>
    <font size="-1"><i18n:message key="poweredBy"/><xsl:text> </xsl:text><a href="http://yanel.wyona.org">Wyona Yanel</a>, <a href="https://svn.wyona.org/repos/public/tomcat-cluster/">Wyona Balancer</a>, <a href="http://tomcat.apache.org">Apache Tomcat</a>, <a href="http://httpd.apache.org">Apache HTTP Server</a></font><br/>
    Copyright &#169; 2006 <a href="http://www.wyona.com">Wyona</a>. <i18n:message key="allRightsReserved"/>. - <a href="?yanel.resource.meta"><i18n:message key="pageInfo"/></a>
  </p>
  </td>
</tr>
</table>
</center>

</body>
</html>
</xsl:template>

<xsl:template name="navigation">
<b>All You Need</b><br/>
&#160;<a href="{$yarep.back2realm}{$yanel.meta.language}/about.html">About</a><br/>
&#160;<a href="{$yarep.back2realm}download.html">Download</a><br/>
&#160;<a href="{$yarep.back2realm}license.html">License</a><br/>
&#160;<a href="{$yarep.back2realm}{$yanel.meta.language}/getting-started.html">Getting Started</a><br/>
&#160;Features<br/>
<!--
&#160;News<br/>
 -->
&#160;<a href="{$yarep.back2realm}news/index.html">News</a><br/>

&#160;<a href="{$yarep.back2realm}en/documentation/index.html">Documentation</a><br/>
<br/>
<b>Development/Community</b><br/>
&#160;<a href="{$yarep.back2realm}download/source-repository.html">Get the Source</a><br/>
&#160;<a href="{$yarep.back2realm}mailing-lists.html">Mailing Lists</a><br/>
&#160;<a href="{$yarep.back2realm}roadmap.html">Roadmap</a><br/>
&#160;<a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/buglist.cgi?short_desc_type=allwordssubstr&amp;short_desc=&amp;product=Yanel&amp;long_desc_type=allwordssubstr&amp;long_desc=&amp;bug_file_loc_type=allwordssubstr&amp;bug_file_loc=&amp;bug_status=NEW&amp;bug_status=ASSIGNED&amp;bug_status=REOPENED&amp;emailassigned_to1=1&amp;emailtype1=substring&amp;email1=&amp;emailassigned_to2=1&amp;emailreporter2=1&amp;emailcc2=1&amp;emailtype2=substring&amp;email2=&amp;bugidtype=include&amp;bug_id=&amp;votes=&amp;changedin=&amp;chfieldfrom=&amp;chfieldto=Now&amp;chfieldvalue=&amp;cmdtype=doit&amp;namedcmd=Mobi&amp;newqueryname=&amp;order=Reuse+same+sort+as+last+time&amp;field0-0-0=noop&amp;type0-0-0=noop&amp;value0-0-0=" target="_blank">Task/Bug Tracker</a><br/>
&#160;<a href="{$yarep.back2realm}testing.html">Continuous Integration</a><br/>
&#160;<a href="{$yarep.back2realm}en/governance.html">Governance</a><br/>
&#160;<a href="{$yarep.back2realm}principles.html">Principles</a><br/>
&#160;<a href="{$yarep.back2realm}acknowledgements.html">Acknowledgements</a><br/>
&#160;<a href="{$yarep.back2realm}references.html">References</a><br/>
<br/>
<b>Professional Services</b><br/>
&#160;Support<br/>
&#160;Consulting<br/>
&#160;Training<br/>
&#160;Solutions<br/>
&#160;<a href="{$yarep.back2realm}en/contact.html">Contact</a><br/>
<br/>
<b><i18n:message key="search"/></b>
&#160;<form action="{$yarep.back2realm}en/search.html" method="GET"><input type="text" name="query" class="searchbox" size="14"/></form>
</xsl:template>

</xsl:stylesheet>
