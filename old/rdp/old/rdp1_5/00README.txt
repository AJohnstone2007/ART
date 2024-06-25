The fifth maintenance release of RDP (version 1.5) is now available via ftp 
from ftp.cs.rhbnc.ac.uk in directory /pub/rdp. Get rdp1_5.zip if you are an
MS-DOS user or rdp1_5.tar if you are a Unix user. You can also access the
ftp server via the rdp web site at      

      http:\\www.dcs.rhbnc.ac.uk\research\languages\rdp.shmtl

This will be the last release of RDP version 1. A new version of RDP
which targets LL(oo) parsing (leftmost parsing with arbitrary
backtracking) will appear in due course: backwards compatibility with
RDP version 1 is not absolutely guaranteed, but I'll do my best.

This release includes the following new features

 - an new iterator operator,

 - new positive closure syntax,

 - a new command line argument processing library,

 - a new graph construction and manipulation library with support for the VCG 
   (Visualisation of Compiler Graphs) tool so that you can look at your data 
   structures under Unix/X-windows and Microsoft Windows,

 - improvements to the formatting of error messages,

 - scanner lookahead for the REAL primitive,

 - support for underscores in INTEGER and REAL literals,

 - a new naming convention that removes the conflict between some C++
   implementations and the double underscore naming convention,

 - support for integer literal, real literal and string literal parameters to
   parser functions,

 - improvements to the entry code for parser functions so that semantic
   actions behave sensibly for productions with optional bodies,

 - extensive new facilities for constructing and manipulating derivation trees 

 - new debugging options that help trace a parser's behaviour,

 - updated user and support library manuals,

 - a new tutorial manual,

 - a new case study manual which describes two interpreters, two compilers and
   a C pretty printer

A fuller list of changes will be found at the end of this message.

*** VERY IMPORTANT WARNING

One seriously non-backwards compatible change involving the <...> construct
has been incorporated in version 1.5. Read the compatibility section below 
before processing a pre-version 1.5 grammar that includes instances of the
<...> with this version of RDP. All occurences of <...>'x' MUST be replaced
with (...)@'x' because the semantics of the <...> construct have been changed.

*** END OF VERY IMPORTANT WARNING

**** Here follows a thumbnail sketch of RDP ****

RDP - a recursive descent parser generator with one symbol of lookahead

RDP compiles attributed LL(1) grammars decorated with C-language
semantic actions into recursive descent parsers. 

RDP is written in strict ANSI-C and produces strict ANSI-C. RDP was
developed using Borland C++ versions 3.1 and 5.1 on a PC and has also
been built with no problems on Alpha/OSF-1, DECstation/Ultrix, HP
Apollo/HPUX, Sun 4/SunOS, Solaris, Linux and NetBSD 0.9 hosts all using
gcc and g++ as well as variety of vendor's own compilers. RDP also
compiles for MS-DOS under Microsoft C V7.0, gcc (using the djpgg port)
and several other compilers. I have reports of successful builds on Mac,
Amiga and the Acorn Archimedes.

The definition of strict ANSI here means compiling under gcc with

gcc -Wmissing-prototypes -Wstrict-prototypes -fno-common -Wall -ansi -pedantic

and getting no warning messages.

