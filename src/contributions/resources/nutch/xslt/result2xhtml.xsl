<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

<xsl:output method="xhtml"/>
<!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
<!--
<xsl:output method="html"/>
-->

<xsl:template match="/">
<xhtml:html>

<xhtml:head>
  <xhtml:title>Results for query: <xsl:value-of select="/nutch/query"/></xhtml:title>
</xhtml:head>

<xhtml:body>
<xhtml:form>
<xhtml:p>
  <xhtml:input type="text" name="query"/>
  <xhtml:input type="submit" value="Search"/>
</xhtml:p>
</xhtml:form>

<xhtml:p>
<xhtml:b>Results:</xhtml:b>
  <xsl:apply-templates/>
</xhtml:p>
</xhtml:body>
</xhtml:html>
</xsl:template>

</xsl:stylesheet>
