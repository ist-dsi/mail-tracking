#!/usr/bin/env bash
mvn --batch-mode -Dresume=false -DpushChanges=true -DscmCommentPrefix="[fenixedu-releaser]" -DtagNameFormat=v$1 -DreleaseVersion=$1 -DdevelopmentVersion=DEV-SNAPSHOT org.apache.maven.plugins:maven-release-plugin:2.5.2:prepare -Darguments="-Dmaven.test.skip=true -DskipTests -Dmaven.javadoc.skip=true"
