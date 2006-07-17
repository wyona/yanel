<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:output method="xhtml"/>
<!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
<!--
<xsl:output method="html"/>
-->

<xsl:template match="/">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <title>Collection</title>
</head>

<body>
Hello World!
</body>
</html>
</xsl:template>
</xsl:stylesheet>
