<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:s="http://www.oscom.org/2002/SlideML/0.9/"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:template match="/">
   <fo:root>
   
    <fo:layout-master-set>
     <fo:simple-page-master master-name="page"
                  page-height="29.7cm" 
                  page-width="21cm"
                  margin-top="1cm" 
                  margin-bottom="2cm" 
                  margin-left="2.5cm" 
                  margin-right="2.5cm">
     <!-- Landscape -->
<!--
     <fo:simple-page-master master-name="page"
                  page-height="21cm" 
                  page-width="29.7cm"
                  margin-top="1cm" 
                  margin-bottom="2cm" 
                  margin-left="2.5cm" 
                  margin-right="2.5cm">
-->
       <fo:region-body margin-top="3cm"/>
       <fo:region-before extent="3cm"/>

       
       <fo:region-after extent="1.5cm"/>
     </fo:simple-page-master>

     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
         <fo:conditional-page-master-reference master-reference="page" page-position="first"/>
         <fo:conditional-page-master-reference master-reference="page" odd-or-even="odd"/>
         <fo:conditional-page-master-reference master-reference="page" odd-or-even="even"/>
         <fo:conditional-page-master-reference master-reference="page" blank-or-not-blank="blank"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>



    <fo:page-sequence master-reference="all">
      <fo:static-content flow-name="xsl-region-before">
	    <fo:block text-align="left" font-size="10pt" font-family="serif" line-height="14pt">
        <!--<fo:external-graphic src="file:/home/michi/src/lenya-samples/slides/resources/images/live/wyona_klein.gif"/>
	
          <fo:external-graphic src="url('http://cocoon.apache.org/lenya/images/apache-lenya-light.png')"/>-->
	
        </fo:block>

      </fo:static-content>

      <fo:static-content flow-name="xsl-region-after">
	    <fo:block text-align="center" font-size="10pt" font-family="serif" line-height="14pt">
          <xsl:value-of select="/s:slideset/s:metadata/s:author/s:name"/> (<xsl:value-of select="/s:slideset/s:metadata/s:author/s:email"/>), <xsl:value-of select="/s:slideset/s:metadata/s:title"/>, <xsl:value-of select="/s:slideset/s:metadata/s:confgroup/s:conftitle"/>
        </fo:block>
	    <fo:block text-align="center" font-size="10pt" font-family="serif" line-height="14pt">
          Seite <fo:page-number/>

        </fo:block>
      </fo:static-content> 

      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates select="/s:slideset/s:metadata"/>
        <xsl:apply-templates select="/s:slideset/s:slide"/>
      </fo:flow>
    </fo:page-sequence>


   </fo:root>

  </xsl:template>




  <xsl:template match="s:metadata">
    <fo:block font-size="36pt" space-before.optimum="24pt" text-align="center">
      <xsl:apply-templates select="s:title"/>
    </fo:block>
    <fo:block text-align="center">
<!--
      <fo:external-graphic src="file:/home/michi/src/lenya-samples/slides/resources/images/live/cocoon.gif"/>
-->

    </fo:block>
    <fo:block font-size="18pt" space-before.optimum="24pt" text-align="center">
      <xsl:apply-templates select="s:author/s:name"/>
    </fo:block>
    <fo:block font-size="18pt" space-before.optimum="24pt" text-align="center">
      <xsl:apply-templates select="s:author/s:email"/>
    </fo:block>
    <fo:block text-align="center" space-before.optimum="24pt">
    </fo:block>

    <fo:block text-align="center">
<!-- NOTE: There seems to be a problem with JPEG -->
<!--
      <fo:external-graphic src="url('http://www.lenya.org/images/lenya-org.jpg')"/>
-->
<!--
      <fo:external-graphic src="file:/home/michi/src/lenya-samples/slides/resources/images/live/wyona_klein.gif"/>
-->
<!--
      <fo:external-graphic src="file:/home/michi/src/lenya-samples/slides/resources/images/live/apache_lenya.gif"/>
-->
<!--
      <fo:external-graphic src="file:/home/michiii/src/lenyacms/src/webapp/lenya/pubs/slides/resources/images/live/lenya_org.gif"/>
-->
    </fo:block>
  </xsl:template>



  <xsl:template match="s:slide">
    <fo:block break-before="page" font-size="36pt" space-before.optimum="24pt" text-align="start">

      <fo:inline text-decoration="none" color="#000000">
<!--
      <fo:inline text-decoration="none" color="#cc9933">
-->
        <xsl:apply-templates select="s:title"/>
      </fo:inline>
    </fo:block>

    <xsl:apply-templates select="s:content"/>
  </xsl:template>


  <xsl:template match="s:content">

    <xsl:apply-templates/>
  </xsl:template>

<!--
fo:block attributes:
http://www.dulug.duke.edu/~mark/docs/dtds/xml/w3c/xsl-fo/elements/fo%3Ablock.html
-->

  <xsl:template match="source">
    <fo:block linefeed-treatment="preserve" white-space-treatment="preserve" white-space-collapse="false" border-right-style="solid" border-left-style="solid" border-before-style="solid" border-after-style="solid" font-size="11pt">
    <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template match="ol">
    <fo:list-block>
      <xsl:for-each select="li">
        <fo:list-item space-before.optimum="40pt">
          <fo:list-item-label end-indent="label-end()">
            <fo:block font-size="18pt">&#x2022;</fo:block>
          </fo:list-item-label>
          <fo:list-item-body start-indent="body-start()">
            <fo:block font-size="18pt">
              <fo:inline text-decoration="none"><xsl:apply-templates/></fo:inline>
            </fo:block>
          </fo:list-item-body>
        </fo:list-item>
      </xsl:for-each>
    </fo:list-block>
  </xsl:template>

</xsl:stylesheet>
