<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:msg="http://www.wyona.org/yanel/contact-message/1.0.0"
>

  <xsl:output method="xhtml" encoding="UTF-8"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Message</title>
      </head>
      
      <body>
        <h3>Contact Message: <xsl:value-of select="/msg:message/@uuid"/></h3>

        Company: <xsl:value-of select="/msg:message/msg:company"/>
<br/>
        Firstname: <xsl:value-of select="/msg:message/msg:firstname"/>
<br/>
        Lastname: <xsl:value-of select="/msg:message/msg:lastname"/>
<br/>
        Address: <xsl:value-of select="/msg:message/msg:address"/>
<br/>
        City: <xsl:value-of select="/msg:message/msg:city"/>
<br/>
        E-Mail: <xsl:value-of select="/msg:message/msg:e-mail"/>
<br/><br/>
        Message:<p><xsl:value-of select="/msg:message/msg:body"/></p>
<br/>
<!-- TODO: Use back2realm -->
        User Profile (Clickstream, etc.): <a href="../my-profile.html?cookie-id={/msg:message/@yanel-analytics-cookie}"><xsl:value-of select="/msg:message/@yanel-analytics-cookie"/></a>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
