<?xml version="1.0"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml" 
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns:xi="http://www.w3.org/2001/XInclude" 
  xmlns:nazws="http://naz.ch/2007/welcomescreen" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:s="http://www.wyona.org/security/1.0"
  >
  
  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yanel.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="language" select="'en'"/>
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Group Overview of <xsl:value-of select="/security-api/s:group/@id"/></title>
      </head>
      <body>
        <h1>Group Overview of <xsl:value-of select="/security-api/s:group/@id"/></h1>
      </body>
    </html>
  </xsl:template>
  
</xsl:stylesheet>
