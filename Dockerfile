FROM tomcat:8.0.20-jre8
COPY target/Java-Quiz-App*.war /usr/local/tomcat/webapps/Java-Quiz-App.war