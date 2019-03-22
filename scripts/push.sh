#!/bin/bash
# -*- coding: utf-8 -*-

yes | sdkmanager "platforms;android-28"
gradle -b wrapper.gradle wrapper
./gradlew bintrayUpload
