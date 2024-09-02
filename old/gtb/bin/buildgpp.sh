#!/bin/sh

rm ~/referenceLanguageCorpora/experiments/try/tools/gtb/*.exe

g++                                  -fpermissive -O3 -ogtb_mgw32_gcc_O3               -Wno-write-strings -I../src/gtb -I../src/gtb_lib ../src/gtb/*.cpp ../src/gtb_lib/*.cpp
g++                    -DBRNGLR_POOL -fpermissive -O3 -ogtb_mgw32_gcc_O3_pool          -Wno-write-strings -I../src/gtb -I../src/gtb_lib ../src/gtb/*.cpp ../src/gtb_lib/*.cpp
g++ -g -fsanitize=address               -fpermissive -O3 -ogtb_mgw32_gcc_O3_sanitize      -Wno-write-strings -I../src/gtb -I../src/gtb_lib ../src/gtb/*.cpp ../src/gtb_lib/*.cpp
g++ -g -fsanitize=address -DBRNGLR_POOL -fpermissive -O3 -ogtb_mgw32_gcc_O3_pool_sanitize -Wno-write-strings -I../src/gtb -I../src/gtb_lib ../src/gtb/*.cpp ../src/gtb_lib/*.cpp

cp ~/ART/old/gtb/bin/gtb_mgw* ~/referenceLanguageCorpora/experiments/tools/gtb/
cp ~/ART/old/gtb/bin/gtb_mgw* ~/referenceLanguageCorpora/experiments/try/tools/gtb/

