#!/usr/bin/sh
g++               -fpermissive -O3 -ogtb_mgw32_gcc_O3      -Wno-write-strings -I../src/gtb -I../src/gtb_lib ../src/gtb/*.cpp ../src/gtb_lib/*.cpp
g++ -DBRNGLR_POOL -fpermissive -O3 -ogtb_mgw32_gcc_O3_pool -Wno-write-strings -I../src/gtb -I../src/gtb_lib ../src/gtb/*.cpp ../src/gtb_lib/*.cpp

