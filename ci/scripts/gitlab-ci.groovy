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
["","Shksknvwr"].each{ssv->
	["","Split"].each{split->
        // app
        def app=["Debug","Debug2","Debug2TestObs","Pg","PgUlt","PgExpermental","Release","ShrinkRelease"]
        app.each{
            this["app$it$split$ssv".toString()].script=mainScript
            this["app$it$split$ssv".toString()].artifacts.paths=artf
            this["app$it$split$ssv".toString()].stage="app".toString()
            this["app$it$split$ssv".toString()].variables.TASK=":app:assemble$it".toString()
        }
        def pre=["","Shrink"]
        pre.each{
            this["app${it}Pre$split$ssv".toString()].script=preScript
            this["app${it}Pre$split$ssv".toString()].artifacts.paths=artf
            this["app${it}Pre$split$ssv".toString()].stage="app".toString()
            this["app${it}Pre$split$ssv".toString()].variables.TASK=":app:assemble${it}Pre".toString()
        }
        // rcon
        def rcon=["App":["Release","Pre"],"PassCrack":["Release"]]
        rcon.entrySet().each{kv->
       	def module=kv.key
     	   	kv.value.each{build->
     	   	    this["rcon$module$build$split$ssv".toString()].script=mainScript
                this["rcon$module$build$split$ssv".toString()].artifacts.paths=artf
                this["rcon$module$build$split$ssv".toString()].stage="rcon".toString()
                this["rcon$module$build$split$ssv".toString()].variables.TASK=":${module}:assemble$build".toString()
            }
        }
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

convertTest.stage="postBuild".toString()
convertTest.script=["./ci/git.sh".toString(),"gradle convertConfigSlurperToBuildscript".toString()]
convertTest.artifacts.paths=["test.gitlab-ci.yml".toString()]
