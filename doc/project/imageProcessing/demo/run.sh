#!/bin/sh

javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls $1.java
java -Dprism.forceGPU=true --module-path /usr/share/openjfx/lib --add-modules javafx.controls $1
