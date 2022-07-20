#!/usr/bin/env sh

export BUILD_RELEASE=true

gradlew bundleRelease clean --info --stacktrace
