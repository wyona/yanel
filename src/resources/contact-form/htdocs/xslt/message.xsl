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
        <h3>Contact Message</h3>

        Company: <xsl:value-of select="/msg:message/msg:company"/>
<!--
<message uuid="0e308c63-8abe-486a-a3be-df402979a833" yanel-analytics-cookie="YA-0b4c4553-63e4-401c-abe8-1544d5803513"><company>Hugo</company><firstname>Michi</firstname><lastname>Wechner</lastname><address>Test</address><city>Test</city><e-mail>michi@wyona.com</e-mail><body>Test öüä><&'éèà </body></message>
-->
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
