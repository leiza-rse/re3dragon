FROM tomcat:9

ARG SERVICEJSON=""

COPY conf/*.xml /usr/local/tomcat/conf/

RUN apt-get update && apt-get install unattended-upgrades apt-listchanges -y

COPY target/re3dragon.war /tmp/ROOT.war

RUN unzip /tmp/ROOT.war -d /usr/local/tomcat/webapps/ROOT/

COPY public/re3dragon_logo.png /usr/local/tomcat/webapps/ROOT/re3dragon_logo.png

COPY public/index.html /usr/local/tomcat/webapps/ROOT/index.html

COPY public/service.json /usr/local/tomcat/webapps/ROOT/service.json
