#! /bin/bash

cd src

javac Server.java -d ../out/production/Portfolio1

cd ../out/production/Portfolio1
java Server 8081
