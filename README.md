# Ambiguity Retained Translation (ART)

ART is the Royal Holloway software language engineering tool written by Adrian Johnstone and tested by Elizabeth Scott; we are the authors of the GLL, RNGLR and BRNGLR algorithms.

The default ART pipline processes language inputs using MGLL Parsing coupled to a general lexer with semantics specified using either SOS-style reduction rules or attribute-action rules. 

A variety of alternative algorithms are also available; in particular 'reference' GLL implementations that illustrate the core ideas and a variety of optimisations. An early publication on these versions, and the corresponding presentation slides are [here](https://github.com/AJohnstone2007/ART/tree/main/doc/referenceImplementations). The corresponding code is [here](https://github.com/AJohnstone2007/ART/tree/main/src/uk/ac/rhul/cs/csle/art/cfg/gll)

See the [doc](https://github.com/AJohnstone2007/ART/tree/main/doc) directory for getting started, technical and tutorial documentation.

We use a variety of langauge grammars and language examples to characterise the space and time performance of our algorithms which you can find [here](https://github.com/AJohnstone2007/ART/tree/main/corpora)

You will find our research papers at https://pure.royalholloway.ac.uk/en/persons/adrian-johnstone/publications

The code and other material in this repository &copy; 2010-2024 by Adrian Johnstone is licensed under CC BY 4.0

Adrian Johnstone, a.johnstone@rhul.ac.uk
