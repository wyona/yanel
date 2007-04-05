<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:yanel="http://www.wyona.org/yanel/1.0"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>
  
  <xsl:param name="yanel.back2context" select="''" />
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Add Realm From Scratch</title>
      </head>
      <body>
      <h1>Add Realm From Scratch</h1>
      <a href="?yanel.resource.viewid=xml">Show XML</a>
      <form>
        <table>
        <xsl:apply-templates select="/yanel:add-realm/yanel:from-scratch/yanel:parameter"/>
        <tr><td colspan="2" align="right"><input type="submit" name="from-scratch" value="Add Realm"/></td></tr>
        </table>
      </form>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="yanel:parameter">
    <tr><td><xsl:value-of select="@yanel:name"/></td><td><input type="text" name="{@yanel:name}"/></td></tr>
    <tr><td>&#160;</td><td align="right">(i.e. <xsl:value-of select="@yanel:sample-value"/>)</td></tr>
  </xsl:template>
   
</xsl:stylesheet>
