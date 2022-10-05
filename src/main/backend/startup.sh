#!/bin/bash

if [ ! -n "$1" ] ;then
  echo "ERROR: JAR is not specified!"
  exit
fi

if [ ! -n "$2" ] ;then
  ACTIVE_TYPE="prod"
else
  ACTIVE_TYPE=$2
fi

java -Xms128M -Xmx512M -jar $1 --spring.profiles.active=$ACTIVE_TYPE --spring.config.location=classpath:/application.yml,./conf/application-$ACTIVE_TYPE.yml & 1>/dev/null 2>&1

echo $! > ./pid_basil
