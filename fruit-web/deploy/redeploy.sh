#!/bin/bash
bindir=`dirname $0`

service suisui stop
rm -rf /opt/tomcat/8000.suisui/webapps/lili
cp -rf ${bindir}/../target/lili.war /opt/tomcat/8000.suisui/webapps/
service suisui start

#jenkins:bash -ex deploy.sh
