#!/bin/bash
mvn clean package
cp target/aston_crud-1.0.war /usr/local/Cellar/tomcat/10.1.25/libexec/webapps/
brew services stop tomcat
brew services start tomcat