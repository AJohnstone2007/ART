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

 class Attribute_S { int v; }

 void semantics_S(Attribute_S S) {
 switch(oracle[co++]) {
 case 1: {
 Initial action 
 match("b");
 Found b 
 break; }

 case 2: {
 Attribute_X X1 = new Attribute_X();

 match("a");

 // Instance 1
 semantics_X(X1);

 match("@");

 break; }

 }
 }

 class Attribute_X { }

 void semantics_X(Attribute_X X) {
 switch(oracle[co++]) {
 case 1: {
 Attribute_X X1 = new Attribute_X();

 match("x");

 // Instance 1
 semantics_X(X1);

 break; }

 case 2: {

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
  System.out.print("\nTree construction phase\n"); cc = 0; co = 0; builtIn_WHITESPACE();
  TreeNode dt = new TreeNode("S", tree_S(), null, TreeKind.NONTERMINAL, GIFTKind.NONE);
  dt.dot("dt.dot");  System.out.print("\nDerivation term\n"); dt.printTerm(0);
  System.out.print("\n\nDerivation tree\n"); dt.printTree(0);
  TreeNode cloneRoot = dt.clone(null, null);
    cloneRoot.dot("clone.dot");

    // System.out.print("\nCloned derivation tree\n");
    // cloneRoot.printTree(0);
    TreeNode rdtEpsilon = dt.evaluateTIF(null, null, true);
    rdtEpsilon.dot("rdtEpsilon.dot");

    //System.out.print("\nRDTEpsilon fold tree\n");
    //rdtEpsilon.printTree(0);
    rdtEpsilon.foldunderEpsilon();
    rdtEpsilon.dot("rdtEpsilonFold.dot");

    //System.out.print("\nAnnotated RDTEpsilon tree\n");
    //rdtEpsilon.printTree(0);
    rdt = rdtEpsilon.evaluateTIF(null, null, true);
    rdt.dot("rdt.dot");

    System.out.print("\nRewritten Derivation term\n"); rdt.printTerm(0);
    System.out.print("\n\nRewritten Derivation Tree\n");
    rdt.printTree(0);
    postParse(rdt);

}

public static void main(String[] args) throws FileNotFoundException{
    if (args.length < 1) {
      System.err.println("No input file name supplied");
      System.exit(1);
    } else
      new ARTGeneratedRDSOB().parse(args[0]);
  }
}
