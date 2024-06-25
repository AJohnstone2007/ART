del big_*.*
del sma_*.*
..\miniloop -Vbig_tree tree.m
vcg -scale 30 -silent -psoutput big_tree.ps big_tree.vcg
pause
..\minitree -Vsma_tree tree.m
vcg -scale 30 -silent -psoutput sma_tree.ps sma_tree.vcg
pause
..\miniloop -Vbig_cmp tree_cmp.m
vcg -scale 30 -silent -psoutput big_cmp.ps big_cmp.vcg
pause
..\minitree -Vsma_cmp tree_cmp.m
vcg -scale 30 -silent -psoutput sma_cmp.ps sma_cmp.vcg
pause
..\miniloop -Vbig_dec tree_dec.m
vcg -scale 30 -silent -psoutput big_dec.ps big_dec.vcg
pause
..\minitree -Vsma_dec tree_dec.m
vcg -scale 30 -silent -psoutput sma_dec.ps sma_dec.vcg
pause
..\miniloop -Vbig_exp tree_exp.m
vcg -scale 30 -silent -psoutput big_exp.ps big_exp.vcg
pause
..\minitree -Vsma_exp tree_exp.m
vcg -scale 30 -silent -psoutput sma_exp.ps sma_exp.vcg
pause
..\miniloop -Vbig_if tree_if.m
vcg -scale 30 -silent -psoutput big_if.ps big_if.vcg
pause
..\minitree -Vsma_if tree_if.m
vcg -scale 30 -silent -psoutput sma_if.ps sma_if.vcg
pause
..\miniloop -Vbig_prn tree_prn.m
vcg -scale 30 -silent -psoutput big_prn.ps big_prn.vcg
pause
..\minitree -Vsma_prn tree_prn.m
vcg -scale 30 -silent -psoutput sma_prn.ps sma_prn.vcg
pause
..\miniloop -Vbig_wh tree_wh.m
vcg -scale 30 -silent -psoutput big_wh.ps big_wh.vcg
pause
..\minitree -Vsma_wh tree_wh.m
vcg -scale 30 -silent -psoutput sma_wh.ps sma_wh.vcg
