FROM openjdk:21
#https://hub.docker.com/_/openjdk
#https://hub.docker.com/_/tomcat
#https://www.middlewareinventory.com/blog/docker-tomcat-example-dockerfile-sample/
#https://spring.io/blog/2018/11/08/spring-boot-in-a-container
MAINTAINER wyona.com

VOLUME /tmp

# TLS 1.0 and 1.1 (TLSv1, TLSv1.1) enabled in order to be able to connect to IMAP servers which do not support more recent protocols, whereas see property jdk.tls.disabledAlgorithms inside file java.security
# For more details see src/main/webapp/email-integration.html
COPY java.security /usr/java/openjdk-21/conf/security/.

RUN mkdir /opt/tomcat/
WORKDIR /opt/tomcat
RUN curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.104/bin/apache-tomcat-9.0.104.tar.gz
#RUN curl -O https://dlcdn.apache.org/tomcat/tomcat-11/v11.0.6/bin/apache-tomcat-11.0.6.tar.gz
RUN tar xvfz apache*.tar.gz
RUN mv apache-tomcat-9.0.104/* /opt/tomcat/.
#RUN mv apache-tomcat-11.0.6/* /opt/tomcat/.
RUN chmod -R 775 /opt/tomcat

COPY build/webapps/yanel /opt/tomcat/webapps/yanel
#RUN rm -rf /opt/tomcat/webapps/ROOT
#COPY build/webapps/yanel /opt/tomcat/webapps/ROOT

EXPOSE 8080
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
