set SRC=../src/
set FILES=-I%SRC%gtb -I%SRC%gtb_lib %SRC%gtb/*.cpp %SRC%gtb_lib/*.cpp

gcc     -ogtb_mgw    -Wno-write-strings %FILES%
gcc -O3 -ogtb_mgw_O3 -Wno-write-strings %FILES%
bcc32         -egtb_bcc32               %FILES%
del/q *.obj
bcc32 -Ospeed -egtb_bcc32_O3            %FILES%
del/q *.obj 
