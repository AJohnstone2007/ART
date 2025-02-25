import java.io.FileNotFoundException;

class ARTGeneratedRDSOB extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBV4Base {

boolean parse_INTEGER() {
  int rc = cc, ro = co;

  /* Nonterminal INTEGER, alternate 1 */
  cc = rc; co = ro; oracleSet(1);
  if (builtIn_INTEGER()) { return true; }

  return false;
}

boolean parse_S() {
  int rc = cc, ro = co;

  /* Nonterminal S, alternate 1 */
  cc = rc; co = ro; oracleSet(1);
  if (parse_INTEGER()) {
  if (match("+")) {
  if (parse_INTEGER()) { return true; }}}

  return false;
}

class Attributes_INTEGER { int v; }

void semantics_INTEGER(Attributes_INTEGER INTEGER) {
  switch(oracle[co++]) {
    case 1:
    builtIn_INTEGER();
    INTEGER.v = Integer.parseInt(lexeme()); 
    break;
  }
 }

class Attributes_S { int v; }

void semantics_S(Attributes_S S) {
  Attributes_INTEGER INTEGER1 = new Attributes_INTEGER(); 
  Attributes_INTEGER INTEGER2 = new Attributes_INTEGER(); 
  switch(oracle[co++]) {
    case 1:
    semantics_INTEGER(INTEGER1);
    match("+");
    semantics_INTEGER(INTEGER2);
    S.v = INTEGER1.v + INTEGER2.v;  System.out.println("Result: "  + S.v); 
    break;
  }
 }

void parse(String filename) throws FileNotFoundException {
  input = readInput(filename);

  System.out.println("Input: " + input);  cc = co = 0; builtIn_WHITESPACE();
  if (!(parse_S() && input.charAt(cc) == '\0')) { System.out.print("Rejected\n"); return; }

  System.out.print("Accepted\n");
  System.out.print("Oracle:"); for (int i = 0; i < co; i++) System.out.printf(" " + oracle[i]); System.out.printf("\n");
  System.out.print("\nSemantics phase\n"); cc = 0; co = 0; builtIn_WHITESPACE(); semantics_S(new Attributes_S());

}

public static void main(String[] args) throws FileNotFoundException{
    if (args.length < 1) {
      System.err.println("No input file name supplied");
      System.exit(1);
    } else
      new ARTGeneratedRDSOB().parse(args[0]);
  }
}

