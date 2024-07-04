# Older generation tools from the Centre for Software Language Engineering

#### RDP (1994-1997) - Recursive Descent Parsers (ANSI C)

A generator for Recursive Descent parsers from EBNF grammars with embedded C actions and a library of support functions for implementing small Domain Specific Languages

#### gramex (2007-2012) - grammar extract (ANSI C)

A tool for extracting grammar rules from text files; used to pull grammars from Standards documents.

#### gramconv (2015-2019) - grammar convert (ANSI C)

A tool for applying various grammar transforms, and writing out grammars in formats suitable for a varoiety of parser generators

#### GTB (1997-2008) - Grammar Tool Box (ANSI C)

Our main research platform during the development of RNGLR, RIGLR, BRNGLR and GLL algorithms. 

#### 2008-2009 ART V1 (ANSI C)

First implementation of GLL generator algorithm hosted by GTB with a separate support library

#### 2009-2012 ART V2 (ANSI C)

A dedicated GLL parser generator with L-attributed evaluation based semantics using a set of recursive functions, much like a recursive descent parser

#### 2012-2019 ART V3 (Java)

Initially a port of ARTV2 to Java, but subsequently heavily developed with variant GLL algorithms an initial version of an SOS rewriter. V3 grammars are represented using a set of Java classes which directly concretise the EBNF metasymbols.

#### Tokenisation

Working files and technical information on the way that we produce `.tok` files by using RDP to extract lexical element