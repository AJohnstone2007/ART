call tg tg\basic.tg
rem call tg tg\lexer.tg
call tg tg\closures.tg
call tg tg\nestedClosures.tg
call tg tg\manualFactorise.tg
call tg tg\leftFactorise.tg
call tg tg\multiplyOut.tg
rem no iterators processed in any ART algorithm!
rem call tg tg\iterators.tg
call tg tg\merged.tg
rem call tg tg\gamma2.tg
dir *.*_* > z
grep File z
