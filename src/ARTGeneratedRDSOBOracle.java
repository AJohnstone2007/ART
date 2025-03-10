import java.io.FileNotFoundException;import java.util.LinkedList;

class ARTGeneratedRDSOBOracle extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBAbstract {

boolean parse_S() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal S, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (match("b")) { return true; }

  /* Nonterminal S, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (match("a")) {
  if (parse_X()) {
  if (match("@")) { return true; }}}

  return false;
}

boolean parse_X() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal X, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (match("1")) {
  if (parse_X()) { return true; }}

  /* Nonterminal X, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (match("0")) {
  if (parse_X()) { return true; }}

  /* Nonterminal X, alternate 3 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(3);
  /* epsilon */  return true; 
}

void semantics_S() {
  Attributes_X X1 = new Attributes_X(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    match("b");
    break;

    case 2:
    match("a");
    semantics_X(X1);
    System.out.println("Result is: " + X1.v); 
    match("@");
    break;
  }
 }

class Attributes_X { int v; }

void semantics_X(Attributes_X X) {
  Attributes_X X1 = new Attributes_X(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    match("1");
    semantics_X(X1);
    X.v = 1 + X1.v; 
    break;

    case 2:
    match("0");
    semantics_X(X1);
    X.v = X1.v; 
    break;

    case 3:
    X.v = 0; 
    /* epsilon */
    break;
  }
 }

void parse(String filename) throws FileNotFoundException {
  input = readInput(filename);

  System.out.println("Input: " + input);  inputIndex = oracleIndex = 0; builtIn_WHITESPACE();
  if (!(parse_S() && input.charAt(inputIndex) == '\0')) { System.out.print("Rejected\n"); return; }

  System.out.print("Accepted\n");
  System.out.print("Oracle:"); for (int i = 0; i < oracleIndex; i++) System.out.printf(" " + oracle[i]); System.out.printf("\n");
  System.out.print("Semantics:\n"); inputIndex = oracleIndex = 0; builtIn_WHITESPACE(); semantics_S();
}

public static void main(String[] args) throws FileNotFoundException{
    if (args.length < 1) System.out.println("Usage: java ARTGeneratedRDSOBOracle <input file name>");
    else  new ARTGeneratedRDSOBOracle().parse(args[0]);

  }
}
