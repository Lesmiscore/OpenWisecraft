#!/bin/bash
echo $CI_BUILD_STAGE
echo $CI_BUILD_NAME
if [[ ${CI_BUILD_STAGE:-} != postBuild ]]
then
	echo "Preparing for build..."
    apt-get install --yes tree zip unzip p7zip p7zip-full > /dev/null
	./ci/git.sh
	export POW_OF_2=false
	if [[ ${CI_BUILD_NAME:-} == *Split ]]
	then
		export SPLIT_APK=true
	else
		export SPLIT_APK=false
	fi
	java -version
fi
