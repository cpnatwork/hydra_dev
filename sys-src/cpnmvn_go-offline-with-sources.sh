#!/bin/bash
mvn dependency:go-offline -Dsilent=true
mvn dependency:sources -Dsilent=true
