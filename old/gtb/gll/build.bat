rem gtb gll_paper_ex.gtb
..\gtb gll_paper_ex.gtb
rem type glltest.cpp
bcc32 -I..\src\gtb_lib gtb_gll.cpp glltest.cpp ..\src\gtb_lib\textio.cpp ..\src\gtb_lib\memalloc.cpp ..\src\gtb_lib\set.cpp 
