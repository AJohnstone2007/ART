This is the RDP V1.20 release distribution. To install RDP look in the
makefile and ensure that the blocks of options at the top are commented out
correctly for either Borland C on MS-DOS or GCC on Unix as the fancy takes
you. Then type 'make' and stand well back. 

If you use another ANSI compiler then you should be able to build RDP after
you have defined suitable compiler switch options in the makefile. I'd really
like to hear about any problems you have with, say, Microsoft C or your Unix
vendor's C compiler.

Further documentation may be found in Postscript form in subdirectory 'doc'.

** RDP release version 1.20 flyer. Adrian Johnstone 6 November 1994 **

The second maintenance release of RDP (version 1.2) is now available via ftp
from cscx.cs.rhbnc.ac.uk (134.219.200.45) in directory /pub/rdp. Get
rdp1_2.zip if you are an MS-DOS user or rdp1_2.tar if you are a Unix user. I
have not compressed the Unix version because some people have been having
trouble decompressing the file - is there a bug in OSF/1 compress?

If you would like your name added to a mailing list which will keep you up to
date with RDP developments please mail me at the address given at the end of
this message.

I'd like to thank the many people who have tried RDP and sent me encouraging
messages, suggestions for improvements and bug reports, in particular Andy
Betts (previously of UCL, now at Inmos) and my students on course CS347 at RHUL.

*** Important note ***

I have made three fundamental changes at this release. Firstly NUMBER is now
called INTEGER (and yes, I have added a REAL primitive too). A global search
and replace of NUMBER for INTEGER will fix this for you.

More seriously, I and several others had become very frustrated with
endlessly having to quote semantic action strings on a line by line basis. In
addition, the use of \ as an escape and " as a code delimiter was a real bad
idea because of the clash with C string use. As of this release, all semantic
actions need to be enclosed in [* ... *] instead of " ... ". Many apologies
for the need to change your files, but I think you will find the new format a
lot easier to use.

Finally, the layout of the symbol table data type symbol__ has been changed.
The id, token and symbol table links are as before. All other data elements
are in a union called data. I have predefined integer, real, character and
void pointer and in params.h constructed typedefs to allow parameterisation.
The main change that you will have to apply is to change all occurrences in
your semantic actions of '->number' to ->data.i. 

** Improvements in this version include **

NEW FEATURES

1. I wrote a user manual.

2. I have also written the first draft of a manual describing the support
routines, in particular the symbol table and how to implement nested scope
routines. 

3. The support routine source and header files are now held in a separate
subdirectory so that a single copy can be centrally held on a multiuser
system. 

4. All identifiers in a BNF file (both production names and attribute names)
are now checked against a list of C++ reserved words and common library
names, so a production called, say, 'register' will be spotted before you get
to the C compilation stage. You can add extra names to the check list by
inserting them into the initialiser for array rdp__reserved_words in
rdp_aux.c, and recompiling.

5. RDP and its generated scanners now report exactly which tokens they were
expecting when a syntax error occurs rather than just referring to the name of
the follow set.

6. RDP now checks for unused productions, and issues a warning message if it
finds any. Unused productions are suppressed in the C parser.

7. A production consisting of just a sequence of semantic actions is now
allowed. The body of such a production will be inserted in-line whenever it is
referenced in the grammar. This allows large semantic actions to be placed
away from the actual grammar rule. By convention, names of the form _1, _2 and
so on are used for semantic productions but you are free to adopt your own
style if you wish. See the user manual for suggestions on how to use this
feature. Thanks to Andy Betts for his suggestions which led to this idea.

8. Generated code is now C++ clean, i.e. I have changed identifier names that
clash with C++ reserved words.

9. The NUMBER scanner primitive has been replaced by INTEGER

10. INTEGER now returns a variable of typedef integer which is defined in
symbol.h.

11. INTEGER now recognises C-style hexadecimal numbers such as 0xFE

12. The HEX_DOLLAR directive has been removed as it is now redundant.

13. A REAL scanner primitive has been added. It will parse C-style floating
point numbers, returning a variable of typedef real which is defined in
symbol.h.

14. Symbol table structure symbol__ has changed. You may need to change your
semantic actions to a slightly different form.

15. The text strings relating to numbers are now left in the text table and
pointed to by scan__sym->id. This is to allow 64-bit users to pull out the
digits and construct 64-bit constants. 

16. Semantic actions are now delimited by [* ... *] instead of  " ... ". This
reduces the need to quote and also allows actions to run over several lines
without needing to separately quote each line.

17. Consecutive semantic actions are now spliced together in the output so that
the actions [* **] [*] *] appear in the output as ' *] '. This avoids the need
for an escape character in semantic actions.

18. A -p (parser only) option has been added to rdp and rdp_gen. When set,
RDP suppresses the writing out of semantic actions, so you get a pure parser
that will just syntax check your source file.
 
