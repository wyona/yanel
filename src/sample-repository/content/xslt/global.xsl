<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<!-- IMPORTANT: Needs to correspond to the mime-type which is sent by the server! -->
<xsl:output method="xhtml"/>
<!--
<xsl:output method="html"/>
-->

<xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
<xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
<xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
<xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>

<xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<xsl:comment>
WARNING: This content has been generated dynamically. All changes will be lost.
</xsl:comment>

<head>
  <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
<xsl:comment>Name: <xsl:value-of select="$yanel.path.name"/> (without suffix: <xsl:value-of select="$name-without-suffix"/>), Path: <xsl:value-of select="$yanel.path"/>, Back 2 Realm: <xsl:value-of select="$yarep.back2realm"/>, Back 2 Context: <xsl:value-of select="$yanel.back2context"/></xsl:comment>

  <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/> - Yanel</title>
</head>

<body>
<h1>YANEL</h1>

<table>
<tr>
<td valign="top">
<b>All You Need</b><br/>
&#160;<a href="{$yarep.back2realm}about.html">About</a><br/>
&#160;<a href="{$yarep.back2realm}download.html">Download</a><br/>
&#160;<a href="{$yarep.back2realm}license.html">License</a><br/>
&#160;Getting Started<br/>
&#160;Features<br/>
&#160;News<br/>
&#160;<a href="{$yarep.back2realm}documentation/index.html">Documentation</a><br/>
<br/>
<b>Community/Developers</b><br/>
&#160;<a href="{$yarep.back2realm}download/source-repository.html">Get the Source</a><br/>
&#160;<a href="{$yarep.back2realm}mailing-lists.html">Mailing Lists</a><br/>
&#160;<a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/buglist.cgi?short_desc_type=allwordssubstr&amp;short_desc=&amp;product=Yanel&amp;long_desc_type=allwordssubstr&amp;long_desc=&amp;bug_file_loc_type=allwordssubstr&amp;bug_file_loc=&amp;bug_status=NEW&amp;bug_status=ASSIGNED&amp;bug_status=REOPENED&amp;emailassigned_to1=1&amp;emailtype1=substring&amp;email1=&amp;emailassigned_to2=1&amp;emailreporter2=1&amp;emailcc2=1&amp;emailtype2=substring&amp;email2=&amp;bugidtype=include&amp;bug_id=&amp;votes=&amp;changedin=&amp;chfieldfrom=&amp;chfieldto=Now&amp;chfieldvalue=&amp;cmdtype=doit&amp;namedcmd=Mobi&amp;newqueryname=&amp;order=Reuse+same+sort+as+last+time&amp;field0-0-0=noop&amp;type0-0-0=noop&amp;value0-0-0=" target="_blank">Task/Bug Tracker</a><br/>
&#160;<a href="{$yarep.back2realm}testing.html">Testing/Continuous Integration</a><br/>
&#160;Acknowledgements<br/>
<br/>
<b>Professional Services</b><br/>
&#160;Support<br/>
&#160;Consulting<br/>
&#160;Training<br/>
&#160;Solutions<br/>
</td>

<td>&#160;&#160;</td>

<td valign="top">
  <xsl:copy-of select="/xhtml:html/xhtml:body/*"/>
</td>
</tr>
</table>
  <p>
    Copyright &#169; 2006 <a href="http://www.wyona.com">Wyona</a> - Powered by <a href="http://yanel.wyona.org">Wyona Yanel</a>, <a href="https://svn.wyona.org/repos/public/tomcat-cluster/">Wyona Balancer</a>, <a href="http://tomcat.apache.org">Apache Tomcat</a>, <a href="http://httpd.apache.org">Apache HTTP Server</a>
  </p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
