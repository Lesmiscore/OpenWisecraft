zip -r autogen-$CI_BUILD_NAME.zip ./*/build/generated/source/
mkdir autogentmp
unzip -d autogentmp/ autogen-$CI_BUILD_NAME.zip
cd autogentmp
7z a autogen-$CI_BUILD_NAME.7z .
mv autogen-$CI_BUILD_NAME.7z ..
cd ..
rm -rf autogentmp
