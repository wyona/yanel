<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml">
  
<xsl:output
  method="xhtml" encoding="UTF-8"
  doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/>
  
<xsl:template match="/">
  <html>
    <head>
      <base target="_top"/>

      <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8"/>
      <link rel="shortcut icon" href="favicon.ico" type="image/vnd.microsoft.icon" />
      <link rel="stylesheet" href="css/global.css" type="text/css"/>
      <title>Welcome to Yanel</title>
    </head>

    <body>
      <img src="img/yanel.png" alt="yanel" id="yanellogo"/>
      <img src="img/everything_is_content.png"
           alt="everything is content" id="everythingiscontent"/>

      <h1>Registered resource types</h1>
      <br/>

      <p>
        Here is a list of all resource types which have been registered inside
        this Yanel instance and can be used/configured inside realms.
      </p>

      <table>
      <xsl:for-each select="/yanel-info/resourcetypes/resourcetype">
        <tr>
          <td style="width:1%;">
            <img style="float:left;width:32px;height:32px;" src="{icon}" alt="{./icon/@alt}"/>
          </td>
          <td style="width:50%;">
            <strong><xsl:value-of select="localname"/></strong><br/>
            <xsl:value-of select="namespace"/>
          </td>
          <td style="width:50%;">
            <xsl:value-of select="description"/>
          </td>
          <td style="width:1%;">
            <a href="{./docu}">Docs</a>
          </td>
        </tr>
      </xsl:for-each>
      </table>

      <br/>
      <hr/>
      <a href="?yanel.resource.viewid=xml">Source XML</a>
    </body>
  </html>
</xsl:template>

<xsl:template match="resourcetypes">
  <ul>
    <xsl:apply-templates select="resourcetype"/>
  </ul>
</xsl:template>

<xsl:template match="resourcetype">
  <li>
    &lt;{<xsl:value-of select="./namespace"/>}<strong><xsl:value-of select="./localname"/></strong>/&gt;&#160;
    (<xsl:value-of select="./description"/>)&#160;
  </li>
</xsl:template>

</xsl:stylesheet>
