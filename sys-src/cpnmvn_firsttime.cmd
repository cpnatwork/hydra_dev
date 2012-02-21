@echo off

set ALPHAROOT=%CD%
set ALPHAHOME=%ALPHAROOT%\alphabuildhub
set HYDRAHOME=%ALPHAROOT%\hydra

cd /D %ALPHAROOT%
cmd /D/C mvn clean eclipse:clean -Dsilent=true
cmd /D/C mvn dependency:go-offline -Dsilent=true
cmd /D/C mvn dependency:sources -Dsilent=true
cmd /D/C mvn install -Dsilent=true

cd /D %ALPHAHOME%
cmd /D/C mvn jxr:jxr jxr:test-jxr -Dsilent=true
cmd /D/C mvn site:site -Dsilent=true
cmd /D/C mvn javancss:report -Dsilent=true

pause