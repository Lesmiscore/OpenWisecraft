#!/bin/bash
while : ; do
  [ ! -e Wisecraft/.git ] || ERROR && rm -rf Wisecraft
  [ ! -e Wisecraft ] && git clone $WISECRAFT_ENDPOINT > build.log 2>&1
  cd Wisecraft
  [ -e ../build.log ] && cp ../build.log .
  git pull >> build.log 2>&1 || { ERROR=1; continue; }
  git checkout $BRANCH >> build.log 2>&1 || { ERROR=1; continue; }
  chmod a+x ci/*
  ./ci/git.sh >> build.log 2>&1
  break
done
