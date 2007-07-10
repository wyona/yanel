<?xml version="1.0" encoding="UTF-8" ?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:wiki="http://www.wyona.org/yanel/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                >
<!--
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                >
-->
  
  <xsl:param name="yanel.path.name" select="'YANEL_PATH_NAME_IS_NULL'"/>
  <xsl:param name="yanel.path" select="'YANEL_PATH_IS_NULL'"/>
  
  <xsl:variable name="name-without-suffix" select="substring-before($yanel.path.name, '.')"/>
  
  <!-- TODO: Lenya stuff which needs to be replaced ... -->
  <xsl:param name="area" select="'AREA_IS_NULL'"/>
  <xsl:param name="publication" select="'PUBLICATION_IS_NULL'"/>
  <xsl:param name="contextprefix" select="'CONTEXT_PREFIX_IS_NULL'"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>A Wiki page</title>
          <link rel="neutron-introspection" href="introspection-{$yanel.path.name}.xml" type="application/neutron+xml"/>
<!--
        <xsl:choose>
          <xsl:when test="string-length($name-without-suffix) > 0">
            <link rel="neutron-introspection" href="introspection-{$name-without-suffix}.xml" type="application/neutron+xml"/>
          </xsl:when>
          <xsl:otherwise>
            <link rel="neutron-introspection" href="introspection-{$yanel.path.name}.xml" type="application/neutron+xml"/>
          </xsl:otherwise>
        </xsl:choose>
-->
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="wiki:wiki">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="wiki:WikiBody">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="wiki:Paragraph">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>
  
  <xsl:template match="wiki:Title">
    <h1>
      <xsl:apply-templates/>
    </h1>
  </xsl:template>
  
  <xsl:template match="wiki:MainTitle">
    <h2>
      <xsl:apply-templates/>
    </h2>
  </xsl:template>
  
  <xsl:template match="wiki:MainMainTitle">
    <h3>
      <xsl:apply-templates/>
    </h3>
  </xsl:template>
  
  <xsl:template match="wiki:Bold">
    <b>
      <xsl:apply-templates/>
    </b>
  </xsl:template>
  
  <xsl:template match="wiki:Italic">
    <i>
      <xsl:apply-templates/>
    </i>
  </xsl:template>
  
  <xsl:template match="wiki:Underline">
    <u>
      <xsl:apply-templates/>
    </u>
  </xsl:template>
  
  <xsl:template match="wiki:ForceNewline">
    <br/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="wiki:BList">
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
 
  <xsl:template match="wiki:BListItem">
    <xsl:param name="depth" select="number(@depth)" />
    <xsl:choose>
      <xsl:when test="$depth &lt;= 1">
        <li><xsl:apply-templates /></li>
      </xsl:when>
      <xsl:otherwise>
        <ul>
          <xsl:apply-templates select=".">
            <xsl:with-param name="depth" select="$depth - 1" />
          </xsl:apply-templates>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  
  <xsl:template match="wiki:NList">
    <ol>
      <xsl:apply-templates/>
    </ol>
  </xsl:template>
  
  <xsl:template match="wiki:NListItem">
    <xsl:param name="depth" select="@depth" />
    <xsl:choose>
      <xsl:when test="$depth &lt;= 1">
        <li><xsl:apply-templates /></li>
      </xsl:when>
      <xsl:otherwise>
        <ol>
          <xsl:apply-templates select=".">
            <xsl:with-param name="depth" select="$depth - 1" />
          </xsl:apply-templates>
        </ol>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="wiki:Table">
    <table style="border:1px #000000 solid; border-collapse: collapse;">
      <xsl:for-each select="wiki:TableRow">
        <tr>
          <xsl:for-each select="wiki:TableCol">
            <td style="border:1px #000000 solid; padding: 2px"><xsl:apply-templates/></td>
          </xsl:for-each>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
  
  <xsl:template match="wiki:Hrule">
    <hr/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="wiki:Link">
    <xsl:choose>
      <xsl:when test="@exist = 'false'">
        <u>
        <xsl:choose>
          <xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="@href"/></xsl:otherwise>
        </xsl:choose>
        </u><a href="?yanel.usecase=create&amp;create.name={@href}">?</a>
<!--
        </u><a href="?yanel.usecase=create&amp;create.name={@href}&amp;resource.type=&lt;{http://www.wyona.org/yanel/resource/1.0}wiki/>">?</a><xsl:text>&#0160;</xsl:text>
-->
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="@label">
            <a href="{@href}"><xsl:value-of select="@label"/></a>
          </xsl:when>
          <xsl:otherwise>
            <a href="{@href}"><xsl:value-of select="@href"/></a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="wiki:Text">
    <xsl:value-of select="@value"/>
  </xsl:template>
  
  <xsl:template match="wiki:PlainText">
    <xsl:value-of select="@value"/>
  </xsl:template>
  
  <xsl:template match="wiki:Plain">
    <pre>
      <xsl:apply-templates/>
    </pre>
  </xsl:template>
  
  <xsl:template match="wiki:Emoticons">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="node()[starts-with(local-name(),'em_')]">
    <xsl:call-template name="emoticon">
      <xsl:with-param name="icon_name" select="local-name()"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="emoticon">
    <xsl:param name="icon_name" select="none.png"/>
    <img alt="{$icon_name}" src="{$contextprefix}/{$publication}/{$area}/{$icon_name}.png?lenya.module=wiki"/>
  </xsl:template>
  
</xsl:stylesheet>
