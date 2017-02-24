image="nao20010128nao/android-build:latest"
before_script=["chmod a+x ./ci/*", "source ./ci/prepare.sh"]
stages=["app","rcon","postBuild"]
def artf=[
      "*.apk",
      "dexdump*.txt",
      "build-*.log",
      "powof2.txt",
      "*/build/outputs/aar/*",
      "tree-*.log",
      "*/build/outputs/mapping/*/*.txt",
      "*/build/generated/source/*",
      "autogen-*.*"
]
def mainScript=[
      "gradle safeDeleteDevXml \$TASK --info --stacktrace | tee -a build-\$CI_BUILD_NAME.log > /dev/null",
      "mv */build/outputs/apk/*.apk .",
      "./ci/packageInformations.sh"
]
def preScript=[
      "DIR=`cat ./app/build.gradle | grep \"def preNum=\"`",
      "DIR=\${DIR% //*}",
      "DIR=\${DIR#*=}",
      "if [ \$DIR != \"0\" ]; then gradle safeDeleteDevXml \$TASK --info --stacktrace | tee -a build-\$CI_BUILD_NAME.log > /dev/null; mv */build/outputs/apk/*.apk . ;./ci/packageInformations.sh ; fi"
]
["","Split"].each{split->
    // app
    def app=["Debug","Debug2","Debug2TestObs","Pg","PgUlt","PgExpermental","Release","ShrinkRelease"]
    app.each{
        this["app$it$split"].script=mainScript
        this["app$it$split"].artifacts.paths=artf
        this["app$it$split"].stage="app"
        this["app$it$split"].variables.TASK=":app:assemble$it"
    }
    def pre=["","Shrink"]
    pre.each{
        this["app$it$split"].script=preScript
        this["app$it$split"].artifacts.paths=artf
        this["app$it$split"].stage="app"
        this["app$it$split"].variables.TASK=":app:assemble${it}Pre"
    }
    // rcon
    def rcon=["App","PassCrack"]
    rcon.each{
        this["rcon${it}All$split"].script=mainScript
        this["rcon${it}All$split"].artifacts.paths=artf
        this["rcon${it}All$split"].stage="rcon"
        this["rcon${it}All$split"].variables.TASK=":app:assemble$it"
    }
}
// postBuild
mainLast.artifacts.paths=artf
mainLast.stage="postBuild"
mainLast.script=[
      "unzip app-release.apk -d unzippedApkA > /dev/null",
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkA/classes.dex > dexdump-app.txt",
      "unzip rconApp-release.apk -d unzippedApkB > /dev/null",
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkB/classes.dex > dexdump-rcon.txt",
      "unzip rconPassCrack-release.apk -d unzippedApkC > /dev/null",
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkC/classes.dex > dexdump-rconPC.txt",
      "unzip app-pg.apk -d unzippedApkD > /dev/null",
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkD/classes.dex > dexdump-appObs.txt",
      "grep \"Git\\sRevision\" build-appDebug.log"
]

copy.stage="postBuild"
copy.script=["source ./ci/copy.sh"]
