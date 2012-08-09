Configuring the Wiki Parser:
----------------------------

The Wiki Parser can be configured at 3 different places:

 - First it looks up the ".yanel-rti":
   wiki-syntax: jspWikiParser

 - Then the wiki resource will look up in the config directory:
   .../wiki/config/wikiParser.config

 - And last it will fallback to the hard-coded version within the Wiki java class


The key of a Wiki Parser can be found at
  .../wiki/src/build/spring-wiki-config.xml

<beans>
  <bean id="jspWikiParser" class="org.wyona.jspwiki.WikiParser"/>
  <bean id ..../>
</beans>