19. All echoing now goes to the stream MESSAGES which is defined in params.h
to be stderr. You can send them somewhere else (such as stdout) by changing
this definition and recompiling everything. 

20. A -f (filter mode) option has been added as a standard option. When set,
RDP and RDP generated parsers read from stdin and write to stdout. Note that
all options are read strictly left to right, and so a subsequent -o option
will override the use of stdout, and a subsequent filename (i.e. any
parameter without a leading - sign) will override the use of stdin.

21. The STRING_ESC primitive now recognises all C-style escape sequences (but
not trigraph sequences - does anybody want them?)

22. COMMENT_LINE and COMMENT_LINE_VISIBLE primitives have been added which
can be used to parse Ada-style comments which start with a token and continue
to the end of the line. (Actually, these were in version 1.1 but I forgot to
document them).

23. The OPTION directive now has an optional semantic action. If missing,
then a line is generated for the help message only. This is useful for adding
text only lines to the help message.

24. RDP generated parsers now announce both when they were generated, and
when they were last compiled when -v is used or if an error occurs.

25. In verbatim mode, if no continuation tokens are needed, this is announced.

26. Parameters have been collected together into header file params.h in
subdirectory rdp_supp. In particular, the datatypes corresponding to INTEGER,
REAL and so on are typedef'ed in here, and you can also define printf()
format strings for them. If you change the typedef's you MUST update the
corresponding FORMAT strings because RDP may use these types internally, and
printing of parser files will break if you create a mismatch between the type
and its FORMAT string.

27. Pascal alternate style comments (*...*) are now accepted by the Pascal 
parser.

BUG FIXES 

28. When no SUFFIX directive is used, RDP generated parsers no longer append
a period (.) to the source file name. This bug slipped through because I
develop on MS-DOS, where filenames test and test. mean the same thing.
Several Unix users castigated me for this, and quite right too.

29. Various aspects of the output indentation code have been cleaned up so
that the output indentation is now reliable and supports sequences of code
items. 

30. A bug which meant that the -t and -T options on generated parsers did not
work has been fixed (Thanks to Andy Betts for diagnosing this one).

31. A very obscure bug that led to continuation tokens not being generated
for the lexically first token has been fixed. (Double thanks to Andy Betts
for finding this one).

32. A very stupid bug that led to line echoing being suppressed before the
first token was encountered, so that initial comments were lost, has been
fixed. How come nobody else noticed this?

33. At the end of a text mode section, the unused file variable was being
fclose'd. Thanks to Simon Richardson for finding this one.

34. When not in verbose mode, generated parsers might end up with sets that
were not big enough to hold a list of tokens. This potentially very serious
bug has now been fixed.

35. I added a MATHS macro to the makefile so that the many Unix systems that
require -lm to be the last option on a link line can be made happy. Apologies
to the approximately one zillion people that told me about this: Ultrix and
OSF/1 don't seem to mind...

36. I added a HERE macro to the makefile which prepends ./ (or .\ on MS-DOS)
to all executable invocations for users that don't have . in their path.

37. The default rule in the makefile for compilation has been changed from
.c.o to .c$(OBJ).

38. Previously, errors that were only reported after EOF had been read on the
outermost file caused a variety of garbage error messages and even core dumps.
I've fixed this by not discarding the outermost file descriptor. Thanks in
particular to Jason Weston and Mark Stitson for supplying files that
illustrated this problem.

39. Unbelievably, iteration brackets {...} were not being checked for
disjoint first and follow sets. This is because I had been adding the first
sets to the follow sets to make the error handling work. Of course, this
generates first/follow errors for all iterations, so I disabled he error
checking...  The correct solution was to do the checking, then add first to
follow for iterations and then reclose the follow sets. I'd like to apologise
to my 1994 compilers class for causing several of them to present me
with incorrect solutions as a result of this bug. I'd like to, but I won't,
because anyone who thinks p ::= 'x' {'a' 'b'} 'a'. is LL(1) doesn't deserve an
apology. 


**** Features for future versions

The next version (1.3) will support inherited attributes, including the
passing of production names into productions allowing macro style grammars in
the style of PRECC. I'd like to thank the authors of PRECC (Jonathan Bowen and
...) for their help and advice. While I'm at it, I'd also like to thank
Terence Parr (the author and maintainer of PCCTS) for his helpful comments. 

I have a C grammar derived directly from the ANSI grammar in Kernighan and
Ritchie second edition. It isn't too well tested at the moment, so I didn't
want to release it at this time. If you are very anxious to try using RDP
with this C grammar, then mail me and I'll send you what I have. I also have
a couple of other grammars for C that have been hacked out of the LL(1) C
grammars available on the net. Look on cscx.cs.rhbnc.ac.uk (134.219.200.45)
in  directory /pub/cgrammar

