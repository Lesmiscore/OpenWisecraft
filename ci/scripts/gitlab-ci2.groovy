image='nao20010128nao/android-build:sdk-gradle-3.5'

before_script='''
wget -q -O /usr/bin/log $LOG_SCRIPT
chmod a+x /usr/bin/log
wget --no-check-certificate -q -O - $WISECRAFT_CLONE | bash
apt update 2>&1 | log apt
apt install -y tree 2>&1 | log apt
'''.trim().readLines()

stages=['app','rcon','asfsls']//,'postBuild']

def branch='publicVersion'

def artf='''
build.log
build**.log.gz
Wisecraft/**.apk
Wisecraft/build.log
Wisecraft/build**.log.gz
Wisecraft/**/build/outputs/mapping/**/**.txt
Wisecraft/**/build/generated/source/
Wisecraft/**/build/outputs/
Wisecraft/test*.gitlab-ci.yml
Wisecraft/compat/build/**.html
Wisecraft/autogen-*.*
'''.trim().readLines()

def baseScript='''
cd Wisecraft
git rev-parse HEAD
mv ../build*.log.gz . || true
./ci/minify_skinviewer.sh 2>&1 | log minify || true
gradle --stop 2>&1 | log daemon-stop || true
gradle tasks 2>&1 | log tasks || true
'''.trim().readLines()

def mainScript=baseScript+'''
gradle clean beforeBuild $TASKS -x lint --info --stacktrace --parallel 2>&1 | log main
'''.trim().readLines()

def preScript=baseScript+'''
DIR=`cat ./app/build.gradle | grep "def preNum = "`
DIR=${DIR% //*}
DIR=${DIR#*= }
if [ $DIR != "0" ]; then gradle clean beforeBuild $TASKS -x lint --info --stacktrace 2>&1 | log main ; fi
'''.trim().readLines()

def afterScript='''
cd Wisecraft
mv */build/outputs/apk/*.apk . || true
tree -fai | log tree
./ci/packageInformations.sh 2>&1 | log package
source ./ci/copy.sh 2>&1 | log copy
'''.trim().readLines()

// app
def app=["Debug","Debug2","Debug2TestObs","Pg","PgUlt","PgExperimental","Release","ShrinkRelease",'displayMode']
app.each{
    this["app$it".toString()].script=mainScript
    this["app$it".toString()].after_script=afterScript
    this["app$it".toString()].artifacts.paths=artf
    this["app$it".toString()].artifacts.when='always'
    this["app$it".toString()].stage='app'
    this["app$it".toString()].variables.TASKS=":app:assemble$it".toString()
    this["app$it".toString()].variables.BRANCH=branch
    this["app$it".toString()].dependencies=[]
}
def pre=["","Shrink"]
pre.each{
    this["app${it}Pre".toString()].script=preScript
    this["app${it}Pre".toString()].after_script=afterScript
    this["app${it}Pre".toString()].artifacts.paths=artf
    this["app${it}Pre".toString()].artifacts.when='always'
    this["app${it}Pre".toString()].stage='app'
    this["app${it}Pre".toString()].variables.TASKS=":app:assemble${it}Pre".toString()
    this["app${it}Pre".toString()].variables.BRANCH=branch
    this["app${it}Pre".toString()].dependencies=[]
}
        
// rcon
def rcon=["App":["Release","Pre"],"PassCrack":["Release"]]
rcon.each{kv->
    def module=kv.key
    kv.value.each{build->
       this["rcon$module$build".toString()].script=mainScript
       this["rcon$module$build".toString()].after_script=afterScript
       this["rcon$module$build".toString()].artifacts.paths=artf
       this["rcon$module$build".toString()].artifacts.when='always'
       this["rcon$module$build".toString()].stage='rcon'
       this["rcon$module$build".toString()].variables.TASKS=":rcon${module}:assemble$build".toString()
       this["rcon$module$build".toString()].variables.BRANCH=branch
       this["rcon$module$build".toString()].dependencies=[]
    }
}

// asfsls
def asfsls=["Debug","Debug2","Release","ShrinkRelease"]
asfsls.each{
    this["asfsls$it".toString()].script=mainScript
    this["asfsls$it".toString()].after_script=afterScript
    this["asfsls$it".toString()].artifacts.paths=artf
    this["asfsls$it".toString()].artifacts.when='always'
    this["asfsls$it".toString()].stage='asfsls'
    this["asfsls$it".toString()].variables.TASKS=":asfsls:assemble$it".toString()
    this["asfsls$it".toString()].variables.BRANCH=branch
    this["asfsls$it".toString()].dependencies=[]
}

// postBuild
