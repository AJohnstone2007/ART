# Ambiguity Retained Translation (ART)

ART is the Royal Holloway software language engineering tool written by Adrian Johnstone with support from Elizabeth Scott; we are the authors of the GLL, MGLL, RNGLR and BRNGLR algorithms.

The default ART pipeline processes language inputs using an MGLL multiparser coupled to a general lexer, with semantics specified using either SOS-style reduction rules or attribute-action rules. The core idea in ART is to delay ambiguity resolution until a natural point in the pipeline by allowing alternative interpretations to coexist.

A variety of other algorithms are available within ART; in particular *reference* GLL implementations that illustrate the central ideas along with a variety of optimisations. An early publication on these versions, and the associated presentation slides are [here](https://github.com/AJohnstone2007/ART/tree/main/doc/referenceImplementations), and a recording of the presentation is [here](https://www.youtube.com/watch?v=lwNhOL4eV2U). The corresponding code is [here](https://github.com/AJohnstone2007/ART/tree/main/src/uk/ac/rhul/cs/csle/art/cfg/gll).

To run from the command line, all you need is a Java installation and a copy of **art.jar** which is the current stable version. **artALPHA.jar** is the current development version which should be treated with caution. 

If you wish to use ART's Integrated Development Environment then you will also need an operating system-specific JavaFX installation which you can download from https://gluonhq.com/products/javafx/. You will also need a copy of **richtextfx.jar** from https://github.com/FXMisc/RichTextFX.

Reference, tutorial and internals information is [here](https://github.com/AJohnstone2007/ART/tree/main/doc). The four main documents are

* [ARTRef](https://github.com/AJohnstone2007/ART/blob/main/doc/artRef.pdf)
Reference material on the ART Script Language, along with a Getting Started guide in Chapter 1.

* [ARTSLE](https://github.com/AJohnstone2007/ART/blob/main/doc/artSLE.pdf)
An introduction to Software Language Engineering using ART, as used in the Royal Holloway undergraduate SLE course.

* [ARTLab](https://github.com/AJohnstone2007/ART/blob/main/doc/artLab.pdf)
A sequence of lab exercises and a project specification for the Royal Holloway undergraduate SLE course.

* [ARTInt](https://github.com/AJohnstone2007/ART/blob/main/doc/artInt.pdf)
A sequence of technical notes on various aspects of ART's implementation.

To characterise the space and time performance of our algorithms, we use a variety of language grammars and language examples which you can find at https://github.com/AJohnstone2007/referenceLanguageCorpora.

You can access our research papers at https://pure.royalholloway.ac.uk/en/persons/adrian-johnstone/publications.

Many of those papers include data generated using our earlier tools, or earlier versions of ART which may be found in the [old](https://github.com/AJohnstone2007/ART/tree/main/old) directory.

The code and other material in this repository is &copy; 1993-2025 Adrian Johnstone but licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)

Adrian Johnstone, a.johnstone@rhul.ac.uk
