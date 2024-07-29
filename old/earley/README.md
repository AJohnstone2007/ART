#Early and EarleyTable in C

This is the build directory for the C implementations reported in our
EarleyTable parisng paper.

`earleyIndexedPool_g++ test.art test.str` builds an Earley parsre from the grammar specification in `test.art` and runs it on the string on `test.str`.

Alternativel, execute these commands:

`java -jar ..\..\art.jar v3 test.art !earleyIndexedData`

`g++ ARTEarleyIndexedPool.cpp`

`a test.str`

The first line runs ART on `test.art` to produce a compact representaion of the grammar which is written out in file `ARTStaticSlotArray.h`.

We then compile the parser with `g++` and run it on `test.str`
