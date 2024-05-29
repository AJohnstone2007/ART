Extraction of grammar rules from the JLS18 document https://docs.oracle.com/javase/specs/jls/se18/jls18.pdf

V02.00 23 July 2022 by Adrian Johnstone a.johnstone@rhul.ac.uk


Overview 

The Java Language Specification is maintained by Oracle. 

At the time of writing, the most recent version is JLS18 accessible as https://docs.oracle.com/javase/specs/jls/se18/jls18.pdf

Syntax rules are specified using typeset conventions which are unsuitable for direct input to most parser generators. 

Here we describe the process by which the JLS18 grammar is extracted for use with the Royal Holloway ART tooling.


1. Challenges

The rules use a set of typset conventions which are similar to those of the original ANSI-C standard, but extended to allow Kleene closure and some other abbreviations.

The JLS version of these typeset conventions is described in section 2.4 of JLS18

Indentation is used to allow long lines to be broken. There is a typesetting error in rule OrdinaryCompilationUnit: which omits indentation, turning a continuation into an alternation

In a few places, rules contain English language phrases which are used as informal specification. These rules need to be replaced by formal rules.

The Java standard requires all Unicode escape sequences of the form \Uxxxx to be converted to Unicode characters before parsing begins. Prior to Java 6? this was done with an external filter. Now this transformation is specified using context free rules, but still as a preprocess. This leads to some entanglement in section 3 of the specification, and ideed to nasty suprises for programmers since \uXXXX escapes are very different animals to the in-string escapes such as \Oxyz.

The use of Unicode escapes outside of character, string and text block literals is extremely rare, and judging by discussion on StackOverflow this aspect of the Java standard is poorly understood by programmers. Ity is natural for them to be rare, since the last paragraph of section 3.2 reads:

Except for comments (§3.7), identifiers (§3.8), and the contents of character literals, string literals, and text blocks (§3.10.4, §3.10.5, §3.10.6), all input elements (§3.5) in a program are formed only from ASCII characters (or Unicode escapes (§3.3) which result in ASCII characters).

Hence, all programs could be written in ASCII except for the contents of characterm string and text block literals.
 
We would prefer to parse over the ASCII character set, since that enables compact table-based lookup in DFA lexers.

As a result, the preprocessor-related rules are omitted from section 3, and Unicode escapes are added to the definition of SingleCharacter, StringCharacter and TextBlockCharacter


2. Selection of source 

There are two sources of the jls18 at https://docs.oracle.com/javase/specs/ each of which may be used to extract raw (textual) rules.

A. A link to an HTML page https://docs.oracle.com/javase/specs/jls/se18/html/index.html

B. A link to a PDF version of the spec https://docs.oracle.com/javase/specs/jls/se18/jls18.pdf

Text cut and paste may be used to extract raw textual versions of the rules from either document

IMPORTANTLY there is some extra vertical whitespace in the PDF version which makes it unsuitable without a little manual editing. For instance rule OrdinaryCompilationUnit is presented in the typeset text and HTML as

OrdinaryCompilationUnit:
[PackageDeclaration] {ImportDeclaration} {TopLevelClassOrInterfaceDeclaration}

but in jls18FromPDF.raw it appears as 

OrdinaryCompilationUnit: 
[PackageDeclaration] {ImportDeclaration} 
{TopLevelClassOrInterfaceDeclaration} 

This is significant because in the JLS, alternate productions appear on separate lines so this looks like two productions not one.

This issue appears in the rules for the following nonterminals, which would need manual editing before extraction

OrdinaryCompilationUnit: ModuleDeclaration: NormalClassDeclaration: UnannClassType: ConstructorDeclarator: RecordDeclaration: NormalInterfaceDeclaration: 
AnnotationInterfaceElementDeclaration: EnhancedForStatement: EnhancedForStatementNoShortIf: UnqualifiedClassInstanceCreationExpression: ClassOrInterfaceTypeToInstantiate:

All but one of these arise from the use of the 'continuation' convention in JLS, that an indented line represents a continuation of the line above. Unfortunately, indentation is not preserved by either Adobe's PDF to text translation nor a text based cut and paste from the rendered PDF.

An important special case is OrdinaryCompilationUnit: In the type set PDF text, both in the body at section 7.3 on page 212 and in the accumulated grammar on page 805 there should be indentation before{TopLevelClassOrInterfaceDeclaration} This intepretation is confirmed by the descriptive text on page 212. The HTML rendering is correct.

