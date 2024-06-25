@echo off
cls
rem **********************************************************************
rem * Set options here - set up individual tests at the bottom
rem **********************************************************************
set artHome=c:\csle\dev\art\art_v3
set testHome=%artHome%\tst
set cppHome=%artHome%\cpp
set runOptions= +statistics

set javaArt=java -cp %artHome%\art.jar uk.ac.rhul.cs.csle.art.ART

set javaCompile=javac -d . -classpath %artHome%\art.jar %artHome%\src\ARTTest.java ARTGeneratedParser.java ARTGeneratedLexer.java
set javaRun=java -XX:+UseParallelGC -XX:NewSize=1000M -XX:SurvivorRatio=100 -Xms2048M -Xmx2048M -verbose:gc -classpath .;%artHome%\art.jar ARTTest %runOptions%

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
md %3
pushd %3

if %2==javaInterpretWithCpp (
for %%F IN (%testHome%\testSource\%1.*_*) DO (
md %%~nxF
pushd %%~nxF
echo **** %3 %%~nxF

%javaART% %testHome%\testSource\%1.art %~4 +inputFile:'%%F'

if not exist log.csv call :abend %3 %%F %2Parser

copy %testHome%\log_full.csv + log.csv z > nul 2>&1
move z %testHome%\log_full.csv > nul 2>&1
del log.csv > nul 2>&1

if exist ARTStaticSlotArray.h (
copy %cppHome%\ART*.cpp > nul 2>&1

g++ -Wno-write-strings -Ofast ARTEarleyIndexedPool.cpp
a %%F

copy %testHome%\log_full.csv + log.csv z > nul 2>&1
move z %testHome%\log_full.csv > nul 2>&1
del log.csv > nul 2>&1
del ARTStaticSlotArray.h > nul 2>&1
)

if exist ARTStaticEarleyTable.h (
copy %cppHome%\ART*.cpp > nul 2>&1

g++ -Wno-write-strings -Ofast ARTEarleyTableIndexedPool.cpp
a %%F

copy %testHome%\log_full.csv + log.csv z > nul 2>&1
move z %testHome%\log_full.csv > nul 2>&1
del log.csv > nul 2>&1
rem del ARTStaticEarleyTable.h > nul 2>&1
)


popd
)
popd
goto :eof
)

if %2==javaInterpret (
for %%F IN (%testHome%\testSource\%1.*_*) DO (
md %%~nxF
pushd %%~nxF
echo **** %3 %%~nxF

rem copy %testHome%\ARTEarleySPPFParserPool.cpp
rem copy %testHome%\ARTPool.cpp

%javaART% %testHome%\testSource\%1.art %~4 +inputFile:'%%F'

if not exist log.csv call :abend %3 %%F %2Parser

copy %testHome%\log_full.csv + log.csv z > nul 2>&1
move z %testHome%\log_full.csv > nul 2>&1
del log.csv > nul 2>&1

popd
)
popd
goto :eof
)

if %2==java (
del ARTGeneratedParser.java > nul 2>&1
del ARTGeneratedParser.class > nul 2>&1
%javaArt% %~4 %testHome%\testSource\%1.art
if not exist ARTGeneratedParser.java (
call :abend %3 %1 %2Art
popd
goto :eof
)
%javaCompile%
if not exist ARTGeneratedParser.class (
call :abend %3 %1 %2Compile
popd
goto :eof
)
call :parse %1 %2 %3 %4
del *.class > nul 2>&1

popd
goto :eof
)

rem **********************************************************************

