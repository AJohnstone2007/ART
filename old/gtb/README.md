# Grammar Tool Box 

## GTB V2.5 1 November 2005 (updated to version 3.0 July 2024)

The Grammar Tool Box (GTB) is a tool for analysing grammars and running a variety of parsers.

It includes our RNGLR, BRNGLR and GLL algorithms for which this codebase includes the first implementations that were used to produce statistics for the relevant papers.

GTB has no support at all for semantics, and so is not useful for building translators: we used it purely as a framework for our research on parsing algorithms.

GTB is written in ANSI C though the source files have filetype .cpp

## Updates to version 3.0

GTB V3.0 is the final development version of GTB from 2005 with two sets of modification. The algorithm implementations have not been changed from those reported in the research papers.

1. All diagnostic outputs have been put under the control of the `verbose` script variable and a unified logging output has been established which is suitable for mass experimental runs. 

2. Pointer variables have been changed from unsigned* to ptrdiff_t* which then adjusts automatically between 32- and 64-bit address spaces, so that GTB can be compiled with both legacy compilers and modern 64-bit compilers. 

## Prebuilt binaries

The **bin** directory contains pre-built executables for Windows made using Borland C version 5.01, the Intel compiler from the Borland C 5.01 distribution, and the latest (in July 2024) version of mingw gcc in both 32- and 64-bit flavours.

## Building GTB

The source code resides in two directories: [src/gtb_lib](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/src/gtb_lib) contains the utility library which is common to GTB and some of our other tools, and [src/gtb](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/src/gtb) contains the code which is specific to GTB, including the parser implementations.

To build using gcc or some other compiler, simply compile together the contents of these two directories using a command line such as

`gcc -ogtb -Wno-write-strings -Isrc/gtb -Isrc/gtb_lib src\gtb\*.cpp src\gtb_lib\*.cpp`

#### Important - compiler warnings

The GTB code also casts mutable arrays of characters to strings, and has some code that triggers dataflow analyser warnings in later versions of gcc; these warnings can be ignored, hence the`no-write-strings` option above.

## Using GTB

Once built, we suggest that you start working through the tutorial
manual [doc/gtb_tut.pdf](https://github.com/AJohnstone2007/ART/blob/main/old/gtb/doc/gtb_tut.pdf) which makes use of gtb scripts in directories [doc/tut_ex](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/doc/tut_ex)
and [doc/lib_ex](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/doc/lib_ex).

## ART first prototype

From around 2007 up until January 2008 we were experimenting with the initial GLL implementation; this became the first version of ART. Some GLL support files are in [src/art_v1](https://github.com/AJohnstone2007/ART/tree/main/old/gtb/src/art_v1)

## VCG
GTB produces various visualisations as VCG files. See the notes [here](https://github.com/AJohnstone2007/ART/tree/main/old/rdp\#a-note-on-vcg) for information on VCG.

Adrian Johnstone, July 2024