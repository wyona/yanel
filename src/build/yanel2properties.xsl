<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:yanel="http://www.wyona.org/yanel/1.0" 
  xmlns="http://www.wyona.org/yanel/1.0"
  >
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:param name="servlet.context.prefix" select="'NULL'"/>
  <xsl:param name="yanel.revision" select="'NULL'"/>
  <xsl:param name="yanel.version" select="'NULL'"/>
  
  <xsl:template match="/">
    <yanel>
      <xsl:comment> NOTE: This file has been generated automatically from conf/yanel.xml!</xsl:comment>
      <version version="{$yanel.version}" revision="{$yanel.revision}"/>
      <xsl:copy-of select="/yanel:yanel/yanel:realms-config"/>
      <xsl:copy-of select="/yanel:yanel/yanel:resource-types-config"/>
    </yanel>
  </xsl:template>

</xsl:stylesheet>
