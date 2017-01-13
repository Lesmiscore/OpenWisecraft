zip -r autogen-$CI_BUILD_STAGE.zip ./*/build/generated/source/
mkdir autogentmp
unzip -d autogentmp/ autogen-$CI_BUILD_STAGE.zip
cd autogentmp
7z a autogen-$CI_BUILD_STAGE.7z .
mv autogen-$CI_BUILD_STAGE.7z ..
cd ..
rm -rf autogentmp
