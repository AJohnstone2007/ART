import java.io.FileNotFoundException;

class ARTGeneratedRDSOB extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBV4Base {

boolean parse_S() {
  int rc = cc, ro = co;

  /* Nonterminal S, alternate 1 */
  cc = rc; co = ro; oracleSet(1);
  if (match("b")) { return true; }

  /* Nonterminal S, alternate 2 */
  cc = rc; co = ro; oracleSet(2);
  if (match("a")) {
  if (parse_X()) {
  if (match("@")) { return true; }}}

  return false;
}

boolean parse_X() {
  int rc = cc, ro = co;

  /* Nonterminal X, alternate 1 */
  cc = rc; co = ro; oracleSet(1);
  if (match("x")) {
  if (parse_X()) { return true; }}

  /* Nonterminal X, alternate 2 */
  cc = rc; co = ro; oracleSet(2);
  /* epsilon */  return true; 
}

 class Attribute_S { }

 void semantics_S(Attribute_S S) {
 switch(oracle[co++]) {
 case 1: {

 match("b");

 break; }

 case 2: {
 Attribute_X X1 = new Attribute_X();

 match("a");

 // Instance 1
 semantics_X(X1);

 match("@");
 System.out.println("Final x count: " + X1.xCount); 
 break; }

 }
 }

 class Attribute_X { int xCount; }

 void semantics_X(Attribute_X X) {
 switch(oracle[co++]) {
 case 1: {
 Attribute_X X1 = new Attribute_X();

 match("x");
 System.out.println("Found an x"); 
 // Instance 1
 semantics_X(X1);
 X.xCount = X1.xCount + 1; 
 break; }

 case 2: {
 X.xCount = 0; 
 /* epsilon */

 break; }

 }
 }

void parse(String filename) throws FileNotFoundException {
  input = readInput(filename);

  System.out.printf("Input: '%s'\n", input);
  cc = co = 0; builtIn_WHITESPACE();
  if (!(parse_S() && input.charAt(cc) == '\0')) {System.out.print("Rejected\n"); return; }

  System.out.print("Accepted\n");
  System.out.print("Oracle:"); for (int i = 0; i < co; i++) System.out.printf(" %d", oracle[i]); System.out.printf("\n");
  System.out.print("\nSemantics phase\n"); cc = 0; co = 0; builtIn_WHITESPACE(); Attribute_S S = new Attribute_S(); semantics_S(S);
}

public static void main(String[] args) throws FileNotFoundException{
    if (args.length < 1) {
      System.err.println("No input file name supplied");
      System.exit(1);
    } else
      new ARTGeneratedRDSOB().parse(args[0]);
  }
}
