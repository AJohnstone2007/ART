@echo off
set arthome=c:/csle/dev/art/art_v3

call clean
java -jar %arthome%/art.jar %1.art 
javac -classpath .;%arthome%/art.jar ARTGLLParser.java ARTGLLLexer.java
java -classpath .;%arthome%/art.jar ARTTest %1.str %2 %3 %4 %5 %6 %7 %8 %9
