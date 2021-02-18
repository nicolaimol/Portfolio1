#! /bin/bash

cd src

javac com/nicolai/Client.java
java com/nicolai/Client localhost 8081 $1
