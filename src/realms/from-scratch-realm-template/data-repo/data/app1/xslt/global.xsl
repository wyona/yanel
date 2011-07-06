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
  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.toolbar-status" select="'TOOLBAR-STATUS_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVED-PREFIX_IS_NULL'"/>
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  <xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
  <xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
  <xsl:param name="languages" select="'LANGUAGES_IS_NULL'"/>
  <xsl:param name="is-mobile-device" select="'IS_MOBILE_IS_NULL'"/>

  <xsl:template match="/">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

        <link rel="shortcut icon" href="{$yanel.back2realm}favicon.ico" type="image/vnd.microsoft.icon" />
        <!-- See http://www.w3.org/TR/REC-CSS2/media.html -->
        <link media="screen" type="text/css" href="{$yanel.back2realm}app1/css/screen.css" rel="stylesheet"/>
        <link media="print" type="text/css" href="{$yanel.back2realm}app1/css/print.css" rel="stylesheet"/>

        <!-- The following copy statement is copying for example header stuff from the content source, but also in the case of the TinyMCE or Xinha resource the important javascript and CSS links! -->
        <xsl:copy-of select="/xhtml:html/xhtml:head/*[name(.) != 'title']"/>
        <xsl:choose>
          <xsl:when test="/xhtml:html/xhtml:head/xhtml:link/@rel='neutron-introspection'">
            <!-- NOTE: Use the one from the copy above in case one exists! -->
          </xsl:when>
          <xsl:otherwise>
            <!-- TODO: Maybe one should better not cut-off the suffix! -->
            <link rel="neutron-introspection" type="application/neutron+xml" href="?yanel.resource.usecase=introspection"/>
<!--
            <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-{$name-without-suffix}.xml"/>
-->
          </xsl:otherwise>
        </xsl:choose>
        <title>
          <xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/>
        </title>

        <!-- Yanel toolbar zone -->
        <script type="text/javascript" src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-js/jquery/1.2.6/jquery.min.js"></script>
        <script type="text/javascript" src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-js/toolbar-zone.js"></script>
        <link media="screen" type="text/css" href="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-css/toolbar-zone.css" rel="stylesheet"/>

      </head>
      <body>
<xsl:if test="$yanel.toolbar-status = 'off'">
<div id="yanelToolbarZone">
  <div id="yanelToolbarZoneLink" style="display: none; height: 60px; width: 60px;">
    <a href="?yanel.toolbar=on" title="Turn on Toolbar" alt="Turn on Toolbar"><img style="position: absolute; right:5px; top:5px;" src="{$yanel.back2realm}{$yanel.reservedPrefix}/yanel-img/yanel_kangaroo.png" height="48" width="109" border="0"/></a>
  </div>
</div>
</xsl:if>

<xsl:comment>
DEBUG:
Is mobile device: <xsl:value-of select="$is-mobile-device"/>
Localization: <xsl:value-of select="$language"/>
Toolbar Status: <xsl:value-of select="$yanel.toolbar-status"/>
Content Language: <xsl:value-of select="$content-language"/>
</xsl:comment>
        <div id="page">


          <div id="header">
           <a href="{$yanel.back2realm}index.html"><img src="{$yanel.back2realm}app1/images/yanel-logo.png" alt="yanel logo" id="header-logo"/></a>
<!--
           <img src="{$yanel.back2realm}app1/images/logo.gif"/>
-->
<!--
            <xi:include href="yanelresource:/navigation/breadcrumb-{$content-language}.xml?path={$yanel.path}"/>
-->

            <!--
            <div id="breadcrumb">
              <a href="{$yanel.back2realm}index.html">Home</a>
            </div>
            -->

            <div id="contact-translations">
              <xsl:choose>
                <!-- Use content language instead localization -->
                <xsl:when test="$content-language = 'de'">
                  <a href="{$yanel.back2realm}de/kontakt.html">Kontakt</a>
                </xsl:when>
                <xsl:otherwise>
                  <a href="{$yanel.back2realm}en/contact.html">Contact</a>
                </xsl:otherwise>
              </xsl:choose>

              <!-- Use content language instead localization -->
              <xi:include href="yanelresource:/navigation/translations.xml?path={$yanel.path}&amp;language={$content-language}"/>

<!--
              <a href="?yanel.toolbar=on">Toolbar</a>
-->
              Localization: <xsl:value-of select="$language"/>
              <div>
                <xsl:choose>
                <xsl:when test="$content-language = 'de'">
                  <form action="{$yanel.back2realm}de/suche.html" method="GET"><input type="text" name="q"/><input type="submit" value="Suche"/></form>
                </xsl:when>
                <xsl:otherwise>
                  <form action="{$yanel.back2realm}en/search.html" method="GET"><input type="text" name="q"/><input type="submit" value="Search"/></form>
                </xsl:otherwise>
              </xsl:choose>
              </div>
            </div>
          </div>
          <!-- END of header -->


          <xsl:call-template name="navi"/>

          <div id="home">
            <div id="body" style="width: 440px;">
              <xsl:apply-templates select="/xhtml:html/xhtml:body/*"/>
            </div>

            <div id="footer">
              Copyright &#169; 2009 - 2011 Wyona &#160;&#160;&#160;&#160;&#160;&#160;<a href="?yanel.toolbar=on" rel="nofollow">Toolbar</a>
            </div>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template name="navi">
    <div id="left">
      <!-- Use content language instead localization -->
      <xi:include href="yanelresource:/navigation/menu-{$content-language}.xml?path={$yanel.path}"/>
    </div>
  </xsl:template>


  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
