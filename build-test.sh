#!/bin/bash
# This mustn't be used for CIs
./gradlew safeDeleteDevXml :app:assembleDebug2 :app:assemblePg && adb install -r -t -f app/build/outputs/apk/app-pg.apk
