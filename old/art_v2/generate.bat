@echo off	
cls
\csle\dev\rdp\bin\rdp -oartparse artparse.bnf
move artparse.c artparse.cpp
echo Don't forget to change char declarations to const char in artparse.cpp
