#!/bin/bash

ALPHAROOT=`pwd`
ALPHAHOME=$ALPHAROOT/alphaflow
HYDRAHOME=$ALPHAROOT/../sys-src_hvs/hydra

cd $HYDRAHOME
mvn clean
mvn install

cd $ALPHAROOT
mvn clean eclipse:clean -Dsilent=true
mvn dependency:go-offline -Dsilent=true
mvn dependency:sources -Dsilent=true
mvn install -Dsilent=true

cd $ALPHAHOME
mvn jxr:jxr jxr:test-jxr -Dsilent=true
mvn site:site -Dsilent=true
mvn javancss:report -Dsilent=true
