<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  >
  <xsl:output method="html"/>
  
  <xsl:param name="testing.result.title" select="'current test'"/>
  <xsl:param name="testing.number.requested.tests"/>
  
  <xsl:variable name="testsuite.list" select="//testsuite"/>
  <xsl:variable name="testcase.list" select="$testsuite.list/testcase"/>
  <xsl:variable name="testcase.error.list" select="$testcase.list/error"/>
  <xsl:variable name="testcase.failure.list" select="$testcase.list/failure"/>
  
  <xsl:template match="/">
        <div id="contenBody">
          <h1 id="{$testing.result.title}">
          <xsl:choose>
            <xsl:when test="$testing.result.title = 'stillTesting'">
              Tests are running...
            </xsl:when>
            <xsl:when test="$testing.result.title = 'testDone'">
              Tests are done.
            </xsl:when>
            <xsl:otherwise>
              Testing results of <xsl:value-of select="$testing.result.title"/>
            </xsl:otherwise>
          </xsl:choose>
          </h1>
          <p>Executed Testcases: <xsl:value-of select="count($testcase.list)"/>
            <xsl:if test="$testing.result.title = 'stillTesting'">
              of <xsl:value-of select="$testing.number.requested.tests"/>
            </xsl:if>
          </p>
          <p>Successful: <xsl:value-of select="count($testcase.list) - count($testcase.failure.list) - count($testcase.error.list)"/>,
          Failures: <xsl:value-of select="count($testcase.failure.list)"/>,
          Errors: <xsl:value-of select="count($testcase.error.list)"/></p>
          <hr/>
          <xsl:apply-templates select="//testsuite"/>
    </div>
  </xsl:template>

  <xsl:template match="testsuite">
    <h3>Name: <xsl:value-of select="@name"/></h3>
    <xsl:choose>
      <xsl:when test="testcase[failure]">
        <h4 style="color:orange">
          Failure occurred!
        </h4>
      </xsl:when>
      <xsl:when test="testcase[error]">
        <h4 style="color:red">
          Error occurred!
        </h4>
      </xsl:when>
      <xsl:otherwise>
        <h4 style="color:green">
          Test Successful!
        </h4>
      </xsl:otherwise>
    </xsl:choose>
    
    <p>Errors:<xsl:value-of select="@errors"/>, Failures:<xsl:value-of select="@failures"/>,
      Execution Time: <xsl:value-of select="@time"/>
    </p>
    <p>Packagesname: <xsl:value-of select="@package"/></p> 
    <p></p>
    <xsl:apply-templates select="testcase"/>
    <hr/>
  </xsl:template>

  <xsl:template match="testcase[failure]">
    <p>Message: <xsl:value-of select="failure/@message"/></p>
    <p>Type: <xsl:value-of select="failure/@type"/></p>
    Failure: <xsl:value-of select="."/>
    <xsl:apply-templates select="../system-out"/>
    <xsl:apply-templates select="../system-err"/>
  </xsl:template>
  
  <xsl:template match="testcase[error]">
    <p>Message: <xsl:value-of select="error/@message"/></p>
    <p>Type: <xsl:value-of select="error/@type"/></p>
    Failure: <xsl:value-of select="."/>
    <xsl:apply-templates select="../system-out"/>
    <xsl:apply-templates select="../system-err"/>
  </xsl:template>
  
  <xsl:template match="system-out">
    <p>System-out: 
    <xsl:value-of select="."/>
    </p>
  </xsl:template>
  
  <xsl:template match="system-err">
    <p>System-error: 
    <xsl:value-of select="."/>
    </p>
  </xsl:template>
  
</xsl:stylesheet>

