#!/bin/sh

#start the mock server
java -Dfile.encoding=UTF-8 -cp /libs/* -Dmockserver.propertyFile=/config/mockserver.properties org.mockserver.cli.Main