I am developing a replacement for Toy which is a true C subset (called,
provisionally, C--). This will be supplied as an interpreter and there will be
changes to the as yet undocumented interpreter and macro hooks in the scanner.

I have had the ISO standard Pascal grammar typed in, which is very different
to the one in this release. In particular it includes productions for
conformant array parameters. I will distribute the new grammar at version 1.3.
Right now it still has an LL(1) conflict which I am trying to resolve
elegantly. If you want an early version, email me. 

RDP V1.30 should be out before the end of 1994.


A future major release (V2.00) will support scanner productions which
supercede the various programmable tokens used at the moment. RDP will then
write out a customised scanner for each language in exactly the same way that
it currently writes out parsers. These generated scanners will look just like
the existing scanner (i.e. they will NOT be DFA based) so that they can be
debugged easily. A set of scanner productions equivalent to the current
scanner will be supplied.

Also at V2.00, I hope to support permutation phrases, as described in a
recent edition of LOPLAS.  

Perhaps at V2.00, you will be able to selectively allow backtracking so
allowing LL(k) grammars to be parsed. If you need this a lot you should be
looking at PCCTS or PRECC not RDP.

RDP V2.00 will not be available until 1995.

Finally (surprise, surprise) a textbook on code generation for modern
processors, using RDP to construct the compiler front ends, will appear
eventually. Anybody interested in beta-testing the book might like to get in
touch, but we're not expecting to make much progress on it until the Spring of
1995.


**** Here follows a thumbnail sketch of RDP ****

RDP - a recursive descent parser generator

RDP compiles attributed LL(1) grammars decorated with C-language semantic
actions into recursive descent compilers. RDP is written in strict ANSI C and
produces strict ANSI C. RDP was developed using Borland C 3.1 on a PC and has
also been built with no problems on Alpha/OSF-1, DECstation/Ultrix Sun
4/SunOS and NetBSD 0.9 hosts all using GCC. The definition of strict ANSI
here means  compiling with 

gcc -Wmissing-prototypes -Wstrict-prototypes -fno-common -Wall -ansi -pedantic

and getting no warning messages. 

RDP also compiles with Borland Turbo-C and Microsoft C version 7.0 although
you may get some bitfield alignment warnings, 

RDP is C++ `clean' i.e. there are no identifiers used that clash with C++
reserved words. RDP generated code has been used with g++ applications, and
compiles with g++ and the Borland C++ (as opposed to ANSI) compiler.

RDP itself and the language processors it generates use standard symbol, set
and scanner modules. The RDP scanner is programmed by loading tokens into the
symbol table at the start of each run. In principle, the RDP scanner can be
used to support runtime extensible languages, such as user defined operators
in Algol-68, although nobody has had the nerve to try this yet.

RDP produces complete runnable programs with built-in help information and
command line switches that are specified as part of the EBNF file. In this
sense RDP output is far more shrink-wrapped than the usual parser generators
which helps beginning students. RDP can generate itself which is a nice
demonstration of the bootstrapping technique used for porting compilers to
new architectures.

I wrote RDP because in October I started giving a course on compiler design.
I don't think the world needs another course on parsing techniques and am
really interested in code generation for exotic processors, so I tried to
produce a compact parser generator that would enable undergraduates to rip
through the syntax and standard code generation parts of the syllabus in a
few weeks, thus leaving me time to get into the black arts of code scheduling
and optimisation.

What you get.

o An implementation of Pascal style set-handling in C.

o A hash-coded symbol table with support for scoping and arbitrary user data.

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

o An EBNF file describing Pascal which generates a parser for the Pascal 
  language. 

o A pre-built RDP executable for MS-DOS.

o Sources, makefiles and Borland 3.1 project files for everything which
  you may use freely on condition that you send copies of any modifications,
  enhancements and ill-conceived changes you might make back to me so that
  I can improve RDP.

o Manuals

What you don't get.

o Tutorial information on parsing and compiling (try Wirth's Algorithms +
  Data Structures = Programs, Chapter 5 or your favourite compiler book).
  Maybe by the end of next year there'll be a book by me.

o Warranties. This code has only just escaped from my personal toolkit.
  I've put a lot of effort into making it fit for others to use, but in
  the very nature of compiler compilers it is hard to test all the angles,
  and the Garbage In - Garbage Out principle holds to the highest degree:
  if you write ill-formed semantic actions you won't find out until you try
  and compile the parser that RDP wrote for you. On the plus
  side, RDP has now been used pretty ferociously by several people
  and has stood up very well. It does seem to be pretty stable, and I will
  continue to respond to bug reports.


                                      Adrian

***
Comments, queries and requests for code to

Dr Adrian Johnstone, CS Dept, Royal Holloway, University of London
Email: adrian@dcs.rhbnc.ac.uk
