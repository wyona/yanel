<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  
  <xsl:output method="html"/>
  
<!--
  <xsl:param name="servlet.context"/>
-->
  
  <xsl:template match="/">
    <html>
      <head>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
        <title>Welcome to Yanel</title>
        <!-- TODO: This page should be generated dynamically! -->
        <!--
        <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-homepage.xml"/>
        -->
      </head>
      <body>
        <h1>Welcome to Yanel</h1>
        <p> If you can see this, it means that the installation of Yanel
          software on this system was successful. Thanks for using Yanel!</p>
        <h2>Getting Started</h2>
        <p>
          <ul>
            <li>What is a realm?</li>
            <li>Create a realm from scratch.</li>
            <li>
              <a href="yanel-website/en/documentation/import-existing-website-as-new-realm.html">Import
                an existing website as a new realm.</a> (Work in progress: <a href="import-site.html">Do it!</a>)
            </li>
            <li>
              <a
                href="yanel-website/en/documentation/how-to-add-a-new-realm.html">Add
                an existing third-party realm.</a>
            </li>
            <li>More information can be found within the Yanel <a href="yanel-website/en/documentation/index.html">documentation</a>.</li>

<!--
            <li>What is a resource resp. resource type?</li>
            <li><a href="yanel-website/en/documentation/create-new-resource.html">Create a new resource type from scratch.</a></li>
            <li>Add an existing third-party resource type.</li>
-->

<!-- TODO: Move this into Yanel tool bar -->
<!--
            <li>Find Updates.</li>
-->
          </ul>
        </p>
        <h2>Registered Realms within this Yanel instance</h2>
        <xsl:apply-templates select="/yanel-info/realms"/>

        <h2>Registered Resources within this Yanel instance</h2>
        <xsl:apply-templates select="/yanel-info/resourcetypes"/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="realms">
    <ul>
      <xsl:apply-templates select="realm"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="realm">
    <li>
      <a href=".{mountpoint}">
<!--
        <xsl:attribute name="href">
          <xsl:value-of select="$servlet.context"/><xsl:value-of select="./mountpoint" />
        </xsl:attribute> 
-->
        <xsl:value-of select="./name"/>
      </a>
    </li>
  </xsl:template>
  
  <xsl:template match="resourcetypes">
    <ul>
      <xsl:apply-templates select="resourcetype"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="resourcetype">
    <li>
      <xsl:value-of select="./localname"/>
      (<xsl:value-of select="./description"/>)
    </li>
  </xsl:template>

</xsl:stylesheet>
