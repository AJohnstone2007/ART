This is the RDP V1.10 release distribution. Look in the makefile and
ensure that the blocks of options at the top are set correctly for
either Borland C on MS-DOS or GCC on Unix as the fancy takes you. Then
type 'make' and stand well back. Look in buildlog.* to see what a
correct build on your machine should look like. Further details may be
found in rdp_user.ps, but you'll need a postscript printer.

** RDP release version 1.10 flyer. Adrian Johnstone 27 March 1994 **

The first maintenance release of RDP (version 1.1) is now
available via ftp from cscx.cs.rhbnc.ac.uk (134.219.200.45) in
directory /pub/rdp. Get rdp1_1.zip if you are an MS-DOS user or
rdp1_1.tar.Z if you are a Unix user. 

If you would like your name added to a mailing list which will
keep you up to date with RDP developments please mail me at the
address given at the end of this message.

** Improvements in this version include **

0. A (very) draft user manual is now included.

1. Very detailed diagnostics given if an LL(1) violation is
   detected, specifying the sub-clauses in error and listing the bad
   tokens.

2. RDP produced C source code is now nicely indented.

3. New PASSES directive supports multiple-pass parsers (such as
   assemblers).

4. New HEX_DOLLAR directive enables Motorla style hexdeimal numbers ($xx)
   in scanner.

5. EOF handling now conforms to ANSI standard (thanks to
   gunnar@fasel.robin.de for pointing this out).

6. OUTPUTFILE now defaults to "rdparser".

7. EOLN_VISIBLE directive superceded by automatic detection of EOLN token.

8. New comment primitives added to support assembler and visible
   comments. COMMENT_VISIBLE directive removed.

9. Bugs in scanner string mode parser fixed.

10. Productions consisting solely of [...] or {...} now handled
    correctly.

11. Toy interpreter now has a full set of control structures.

12. Mini (cut down Toy) language added as a tutorial introduction.

13. Pascal parser now handles null statements.

14. -bfilename switch dropped since new diagnostics reduce the
    need to look at the internal productions. New -e switch dumps
    internal productions to stdout instead.

15. Multiple user command line switches now handled correctly.

16. Force flag removed from default parsers.

17. HELP directive now renamed OPTION.

18. New makefile and project files included.

**** Here follows a thumbnail sketch of RDP ****

RDP - a recursive descent parser generator

RDP compiles attributed LL(1) grammars decorated with C-language semantic
actions into recursive descent compilers. RDP is written in strict ANSI C
and produces strict ANSI C. RDP was developed using Borland C 3.1 on a PC
and has also been built with no problems on Alpha/OSF-1, DECstation/Ultrix
and Sun 4/SunOS hosts all using GCC. The definition of strict ANSI here means 
compiling with gcc -Wall -Wstrict-prototypes -fno-common  -pedantic -ANSI and 
getting no warning messages.

RDP itself and the language processors it generates use standard symbol,
set and scanner modules. The RDP scanner is programmed by loading tokens
into the symbol table at the start of each run.

RDP produces complete runnable programs with built-in help information and
command line switches that are specified as part of the EBNF file. In this
sense RDP output is far more shrink-wrapped than the usual parser generators
which helps beginning students. RDP can generate itself which is a nice
demonstration of the bootstrapping technique used for porting compilers to
new architectures.

I wrote RDP because in October I start giving a course on compiler design.
I don't think the world needs another course on parsing techniques and am
really interested in code generation for exotic processors, so I tried to
produce a compact parser generator that would enable undergraduates to rip
through the syntax and standard code generation parts of the syllabus in a
few weeks, thus leaving me time to get into the black arts of code
scheduling and optimisation.

What you get.

o An implementation of Pascal style set-handling in C.

o A hash-coded symbol table with support for scoping and arbitrary user data

o A programmable high-speed scanner with integrated error handling and
  hooks for macros (RDP is being used to generate assemblers for two novel
  microprocessors in addition to the usual applications of LL(1) parsers).

o The source to a hand-coded version of the RDP translator. RDP checks that
  the source grammar is LL(1) and explains exactly why a non-LL(1) grammar
  is unacceptable. RDP does not attempt to rework a grammar by itself.

o A decorated EBNF file describing RDP that may be processed by the
  hand-coded RDP translator to produce a machine generated version of RDP.
  This is good for boggling undergraduate's minds with.

o A decorated EBNF file describing an interpreter for a language called TOY.

o An EBNF file describing Pascal which generates a parser for
  the Pascal language. 

o A pre-built RDP executable for MS-DOS.

o Sources, makefiles and Borland 3.1 project files for everything which
  you may use freely on condition that you send copies of any modifications,
  enhancements and ill-conceived changes you might make back to me so that
  I can improve RDP.

What you don't get.

o Decent manuals. A preliminary draft of the main user manual
  which includes a tutorial introduction to RDP is supplied. Better
  manuals will follow.

o Tutorial information on parsing and compiling (try Wirth's Algorithms +
  Data Structures = Programs, Chapter 5 or your favourite compiler book).

o Warranties. (This code has only just escaped from my personal toolkit.
  I've put a lot of effort into making it fit for others to use, but in
  the very nature of compiler compilers it is hard to test all the angles,
  and the Garbage In - Garbage Out principle holds to the highest degree:
  if you write ill-formed semantic actions you won't find out until you try
  and compile the parser that RDP wrote for you.)

Comments, queries and requests for code to

Dr Adrian Johnstone, CS Dept, Royal Holloway, University of London
Email: adrian@dcs.rhbnc.ac.uk
