Yanel is is encoding/decoding the slash using %2f for retrieving resource specific files, for example inside

src/realms/welcome-admin/yanel/resources/show-realms/src/java/org/wyona/yanel/impl/resources/showrealms/ShowRealms.java
src/webapp/src/java/org/wyona/yanel/servlet/YanelServlet.java

For security reasons Tomcat 6.0.10 and higher does not allow encoded slashes

http://tomcat.apache.org/security-6.html#Fixed%20in%20Apache%20Tomcat%206.0.10

and hence does not forward such URLs to Yanel, but rather sends back directly a 400 response to the client/browser.

As a workaround one can allow the encoding of the slash by setting

JAVA_OPTS="$JAVA_OPTS -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"

inside bin/catalina.sh
