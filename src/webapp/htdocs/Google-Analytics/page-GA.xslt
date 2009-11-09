<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:yanel-xsl="http://www.wyona.org/yanel/xsl/1.0"
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:import href="../commons/identity-transformation.xsl"/>
<xsl:include href="GA.xslt"/>

<xsl:template match="xhtml:body">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <xsl:call-template name="yanel-xsl:insert-GA-code"/>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>

</xsl:transform>
