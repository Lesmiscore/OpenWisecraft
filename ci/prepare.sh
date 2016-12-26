#!/bin/bash
if [ "$CI_BUILD_STAGE" == "main" ]
then
	./ci/git.sh 2> /dev/null > /dev/null
	export POW_OF_2=false
	java -version
fi
