# Tokenisation

Some parsing algorithms perform lexicalisation as a separate phase that runs before the parsers starts; some parsers are in a producer-consumer relationship with the lexical phase. In the latter case, the lexicalisation computations are intermingled with the parser which complicates performance measurement.

We have two approaches to smoothing out the overheads presented by lexicalisation.

1. Use a 'character level' grammar, that is one in which the grammar's terminals are individual characters

2. 'Tokenise' the input strings, by replacing (say) all identifiers with the keyword ID, and similarly for other paraterminal patterns.

These tokenised strings have file type `.tok`; this is a technical note describing one technique for building tokenisation tools.

RDP has a little-known option -L which prints out a tokenised version
of an input string. Whitespace and comments are output as a single
space character; matches to the RDP primitives such as INTEGER, STRING
and ID are output as the name of the primitive, and all other tokens
are passed to the output unchanged.

Now, this process requires an RDP-generated parser to be built for the
language of the strings. RDP only admits LL(1) grammars, so in general
we cannot expect to simply put, say, the ANSI-C grammar through RDP to
build a parser from which lexicalised strings are made. However, we do
not really care about the parser, so in fact a grammar of the form

`S ::= { STRING | ID | INTEGER | ....   (* all RDP primitives *) |

`        'keyword1' | 'keyword2' | ...  (* all keywords in the 'real' grammar *)`

`      }.``

will be sufficient. This is just the Kleene closure over all the
tokens in the 'real' grammar.

Now, the question is how best to extract the keywords from a grammar?
Well, when RDP parses a .bnf file, it collects all of the keywords
into a symbol table called 'tokens'. The -S option to RDP causes the
contents of all symbol tables to be printed after generation of the
parser. For this to work with a non-LL(1) parser we also need the -F
RDP option so as to force the parser (which we shall discard) to be
output. If we capture this output, it is quite simple to edit it down
into a grammar of the form above. We then submit this lexicalising
grammar to RDP, build the parser, and run it on our strings with the
-L option.

## Worked example using the ANSI-C grammar.

First get a grammar in rdp format: see the file ansi_c.bnf in this directory.

Now pass through rdp to catch the symbol table output:

`rdp -S -F ansi_c.bnf > ansi_c_lex.bnf`

Now edit the `ansi_c_lex.bnf` file to remove everything except for the
dump by scope chain for tables tokens.

Add to the top of the grammar `S ::= { INTEGER | REAL | STRING_ESC('"' '\\') | STRING_ESC('\'' '\\') | ID |`

and add to the bottom of the grammar `}.`

Wrap each token line with ' and ' |

Suppress the tokens for ' and " which are taken care of with the
STRING declarations above.

Add in specifications for comments and preprocessor directives which
are not in the main grammar:

`'#include' | '#if' | '#endif' | '#define' |`

Now pass through rdp to make the lexical parser and build:

`rdp ansi_c_lex.bnf`