rem parse <%1 fileName> <%2 targetLanguage> <%3 testDirectoryName> <%4 artOptions>
:parse
rem echo Starting parse 1:%1 2:%2 3:%3
for %%F IN (%testHome%\testSource\%1.*_*) DO (
md %%~nxF
pushd %%~nxF
echo **** %3 %%~nxF

rem *** this is ugly: it's hard to append to the Java classpath, so copy the classes in here
if %2==java copy ..\*.class > nul 2>&1
if %2==java (
%javaRun% +statistics %3 %%F %4 > parse.log 2>&1
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
echo %1,,%~nx2,ABEND,bad > log.csv
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

call :buildTest %1 java          gllGeneratorPool                  "+gllGeneratorPool +trace"
call :buildTest %1 java          gllRecogniserGeneratorPool        "+gllTWEGeneratorPool +trace"
rem call :buildTest %1 java          mgllGeneratorPool                 "+mgllGeneratorPool"
rem call :buildTest %1 cpp           gllGeneratorPool               ""

rem call :buildTest %1 java      predictiveGLLGeneratorPool     "+gllGeneratorPool +predictivePops"
rem call :buildTest %1 cpp       predictiveGLLGeneratorPool     "-E"

rem call :buildTest %1 java      clusteredGeneratorPool              "+gllClusteredGeneratorPool"
rem call :buildTest %1 cpp       clusteredGLLGeneratorPool      "-U"

rem call :buildTest %1 java      predictiveClusteredGLLGeneratorPool "+gllClusteredGeneratorPool +predictivePops"
rem call :buildTest %1 cpp       predictiveClusteredGLLGeneratorPool "-E -U"

rem call :buildTest %1 java      FIFOgllGeneratorPool           "+gllGeneratorPool +FIFODescriptors"
rem call :buildTest %1 cpp       FIFOGeneratorPool              "-F"

rem call :buildTest %1 javaInterpret        CNPLinkedAPI               "+cnpLinkedAPI"
rem call :buildTest %1 javaInterpret        CNPIndexedAPI              "+cnpIndexedAPI"
rem call :buildTest %1 javaInterpret        CNPIndexedPool             "+cnpIndexedPool"
rem call :buildTest %1 java                 CNPGeneratorPool           "+cnpGeneratorPool"

rem call :buildTest %1 javaInterpret        LCNPLinkedAPI              "+lcnpLinkedAPI"
rem NOT IMPLEMENTED YET call :buildTest %1 javaInterpret LCNPIndexedAPI       "+lcnpIndexedAPI"
rem NOT IMPLEMENTED YET call :buildTest %1 javaInterpret LCNPIndexedPool      "+lcnpIndexedPool"
rem NOT IMPLEMENTED YET call :buildTest %1 javaInterpret LCNPGeneratorPool    "+lcnpGeneratorPool"

rem call :buildTest %1 javaInterpret        Earley2007LinkedAPI        "+earley2007LinkedAPI"

rem call :buildTest %1 javaInterpret        EarleyLinkedAPI            "+earleyLinkedAPI"
rem call :buildTest %1 javaInterpret        EarleyIndexedAPI           "+earleyIndexedAPI"
rem call :buildTest %1 javaInterpretWithCpp EarleyIndexedPool          "+earleyIndexedPool"

rem call :buildTest %1 javaInterpret        EarleyTableLinkedAPI       "+earleyTableLinkedAPI"
rem call :buildTest %1 javaInterpret        EarleyTableIndexedAPI      "+earleyTableIndexedAPI" 
rem call :buildTest %1 javaInterpretWithCpp EarleyTableIndexedPool     "+earleyTableIndexedPool"

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
rem call :buildTest %1 java  base                  "-M"
rem call :buildTest %1 java  baseFIFOJava              "-M -F"
rem call :buildTest %1 java  delayedDescriptorLIFOJava "-M -D"
rem call :buildTest %1 java  delayedDescriptorFIFOJava "-M -D -F"

rem SLE paper first table
rem call :buildTest %1 java  RGLL         "-M -U"
rem call :buildTest %1 java  FGLL         "-M -L "
rem call :buildTest %1 java  RFGLL        "-M -L -U "
rem call :buildTest %1 java  NT           "-M -L -B"

rem call :buildTest %1 java  allthreeLIFOJava      "-M -L -U -D -E"
rem call :buildTest %1 java  allthreeFIFOJava      "-M -L -U -D -F -E"

rem call :buildTest %1 java  NTfactoredJava        "-M -L -B"
rem call :buildTest %1 java  NTallthreeLIFOJava    "-M -L -U -D -B"
rem call :buildTest %1 java  NTallthreeFIFOJava    "-M -L -U -D -F -B"

rem Earley/CNP paper
rem call :buildTest %1 java baseJava                ""

rem JACM EarleyTable paper
call :buildTest %1 javaInterpret        EarleyLinkedAPI            "+earleyLinkedAPI"
call :buildTest %1 javaInterpret        EarleyLinkedAPI            "+earleyLinkedAPI"
call :buildTest %1 javaInterpret        EarleyLinkedAPI            "+earleyLinkedAPI"
call :buildTest %1 javaInterpret        EarleyLinkedAPI            "+earleyLinkedAPI"
call :buildTest %1 javaInterpret        EarleyLinkedAPI            "+earleyLinkedAPI"

call :buildTest %1 javaInterpret        EarleyIndexedAPI           "+earleyIndexedAPI"
call :buildTest %1 javaInterpret        EarleyIndexedAPI           "+earleyIndexedAPI"
call :buildTest %1 javaInterpret        EarleyIndexedAPI           "+earleyIndexedAPI"
call :buildTest %1 javaInterpret        EarleyIndexedAPI           "+earleyIndexedAPI"
call :buildTest %1 javaInterpret        EarleyIndexedAPI           "+earleyIndexedAPI"

call :buildTest %1 javaInterpretWithCpp EarleyIndexedPool          "+earleyIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyIndexedPool          "+earleyIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyIndexedPool          "+earleyIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyIndexedPool          "+earleyIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyIndexedPool          "+earleyIndexedPool"


call :buildTest %1 javaInterpret        EarleyTableLinkedAPI       "+earleyTableLinkedAPI"
call :buildTest %1 javaInterpret        EarleyTableLinkedAPI       "+earleyTableLinkedAPI"
call :buildTest %1 javaInterpret        EarleyTableLinkedAPI       "+earleyTableLinkedAPI"
call :buildTest %1 javaInterpret        EarleyTableLinkedAPI       "+earleyTableLinkedAPI"
call :buildTest %1 javaInterpret        EarleyTableLinkedAPI       "+earleyTableLinkedAPI"

call :buildTest %1 javaInterpret        EarleyTableIndexedAPI      "+earleyTableIndexedAPI" 
call :buildTest %1 javaInterpret        EarleyTableIndexedAPI      "+earleyTableIndexedAPI" 
call :buildTest %1 javaInterpret        EarleyTableIndexedAPI      "+earleyTableIndexedAPI" 
call :buildTest %1 javaInterpret        EarleyTableIndexedAPI      "+earleyTableIndexedAPI" 
call :buildTest %1 javaInterpret        EarleyTableIndexedAPI      "+earleyTableIndexedAPI" 

call :buildTest %1 javaInterpretWithCpp EarleyTableIndexedPool     "+earleyTableIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyTableIndexedPool     "+earleyTableIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyTableIndexedPool     "+earleyTableIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyTableIndexedPool     "+earleyTableIndexedPool"
call :buildTest %1 javaInterpretWithCpp EarleyTableIndexedPool     "+earleyTableIndexedPool"

goto :eof

rem **End of batch file **************************************************
