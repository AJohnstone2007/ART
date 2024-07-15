set SRC=../src/
set FILES=-I%SRC%gtb -I%SRC%gtb_lib %SRC%gtb/*.cpp %SRC%gtb_lib/*.cpp

del /q *.exe

call runWithLocalPath \bc5\bin bcc32 -egtb_bcc32 %FILES% 
del/q *.obj
call runWithLocalPath \bc5\bin bcc32 -Ospeed -egtb_bcc32_O3 %FILES%
del/q *.obj 


call runWithLocalPath \bc5\bin bcc32i -egtb_intel32 %FILES%
del/q *.obj
call runWithLocalPath \bc5\bin bcc32i -Ospeed -egtb_intel32_O3 %FILES%
del/q *.obj 


call runWithLocalPath \mingw\mingw32\bin gcc     -ogtb_mgw32_gcc    -Wno-write-strings %FILES%
call runWithLocalPath \mingw\mingw32\bin gcc -O3 -ogtb_mgw32_gcc_O3 -Wno-write-strings %FILES%

call runWithLocalPath \mingw\mingw32\bin clang     -ogtb_mgw32_clang    -Wno-write-strings %FILES%
call runWithLocalPath \mingw\mingw32\bin clang -O3 -ogtb_mgw32_clang_O3 -Wno-write-strings %FILES%

call runWithLocalPath \mingw\mingw64\bin gcc     -ogtb_mgw64_gcc    -fpermissive %FILES%
call runWithLocalPath \mingw\mingw64\bin gcc -O3 -ogtb_mgw64_gcc_O3 -fpermissive %FILES%

call runWithLocalPath \mingw\mingw64\bin clang     -ogtb_mgw64_clang     -Wno-write-strings -fpermissive %FILES%
call runWithLocalPath \mingw\mingw64\bin clang -O3 -ogtb_mgw64_clang_O3 -fpermissive %FILES%



