@echo off
del diffs
FOR %%f IN (*.bnf) DO call regress_one %%f
echo ***** Accumulated diffs ****
type diffs
echo ***** End of accumulated diffs ****
echo ***** Lines containing 'differ' ****
grep differ diffs
echo ***** End of lines containing 'differ' ****
