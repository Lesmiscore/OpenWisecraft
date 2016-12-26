wget --quiet --output-document=android-sdk.tgz https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz
tar --extract --gzip --file=android-sdk.tgz
echo y | android-sdk-linux/tools/android --silent update sdk --no-ui --all --filter platform-tools,tools,build-tools-25.0.0,android-25,extra-android-m2repository,extra-google-m2repository
wget --quiet --output-document=gradle.zip https://services.gradle.org/distributions/gradle-2.14.1-bin.zip
unzip -q gradle.zip
export ANDROID_HOME=$PWD/android-sdk-linux
