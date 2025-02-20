#!/bin/sh
javac                      --module-path /usr/share/openjfx/lib --add-modules javafx.controls -cp .:art.jar ARTValuePlugin.java
java -Dprism.forceGPU=true --module-path /usr/share/openjfx/lib --add-modules javafx.controls -cp .:art.jar uk.ac.rhul.cs.csle.art.ARTFX Reduction.art
