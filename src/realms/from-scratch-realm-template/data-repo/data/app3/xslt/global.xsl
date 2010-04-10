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
	<title>Wyona</title>
	<link rel="stylesheet" type="text/css" href="{$yarep.back2realm}app3/css/all.css" media="all" />
	<!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="{$yarep.back2realm}app3/css/ie6.css" media="screen"/><![endif]-->
</head>
<body>
	<div id="wrapper">
		<div id="main">
			<div class="main-t"></div>
			<div class="main-c">
				<div id="two-columns">
					<div id="content">
						<h1>Yanel Online Classifieds<br /> Edition</h1>
						<p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. </p>
						<div class="img-box">
							<div class="holder">								<div class="frame">
									<a href="#"><img src="{$yarep.back2realm}app3/images/img-1.jpg" alt="" width="310" height="300" /></a>
								</div>							</div>
						</div>
						<p>Stet clita kasd gubergren, no sea akimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</p>
						<div class="page-info">
							<a href="#" class="back">back</a>
							<a href="#" class="article">reccomend article</a>
							<a href="#" class="print">print</a>
						</div>
					</div>
					<div class="aside">
						<h2>More information</h2>
						<ul class="aside-nav">
							<li><a href="#">Yanel Online Classifieds Edition Product Sheet</a></li>
							<li><a href="#">White Paper „The future of Online Media“</a></li>
							<li><a href="#">Online Media Solutions</a></li>
						</ul>
					</div>
				</div>
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
			
		</div>		<div id="header">
			<div class="add-nav-box">
				<form action="#" class="header-forms">
					<fieldset>
						<div class="text">
							<input type="text" value="" />
						</div>
						<input type="submit" value="Search" class="submit" />
					</fieldset>
				</form>
				<ul class="add-nav">					<li><a href="#">Contact</a></li>					<li><a href="#">Register</a></li>
					<li><a href="#">Extranet</a></li>					<li><a href="#">RSS</a></li>					<li><a href="#">Follow us</a></li>					<li><a href="#">DE</a></li>				</ul>
			</div>
			<div class="header-box">
					<strong class="logo">
						<a href="#">wyona</a>
					</strong>
					<ul id="nav">						<li><a href="#">Products</a></li>						<li><a href="#">Services</a></li>
						<li><a href="#">Solutions</a></li>						<li><a href="#">Impact</a></li>						<li><a href="#">Blog/News</a></li>						<li><a href="#">Resources</a></li>						<li><a href="#">About us</a></li>					</ul>
			</div>
		</div>		<div id="footer">
			<ul class="footer-nav">				<li><a href="#">Site Map</a></li>				<li><a href="#">Impressum/Legal</a></li>				<li><a href="#">Privacy Policy</a></li>
				<li class="contact">Contact: <address>Wyona AG, Hardstrasse 219, CH-8005 Zurich, +41 44 272 91 61</address></li>			</ul>
		</div>	</div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
