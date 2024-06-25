echo %1
echo ******************** >> diffs
echo %1 >> diffs
del /q old\*.*
del /q new\*.*

bin\gramconv_210.exe %1 > gc.out
move gc.* old > nul

..\gramconv %1 > gc.out
move gc.* new > nul

diff -q -s -r old new >> diffs

grep differ diffs
