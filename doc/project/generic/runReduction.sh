#!/bin/sh
javac -cp .:art.jar ARTValuePlugin.java
java  -cp .:art.jar uk.ac.rhul.cs.csle.art.ART reduction.art
