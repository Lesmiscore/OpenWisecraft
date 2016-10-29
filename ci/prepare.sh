apt-get --quiet update --yes > /dev/null
apt-get --quiet upgrade --yes > /dev/null
apt-get --quiet install --yes --force-yes wget tar unzip lib32stdc++6 lib32z1 gzip tree > /dev/null
chmod a+x ./ci/*
./ci/git.sh 2> /dev/null
./ci/sdk.sh > /dev/null
export POW_OF_2=false
java -version
