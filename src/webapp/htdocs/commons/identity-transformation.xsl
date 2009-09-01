<?xml version="1.0" encoding="UTF-8"?>
<!--
XSLT identity transformation:
 handy as a starting point for decorating stylesheets.

 IMPORTANT: It has to be imported and not included,
  so use it for example like this:
  <xsl:import href="yanelhtdocs:/commons/Id.xslt"/>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>


<xsl:template match="@*|node()" priority="-1">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>


</xsl:stylesheet>
