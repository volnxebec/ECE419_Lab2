#!/bin/bash

ECE419_HOME=/cad2/ece419s/
JAVA_HOME=/usr/

if [ "$#" -ne 1 ]; then
    echo "Usage: ./server.sh  <port>"
    exit 1
fi

${JAVA_HOME}/bin/java Server $1 

