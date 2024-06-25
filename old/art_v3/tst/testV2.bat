@echo off
cls
rem **********************************************************************
rem * Set options here - set up individual tests at the bottom
rem **********************************************************************
set art2Home=c:\csle\dev\art\art_v2
set art3Home=c:\csle\dev\art\art_v3
set runOptions= -noVCG -n 1
set testHome=%art3Home%\tst

set javaArtRaw=art -w -gJava -dfragment
set javaArt=%art2Home%\%javaArtRaw%
set javaCompileRaw=javac -d . -classpath art_gll.jar ARTLogTest.java ARTGLLParser.java ART_GLLLexer.java
set javaCompile=javac -d . -classpath %art3Home%\art.jar %art3Home%\src\ARTLogTest.java ARTGLLParser.java ARTGLLLexer.java
set javaRun=java -classpath .;%art3Home%\art.jar ARTLogTest %runOptions%

set cppArtRaw=art -w -gC++
set cppArt=%art2Home%\%cppArtRaw%
set cppCompileRaw=g++ -DRUSAGE -Ofast -o GLLTest -I. GLLTest.cpp
set cppCompile=g++ -Ofast -o GLLTest -I. -I%art2Home% %art2Home%\GLLTest.cpp
set cppBCCCompile=bcc32 -O -w-inl -P -I. -I%art2Home% %art2Home%\GLLTest.cpp
set cppRunRaw=GLLTest %runOptions%
set cppRun=..\%cppRunRaw%

rem **********************************************************************
rem * Top level code
rem **********************************************************************

cd %testHome%
del log_full.csv
if exist log_full.csv (
echo ** Failed to delete old log_full.csv 
goto:eof
)
touch log_full.csv

echo Deleting old test results
rd /q/s testResults > nul 2>&1
if exist testResults (
echo ** Failed to delete old test results
goto:eof
)

md testResults
pushd testResults
for %%F IN (%testHome%\testSource\*.art) DO call :buildTests %%~nF
popd
goto :eof

rem **********************************************************************

rem buildTests <%1 fileName>
:buildTests
md %1
pushd %1

rem **********************************************************************
rem * Calls to test blocks here - comment in and out as required
rem **********************************************************************

rem call :performance %1
call :correctness %1

rem **********************************************************************
rem * End of set up individual tests
rem **********************************************************************

popd
goto :eof

rem **********************************************************************

rem buildTest <%1 fileName> <%2 targetLanguage> <%3 testDirectoryName> <%4 artOptions>
:buildTest
rem echo Starting buildTest 1:%1 2:%2 3:%3 4:%4
cd
md %3
pushd %3

if %2==java (
%javaArt% %~4 %testHome%\testSource\%1.art
if not exist ARTGLLParser.java (
call :abend %3 %1 %2Art
popd
goto :eof
)
copy art_IFTdump.txt %testHome%\V2_OUT\%1_IFT.txt
copy ARTGLLParser.java %testHome%\V2_OUT\%1_gen.java
%javaCompile%
if not exist ARTGLLParser.class (
call :abend %3 %1 %2Compile
popd
goto :eof
)
)

call :parse %1 %2 %3
del *.class > nul 2>&1

popd
goto :eof

rem **********************************************************************

rem parse <%1 fileName> <%2 targetLanguage> <%3 testDirectoryName>
:parse
rem echo Starting parse 1:%1 2:%2 3:%3
for %%F IN (%testHome%\testSource\%1.*_*) DO (
md %%~nxF
pushd %%~nxF
echo %3 %%~nxF

rem *** this is ugly: it's hard to append to the Java classpath, so copy the classes in here
if %2==java copy ..\*.class > nul 2>&1
if %2==java (
%javaRun% -L %3 %%F > parse.log 2>&1
del *.class > nul 2>&1
)

if %2==cpp (
%cppRun% -L %3 %%F > parse.log 2>&1
)

if not exist log.csv call :abend %3 %%F %2Parser

copy %testHome%\log_full.csv + log.csv z > nul 2>&1
move z %testHome%\log_full.csv > nul 2>&1
del log.csv > nul 2>&1

popd
)

goto :eof


