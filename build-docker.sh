#!/usr/bin/env bash

./gradlew build

echo "please paste and run command..."

echo "docker push crpi-ghlb0lap2gtu95ve.cn-beijing.personal.cr.aliyuncs.com/visionbagel/visionbagel-api:latest" | pbcopy
