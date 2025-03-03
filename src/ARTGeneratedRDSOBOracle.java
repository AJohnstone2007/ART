import java.io.FileNotFoundException;import java.util.LinkedList;

class ARTGeneratedRDSOBOracle extends uk.ac.rhul.cs.csle.art.cfg.rdsob.RDSOBAbstract {

boolean parse_S() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal S, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (parse_e1()) { return true; }

  return false;
}

boolean parse_digit() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal digit, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (match("0")) { return true; }

  /* Nonterminal digit, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (match("1")) { return true; }

  /* Nonterminal digit, alternate 3 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(3);
  if (match("2")) { return true; }

  /* Nonterminal digit, alternate 4 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(4);
  if (match("3")) { return true; }

  /* Nonterminal digit, alternate 5 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(5);
  if (match("4")) { return true; }

  /* Nonterminal digit, alternate 6 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(6);
  if (match("5")) { return true; }

  /* Nonterminal digit, alternate 7 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(7);
  if (match("6")) { return true; }

  /* Nonterminal digit, alternate 8 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(8);
  if (match("7")) { return true; }

  /* Nonterminal digit, alternate 9 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(9);
  if (match("8")) { return true; }

  /* Nonterminal digit, alternate 10 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(10);
  if (match("9")) { return true; }

  return false;
}

boolean parse_e1() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal e1, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (parse_e2()) {
  if (match("+")) {
  if (parse_e1()) { return true; }}}

  /* Nonterminal e1, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (parse_e2()) {
  if (match("-")) {
  if (parse_e1()) { return true; }}}

  /* Nonterminal e1, alternate 3 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(3);
  if (parse_e2()) { return true; }

  return false;
}

boolean parse_e2() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal e2, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (parse_e3()) {
  if (match("*")) {
  if (parse_e2()) { return true; }}}

  /* Nonterminal e2, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (parse_e3()) {
  if (match("/")) {
  if (parse_e2()) { return true; }}}

  /* Nonterminal e2, alternate 3 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(3);
  if (parse_e3()) { return true; }

  return false;
}

boolean parse_e3() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal e3, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (match("(")) {
  if (parse_e1()) {
  if (match(")")) { return true; }}}

  /* Nonterminal e3, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (parse_integer()) { return true; }

  return false;
}

boolean parse_integer() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal integer, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (parse_digit()) {
  if (parse_integer()) { return true; }}

  /* Nonterminal integer, alternate 2 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(2);
  if (parse_digit()) { return true; }

  return false;
}

boolean parse_integerB() {
  int iiAtEntry = inputIndex, oiAtEntry = oracleIndex;

  /* Nonterminal integerB, alternate 1 */
  inputIndex = iiAtEntry; oracleIndex = oiAtEntry; oracleSet(1);
  if (builtIn_INTEGER()) { return true; }

  return false;
}

void semantics_S() {
  Attributes_e1 e11 = new Attributes_e1(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    semantics_e1(e11);
    System.out.println("Result: " + e11.v); 
    break;
  }
 }

class Attributes_digit { int v; }

void semantics_digit(Attributes_digit digit) {
  switch(oracle[oracleIndex++]) {
    case 1:
    match("0");
    digit.v = 0; 
    break;

    case 2:
    match("1");
    digit.v = 1; 
    break;

    case 3:
    match("2");
    digit.v = 2; 
    break;

    case 4:
    match("3");
    digit.v = 3; 
    break;

    case 5:
    match("4");
    digit.v = 4; 
    break;

    case 6:
    match("5");
    digit.v = 5; 
    break;

    case 7:
    match("6");
    digit.v = 6; 
    break;

    case 8:
    match("7");
    digit.v = 7; 
    break;

    case 9:
    match("8");
    digit.v = 8; 
    break;

    case 10:
    match("9");
    digit.v = 9; 
    break;
  }
 }

class Attributes_e1 { int v; }

void semantics_e1(Attributes_e1 e1) {
  Attributes_e2 e21 = new Attributes_e2(); 
  Attributes_e1 e11 = new Attributes_e1(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    semantics_e2(e21);
    match("+");
    semantics_e1(e11);
    e1.v = e21.v + e11.v; 
    break;

    case 2:
    semantics_e2(e21);
    match("-");
    semantics_e1(e11);
    e1.v = e21.v - e11.v; 
    break;

    case 3:
    semantics_e2(e21);
    e1.v = e21.v; 
    break;
  }
 }

class Attributes_e2 { int v; }

void semantics_e2(Attributes_e2 e2) {
  Attributes_e3 e31 = new Attributes_e3(); 
  Attributes_e2 e21 = new Attributes_e2(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    semantics_e3(e31);
    match("*");
    semantics_e2(e21);
    e2.v = e31.v * e21.v; 
    break;

    case 2:
    semantics_e3(e31);
    match("/");
    semantics_e2(e21);
    e2.v = e31.v / e21.v; 
    break;

    case 3:
    semantics_e3(e31);
    e2.v = e31.v; 
    break;
  }
 }

class Attributes_e3 { int v; }

