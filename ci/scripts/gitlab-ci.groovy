image="nao20010128nao/android-build:latest".toString()
before_script=["chmod a+x ./ci/*".toString(), "source ./ci/prepare.sh".toString()]
stages=["app".toString(),"rcon".toString(),"postBuild".toString()]
def artf=[
      "*.apk".toString(),
      "dexdump*.txt".toString(),
      "build-*.log".toString(),
      "powof2.txt".toString(),
      "*/build/outputs/aar/*".toString(),
      "tree-*.log".toString(),
      "*/build/outputs/mapping/*/*.txt".toString(),
      "*/build/generated/source/*".toString(),
      "autogen-*.*".toString()
]
def mainScript=[
      "gradle safeDeleteDevXml \$TASK --info --stacktrace | tee -a build-\$CI_BUILD_NAME.log > /dev/null".toString(),
      "mv */build/outputs/apk/*.apk .".toString(),
      "./ci/packageInformations.sh".toString()
]
def preScript=[
      "DIR=`cat ./app/build.gradle | grep \"def preNum=\"`".toString(),
      "DIR=\${DIR% //*}".toString(),
      "DIR=\${DIR#*=}".toString(),
      "if [ \$DIR != \"0\" ]; then gradle safeDeleteDevXml \$TASK --info --stacktrace | tee -a build-\$CI_BUILD_NAME.log > /dev/null; mv */build/outputs/apk/*.apk . ;./ci/packageInformations.sh ; fi".toString()
]
["".toString(),"Split".toString()].each{split->
    // app
    def app=["Debug".toString(),"Debug2".toString(),"Debug2TestObs".toString(),"Pg".toString(),"PgUlt".toString(),"PgExpermental".toString(),"Release".toString(),"ShrinkRelease".toString()]
    app.each{
        this["app$it$split".toString()].script=mainScript
        this["app$it$split".toString()].artifacts.paths=artf
        this["app$it$split".toString()].stage="app".toString()
        this["app$it$split".toString()].variables.TASK=":app:assemble$it".toString()
    }
    def pre=["".toString(),"Shrink".toString()]
    pre.each{
        this["app${it}Pre$split".toString()].script=preScript
        this["app${it}Pre$split".toString()].artifacts.paths=artf
        this["app${it}Pre$split".toString()].stage="app".toString()
        this["app${it}Pre$split".toString()].variables.TASK=":app:assemble${it}Pre".toString()
    }
    // rcon
    def rcon=["App".toString(),"PassCrack".toString()]
    rcon.each{
        this["rcon${it}All$split".toString()].script=mainScript
        this["rcon${it}All$split".toString()].artifacts.paths=artf
        this["rcon${it}All$split".toString()].stage="rcon".toString()
        this["rcon${it}All$split".toString()].variables.TASK=":app:assemble$it".toString()
    }
}
// postBuild
mainLast.artifacts.paths=artf
mainLast.stage="postBuild"
mainLast.script=[
      "unzip app-release.apk -d unzippedApkA > /dev/null".toString(),
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkA/classes.dex > dexdump-app.txt".toString(),
      "unzip rconApp-release.apk -d unzippedApkB > /dev/null".toString(),
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkB/classes.dex > dexdump-rcon.txt".toString(),
      "unzip rconPassCrack-release.apk -d unzippedApkC > /dev/null".toString(),
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkC/classes.dex > dexdump-rconPC.txt".toString(),
      "unzip app-pg.apk -d unzippedApkD > /dev/null".toString(),
      "\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkD/classes.dex > dexdump-appObs.txt".toString(),
      "grep \"Git\\sRevision\" build-appDebug.log".toString()
]

copy.stage="postBuild".toString()
copy.script=["source ./ci/copy.sh".toString()]
