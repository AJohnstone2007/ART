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
  if (match("x")) {
  if (parse_X()) { return true; }}

  /* Nonterminal X, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  /* epsilon */  return true; 
}

void semantics_S() {
  switch(oracle[oracleIndex++]) {
    case 1:
    match("b");
    break;

    case 2:
    match("a");
    semantics_X();
    match("@");
    break;
  }
 }

void semantics_X() {
  switch(oracle[oracleIndex++]) {
    case 1:
    match("x");
    semantics_X();
    break;

    case 2:
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
