<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:yanel-xsl="http://www.wyona.org/yanel/xsl/1.0"
 xmlns:xhtml="http://www.w3.org/1999/xhtml"
>

<xsl:param name="GA-key" select="'UA-xxxxxx-x'"/>


<xsl:variable name="non-asset-URL-suffix" select="'.html'"/>


<xsl:template name="yanel-xsl:is-asset-URL">
  <xsl:param name="URL"/>
  <xsl:choose>
    <xsl:when test="starts-with($URL, 'http://') or starts-with($URL, 'https://')">
      <!-- do not track assets on full-qualified (presumably other than the current) domains -->
      <!--TODO(?) check the realm URL in case someone wants to use the full URL? -->
    </xsl:when>
    <!--FIXME HACK: find a better method to differentiate document assets from pages: -->
    <xsl:when test="substring($URL, string-length($URL) - string-length($non-asset-URL-suffix)) != $non-asset-URL-suffix"><!--ends-with($URL, $non-asset-URL-suffix)...-->
      <xsl:text>yes</xsl:text>
    </xsl:when>
  </xsl:choose>
</xsl:template>


<xsl:template match="xhtml:a">
  <xsl:param name="URL" select="@href"/>
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
<!--TODO?(performance) move that into a separate JS file: -->
function Yanel_requestURIFromFQURL(HTMLAelement) {
  var FQURL = HTMLAelement.href<!--alert('FQURL: ' + FQURL);-->;
  var i = FQURL.indexOf('://');<!--alert('i: ' + i);-->
  i = FQURL.indexOf('/', i + 3);<!--alert('i: ' + i);-->
  var filename = FQURL.substring(i);<!--alert('filename: ' + filename); return false;-->
  return filename;
}
</xsl:text></script>
</xsl:template>


<xsl:template name="yanel-xsl:put-GA-asset-onclick-handler">
  <xsl:param name="URL"/>
  <xsl:copy>
    <xsl:apply-templates select="@*[name()!='onclick']"/>
    <xsl:attribute name="onclick">
      <xsl:text>pageTracker._trackPageview(<!----></xsl:text>
      <xsl:call-template name="yanel-xsl:GA-asset-filename-JSexpr-from-URL">
        <xsl:with-param name="URL" select="$URL"/>
      </xsl:call-template>
      <xsl:text>)<!---->;</xsl:text>
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
