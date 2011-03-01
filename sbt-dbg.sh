#!/bin/bash
export SBT_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
./sbt.sh
