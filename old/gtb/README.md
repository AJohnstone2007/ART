# Grammar Tool Box 

## GTB V2.5 1 November 2005

The Grammar Tool Box (GTB) is a tool for analysing grammars and running a variety of parsers.

It includes our RNGLR, BRNGLR and GLL algorithms for which this codebase includes the first implementations that were used to produce statistics for the relevant papers.

GTB is written in ANSI C though the source files have filetype .cpp

## Compiling GTB

The source code lives in two directories: [src/gtb_lib](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/src/gtb_lib) contains the library which is common to GTB and some of our other tools, and [src/gtb](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/src/gtb) contains the modules which are specific to GTB.   

To build using gcc or some other compiler, simply compile together the contents of these two directories using a command line such as

`gcc -fpermissive -ogtb -ansi -Wno-write-strings -Isrc/gtb -Isrc/gtb_lib src\gtb\*.cpp src\gtb_lib

#### Important - compiler warnings

GTB assumes that pointers and integers are the same size. On
64-bit architectures (such as modern Intel based PCs) this assumption is not valid. g++ will issue warnings in such cases. The solution on Windows at least is to compile using a 32-bit compiler such as mingw32.

The GTB code also casts mutable arrays of characters to strings, and has some code that triggers dataflow analyser warnings in later versions of gcc; hence the `permissive` and `no-write-strings` options above.

## Using GTB

Once built, we suggest that you start working through the tutorial
manual [doc/gtb_tut.pdf](https://github.com/AJohnstone2007/ART/blob/main/old/gtb/doc/gtb_tut.pdf) which makes use of gtb scripts in directories [doc/tut_ex](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/doc/tut_ex)
and [doc/lib_ex](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/doc/lib_ex).

## ART first prototype

From around 2007 up until January 2008 we were experimenting with the initial GLL implementation; this became the first version of ART. Some GLL support files are in [src/art_v1](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/src/art_v1)

Adrian Johnstone, June 2024