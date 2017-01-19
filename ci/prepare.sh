#!/bin/bash
echo $CI_BUILD_STAGE
echo $CI_BUILD_NAME
if [[ $CI_BUILD_NAME == main* ]]
then
	echo "Preparing for build..."
    apt-get install --yes tree zip unzip p7zip p7zip-full > /dev/null
	./ci/git.sh
	export POW_OF_2=false
	java -version
fi
