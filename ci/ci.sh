git config --global user.email "nao20010128@gmail.com"
git config --global user.name "nao20010128nao"
git submodule update --init --recursive
cd wisecraft-i18n
git pull origin master > NUL
git checkout master > NUL
cd ../statusesLayout
git pull origin master > NUL
git checkout master > NUL
cd ../MaterialIcons
git pull origin master > NUL
git checkout master > NUL
cd ../calligraphy
git pull origin master > NUL
git checkout master > NUL
cd ../psts
git pull origin master > NUL
git checkout master > NUL
cd ../colorPicker
git pull origin master > NUL
git checkout master > NUL
cd ../
  