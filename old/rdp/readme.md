# RDP V1.7 ALPHA

This is the alpha version of RDP V1.7, which may not be a stable. RDP 1.7 has never been formally released, but it has been used successfully to build the examples here and is used to build the front ends for later tools. Both RDP and the parser it generates are written in ANSI C. 

The stable distributed versions from V1.0 to V1.6 may be found in subdirectory [old](https://github.com/AJohnstone2007/ART/tree/main/old/rdp/old).

## RDP History

In September 1993, I and my colleague Andy Betts from UCL were *en route* to the fourth Eurochip conference in Toledo (Spain) and had an afternoon's wait at Madrid airport. He had a parsing problem: while we waited for our lift I sketched out a very simple LL(1) parser generator for him which I subsequently developed into RDP V1.0, released via comp.compilers on 16 February 1994. A short note on RDP was also published in [SIGPlan Notices](https://pure.royalholloway.ac.uk/en/publications/rdp-an-iterator-based-recursive-descent-parser-generator-with-tre).

We started using RDP for teaching in Autumn 1994 and one of our courses still makes use of it. Development essentally stopped in mid-1997 but this internal 1.7 version includes a few changes  that were made during development of the script language front ends for [GTB](https://github.com/AJohnstone2007/ART/tree/main/old/gtb) and [ART_V2](https://github.com/AJohnstone2007/ART/tree/main/old/art_v2), some of them as late as 2012.

#### RDP release dates - see [here](https://github.com/AJohnstone2007/ART/tree/main/old/rdp/old) for distribution zip files

* 1.0 16 February 1994
* 1.1 27 March 1994
* 1.2 6 November 1994
* 1.3 21 December 1994
* 1.4 30 January 1995
* 1.5Beta 8 April 1996 
* 1.5 20 December 1997
* 1.6 16 August 1997

## A note on VCG

RDP produces graph outputs in VCG (Visualisation of Compiler Graphs) format. VCG was written by Georg Sander as part of the Esprit project COMPARE in the 1990s. As of June 2024, the original web page is still available at https://www.rw.cdl.uni-saarland.de/people/sander/private/html/gsvcg1.html, but may dissapear. Many years ago Georg gave permission for the Windows VCG binaries to be distributed with RDP; you will find them in the [VCG](https://github.com/AJohnstone2007/ART/tree/main/old/rdp/vcg) directory.

Adrian Johnstone, June 2024 