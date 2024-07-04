rem Windows build file for the Java parser components

rem First generate and compile the lexer
\csle\dev\rdp\bin\rdp -ojava_lex java_lex.rdp

g++ -o java_lex -Wno-write-strings -I/csle/dev/rdp/src/rdp_supp java_lex.c /csle/dev/rdp/src/rdp_supp/arg.c /csle/dev/rdp/src/rdp_supp/graph.c /csle/dev/rdp/src/rdp_supp/hist.c /csle/dev/rdp/src/rdp_supp/memalloc.c /csle/dev/rdp/src/rdp_supp/scan.c /csle/dev/rdp/src/rdp_supp/scanner.c /csle/dev/rdp/src/rdp_supp/set.c /csle/dev/rdp/src/rdp_supp/symbol.c /csle/dev/rdp/src/rdp_supp/textio.c

rem Now lexicalise the test string
java_lex -L Life.java > Life.java.tok

