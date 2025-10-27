#!/bin/bash

# Set working directory to the script's location
cd "$(dirname "$0")"

echo "Starting build process for WebAppQuiz..."

# Clean and build with Gradle wrapper
./gradlew clean build -x test

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build completed successfully!"
    echo "JAR file location: $(find ./build/libs -name '*.jar' | grep -v 'plain')"
else
    echo "Build failed. Please check the logs above for errors."
    exit 1
fi
