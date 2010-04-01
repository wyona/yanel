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

  <xsl:template match="/">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

        <!-- See http://www.w3.org/TR/REC-CSS2/media.html -->
        <link media="screen" type="text/css" href="{$yarep.back2realm}app1/css/screen.css" rel="stylesheet"/>
        <link media="print" type="text/css" href="{$yarep.back2realm}app1/css/print.css" rel="stylesheet"/>

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

        <!-- JQuery inclusion -->
        <script type="text/javascript" src="{$yarep.back2realm}app1/js/jquery/1.2.6/jquery.min.js"></script>
        <!-- Loading the script for the edit button -->
        <script type="text/javascript">
            $(document).ready( function()
            {
                $("#yanelToolbarZone").hover( function()
                {
                    $("#yanelToolbarZoneLink").stop().animate({opacity: 1}, 1000, function()
                    {
                        $("#yanelToolbarZoneLink").fadeIn();
                    });
                }
                , function ()
                {
                    $("#yanelToolbarZoneLink").stop().fadeOut();
                });
            });
        </script>
<style>
#yanelToolbarZone {
/* Netscape 4, IE 4.x-5.0/Win and other lesser browsers will use this */
  position: absolute;
	right: 0;
	top: 0;
	height: 60px;
	width: 60px;
	z-index: 1000;
}

body > div#yanelToolbarZone {
/* used by Opera 5+, Netscape6+/Mozilla, Konqueror, Safari, OmniWeb 4.5+, iCab, ICEbrowser */
  position: fixed;
}

</style>
      </head>
      <body>
<div id="yanelToolbarZone">
  <div id="yanelToolbarZoneLink" style="display: none; height: 60px; width: 60px;">
    <a href="?yanel.toolbar=on" title="Login" alt="Login"><img style="position: absolute; right:5px; top:5px;" src="{$yarep.back2realm}app1/images/yulup.gif" height="92" width="230" border="0"/></a>
  </div>
</div>

<xsl:comment>
DEBUG:
Localization: <xsl:value-of select="$language"/>
Content Language: <xsl:value-of select="$content-language"/>
</xsl:comment>
        <div id="page">


          <div id="header">
           <a href="{$yarep.back2realm}index.html"><img src="{$yarep.back2realm}app1/images/yanel-logo.png" alt="yanel logo" id="header-logo"/></a>
<!--
           <img src="{$yarep.back2realm}app1/images/logo.gif"/>
-->
<!--
            <xi:include href="yanelresource:/navigation/breadcrumb-{$content-language}.xml?path={$yanel.path}"/>
-->

            <!--
            <div id="breadcrumb">
              <a href="{$yarep.back2realm}index.html">Home</a>
            </div>
            -->

            <div id="contact-translations">
              <xsl:choose>
                <!-- Use content language instead localization -->
                <xsl:when test="$content-language = 'de'">
                  <a href="{$yarep.back2realm}de/kontakt.html">Kontakt</a>
                </xsl:when>
                <xsl:otherwise>
                  <a href="{$yarep.back2realm}en/contact.html">Contact</a>
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
                  <form action="{$yarep.back2realm}de/suche.html" method="GET"><input type="text" name="q"/><input type="submit" value="Suche"/></form>
                </xsl:when>
                <xsl:otherwise>
                  <form action="{$yarep.back2realm}en/search.html" method="GET"><input type="text" name="q"/><input type="submit" value="Search"/></form>
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
              Copyright &#169; 2009 - 2010 Wyona &#160;&#160;&#160;&#160;&#160;&#160;<a href="?yanel.toolbar=on" rel="nofollow">Toolbar</a>
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
