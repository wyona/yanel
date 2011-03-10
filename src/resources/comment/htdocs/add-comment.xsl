<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:dir="http://apache.org/cocoon/directory/2.0"
  xmlns:yanel="http://www.wyona.org/yanel/resource/directory/1.0"
  xmlns:i18n="http://www.wyona.org/yanel/i18n/1.0"
  >

  <xsl:output method="xhtml"/>

  <!-- NOTE: Must correspond with the mime-type delivered by the server. See src/java/org/wyona/yanel/impl/resources/DirectoryResource.java -->
  <!--
<xsl:output method="html"/>
-->

  <xsl:param name="yanel.path.name" select="'NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'PATH_IS_NULL'"/>
  <xsl:param name="yanel.back2context" select="'BACK2CONTEXT_IS_NULL'"/>
  <xsl:param name="yarep.back2realm" select="'BACK2REALM_IS_NULL'"/>
  <xsl:param name="yarep.parent" select="'YAREPPARENT_IS_NULL'"/>
  <xsl:param name="yanel.htdocs" select="'YANELHTDOCS_IS_NULL'"/>

  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Add comment</title>
      </head>
      <body>
        <xsl:apply-templates select="/exception"/>

        <h2>Add comment re page '<xsl:value-of select="/no-comment-yet/@path"/><xsl:value-of select="/comment/@path"/>'</h2>
        <xsl:apply-templates select="/no-comment-yet"/>
        <xsl:apply-templates select="/comment"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="exception">
    <div style="color: red;">EXCEPTION: <xsl:value-of select="."/></div>
  </xsl:template>

  <xsl:template match="no-comment-yet">
    <p>Please enter your comment below:</p>
    <form>
      <input type="hidden" name="path" value="{@path}"/>
      Title<br/>
      <input type="text" name="title"/>
      <br/><br/>
      Body<br/>
      <textarea cols="50" rows="10" name="body"/>
      <br/><br/>
      E-Mail (please note that your email will not be displayed to the public)<br/>
      <input type="text" name="email"/>
      <br/><br/>
      Name (optional, whereas please note that your name will not be displayed to the public)<br/>
      <input type="text" name="name"/>
      <br/><br/>
      <input type="submit" value="Add comment"/>
    </form>
  </xsl:template>

  <xsl:template match="comment">
    <p>The following comment has been added successfully:</p>
    <p><xsl:value-of select="."/></p>
    <p>Return to <a href="{$yarep.back2realm}{substring-after(@path, '/')}">page</a></p>
  </xsl:template>

</xsl:stylesheet>
