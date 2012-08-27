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

      <h1>Welcome to Yanel</h1>

      <p>
        If you can see this, it means that the installation of Yanel
        on this system was successful. Thanks for using Yanel!
      </p>

      <p>

        <strong>Important note:</strong> Certain links below are protected for
        security reasons. In order to login use the username <em>lenya</em> and
        the password <em>levi</em>.  If you are running Yanel within a publicly
        accessible environment, then please make sure to at least
        <a href="yanel/users/lenya.html">change the password of the user
        <em>lenya</em></a>. More information on how to further
        <a href="yanel-website/en/documentation/security/overview.html">protect
        this realm</a> or even
        <a href="yanel-website/en/documentation/how-to-add-a-new-realm.html">remove
        it</a> completely can be found within the Yanel
        <a href="yanel-website/en/documentation/index.html">documentation</a>.

      </p>

      <h2>Getting Started</h2>
      <p>
        <ul>
          <li>
            <a href="yanel-website/en/documentation/index.html">
              Documentation
            </a>
            <ul>
              <li>
                <a href="yanel-website/en/documentation/what-is-a-realm.html">
                  What is a realm?
                </a>
              </li>
              <li>
                <a href="yanel-website/en/documentation/how-to-add-a-new-realm.html">
                  How to add/configure a new realm
                </a>
              </li>
              <li>
                <a href="yanel-website/en/documentation/import-existing-website-as-new-realm.html">
                  Manually import an existing website as realm
                </a>
              </li>
            </ul>
          </li>
        </ul>
        <ul>
          <li>
            Administration
            <ul>
              <li>
                <a href="add-realm-from-scratch.html">Create a new realm from
                scratch</a>
              </li>
              <li>
                <a href="add-realm-from-existing-website.html">Import an
                existing website as a realm</a>
              </li>
              <li>
                <a href="reload-realms.html">Reload all realm 
                configurations</a>
              </li>
              <li>
                <a href="?yanel.toolbar=on">Turn on the Yanel toolbar</a> to
                administrate this realm.
              </li>
              <li>
                Look at the <a href="yanel/session-manager.html">session
                manager</a> to check how many sessions are currently active.
              </li>
            </ul>
          </li>
        </ul>

        <ul>
          <li>
            Other
            <ul>
              <li><a href="update-webapp.html">Find updates of Yanel</a></li>
              <li><a href="resources.html">Show registered resource types</a></li>
            </ul>
          </li>
        </ul>
      </p>
      <h2>Registered Realms within this Yanel instance</h2>
      <xsl:apply-templates select="/yanel-info/realms"/>

      <br/>
      <hr/>
      <a href="?yanel.resource.viewid=xml">Source XML</a>
    </body>
  </html>
</xsl:template>

<xsl:template match="realms">
  <p>
    The realms listed below are registered inside the configuration file:
    <code><xsl:value-of select="@config"/></code>
  </p>
  <ul>
    <xsl:apply-templates select="realm"/>
  </ul>
</xsl:template>

<xsl:template match="realm">
  <li>
    <a href=".{mountpoint}">
      <xsl:value-of select="name"/>
    </a>
  </li>
</xsl:template>

</xsl:stylesheet>