void semantics_e3(Attributes_e3 e3) {
  Attributes_e1 e11 = new Attributes_e1(); 
  Attributes_integer integer1 = new Attributes_integer(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    match("(");
    semantics_e1(e11);
    match(")");
    e3.v = e11.v; 
    break;

    case 2:
    semantics_integer(integer1);
    e3.v = integer1.v; 
    break;
  }
 }

class Attributes_integer { int v; }

void semantics_integer(Attributes_integer integer) {
  Attributes_digit digit1 = new Attributes_digit(); 
  Attributes_integer integer1 = new Attributes_integer(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    semantics_digit(digit1);
    integer1.v = integer.v * 10 + digit1.v; 
    semantics_integer(integer1);
    integer.v = integer1.v; 
    break;

    case 2:
    semantics_digit(digit1);
    integer.v  = integer.v * 10 + digit1.v; 
    break;
  }
 }

class Attributes_integerB { int v; }

void semantics_integerB(Attributes_integerB integerB) {
  switch(oracle[oracleIndex++]) {
    case 1:
    builtIn_INTEGER();
    integerB.v = Integer.parseInt(lexeme()); 
    break;
  }
 }

String term_S(LinkedList<String> children) {
  String newRoot = null;
  Attributes_e1 e11 = new Attributes_e1(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    term_e1(new LinkedList<>());
    break;
  }
  return newRoot;
 }

String term_digit(LinkedList<String> children) {
  String newRoot = null;
  switch(oracle[oracleIndex++]) {
    case 1:
    match("0");
    break;

    case 2:
    match("1");
    break;

    case 3:
    match("2");
    break;

    case 4:
    match("3");
    break;

    case 5:
    match("4");
    break;

    case 6:
    match("5");
    break;

    case 7:
    match("6");
    break;

    case 8:
    match("7");
    break;

    case 9:
    match("8");
    break;

    case 10:
    match("9");
    break;
  }
  return newRoot;
 }

String term_e1(LinkedList<String> children) {
  String newRoot = null;
  Attributes_e2 e21 = new Attributes_e2(); 
  Attributes_e1 e11 = new Attributes_e1(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    term_e2(new LinkedList<>());
    match("+");
    term_e1(new LinkedList<>());
    break;

    case 2:
    term_e2(new LinkedList<>());
    match("-");
    term_e1(new LinkedList<>());
    break;

    case 3:
    term_e2(new LinkedList<>());
    break;
  }
  return newRoot;
 }

String term_e2(LinkedList<String> children) {
  String newRoot = null;
  Attributes_e3 e31 = new Attributes_e3(); 
  Attributes_e2 e21 = new Attributes_e2(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    term_e3(new LinkedList<>());
    match("*");
    term_e2(new LinkedList<>());
    break;

    case 2:
    term_e3(new LinkedList<>());
    match("/");
    term_e2(new LinkedList<>());
    break;

    case 3:
    term_e3(new LinkedList<>());
    break;
  }
  return newRoot;
 }

String term_e3(LinkedList<String> children) {
  String newRoot = null;
  Attributes_e1 e11 = new Attributes_e1(); 
  Attributes_integer integer1 = new Attributes_integer(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    match("(");
    term_e1(new LinkedList<>());
    match(")");
    break;

    case 2:
    term_integer(new LinkedList<>());
    break;
  }
  return newRoot;
 }

String term_integer(LinkedList<String> children) {
  String newRoot = null;
  Attributes_digit digit1 = new Attributes_digit(); 
  Attributes_integer integer1 = new Attributes_integer(); 
  switch(oracle[oracleIndex++]) {
    case 1:
    term_digit(new LinkedList<>());
    term_integer(new LinkedList<>());
    break;

    case 2:
    term_digit(new LinkedList<>());
    break;
  }
  return newRoot;
 }

String term_integerB(LinkedList<String> children) {
  String newRoot = null;
  switch(oracle[oracleIndex++]) {
    case 1:
    builtIn_INTEGER();
    break;
  }
  return newRoot;
 }

void parse(String filename) throws FileNotFoundException {
  input = readInput(filename);

  System.out.println("Input: " + input);  inputIndex = oracleIndex = 0; builtIn_WHITESPACE();
  if (!(parse_S() && input.charAt(inputIndex) == '\0')) { System.out.print("Rejected\n"); return; }

  System.out.print("Accepted\n");
  System.out.print("Oracle:"); for (int i = 0; i < oracleIndex; i++) System.out.printf(" " + oracle[i]); System.out.printf("\n");
  System.out.print("\nSemantics:\n"); inputIndex = oracleIndex = 0; builtIn_WHITESPACE(); semantics_S();
  System.out.print("\nTerm:\n"); inputIndex = oracleIndex = 0; builtIn_WHITESPACE(); term_S(new LinkedList<>());
}

public static void main(String[] args) throws FileNotFoundException{
    if (args.length < 1) System.out.println("Usage: java ARTGeneratedRDSOBOracle <input file name>");
    else  new ARTGeneratedRDSOBOracle().parse(args[0]);

  }
}
