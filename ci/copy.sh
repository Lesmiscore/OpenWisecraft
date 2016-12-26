#!/bin/sh
git clone https://github.com/RevealEverything/Files.git
cd Files
git remote add dest https://$GITLAB_USER:$GITLAB_PASS@gitlab.com/nao20010128nao/Wisecraft_Steals_2.git
git fetch dest
git push dest master
cd ..
git clone --mirror https://$GITLAB_USER:$GITLAB_PASS@gitlab.com/nao20010128nao/Wisecraft.git Wisecraft
cd Wisecraft
git remote add dest https://$GITHUB_USER:$GITHUB_PASS@github.com/nao20010128nao/Wisecraft-mirror
git fetch dest
git push -f --tags dest refs/heads/*:refs/heads/*
