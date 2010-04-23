<?xml version="1.0"?>

<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:yanel-xsl="http://www.wyona.org/yanel/xsl/1.0"
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:import href="../commons/identity-transformation.xsl"/>

<!-- NOTE: Overwrite the GA-key parameter, for example by including this XSLT within another XSLT -->
<xsl:param name="GA-key" select="'UA-xxxxxx-x'"/>

<xsl:variable name="non-asset-URL-suffix" select="'.html'"/>

<xsl:template match="xhtml:body">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <xsl:call-template name="yanel-xsl:insert-GA-code"/>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>

<xsl:template name="yanel-xsl:is-asset-URL">
  <xsl:param name="URL"/>
  <xsl:variable name="url_without_qs" select="substring-before($URL, '?')"/>
  <xsl:choose>
    <!-- do not track assets on full-qualified (presumably other than the current) domains -->
    <!--TODO(?) check the realm URL in case someone wants to use the full URL? -->
<!--
    <xsl:when test="starts-with($URL, 'http://') or starts-with($URL, 'https://')">
      <xsl:text>no</xsl:text>
    </xsl:when>
-->

    <!-- INFO: Check if URL ends with forward slash (Equals to expression: ends-with($URL, '/')) -->
    <xsl:when test="substring($url_without_qs, string-length($url_without_qs)) = '/'">
      <xsl:text>no</xsl:text>
    </xsl:when>

    <!-- NOTE: At the moment the suffix is only compared with .html (see $non-asset-URL-suffix) -->
    <!-- At the moment the following cases are not checked: .htm, foo-bar/, foo-bar -->
    <!--FIXME HACK: find a better method to differentiate document assets from pages: -->
    <xsl:when test="substring($url_without_qs, 1 + string-length($url_without_qs) - string-length($non-asset-URL-suffix)) != $non-asset-URL-suffix"><!-- Equals to expression: not(ends-with($url_without_qs, $non-asset-URL-suffix)) -->
      <xsl:text>yes</xsl:text>
    </xsl:when>

    <xsl:otherwise>
      <xsl:text>no</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match="xhtml:a">
  <xsl:param name="URL" select="@href"/>

<!-- DEBUG: Output URL suffix
  <xsl:attribute name="suffix"><xsl:value-of select="substring($URL, string-length($URL) - string-length($non-asset-URL-suffix) + 1)"/></xsl:attribute>
-->

  <xsl:variable name="is-asset">
    <xsl:call-template name="yanel-xsl:is-asset-URL">
      <xsl:with-param name="URL" select="$URL"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$is-asset = 'yes'">
      <xsl:call-template name="yanel-xsl:put-GA-asset-onclick-handler">
        <xsl:with-param name="URL" select="$URL"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-imports/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="yanel-xsl:insert-GA-code">
  <xsl:call-template name="yanel-xsl:insert-new-GA-code"/>
</xsl:template>


<xsl:template name="yanel-xsl:insert-new-GA-code">
<script type="text/javascript"><xsl:text>
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</xsl:text></script>


<script type="text/javascript"><xsl:text>
  var pageTracker = _gat._getTracker("</xsl:text><xsl:value-of select="$GA-key"/><xsl:text>");
  pageTracker._trackPageview();

<!-- INFO: Remove http:// or https:// and hostname from asset URLs -->
<!--TODO?(performance) move that into a separate JS file: -->
function Yanel_requestURIFromFQURL(HTMLAelement) {
  var FQURL = HTMLAelement.href<!--alert('FQURL: ' + FQURL);-->;
  var i = FQURL.indexOf('://');<!--alert('i: ' + i);-->
  i = FQURL.indexOf('/', i + 3);<!--alert('i: ' + i);-->
  var filename = FQURL.substring(i);
<!-- DEBUG: In oder to see this alert, make sure to comment/uncomment the code line where this function Yanel_requestURIFromFQURL is used
  alert('DEBUG: filename: ' + filename); return false;
-->
  return filename;
}
</xsl:text></script>

</xsl:template>


<xsl:template name="yanel-xsl:put-GA-asset-onclick-handler">
  <xsl:param name="URL"/>

  <xsl:copy>
    <xsl:apply-templates select="@*[name() != 'onclick']"/>
    <xsl:attribute name="onclick">

      <!-- DEBUG: Uncomment the following call and comment the call with the pageTracker in order to debug the function Yanel_requestURIFromFQURL
      <xsl:call-template name="yanel-xsl:GA-asset-filename-JSexpr-from-URL">
        <xsl:with-param name="URL" select="$URL"/>
      </xsl:call-template>
      -->
      <xsl:text>javascript: pageTracker._trackPageview(</xsl:text>
      <xsl:call-template name="yanel-xsl:GA-asset-filename-JSexpr-from-URL">
        <xsl:with-param name="URL" select="$URL"/>
      </xsl:call-template>
      <xsl:text>);</xsl:text>

      <xsl:value-of select="@onclick"/>
    </xsl:attribute>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>


<xsl:template name="yanel-xsl:GA-asset-filename-JSexpr-from-URL">
  <xsl:param name="URL"/>
  <xsl:text>Yanel_requestURIFromFQURL(this<!--, '</xsl:text><xsl:value-of select="$URL"/><xsl:text>'-->)</xsl:text>
</xsl:template>


</xsl:transform>
