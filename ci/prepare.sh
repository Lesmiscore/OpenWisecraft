#!/bin/bash
if [[ $CI_BUILD_STAGE == main* ]]
then
    apt-get install --yes tree zip unzip p7zip p7zip-full > /dev/null
	./ci/git.sh 2> /dev/null > /dev/null
	export POW_OF_2=false
	java -version
fi
