#!/bin/bash
if [ "$CI_BUILD_STAGE" == "main" ]
then
	apt-get --quiet update --yes > /dev/null
	apt-get --quiet upgrade --yes > /dev/null
	apt-get --quiet install --yes --force-yes wget tar unzip lib32stdc++6 lib32z1 gzip tree > /dev/null
	./ci/git.sh 2> /dev/null > /dev/null
	source ./ci/sdk.sh > /dev/null
	export POW_OF_2=false
	java -version
fi
