#!/bin/bash
# -*- coding: utf-8 -*-

yes | sdkmanager "platforms;android-28"
gradle -b wrapper.gradle wrapper
chmod +x ./gradlew
./gradlew bintrayUpload
