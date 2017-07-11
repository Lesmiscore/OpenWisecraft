#!/bin/sh

# Keep current directory
CURRENT_DIR=`pwd`

# Update packages list
apt update

# Install NPM and HTML Minifier
apt install -y npm nodejs-legacy
npm install html-minifier -g

# Install Google Closure compiler
apt install -y wget unzip
mkdir binaries
cd binaries
wget -q -O compiler.zip http://dl.google.com/closure-compiler/compiler-latest.zip
unzip compiler.zip
TMP=`ls -1 *.jar | head -n 1`
export CLOSURE_COMPILER_JAR=`realpath $TMP`
cd $CURRENT_DIR

# Minify files
cd app/src/main/assets/3dskin

html-minifier --html5 --minify-css true --minify-js true --remove-comments --collapse-whitespace -o index.min.html index.html
mv --force index.min.html index.html

java -jar $CLOSURE_COMPILER_JAR --js js/2dskin.js --js_output_file js/2dskin.min.js
mv --force js/2dskin.min.js js/2dskin.js

java -jar $CLOSURE_COMPILER_JAR --js js/webgl_check.js --js_output_file js/webgl_check.min.js
mv --force js/webgl_check.min.js js/webgl_check.js
