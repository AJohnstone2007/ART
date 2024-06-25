package uk.ac.rhul.cs.csle.art.esos;

/*
 * This is a compiler and simulator for the HeSOS hardware eSOS implementation
 * Rough counts:
 *
 * ARTValue provides 52 functions, plus one type check isxxx function - six bits
 * ARTValue provides 46 types - six
 *
 * With function evaluation as part of substitution, 8 variables should be enough for most rules (and in fact four might be enough) but let's allow 16

 * We are going to use a ten bit wide external memory with a twenty bit address bus for this first implementation, allowing 20 bits for the term address to 1M terms
 *
 * Allow seven bits for functions and seven for types (ie up to 128 of each)
 * Allow four bits for variables
 *
 * Special constructors:
 * zero - error
 * This would allow:
 * 512 constructors
 * 256
 *
 */
public class ARTHeSOS {

}
