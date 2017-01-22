#!/bin/bash
echo $CI_BUILD_STAGE
echo $CI_BUILD_NAME
if [[ ${CI_BUILD_STAGE:-} != postBuild ]]
then
	echo "Preparing for build..."
    apt-get install --yes tree zip unzip p7zip p7zip-full tor > /dev/null
	./ci/git.sh
	export POW_OF_2=false
	if [[ ${CI_BUILD_NAME:-} == *Split ]]
	then
		export SPLIT_APK=true
		echo "Split APK build is enabled"
	else
		export SPLIT_APK=false
		echo "Split APK build is disabled"
	fi
	export USE_TOR=false
	java -version
fi