RDP is C++ `clean' i.e. there are no identifiers used that clash with C++
reserved words. RDP generated code has been used with g++ applications, and
compiles with g++ and the Borland C++ (as opposed to ANSI C) compiler.

RDP itself, and the language processors it generates, use standard library
modules to manage symbol tables, sets, graphs, memory allocation, text
buffering, command line argument processing and scanning. The RDP scanner is
programmed by loading tokens into a symbol table at the start of each run. In
principle, the RDP scanner can be used to support runtime extensible
languages, such as user defined operators in Algol-68, although nobody has had
the nerve to try this yet.

RDP produces complete runnable programs with built-in help information and
command line switches that are specified as part of the EBNF file. In this
sense RDP output is far more shrink-wrapped than the usual parser generators
which helps beginners. 

The RDP text buffering routines automatically handle nested files, error
message reporting and text data buffering to provide an efficient general
purpose front end. This is also a great help to new users since writing
efficient (and correct) text buffering and scanning routines from scratch is,
in my experience, harder than it appears.

The RDP graph handling package provides a general framework for building
graph data structures that may then be output in a form suitable for display 
with the VCG (Visualisation of Compiler Graphs) tool. RDP generated parsers
can be set to automatically build derivation trees in a form suitable for
human viewing. VCG runs on Windows 3.1, Windows-95 and Unix/X-windows systems.
We are grateful to the author of VCG for permission to supply VCG with RDP: you
can fetch a copy of VCG from the home FTP site for RDP 
(ftp://ftp.cs.rhbnc.ac.uk/pub/rdp)

RDP generates itself (you mean you use a parser generator which _isn't_
written in its own source language?) which is a nice demonstration of
the bootstrapping technique used for porting compilers to new
architectures.

I wrote RDP because in October '94 I started giving a course on compiler
design. I am mainly interested in code generation for exotic processors,
so I tried to produce a compact parser generator that would enable
undergraduates to rip through the syntax and standard code generation
parts of the syllabus in a few weeks, thus leaving me time to get into
the black arts of code scheduling and optimisation.

What you get.

- The machine generated source for the RDP translator (rdp.c). RDP checks that
  the source grammar is LL(1) and explains exactly why a non-LL(1) grammar is 
  unacceptable. This version of RDP does not attempt to rework a grammar by
  itself.

- The decorated EBNF file describing RDP that was processed by the RDP
  executable to produce its own source code (rdp.bnf). This is good for
  boggling undergraduate's minds with.

- Decorated EBNF files for the languages minicalc, minicond, miniloop
  and minitree that are used as examples in the case study manual. The 
  languages illustrate the development of a simple programming language 
  by way of a syntax checker, two interpreters and finally two syntax-directed
  compilers that produces assembly language for a mythical machine called
  MVM (Mini Virtual Machine). One of the compilers is a single pass
  translator and the other uses a tree-based intermediate form.
 
- The decorated EBNF file for mvmasm, an assembler for the language
  produced by the above compilers along with a simulator called mvmsim for the
  resulting executable files.

- An EBNF file describing a not particularly standard Pascal with some
  extensions for Turbo Pascal which generates a fully working parser.

- An ANSI C pretty printer written using RDP

- A set of functions to automate the handling of command line arguments (arg.c).

- Routines to implement general graph data structures (graph.c).

- A set of wrapper functions for the standard C memory allocation routines
  with built in fatal error handling for out of memory errors (memalloc.c).

- A programmable scanner with integrated error handling (scan.c and scanner.c).

- A set-handling package that supports dynamically sizable sets (set.c).

- A hash-coded symbol table with support for multiple symbol tables,
  nested scope rules and arbitrary user data (symbol.c).

- A standard text buffering package with integrated messaging utilities that
  are used for all communication with the user (textio.c).

- Sources and makefiles for everything which you may use freely on condition
  that you send copies of any modifications, enhancements and ill-conceived
  changes you might make back to me so that I can improve RDP.

- User, library support, tutorial and case study manuals.

What you don't get.

- Warranties. This code has only just escaped from my personal toolkit.
  I've put a lot of effort into making it fit for others to use, but in
  the very nature of compiler compilers it is hard to test all the angles,
  and the Garbage In - Garbage Out principle holds to the highest degree:
  if you write ill-formed semantic actions you won't find out until you try
  and compile the compiler that RDP wrote for you. On the plus side, RDP has
  now been used pretty ferociously by lots of people and has stood up very
  well. It does seem to be pretty stable, and I will continue to respond to
  bug reports.

** RDP release version 1.50 flyer. Adrian Johnstone 20 December 1997 **

This is the RDP V1.50 release distribution. To install RDP look in the
makefile and ensure that the blocks of options at the top are commented out
correctly for your compiler and operating system. Then type 'make' (or 'nmake'
if you are a Microsoft  compiler user) and stand well back.

If you use an ANSI compiler then you should be able to build RDP after you
have uncommented suitable compiler switch options in the makefile. I'd really
like to hear about any problems you have with other MS-DOS or Unix C
compilers - my aim is to make RDP as portable as TeX (some hope).

Further documentation may be found in Postscript and DVI forms in subdirectory
'manuals'.

We'd like to thank the many people who have tried RDP and sent me encouraging
messages, suggestions for improvements and bug reports, in particular Andy
Betts (previously of UCL, now at Inmos), my students on course CS347
(compilers and code generation) at RHUL.

** Improvements in this version include **

NEW FEATURES

1. A new iteration operator @ has been introduced which subsumes all of the
EBNF brackets which are now represented internally as special cases of the @
operator. The old <...>'x' construct should now be written as (...)@'x'.

2. <...> is now used to represent positive closure (one or many).

3. Scanner lines are now echoed after error messages have been written. This
allows a more compact `pointing' notation, and the delayed echo makes it
possible to insert text before the echoed line which is useful for assemblers,
as demonstrated by the mvmasm assembler described in the tutorial manual.

