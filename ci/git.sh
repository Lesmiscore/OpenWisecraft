git config --global user.email "nao20010128@gmail.com"
git config --global user.name "nao20010128nao"
git submodule update --init --recursive
cd wisecraft-i18n
git pull origin master > /dev/null
git checkout master > /dev/null
cd ../statusesLayout
git pull origin master > /dev/null
git checkout master > /dev/null
cd ../MaterialIcons
git pull origin master > /dev/null
git checkout master > /dev/null
cd ../calligraphy
git pull origin master > /dev/null
git checkout master > /dev/null
cd ../psts
git pull origin master > /dev/null
git checkout master > /dev/null
cd ../colorPicker
git pull origin master > /dev/null
git checkout master > /dev/null
cd ../