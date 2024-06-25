@echo on
rem 26 March 2017 Adrian Johnstone
rem Update versioned directories from Adrian's Eclipse workspace
rem modified 07 March 2019 to iterate over all uk.ac.rhul.cs.csle sibdirectories
rem modified 21 October 2019 to fech cpp files
rem modified 19 December 2019 to take account of Java 13 modularisation and Open FX
rem modified 10 February 2020 to take account of osbrd renaming

set artHome=c:\csle\dev\art\art_v3

rem remove old jar and src directory
del/q art.jar
rd /s/q src
rd /s/q bin
rd /s/q cpp

rem copy cpp src directory from Adrian's Eclipse workspace
md cpp
copy \adrian\eclipse\ART\cpp\ARTMain.cpp cpp
copy \adrian\eclipse\ART\cpp\ARTPool.cpp cpp
copy \adrian\eclipse\ART\cpp\ARTLexer.cpp cpp
copy \adrian\eclipse\ART\cpp\ARTEarleyIndexedPool.cpp cpp
copy \adrian\eclipse\ART\cpp\ARTEarleyTableIndexedPool.cpp cpp
copy \adrian\eclipse\ART\cpp\ectst.bat cpp

rem copy src directory from Adrian's Eclipse workspace
md src
md bin
xcopy /S \adrian\eclipse\ART\src\* src

rem compile everything
cd bin
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\ARTTest.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\ARTTreeTest.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\ART.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\alg\cnp\generatedpool\*.*
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\util\graphics\*.*
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\util\audio\*.*
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\cava\Cava.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\cava\CavaFX.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\esos\ESOS.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\tg\ARTTG.java
javac --module-path "c:\openJFX\javafx-sdk-13.0.1\lib" --add-modules javafx.controls,javafx.fxml -d . -classpath ..\src ..\src\uk\ac\rhul\cs\csle\art\alg\osbrd\generator\*.java

rem remove default package parser and lexer classes to ensure they're not accidentally picked up
del ARTGenerated*.class

rem make the jar file
jar cfm ..\art.jar ..\manifest.local *

cd ..
rem rd /s/q bin
