import java.io.FileNotFoundException;

class ARTGeneratedRDSOB extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBV4Base {

  boolean parse_E1() {
    int rc = cc, ro = co;

    /* Nonterminal E1, alternate 1 */
    cc = rc;
    co = ro;
    oracleSet(1);
    if (parse_E2()) {
      if (match("+")) {
        if (parse_E1()) {
          return true;
        }
      }
    }

    /* Nonterminal E1, alternate 2 */
    cc = rc;
    co = ro;
    oracleSet(2);
    if (parse_E2()) {
      return true;
    }

    return false;
  }

  boolean parse_E2() {
    int rc = cc, ro = co;

    /* Nonterminal E2, alternate 1 */
    cc = rc;
    co = ro;
    oracleSet(1);
    if (parse_E3()) {
      if (match("*")) {
        if (parse_E2()) {
          return true;
        }
      }
    }

    /* Nonterminal E2, alternate 2 */
    cc = rc;
    co = ro;
    oracleSet(2);
    if (parse_E3()) {
      return true;
    }

    return false;
  }

  boolean parse_E3() {
    int rc = cc, ro = co;

    /* Nonterminal E3, alternate 1 */
    cc = rc;
    co = ro;
    oracleSet(1);
    if (builtIn_INTEGER()) {
      return true;
    }

    /* Nonterminal E3, alternate 2 */
    cc = rc;
    co = ro;
    oracleSet(2);
    if (match("(")) {
      if (parse_E1()) {
        if (match(")")) {
          return true;
        }
      }
    }

    return false;
  }

  boolean parse_S() {
    int rc = cc, ro = co;

    /* Nonterminal S, alternate 1 */
    cc = rc;
    co = ro;
    oracleSet(1);
    if (parse_E1()) {
      return true;
    }

    return false;
  }

  class Attributes_E1 {
    int v;
  }

  void semantics_E1(Attributes_E1 E1) {
    Attributes_E2 E21 = new Attributes_E2();
    Attributes_E1 E11 = new Attributes_E1();
    switch (oracle[co++]) {
    case 1:
      semantics_E2(E21);
      match("+");
      semantics_E1(E11);
      E1.v = E21.v + E11.v;
      break;

    case 2:
      semantics_E2(E21);
      E1.v = E21.v;
      break;
    }
  }

  class Attributes_E2 {
    int v;
  }

  void semantics_E2(Attributes_E2 E2) {
    Attributes_E3 E31 = new Attributes_E3();
    Attributes_E2 E21 = new Attributes_E2();
    switch (oracle[co++]) {
    case 1:
      semantics_E3(E31);
      match("*");
      semantics_E2(E21);
      E2.v = E31.v * E21.v;
      break;

    case 2:
      semantics_E3(E31);
      E2.v = E31.v;
      break;
    }
  }

  class Attributes_E3 {
    int v;
  }

  void semantics_E3(Attributes_E3 E3) {
    Attributes_E1 E11 = new Attributes_E1();
    switch (oracle[co++]) {
    case 1:
      builtIn_INTEGER();
      E3.v = Integer.parseInt(lexeme());
      break;

    case 2:
      match("(");
      semantics_E1(E11);
      match(")");
      E3.v = E11.v;
      break;
    }
  }

  void semantics_S() {
    Attributes_E1 E11 = new Attributes_E1();
    switch (oracle[co++]) {
    case 1:
      semantics_E1(E11);
      System.out.println("Result: " + E11.v);
      break;
    }
  }

  void parse(String filename) throws FileNotFoundException {
    input = readInput(filename);

    System.out.println("Input: " + input);
    cc = co = 0;
    builtIn_WHITESPACE();
    if (!(parse_S() && input.charAt(cc) == '\0')) {
      System.out.print("Rejected\n");
      return;
    }

    System.out.print("Accepted\n");
    System.out.print("Oracle:");
    for (int i = 0; i < co; i++)
      System.out.printf(" " + oracle[i]);
    System.out.printf("\n");
    System.out.print("\nSemantics phase\n");
    cc = 0;
    co = 0;
    builtIn_WHITESPACE();
    semantics_S();

  }

  public static void main(String[] args) throws FileNotFoundException {
    if (args.length < 1) {
      System.err.println("No input file name supplied");
      System.exit(1);
    } else
      new ARTGeneratedRDSOB().parse(args[0]);
  }
}
