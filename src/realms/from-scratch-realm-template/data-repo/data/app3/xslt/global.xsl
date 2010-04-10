<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="xhtml dc">

  <xsl:import href="header.xsl"/>
  <xsl:import href="footer.xsl"/>

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yanel.toolbar-status" select="'TOOLBAR-STATUS_IS_NULL'"/>
  <xsl:param name="yanel.reservedPrefix" select="'RESERVED-PREFIX_IS_NULL'"/>
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  <xsl:param name="language" select="'LANGUAGE_IS_NULL'"/>
  <xsl:param name="content-language" select="'CONTENT_LANGUAGE_IS_NULL'"/>
  <xsl:param name="languages" select="'LANGUAGES_IS_NULL'"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><xsl:value-of select="/xhtml:html/xhtml:head/xhtml:title"/></title>
	<link rel="stylesheet" type="text/css" href="{$yarep.back2realm}app3/css/all.css" media="all" />
	<!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="{$yarep.back2realm}app3/css/ie6.css" media="screen"/><![endif]-->

        <!-- Yanel toolbar zone -->
        <script type="text/javascript" src="{$yarep.back2realm}{$yanel.reservedPrefix}/yanel-js/jquery/1.2.6/jquery.min.js"></script>
        <script type="text/javascript" src="{$yarep.back2realm}{$yanel.reservedPrefix}/yanel-js/toolbar-zone.js"></script>
        <link media="screen" type="text/css" href="{$yarep.back2realm}{$yanel.reservedPrefix}/yanel-css/toolbar-zone.css" rel="stylesheet"/>
</head>
<body>
<xsl:if test="$yanel.toolbar-status = 'off'"><div id="yanelToolbarZone">
  <div id="yanelToolbarZoneLink" style="display: none; height: 60px; width: 60px;">
    <a href="?yanel.toolbar=on" title="Turn on Toolbar" alt="Turn on Toolbar"><img style="position: absolute; right:5px; top:5px;" src="{$yarep.back2realm}{$yanel.reservedPrefix}/yanel-img/yanel_kangaroo.png" height="48" width="109" border="0"/></a>
  </div>
</div>
</xsl:if>

	<div id="wrapper">
		<div id="main">
			<div class="main-t"></div>
			<div class="main-c">
				<div id="two-columns">
					<div id="content">
<xsl:apply-templates select="/xhtml:html/xhtml:body/*"/>

<!-- TODO: Where does 'back' link to?
						<div class="page-info">
							<a href="#" class="back">back</a>
							<a href="#" class="article">reccomend article</a>
							<a href="#" class="print">print</a>
						</div>
-->
					</div>

  <!-- START: Related information -->
  <div class="aside">
    <h2>More information</h2> <!-- TODO: i18n -->
      <ul class="aside-nav">
        <xsl:copy-of select="/xhtml:html/xhtml:body/xhtml:div[@id='related']/xhtml:ul/xhtml:li"/>
      </ul>
  </div>
  <!-- END: Related information -->
</div>



<!-- START: Left hand side navigation -->
<div id="sidebar">
					<ul class="accordion">
						<li class="active"><a href="#">Yanel</a>
							<ul>
								<li><a href="#">What it is</a></li>
								<li><a href="#">What our stake is</a></li>
								<li><a href="#">What we do with it</a></li>
								<li><a href="#">Further information</a></li>
							</ul>
						</li>
						<li><a href="#">Yanel Editions</a></li>
						<li><a href="#">Yulup</a></li>
						<li><a href="#">Coocoon/Portal</a></li>
						<li><a href="#">Lucene/Nutch</a></li>
						<li><a href="#">Forrest</a></li>
						<li><a href="#">JSR170/Jackrabbit</a></li>
					</ul>
				</div>
			</div>
			<div class="main-b"></div>
			
		</div>


<xsl:call-template name="header"/>
<xsl:call-template name="footer"/>

</div>
</body>
</html>
</xsl:template>

<xsl:template match="xhtml:div[@id='related']">
<!--
Ignore, because this info will be displayed within right column
-->
</xsl:template>

<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
