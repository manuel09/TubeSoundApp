#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Imposta JAVA_HOME se mancante
if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))
fi

# Trova comando Java
if [ -x "$JAVA_HOME/bin/java" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD=$(which java)
fi

# Set the location of the Gradle wrapper jar and properties file
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Find the real path of the script
WRAPPER_DIR=$(cd "$(dirname "$0")" && pwd)
exec "$JAVACMD" $JAVA_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
