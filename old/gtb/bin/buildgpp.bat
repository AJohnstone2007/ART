set SRC=../src/
set FILES=-I%SRC%gtb -I%SRC%gtb_lib %SRC%gtb/*.cpp %SRC%gtb_lib/*.cpp

del /q *.exe

g++               -fpermissive -O3 -ogtb_mgw32_gcc_O3      -Wno-write-strings %FILES%
g++ -DBRNGLR_POOL -fpermissive -O3 -ogtb_mgw32_gcc_O3_pool -Wno-write-strings %FILES%
