#!/bin/sh
javac -cp .:art.jar ARTValuePlugin.java
java  -cp .:art.jar uk.ac.rhul.cs.csle.art.ART $1 !generate actions
javac -cp .:art.jar ARTGeneratedActions.java
java  -cp .:art.jar uk.ac.rhul.cs.csle.art.ART !interpreter attributeAction $1






