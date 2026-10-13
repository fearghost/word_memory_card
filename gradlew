#!/bin/bash
# This is a corrected minimal wrapper script for the Gradle wrapper.

# Get the directory of the script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Execute java with the wrapper jar in the classpath
exec java -cp "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