rem **********************************************************************

rem abend <%1 testDirectoryName> <%2 fileName> <%3 abend message> 
:abend
echo %1,,,%2,ABEND%3,bad > log.csv
copy %testHome%\log_full.csv + log.csv z > nul 2>&1
move z %testHome%\log_full.csv > nul 2>&1
del log.csv > nul 2>&1

goto :eof

rem **********************************************************************

rem **********************************************************************
rem * Set up individual test blocks here
rem **********************************************************************

rem correctness <%1 fileName>
:correctness

call :buildTest %1 java   baseJavaCorrectness       ""
rem call :buildTest %1 cpp    baseCppCorrectness        ""

rem call :buildTest %1 java   predictiveJavaCorrectness       "-E"
rem call :buildTest %1 cpp    predictiveCppCorrectness        "-E"

rem call :buildTest %1 java   clusteredJavaCorrectness       "-U"
rem call :buildTest %1 cpp    clusteredCppCorrectness        "-U"

rem call :buildTest %1 java   predictiveClusteredJavaCorrectness       "-E -U"
rem call :buildTest %1 cpp    predictiveClusteredCppCorrectness        "-E -U"

rem call :buildTest %1 java   baseJavaCorrectnessFIFO       "-F"
rem call :buildTest %1 cpp    baseCppCorrectnessFIFO        "-F"

goto :eof

rem **********************************************************************

rem performance <%1 fileName>
:performance

rem call :buildTest %1 java baseJava                ""
rem call :buildTest %1 java FIFODescriptorJava      "-F"
rem call :buildTest %1 java delayedDescriptorJava   "-D"
rem call :buildTest %1 java clusteredJava           "-c -M -U"
rem call :buildTest %1 java factorededJava          "-c -M -L"
rem call :buildTest %1 java factorededClusteredJava "-c -M -L -U"

rem SLE paper second table
rem call :buildTest %1 cpp  base                  "-M"
rem call :buildTest %1 cpp  baseFIFOCpp              "-M -F"
rem call :buildTest %1 cpp  delayedDescriptorLIFOCpp "-M -D"
rem call :buildTest %1 cpp  delayedDescriptorFIFOCpp "-M -D -F"

rem SLE paper first table
rem call :buildTest %1 cpp  RGLL         "-M -U"
rem call :buildTest %1 cpp  FGLL         "-M -L "
rem call :buildTest %1 cpp  RFGLL        "-M -L -U "
rem call :buildTest %1 cpp  NT           "-M -L -B"

rem call :buildTest %1 cpp  allthreeLIFOCpp      "-M -L -U -D -E"
rem call :buildTest %1 cpp  allthreeFIFOCpp      "-M -L -U -D -F -E"

rem call :buildTest %1 cpp  NTfactoredCpp        "-M -L -B"
rem call :buildTest %1 cpp  NTallthreeLIFOCpp    "-M -L -U -D -B"
rem call :buildTest %1 cpp  NTallthreeFIFOCpp    "-M -L -U -D -F -B"

rem SLE paper tables using Java for resubmission in December 2015
rem SLE paper second table
call :buildTest %1 java  base                  "-M"
call :buildTest %1 java  baseFIFOJava              "-M -F"
call :buildTest %1 java  delayedDescriptorLIFOJava "-M -D"
call :buildTest %1 java  delayedDescriptorFIFOJava "-M -D -F"

rem SLE paper first table
call :buildTest %1 java  RGLL         "-M -U"
call :buildTest %1 java  FGLL         "-M -L "
call :buildTest %1 java  RFGLL        "-M -L -U "
call :buildTest %1 java  NT           "-M -L -B"

call :buildTest %1 java  allthreeLIFOJava      "-M -L -U -D -E"
call :buildTest %1 java  allthreeFIFOJava      "-M -L -U -D -F -E"

call :buildTest %1 java  NTfactoredJava        "-M -L -B"
call :buildTest %1 java  NTallthreeLIFOJava    "-M -L -U -D -B"
call :buildTest %1 java  NTallthreeFIFOJava    "-M -L -U -D -F -B"

goto :eof

rem **End of batch file **************************************************