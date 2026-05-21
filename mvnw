#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# Required ENV vars:
#   JAVA_HOME - location of a JDK home dir
# Optional ENV vars:
#   MAVEN_OPTS    - parameters passed to the Java VM when running Maven
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ]; then
  if [ -f /usr/local/etc/mavenrc ]; then
    . /usr/local/etc/mavenrc
  fi
  if [ -f /etc/mavenrc ]; then
    . /etc/mavenrc
  fi
  if [ -f "$HOME/.mavenrc" ]; then
    . "$HOME/.mavenrc"
  fi
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
mingw=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*)  mingw=true  ;;
  Darwin*) darwin=true ;;
esac

## Path to the script
PRG=$(readlink -f "$0" 2>/dev/null || ls -ld "$0" | awk '{print $NF}' | sed "s|$0||")
PRG="${PRG:-$0}"
PRGDIR=$(dirname "$PRG")
BASEDIR=$(cd "$PRGDIR" > /dev/null && pwd)

## Locate the maven installation
if [ -n "$M2_HOME" ]; then
  [ "$M2_HOME" = "$(cd "$M2_HOME" > /dev/null && pwd)" ] || echo "WARNING: M2_HOME is set but does not exist: $M2_HOME"
fi

if [ -z "$JAVA_HOME" ]; then
  javaExecutable="$(which java 2>/dev/null)"
  if [ -n "$javaExecutable" ] && ! [ "$(expr "$javaExecutable" : '\([^ ]*\)')" = "no" ]; then
    JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$javaExecutable")")")"
    export JAVA_HOME
  fi
fi

WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$BASEDIR/.mvn/wrapper/maven-wrapper.properties"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if [ -r "$WRAPPER_PROPERTIES" ]; then
  . "$WRAPPER_PROPERTIES"
fi

if [ -n "$MVNW_REPOURL" ]; then
  DISTRIBUTION_URL="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  DOWNLOAD_URL="$DISTRIBUTION_URL"
fi

if [ ! -e "$WRAPPER_JAR" ]; then
  echo "Downloading $WRAPPER_JAR from $DOWNLOAD_URL ..."
  if command -v wget > /dev/null; then
    wget "$DOWNLOAD_URL" -O "$WRAPPER_JAR" || rm -f "$WRAPPER_JAR"
  elif command -v curl > /dev/null; then
    curl -o "$WRAPPER_JAR" "$DOWNLOAD_URL" -f -L || rm -f "$WRAPPER_JAR"
  fi
fi

if [ "$cygwin" = "true" ] || [ "$mingw" = "true" ]; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --unix "$JAVA_HOME")
  [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --unix "$CLASSPATH")
fi

if [ "$cygwin" = "true" ] || [ "$mingw" = "true" ]; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --windows "$JAVA_HOME")
  [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
fi

MAVEN_OPTS="${MAVEN_OPTS:--Xmx512m}"

exec "$JAVA_HOME/bin/java" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$BASEDIR" \
  $WRAPPER_LAUNCHER "$@"