As a result, it is best to use copy and paste from the HTML rendering of the document. As a side-benefit, the process is now independent of PDF tooling which may change in the future.


3. Creating the raw specification file from section 3

Use a Web browser and text cut and paste tocreate file jls18_sec3_FromHTML.raw as follows

Include all rules from section 3 (https://docs.oracle.com/javase/specs/jls/se18/html/jls-3.html) EXCEPT FOR 

UnicodeInputCharacter:
RawInputCharacter:
LineTerminator:
Input:
InputElement:
Token:
Sub:
WhiteSpace:
NotStar:
NotStarNotSlash:
JavaLetter:
JavaLetterOrDigit:
Literal:
SingleCharacter:
StringCharacter:
TextBlockCharacter:

NOTE: there are two instances of the rule for OctalDigit in section 3, on pages 35 and 51 of the PDF document, that is in sections 3.10.1 and 3.10.7. Only one should be included.

NOTE: there are two instances of the rule for HexDigit in section 3, on pages 20 and 34 of the PDF document, that is in sections 3.3 and 3.10.1. Only one should be included.

NOTE: paraterminals may not reference each other, so in rules TypeIdentifier: UnqualifiedMethodIdentifier: change Identifier to IdentifierChars

NOTE: remove the annotation (underscore) from the last alternative of nonterminal ReservedKeyword


3. Creating the raw specification file from section 19

Use a Web browser and text cut and paste to create file jls18_sec19_FromHTML.raw

Include all rules from section 19 (https://docs.oracle.com/javase/specs/jls/se18/html/jls-19.html) EXCEPT FOR 
TypeIdentifier:
UnqualifiedMethodIdentifier:
Identifier:
IdentifierChars:
JavaLetter:
JavaLetterOrDigit:

4. Create JLS18CharacterClassRules.raw

Some of the omitted section 3 rules are instanced by other rules. The extractor needs to know that these names refer to nonterminals, not terminals.

Create file JLS18CharacterClassRules.raw with these contents.

LineTerminator
InputCharacter
WhiteSpace
NotStar
NotStarNotSlash
JavaLetter
JavaLetterOrDigit
SingleCharacter
StringCharacter 
TextBlockCharacter

5. Use the extractor to make jls18Base.art

The class uk.ac.rhul.cs.csle.art.cfg.extract.ExtractJLS is a hard coded translator from the textual forms discussed above to ART source code.

For details of the translations, examine the source file src/uk/ac/rhul/cs/csle/art/cfg/extract.ExtractJLS.java

The ART directive !extractJLS takes five argments: <omitted character class nonterminals filename> <lex raw filename> <parse raw filename> <output filename> <start symbol>
 
Run ART V4 with !extractJLS JLS18CharacterClassRules.raw jls18_sec3_FromHTML.raw jls18_sec19_FromHTML jls18Base CompilationUnit


6. Incorporation of character class rules

The omitted rules mostly describe subsets of the character classes. In this exercise we restrict ourselves to ASCII-only source files. 

File jls18LexCharacters.art contains a suitable set of ASCII character rules.

NOTE nonterminals SingleCharacter, StringCharacter and TextBlockCharacter have been extended with a production x ::= UnicodeEscape to allow Unicode escapes in text literals

This file should be concatenated with jls18Base.art to make a complete parser jls18.art - if using Windows copy x + y command remeber to remove the trailing ^z character


7. Simple test

Test the final jls18 grammar by running it on the two files in the main corpus. The file parseTwo.bat performs these actions, and leaves the generated porser and lexer in place for subsequent bulk testing

8. Bulk tests

Run bulk test batch files and capture outputs. The second step takes about an hour on a fast laptop under Windows

parseSLETestSet.bat > parseSLETestSetLog.txt 2>&1
parsejfxSourceFlatCompressed > parsejfxSourceFlatCompressedLog.txt >2&1

9. Unusual examples

There is one file in directory jfxSourceFlatCompressed which fails to be parsed.

C:\csle\pub\journals\toplas_lexerParser\current\revision\experiments\grammars\jls18>java -classpath .;\csle\dev\art/art.jar ARTV3TestGenerated jfxSourceFlatCompressed\JavaScriptBridgeTest.java 
360,20 Parser error: found unexpected token after `E
  360: assertEquals('\u00CE\u00A9', charTest.c);
--------------------------^

** Reject

This is in fact correctly rejected: the character constant contains two characters. Both this file and the corresponding original (pre-compression) file are rejected by the Oracle javac compiler
 