4. A new scanner primitive CHARACTER has been added that reads a single stropped,
quoted character.

5. LL(1) errors detected during grammar parsing are now flagged in the parser
source code, so that if you are stepping through a parser using a debugger
you will be reminded that something odd is about to happen. Look in pascal.c
(which is created during a make all) for an example.

6. A new option -P has been added to RDP. Parsers generated with this option
issue information messages on entry and exit from each parser function: this
can be useful for tracing parser operation but as you might expect it produces
a lot of output.

7. A new support library module (arg.c) has been added to handle processing of
command line arguments in a standard way. New directives ARG_BOOLEAN,
ARG_NUMERIC and ARG_STRING have been added to RDP and the OPTION directive has
been dropped. The old OPTION directive has been dropped.

8. A new support library module (graph.c) has been added which handles general
graph datastructures. 

9. The graph library can output any graph in a form suitable for viewing with
the VCG (Visualisation of Compiler Graphs) tool written by
You can fetch a copy of VCG for Unix/X-windows or MS-Windows systems from 
ftp://ftp.dcs.rhbncs.ac.uk/pub/rdp. 

10. Three new directives TREE, EPSILON_TREE and ANNOTATED_EPSILON_TREE have been 
added that cause parsers to build derivation trees using the new graph library.

11. Node promotion operators (^, ^^, ^^^ and ^_) have been added that
allow the standard derivation trees to be modified during construction
into intermediate tree forms suitable for use with multiple pass
compilers. This notation is rather experimental and we would very much
like to hear users' opinions on its ease or difficulty of use. (See
Chapters 9 and 10 of the user manual and Chapter 8 of the case study
manual for descriptions of the new tree features.)

12. A new option -V<name> has been added to parsers generated with the 
TREE directive. At completion of a parse that parser will dump a VCG
compatible description of the derivation tree to <name>. You can then
use the VCG tool under MS-Windows or Unix/X-windows to view the graph.

13. Support for parameter passing into parser functions (inherited attributes)
has been improved. Formal parameter types may now include indirect types such
as char*. In addition, literal strings, real and integer numbers can be used
as actual parameters.

14. Underscores may now be inserted into numeric literals in the style
of Ada, so for instance 278_101_123 is a valid INTEGER literal.

15. A new tutorial guide for new users has been added, covering the
basic steps required to build a parser for a new language.

16. A new case study manual has been added that describes the
development of a family of language translators, culminating in a
compiler that produces code for a virtual machine. An assembler and
simulator for the target machine are also provided.

FIXES and ENHANCEMENTS

17. All RDP indentifiers of the form xxx__yyy are renamed to xxx_yyy to avoid
clashes with some C++ implementations (sigh). RDP now checks explicitly for
identifiers that begin arg_, graph_, mem_, rdp_, scan_ set_, sym_ or text_ and
issues an error message if it finds any so as to avoid clashes with built in
identifer names. Sorry about this change, but I didn't know that the orginal
AT&T C++ front end uses double underscore internally rather a lot. Teach me to
read the book more carefully.

18. The set_print routine now takes a line length argument so that line breaks
can be inserted in long lists. This feature is used to suppress the very long
lines in set initialisation code that have been causing some editors to break.

