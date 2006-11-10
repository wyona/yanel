<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
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

<xsl:param name="language" select="'en'"/>

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

      <!-- This is needed such that Microsoft Internet Explorer displays characters such as &nbsp; correctly (also see xsl:output, whereas I (Michi) am not sure if the encoding is really needed there) -->
      <meta content="application/xhtml+xml; charset=UTF-8" http-equiv="Content-Type"/>
      <meta name="generator" content="Wyona Yanel"/>

      <!-- http://dublincore.org/documents/2001/04/12/usageguide/simple-html.shtml -->
      <meta name="DC.Creator" content="TODO"/>
      <meta name="DC.Language" content="TODO"/>
      
      <!-- this is now the only stylesheet we need ;) -->
      <link rel="stylesheet" href="{$yarep.back2realm}css/base.css" type="text/css" />
    </head>

    
    <body>
      <div id="mainFrame">
        <div id="header">
          <!-- TODO: replace me with yanel logo ;) -->
          <img name="logo" src="{$yarep.back2realm}img/yanel.gif" border="0" alt="yanel.gif" width="100"/>
        </div>
        <div id="navigation">
          <b>All You Need</b><br/>
          <a href="{$yarep.back2realm}{$language}/about.html">About</a><br/>
          <a href="{$yarep.back2realm}download.html">Download</a><br/>
          <a href="{$yarep.back2realm}license.html">License</a><br/>
          <a href="{$yarep.back2realm}{$language}/getting-started.html">Getting Started</a><br/>
          Features<br/>
          News<br/>
          <a href="{$yarep.back2realm}documentation/index.html">Documentation</a><br/>
          <br/>
          <b>Development/Community</b><br/>
          <a href="{$yarep.back2realm}download/source-repository.html">Get the Source</a><br/>
          <a href="{$yarep.back2realm}mailing-lists.html">Mailing Lists</a><br/>
          <a href="http://bugzilla.wyona.com/cgi-bin/bugzilla/buglist.cgi?short_desc_type=allwordssubstr&amp;short_desc=&amp;product=Yanel&amp;long_desc_type=allwordssubstr&amp;long_desc=&amp;bug_file_loc_type=allwordssubstr&amp;bug_file_loc=&amp;bug_status=NEW&amp;bug_status=ASSIGNED&amp;bug_status=REOPENED&amp;emailassigned_to1=1&amp;emailtype1=substring&amp;email1=&amp;emailassigned_to2=1&amp;emailreporter2=1&amp;emailcc2=1&amp;emailtype2=substring&amp;email2=&amp;bugidtype=include&amp;bug_id=&amp;votes=&amp;changedin=&amp;chfieldfrom=&amp;chfieldto=Now&amp;chfieldvalue=&amp;cmdtype=doit&amp;namedcmd=Mobi&amp;newqueryname=&amp;order=Reuse+same+sort+as+last+time&amp;field0-0-0=noop&amp;type0-0-0=noop&amp;value0-0-0=" target="_blank">Task/Bug Tracker</a><br/>
          <a href="{$yarep.back2realm}testing.html">Testing/Continuous Integration</a><br/>
          Governance<br/>
          <a href="{$yarep.back2realm}principles.html">Principles</a><br/>
          Acknowledgements<br/>
          <br/>
          <b>Professional Services</b><br/>
          Support<br/>
          Consulting<br/>
          Training<br/>
          Solutions<br/>
          <br/>
          <b>Search</b><br/>
          <form action="{$yarep.back2realm}en/search.html" method="GET"><input type="text" name="query"/></form>
        </div>
        <div id="content">
          <xsl:copy-of select="/xhtml:html/xhtml:body/*"/>
        </div>
        <div id="footer_container">
          <div id="footer">
            <p>Copyright Wyona &#169; 2006</p>
          </div>
        </div>
      </div>
    </body>
  </html>
</xsl:template>
</xsl:stylesheet>
