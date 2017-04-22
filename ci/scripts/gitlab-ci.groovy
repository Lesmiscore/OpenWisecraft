image='nao20010128nao/android-build:latest'
before_script=['chmod a+x ./ci/*', 'source ./ci/prepare.sh']
stages=['app','rcon','postBuild']
def artf=[
      '*.apk',
      'build-*.log',
      'powof2.txt',
      '*/build/outputs/aar/*',
      'tree-*.log',
      '*/build/outputs/mapping/*/*.txt',
      '*/build/generated/source/*',
      'autogen-*.*'
]
def mainScript=[
      'gradle safeDeleteDevXml \$TASK --info --stacktrace | tee -a build-\$CI_BUILD_NAME.log > /dev/null',
      'mv */build/outputs/apk/*.apk .',
      './ci/packageInformations.sh'
]
def preScript=[
      'DIR=`cat ./app/build.gradle | grep \"def preNum=\"`',
      'DIR=\${DIR% //*}',
      'DIR=\${DIR#*=}',
      'if [ \$DIR != \"0\" ]; then gradle safeDeleteDevXml \$TASK --info --stacktrace | tee -a build-\$CI_BUILD_NAME.log > /dev/null; mv */build/outputs/apk/*.apk . ;./ci/packageInformations.sh ; fi'
]
["","Shksknvwr"].each{ssv->
	["","Split"].each{split->
        // app
        def app=["Debug","Debug2","Debug2TestObs","Pg","PgUlt","PgExperimental","Release","ShrinkRelease"]
        app.each{
            this["app$it$split$ssv".toString()]{
                script=mainScript
                artifacts{
                    paths=artf
                    when='always'
                }
                stage='app'
                variables.TASK=":app:assemble$it".toString()
                dependencies=[]
            }
        }
        def pre=["","Shrink"]
        pre.each{
            this["app${it}Pre$split$ssv".toString()]{
                script=preScript
                artifacts{
                    paths=artf
                    when='always'
                }
                stage='app'
                variables.TASK=":app:assemble${it}Pre".toString()
                dependencies=[]
            }
        }
        // rcon
        def rcon=["App":["Release","Pre"],"PassCrack":["Release"]]
        rcon.each{kv->
            def module=kv.key
     	   kv.value.each{build->
     	       this["rcon$module$build$split$ssv".toString()]{
                    script=mainScript
                    artifacts{
                        paths=artf
                        when='always'
                    }
                    stage='rcon'
                    variables.TASK=":rcon${module}:assemble$build".toString()
                    dependencies=[]
                }
            }
        }
    }
}
// postBuild
mainLast{
    artifacts{
        paths=['dexdump*.txt']
    }
    stage='postBuild'
    script=[
        'unzip app-release.apk -d unzippedApkA > /dev/null',
        '\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkA/classes.dex > dexdump-app.txt',
        'unzip rconApp-release.apk -d unzippedApkB > /dev/null',
        '\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkB/classes.dex > dexdump-rcon.txt',
        'unzip rconPassCrack-release.apk -d unzippedApkC > /dev/null',
        '\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkC/classes.dex > dexdump-rconPC.txt',
        'unzip app-pg.apk -d unzippedApkD > /dev/null',
        '\$ANDROID_HOME/build-tools/25.0.0/dexdump -d unzippedApkD/classes.dex > dexdump-appObs.txt',
        'grep \"Git\\sRevision\" build-appDebug.log'
    ]
    allow_failure=true
}
copy{
    stage='postBuild'
    script=['source ./ci/copy.sh']
    dependencies=[]
}

convertTest{
    stage='postBuild'
    script=['./ci/git.sh','gradle convertConfigSlurperToBuildscript']
    artifacts{
        paths=['test.gitlab-ci.yml']
        when='always'
    }
    dependencies=[]
    when='always'
}
