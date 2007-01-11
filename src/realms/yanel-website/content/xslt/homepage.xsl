<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
  xmlns="http://www.w3.org/1999/xhtml"
>

<xsl:output method="xhtml" encoding="UTF-8" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
  <title>Welcome to Yanel</title>
  <link rel="neutron-introspection" type="application/neutron+xml" href="introspection-homepage.xml"/>
  <link rel="stylesheet" href="css/global.css" type="text/css"/>
  <style>
#download-download {
  color: #000;
  font-weight: bold; 
  font-size: 14px;
}
#download-yanel-version {
  color: #fff;
  font-weight: bold; 
  font-size: 14px;
}
  </style>
</head>
<body>
<xsl:copy-of select="/xhtml:html/xhtml:body"/>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
