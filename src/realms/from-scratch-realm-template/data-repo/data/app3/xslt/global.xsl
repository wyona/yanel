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
	<script type="text/javascript" src="{$yarep.back2realm}app3/js/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="{$yarep.back2realm}app3/js/jquery.galleryScroll.1.5.2.js"></script>
	<script type="text/javascript" src="{$yarep.back2realm}app3/js/carusel.js"></script>
	<!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="{$yarep.back2realm}app3/css/ie6.css" media="screen"/><![endif]-->
</head>
<body>
	<div id="wrapper">
		<div id="main" class="home">
			<div class="main-t"></div>
			<div class="main-c">
				<div class="main-content">
					<div class="gallery">
						<div class="g1">
							<ul>
								<li>
									<a href="http://www.nzz.ch"><img src="{$yarep.back2realm}app3/images/img-2.jpg" alt="" width="788" height="202" /></a>
									<div class="gallery-text">
										<div class="holder">
											<strong class="logo-gallery">globus savoir vivre</strong>
											<a href="#" class="more">Globus.ch driving offline business <br /> through online channel <img src="{$yarep.back2realm}app3/images/arrow-6.gif" alt="" width="26" height="26" /></a>
										</div>
									</div>
								</li>
								<li>
									<a href="http://www.wyona.com"><img src="{$yarep.back2realm}app3/images/img-2.jpg" alt="" width="788" height="202" /></a>
								</li>
								<li>
									<a href="http://www.wyona.com"><img src="{$yarep.back2realm}app3/images/img-2.jpg" alt="" width="788" height="202" /></a>
								</li>
								<li>
									<a href="http://www.wyona.com"><img src="{$yarep.back2realm}app3/images/img-2.jpg" alt="" width="788" height="202" /></a>
								</li>
								<li>
									<a href="http://www.wyona.com"><img src="{$yarep.back2realm}app3/images/img-2.jpg" alt="" width="788" height="202" /></a>
								</li>
							</ul>
						</div>
						<a href="#" class="link-prev">prev</a>
						<a href="#" class="link-next">next</a>
						<div class="swicher">
							
						</div>
					</div>
					<div id="tree-columns">
						<div class="content-box">
							<div class="container">
								<h1>Latest News and Opinions</h1>
								<ul class="content-nav">									<li><a href="#">Wyona launches Yanel 2.1 and implements it for Tamedia<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></li>									<li><a href="#">Yanel Online Business Development Engine wins Venture 2010 award<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></li>									<li><a href="#">Why selecting a CMS is more than prioritizing features and functions<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></li>									<li><a href="#">Wyona implements Yanel Online Classifieds Platform for car4you<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></li>									<li><a href="#">Wyona with 240% growth in 2010<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></li>								</ul>
							</div>
							<div class="quotes-box">
								<h2>Quotes</h2>
								<blockquote>
									<div>
										<div class="holder">
											<div class="frame">
												<q>“Driving business online is all about qualified traffic and conversion”</q>
											</div>
										</div>
										<cite>
											<img src="{$yarep.back2realm}app3/images/img-3.jpg" alt="" width="69" height="69" />
											<span>Andy Brügger, <br />Globus</span>
										</cite>
									</div>
								</blockquote>
								<blockquote>
									<div>
										<div class="holder">
											<div class="frame">
												<q>“Lorem ipsum dolor et samet massa maretima”</q>
											</div>
										</div>
										<cite>
											<img src="{$yarep.back2realm}app3/images/img-4.jpg" alt="" width="69" height="69" />
											<span>David Zimmerli, <br />NAZ</span>
										</cite>
									</div>
								</blockquote>
							</div>
						</div>
						<div class="side-columns">
							<h2>What would you like to do?</h2>
							<ul class="user-action">								<li><img src="{$yarep.back2realm}app3/images/ico-source.gif" alt="" width="38" height="38" /><p><a href="#">I need advise around Open Source<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></p></li>								<li><img src="{$yarep.back2realm}app3/images/ico-support.gif" alt="" width="38" height="38" /><p><a href="#">I need technical support around Yanel or other Open Source technologies<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></p></li>								<li><img src="{$yarep.back2realm}app3/images/ico-integrator.gif" alt="" width="38" height="38" /><p><a href="#">I am looking for a System Integrator to help with my web application<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></p></li>								<li><img src="{$yarep.back2realm}app3/images/ico-cms.gif" alt="" width="38" height="38" /><p><a href="#">I am searching for a new Content Management System<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></p></li>								<li><img src="{$yarep.back2realm}app3/images/ico-channel.gif" alt="" width="38" height="38" /><p><a href="#">I want to improve the efficiency of my online channel and look for help<img src="{$yarep.back2realm}app3/images/arrow-7.gif" alt="" width="15" height="9" /></a></p></li>
							</ul>
						</div>					</div>
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
				<ul id="nav">					<li><a href="#">Products</a></li>					<li><a href="#">Services</a></li>
					<li><a href="#">Solutions</a></li>					<li><a href="#">Impact</a></li>					<li><a href="#">Blog/News</a></li>					<li><a href="#">Resources</a></li>					<li><a href="#">About us</a></li>				</ul>
			</div>
		</div>		<div id="footer">
			<ul class="footer-nav">				<li><a href="#">Site Map</a></li>				<li><a href="#">Impressum/Legal</a></li>				<li><a href="#">Privacy Policy</a></li>
				<li class="contact">Contact: <address>Wyona AG, Hardstrasse 219, CH-8005 Zurich, +41 44 272 91 61</address></li>			</ul>
		</div>	</div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
