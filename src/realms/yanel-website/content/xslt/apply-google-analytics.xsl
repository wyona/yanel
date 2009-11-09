<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:import href="yanelhtdocs:/Google-Analytics/GA.xslt"/>

<!-- Overwrites GA-key within yanelhtdocs:/Google-Analytics/GA.xslt which is included by page-GA.xslt -->
<xsl:variable name="GA-configuration" select="document('yanelrepo:/google-analytics-key-config.xml')/configuration/Google-Analytics"/>
<xsl:param name="GA-key" select="$GA-configuration/@key"/>

</xsl:transform>
