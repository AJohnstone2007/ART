import java.io.FileNotFoundException;import java.util.LinkedList;

class ARTGeneratedRDSOBOracle extends uk.ac.rhul.cs.csle.art.cfg.rdsob.AbstractRDSOB {

boolean parse_S() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal S, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (match("a")) { return true; }

  /* Nonterminal S, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (match("b")) {
  if (match("{")) {
  if (match("x")) {
  if (match("}")) { return true; }}}}

  return false;
}

void semantics_S() {
  switch(oracle[oracleIndex++]) {
    case 1:
    match("a");
    break;

    case 2:
    match("b");
    match("{");
    match("x");
    match("}");
    break;
  }
 }

void parse(String filename) throws FileNotFoundException {
  input = readInput(filename);

  System.out.println("Input: " + input);  inputIndex = oracleIndex = 0; builtIn_WHITESPACE();
  if (!(parse_S() && input.charAt(inputIndex) == '\0')) { System.out.print("Rejected\n"); return; }

  System.out.print("Accepted\n");
  System.out.print("Oracle:"); for (int i = 0; i < oracleIndex; i++) System.out.println(" " + oracle[i]); System.out.println();
  System.out.print("Semantics:\n"); inputIndex = oracleIndex = 0; builtIn_WHITESPACE(); semantics_S();
}

public static void main(String[] args) throws FileNotFoundException{
    if (args.length < 1) System.out.println("Usage: java ARTGeneratedRDSOBOracle <input file name>");
    else  new ARTGeneratedRDSOBOracle().parse(args[0]);

  }
}