19. Parsers now exit at the end of a pass in which errors occur.

20. The scanner now looks ahead when checking for a real to make sure things of
the form 1..3 are reported as 1 followed by .. followed by 3 rather than 1.0
followed by 0.3.

21. The nasm assembler has been replaced by mvmasm, an assembler along
with a simulator called mvmsim that takes the output from the miniloop
and minitree compilers.

22. An unterminated comment (i.e. an EOF appearing within a comment) is
now detected and reported as: `fatal: EOF detected inside comment'.

23. If the -T command line option is used to set the text buffer size to a
value that is larger than the maximum value of size_t on the architecture, a
warning message is issued and the type recast to size_t (which usually
results in a wrap-around).

24. symbol_lookup() now takes a scope argument.

25. A new routine, symbol_find() allows common symbol update operations
to be implemented in a single call.

26. rdp productions returning void* (or for that matter void**...) results
are now correctly implemented. Previously, the word void in a return type
was enough to suppress generation of a result variable.

27. Continuation tokens are now correctly added for tokens that appear
in extended scanner primitives like STRING and COMMENT.

28. Extended tokens (string and comment scanner primitives) now map to their
opening tokens rather than the equivalent scanner primitive. This corrects a
truly awful design error that I discovered the hard way when I wanted two
alternates to start with different kinds of escaped strings. The fixes to the
scanner are rather ugly.

29. When using included files (i.e. nested called to text_open() ) a token
was being lost in the parent file after the included file closed. This has been
corrected.

30. A production containing only references to the three non-visible COMMENT
scanner primitives will not generate a `production never called' warning.

31. Grammars containing only scanner primitives and nonterminals (i.e. grammars with
no keywords) are now handled correctly.

32. New functions have been added to the text library to get the current tabwidth
setting and print strings as ANSI C character constants.

33. Multiline comments are now returned correctly in the COMMENT...VISIBLE
scanner primitives.

34. The lexeme for INTEGER and REAL scanner primitives is now returned in the
id field.

35. The rdp_sourcefilename global variable is now updated to show the actual file
used (after file type processing) rather than the name used on the command line.

36. Generate and compiler date stamps are now correctly implemented.

NON-BACKWARDS COMPATIBLE CHANGES -- IMPORTANT!

37. The <body> 'token' construct is now represented by (body)@'token'.

<body> on its own now represents the positive closure of body, that is
body{body}. Very sorry about this. Be careful!

**** Features for future versions

This will be the last functionality release for RDP version 1, although
I will put out bug fixes as necessary. For most of this year I have been
building small prototypes of a system for performing rather more general
recursive descent parsers. Sometime soon a new translator generator tool
called PGT (the Permutation Grammar Tool) will appear.

PGT will support LL(oo) parsing, that is arbitrary backtracking will be
applied where necessary.  In principle this will allow many more context
free grammar to be parsed, but not necesarilly in linear time. You will
be warned of any non-linearities.

PGT will support scanner productions which supersede the various
programmable tokens used at the moment. A customised scanner for each
language will then be written out for each language.

As part of the scanner production support, subproductions comprising a list
of alternates each of which is exactly one terminal long will automatically
be translated into a set of such tokens. NOT and wildcard operators will be
provided.

The iteration operator @ will be enhanced to handle general iteration
and permutation phrases.

Other suggestions for improvements are welcome.

***
Comments, queries and requests for code to Adrian Johnstone, address below.

***
Primary code author and joint author of manuals

Smail: Dr Adrian Johnstone, Computer Science Department, Royal Holloway, 
       University of London, Egham, Surrey, TW20 0EX, UK.

Email: A.Johnstone@rhbnc.ac.uk  Tel: +44 (0)1784 443425  Fax: +44 (0)1784 443420

Joint authorship of manuals and chief code checker

Smail: Dr E Scott, Computer Science Department, Royal Holloway, 
       University of London, Egham, Surrey, TW20 0EX, UK.

Web:   http:\\www.dcs.rhbnc.ac.uk\research\languages\index.html
***
