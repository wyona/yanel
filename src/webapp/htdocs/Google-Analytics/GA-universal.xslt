<?xml version="1.0"?>

<!-- INFO: Asynchronous Tracking Code (https://support.google.com/analytics/bin/answer.py?hl=en&hlrm=de&utm_id=ad&answer=1008080) -->

<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:yanel-xsl="http://www.wyona.org/yanel/xsl/1.0"
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:import href="../commons/identity-transformation.xsl"/>

<!-- NOTE: Overwrite the GA-key parameter, for example by including this XSLT within another XSLT -->
<xsl:param name="GA-key" select="'UA-xxxxxx-x'"/>

<xsl:variable name="non-asset-URL-suffix" select="'.html'"/>

<xsl:template match="xhtml:head">
  <xsl:copy>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates select="node()"/>
    <xsl:call-template name="yanel-xsl:insert-GA-code"/>
  </xsl:copy>
</xsl:template>



<!-- INFO: Check if URL corresponds to an asset -->
<xsl:template name="yanel-xsl:is-asset-URL">
  <xsl:param name="URL"/>

  <!-- INFO: Remove query string from URL -->
  <xsl:variable name="url_without_qs"><xsl:choose><xsl:when test="contains($URL, '?')"><xsl:value-of select="substring-before($URL, '?')"/></xsl:when><xsl:otherwise><xsl:value-of select="$URL"/></xsl:otherwise></xsl:choose></xsl:variable>

  <xsl:choose>
    <!-- INFO: Do not track assets on full-qualified (presumably other than the current/local) domains -->
    <!--TODO: Check the realm URL in case someone wants to use the full URL (also locally)? -->
<!-- INFO: Commented, because in many cases it makes sense to track full-qualified domains!
    <xsl:when test="starts-with($URL, 'http://') or starts-with($URL, 'https://')">
      <xsl:text>no</xsl:text>
    </xsl:when>
-->

    <!-- INFO: Check if URL ends with forward slash (Equals to expression: ends-with($URL, '/')) -->
    <xsl:when test="substring($url_without_qs, string-length($url_without_qs)) = '/'">
      <xsl:text>no</xsl:text>
    </xsl:when>


    <!-- NOTE: Check if an URL does NOT end with .html and hence assume it is an asset. At the moment the suffix is only compared with .html (see $non-asset-URL-suffix) and hence .htm and .php is handled exceptionally -->
    <!-- At the moment the following cases are not checked: .htm, foo-bar/, foo-bar -->
    <!-- TODO: find a better method to differentiate document assets (e.g. pdf, doc) and regular pages! -->
    <!-- NOTE: As a workaround we just hardcode all other suffixes which shall be excluded! -->
    <xsl:when test="(substring($url_without_qs, 1 + string-length($url_without_qs) - string-length($non-asset-URL-suffix)) != $non-asset-URL-suffix) and (substring($url_without_qs, 1 + string-length($url_without_qs) - string-length('.htm')) != '.htm') and (substring($url_without_qs, 1 + string-length($url_without_qs) - string-length('.php')) != '.php')  and (substring($url_without_qs, 1 + string-length($url_without_qs) - string-length('#')) != '#')">
      <!-- Equals to expression: not(ends-with($url_without_qs, $non-asset-URL-suffix)) -->
      <xsl:text>yes</xsl:text>
    </xsl:when>

    <xsl:otherwise>
      <xsl:text>no</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>



<!-- INFO: Add onclick if URL corresponds to an asset -->
<xsl:template match="*[local-name()='a']">
<!-- TODO: It seems that all links inserted with TinyMCE are loosing their namespace and hence the below does not match
<xsl:template match="xhtml:a">
-->
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

<script type="text/javascript">
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', '<xsl:value-of select="$GA-key"/>', 'auto');
  ga('send', 'pageview');
</script>
</xsl:template>


<xsl:template name="yanel-xsl:put-GA-asset-onclick-handler">
  <xsl:param name="URL"/>

  <xsl:copy>
    <xsl:apply-templates select="@*[name() != 'onclick']"/>
    <xsl:attribute name="onclick">_gaq.push(['_trackEvent', 'File', 'Download', '<xsl:value-of select="$URL"/>']);<xsl:value-of select="@onclick"/></xsl:attribute>
    <xsl:apply-templates select="node()"/>
  </xsl:copy>
</xsl:template>


<!-- TBD: Is still necessary?! -->
<xsl:template name="yanel-xsl:GA-asset-filename-JSexpr-from-URL">
  <xsl:param name="URL"/>
  <xsl:text>Yanel_requestURIFromFQURL(this<!--, '</xsl:text><xsl:value-of select="$URL"/><xsl:text>'-->)</xsl:text>
</xsl:template>


</xsl:transform>
