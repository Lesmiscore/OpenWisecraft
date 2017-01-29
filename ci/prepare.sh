#!/bin/bash
echo $CI_BUILD_STAGE
echo $CI_BUILD_NAME
if [[ ${CI_BUILD_STAGE:-} != postBuild ]]
then
	echo "Preparing for build..."
    apt-get install --yes tree zip unzip p7zip p7zip-full > /dev/null
	./ci/git.sh
	if [[ ${CI_BUILD_NAME:-} == *Split* ]]
	then
		export SPLIT_APK=true
		echo "Split APK build is enabled"
	else
		export SPLIT_APK=false
		echo "Split APK build is disabled"
	fi
	if [[ ${CI_BUILD_NAME:-} == *Jack* ]]
	then
		export JACK_BUILD=true
		echo "Jack build is enabled"
	else
		export JACK_BUILD=false
		echo "Jack build is disabled"
	fi
	export POW_OF_2=false
	export USE_TOR=false
	java -version
fi
