#! /bin/bash

cd src

javac Client.java -d ../out/production/Portfolio1

cd ../out/production/Portfolio1
java Client localhost 8081 $1
