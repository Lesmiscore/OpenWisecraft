zip -r autogen.zip ./*/build/generated/source/
mkdir autogentmp
unzip -d autogentmp/ autogen.zip
cd autogentmp
7z a autogen.7z .
mv autogen.7z ..
cd ..
rm -rf autogentmp
