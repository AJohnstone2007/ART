rem Windows build file for the C++ lexer component

rem First generate and compile the lexer
\csle\dev\rdp\bin\rdp -ocpp_lex cpp_lex.rdp

g++ -o cpp_lex -Wno-write-strings -I\csle\dev\rdp\src\rdp_supp cpp_lex.c \csle\dev\rdp\src\rdp_supp\arg.c \csle\dev\rdp\src\rdp_supp\graph.c \csle\dev\rdp\src\rdp_supp\hist.c \csle\dev\rdp\src\rdp_supp\memalloc.c \csle\dev\rdp\src\rdp_supp\scan.c \csle\dev\rdp\src\rdp_supp\scanner.c \csle\dev\rdp\src\rdp_supp\set.c \csle\dev\rdp\src\rdp_supp\symbol.c \csle\dev\rdp\src\rdp_supp\textio.c

rem Now lexicalise the test strings
cpp_lex -T10000000 -L art_cpp.cpp > art_cpp.cpp.tok
cpp_lex -T10000000 -L artsupport.cpp > artsupport.cpp.tok
