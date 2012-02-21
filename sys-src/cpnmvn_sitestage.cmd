@echo off

set ALPHAROOT=%CD%
set ALPHAHOME=%ALPHAROOT%\alphaflow

set MVNSTDOPT=-Dsilent=true -Dmaven.test.skip=false

cd /D %ALPHAHOME%
cmd /D/C mvn install %MVNSTDOPT%
cmd /D/C mvn jxr:jxr jxr:test-jxr %MVNSTDOPT%
rem cmd /D/C mvn site:site %MVNSTDOPT%
cmd /D/C mvn site:stage %MVNSTDOPT%
cmd /D/C mvn javancss:report %MVNSTDOPT%

pause