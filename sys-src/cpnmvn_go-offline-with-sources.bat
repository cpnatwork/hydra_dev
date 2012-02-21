@echo off

cmd /D /C mvn dependency:go-offline -Dsilent=true
cmd /D /C mvn dependency:sources -Dsilent=true

pause