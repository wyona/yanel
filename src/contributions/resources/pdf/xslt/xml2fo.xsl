<?xml version="1.0"?>

<!--
Also see http://www.xml.com/pub/a/2001/01/17/xsl-fo/index.html
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:output method="xml"/>

  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

      <fo:layout-master-set>
        <fo:simple-page-master master-name="simple"
              page-height  ="29.7cm"   
              page-width   ="21cm"
              margin-left  ="2.5cm"
              margin-right ="2.5cm">
          <fo:region-body name="xsl-region-body" margin-top="3cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="simple">
        <fo:flow flow-name="xsl-region-body">
          <!-- Also see http://www.w3schools.com/xslfo/xslfo_blocks.asp -->
          <fo:block border-style="solid" border-width="1pt">My first XSL-FO PDF. Some dynamic content can be found below:</fo:block>

          <xsl:apply-templates/>
        </fo:flow>
      </fo:page-sequence>

    </fo:root>
  </xsl:template>

  <xsl:template match="document">
    <fo:block margin="2cm">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>


  <xsl:template match="head">
    <fo:block>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="para">
    <fo:block>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="em">
    <fo:inline font-style="italic">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>

</xsl:stylesheet>
