# Ambiguity Retained Translation (ART)

ART is the Royal Holloway software language engineering tool written by Adrian Johnstone and tested by Elizabeth Scott; we are the authors of the GLL, RNGLR and BRNGLR algorithms.

The default ART pipeline processes language inputs using an MGLL parser coupled to a general lexer, with semantics specified using either SOS-style reduction rules or attribute-action rules. The core idea in ART is to delay ambiguity resolution until a natural point in the pipeline by allowing alternative interpretations to coexist.

See the [doc](https://github.com/AJohnstone2007/ART/tree/main/doc) directory for getting started, technical and tutorial documentation.

A variety of alternative algorithms are available in ART; in particular 'reference' GLL implementations that illustrate the central ideas along with a variety of optimisations. An early publication on these versions, and the associated presentation slides are [here](https://github.com/AJohnstone2007/ART/tree/main/doc/referenceImplementations). The corresponding code is [here](https://github.com/AJohnstone2007/ART/tree/main/src/uk/ac/rhul/cs/csle/art/cfg/gll).

To characterise the space and time performance of our algorithms, 
we use a variety of language grammars and language examples which you can find [here](https://github.com/AJohnstone2007/ART/tree/main/corpora).

You can access our research papers at https://pure.royalholloway.ac.uk/en/persons/adrian-johnstone/publications.

The code and other material in this repository is &copy; 2010-2024 by Adrian Johnstone but licensed under CC BY 4.0

Adrian Johnstone, a.johnstone@rhul.ac.uk
