FROM tomcat

COPY conf/*.xml /usr/local/tomcat/conf/

RUN apt-get update && apt-get install unattended-upgrades apt-listchanges -y

COPY target/*.war /usr/local/tomcat/webapps/