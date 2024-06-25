package uk.ac.rhul.cs.csle.art.value;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.ARTException;

/**
 * ARTValue is the abstract base class for all value operations in ART value system.
 *
 * The ART value system provides a set of types T and a set of operations V. In general, an element of T will only support a subset of the operations in V.
 *
 * The purpose of the base class is to provide lightweight runtime type checking.
 *
 * In the base class, there is one method for each member of the complete set of operations provided by the ART value system. Each of these methods calls an
 * error message generating method which will thus report invalid operations at run time.
 *
 * We provide concrete subclasses, one per type, and within them operations that are implemented for that type are overriden. An attempt to access an
 * unsupported operation will therefore call the error-generating method in the superclass.
 *
 * In addition, we provide support for automatic widening of operands within th enumeric tower, using fixed coercian priorities defines in ARTValueNumber
 *
 * @author Adrian Johnstone
 *
 */
public abstract class ARTValue {

  static public ARTValueBoolean trueValue = new ARTValueBoolean(true);
  static public ARTValueBoolean falseValue = new ARTValueBoolean(false);

  public ARTValue() {
  }

  public ARTValue(ARTValue r) throws ARTValueException {
    throw new ARTValueException(
        "value class " + strip(this.getClass().toString()) + " does not provide constructor for class " + strip(r.getClass().toString()));

  }

  // Internal methods used for display, equality, hashCode etc
  abstract public Object getPayload();

  @Override
  public String toString() {
    return "" + getPayload();
  }

  public String toLatexString(Map<String, String> map) {
    return mapString("" + getPayload(), map);
  }

  protected String mapString(String s, Map<String, String> map) {
    if (map.containsKey(s)) return map.get(s);

    // System.out.println("Replacing " + s + " with " + s.replaceAll("_", "\\\\_"));
    return s.replaceAll("_", "\\\\_");
  }

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

  // Internal methods used for error reporting
  protected String strip(String s) {
    return s.substring(s.indexOf("ART"));
  }

  private void errorOp(String op) throws ARTValueException {
    throw new ARTValueException("value class " + strip(this.getClass().toString()) + " does not provide operation '" + op + "'");
  }

  private void errorOp(String op, ARTValue r) throws ARTValueException {
    throw new ARTValueException(
        "value class " + strip(this.getClass().toString()) + " does not provide operation '" + op + "' with class " + strip(r.getClass().toString()));
  }

  protected void error(String message) throws ARTValueException {
    throw new ARTValueException("value error: " + message);
  }

  // Internal method used as default for getCoercionPriority() calls on non-numbers
  protected int getCoercionPriority() throws ARTValueException {
    error(" attempted coercion of non-number class " + strip(this.getClass().toString()));
    return 0;
  }

  // Despatcher used by eSOS
  static public ARTValue despatch(ARTValueOp op, ARTValue left, ARTValue right, ARTValue supp) throws ARTException {
    // We group by arity for arity checking purposes

    // @formatter:off
    // Arity one
    if (left == null) throw new ARTException("Attempt to despatch function " + op + " with no arguments");
    switch (op) {
    case bnot: return left.bnot();
    case body: return left.body();
    case cardinality: return left.cardinality();
    case castToArray: return left.castToArray();
    case castToBoolean: return left.castToBoolean();
    case castToCharacter: return left.castToCharacter();
    case castToInteger32: return left.castToInteger32();
    case castToIntegerArbitrary: return left.castToIntegerArbitrary();
    case castToList: return left.castToList();
    case castToMap: return left.castToMap();
    case castToMapOrdered: return left.castToMapOrdered();
    case castToMapOrderedHierarchy: return left.castToMapOrderedHierarchy();
    case castToMetavariable: return left.castToMetavariable();
    case castToReal64: return left.castToReal64();
    case castToRealArbitrary: return left.castToRealArbitrary();
    case castToRecord: return left.castToRecord();
    case castToSet: return left.castToSet();
    case castToString: return left.castToString();
    case castToTerm: return left.castToTerm();
    case castToTuple: return left.castToTuple();
    case delete: return left.delete();
    case isArray: return left.isArray();
    case isBoolean: return left.isBoolean();
    case isCharacter: return left.isCharacter();
    case isInteger: return left.isInteger();
    case isInteger32: return left.isInteger32();
    case isIntegerArbitrary: return left.isIntegerArbitrary();
    case isRational: return left.isRational();
    case isComplex: return left.isComplex();
    case isQuantity: return left.isQuantity();
    case isNumber: return left.isNumber();
    case isList: return left.isList();
    case isMap: return left.isMap();
    case isMapOrdered: return left.isMapOrdered();
    case isMapOrderedHierarchy: return left.isMapOrderedHierarchy();
    case isReal: return left.isReal();
    case isReal64: return left.isReal64();
    case isRealArbitrary: return left.isRealArbitrary();
    case isRecord: return left.isRecord();
    case isSet: return left.isSet();
    case isString: return left.isString();
    case isTerm: return left.isTerm();
    case isTermVariable: return left.isTermVariable();
    case isTuple: return left.isTuple();
    case lnot: return left.lnot();
    case neg: return left.neg();
    case parameters: return left.parameters();
    case value: return left.value();
    default: break;
    }

    // Arity two
    if (right == null) throw new ARTException("Attempt to despatch function " + op + " with only one argument");
    switch (op) {
    case add: return left.add(right);
    case ash: return left.ash(right);
    case band: return left.band(right);
    case bor: return left.bor(right);
    case bxor: return left.bxor(right);
    case cat: return left.cat(right);
    case cnd: return left.cnd(right);
    case contains: return left.contains(right);
    case deleteKey: return left.deleteKey(right);
    case difference: return left.difference(right);
    case div: return left.div(right);
    case eq: return left.eq(right);
    case exp: return left.exp(right);
    case finalKey: return left.finalKey(right);
    case ge: return left.ge(right);
    case gt: return left.gt(right);
    case input: return left.input(right);
    case insert: return left.insert(right);
    case intersection: return left.intersection(right);
    case key: return left.key(right);
    case land: return left.land(right);
    case le: return left.le(right);
    case lor: return left.lor(right);
    case lsh: return left.lsh(right);
    case lt: return left.lt(right);
    case lxor: return left.lxor(right);
    case mod: return left.mod(right);
    case mul: return left.mul(right);
    case ne: return left.ne(right);
    case output: return left.output(right);
    case range: return left.range(right);
    case rol: return left.rol(right);
    case ror: return left.ror(right);
    case rsh: return left.rsh(right);
    case sub: return left.sub(right);
    case union: return left.union(right);
    case updateOrdered: return left.updateOrdered((ARTValueCollection) right);
    case valueKey: return left.valueKey(right);
    default: break;
    }

    // Arity three
    if (supp == null) throw new ARTException("Attempt to despatch function " + op + " with only two arguments");
    switch (op) {
    case insertKey: return left.insertKey(right, supp);
    case update: return left.update(right, supp);
    default:     throw new ARTException("Unimplemented despatch for op " + op);
    }
  }

  public ARTValue despatch(ARTValueOp op, ARTValue left, ARTValue right) throws ARTException {
    return despatch(op, left, right, null);
  }

  public ARTValue despatch(ARTValueOp op, ARTValue left) throws ARTException {
    return despatch(op, left, null, null);
  }

  public static ARTValue despatch(String op, ARTValue left, ARTValue right, ARTValue supp) throws ARTException {
    ARTValueOp opEnum;
    try {
      opEnum = ARTValueOp.valueOf(op);
    } catch (java.lang.IllegalArgumentException e) {
      throw new ARTException("ARTValue.despatch(): unknown function name " + op);
    }

    return despatch(opEnum, left, right, supp);
  }

  public ARTValue despatch(String op, ARTValue left, ARTValue right) throws ARTException {
    return despatch(ARTValueOp.valueOf(op), left, right, null);
  }

  public ARTValue despatch(String op, ARTValue left) throws ARTException {
    return despatch(ARTValueOp.valueOf(op), left, null, null);
  }

  // Clone and cast operations

  public ARTValueBoolean castToBoolean() throws ARTValueException {
    errorOp("castToBoolean");
    return null;
  }

  public ARTValueCharacter castToCharacter() throws ARTValueException {
    errorOp("castToCharacter");
    return null;
  }

  public ARTValueInteger32 castToInteger32() throws ARTValueException {
    errorOp("castToInteger32");
    return null;
  }

  public ARTValueIntegerArbitrary castToIntegerArbitrary() throws ARTValueException {
    errorOp("castToIntegerArbitrary");
    return null;
  }

  public ARTValueReal64 castToReal64() throws ARTValueException {
    errorOp("castToReal64");
    return null;
  }

  public ARTValueRealArbitrary castToRealArbitrary() throws ARTValueException {
    errorOp("castToRealArbitrary");
    return null;
  }

  public ARTValueString castToString() throws ARTValueException {
    errorOp("castToString");
    return null;
  }

  public ARTValueArray castToArray() throws ARTValueException {
    errorOp("castToArray");
    return null;
  }

  public ARTValueTuple castToTuple() throws ARTValueException {
    errorOp("castToTuple");
    return null;
  }

  public ARTValueRecord castToRecord() throws ARTValueException {
    errorOp("castToRecord");
    return null;
  }

  public ARTValueList castToList() throws ARTValueException {
    errorOp("castToList");
    return null;
  }

  public ARTValueSet castToSet() throws ARTValueException {
    errorOp("castToSet");
    return null;
  }

  public ARTValueMap castToMap() throws ARTValueException {
    errorOp("castToMap");
    return null;
  }

  public ARTValueMap castToMapOrdered() throws ARTValueException {
    errorOp("castToMapOrdered");
    return null;
  }

  public ARTValueEnvironment castToMapOrderedHierarchy() throws ARTValueException {
    errorOp("castToMapOrderedHierarchy");
    return null;
  }

  public ARTValueTermUsingList castToTerm() {
    return this instanceof ARTValueTermUsingList ? (ARTValueTermUsingList) this : new ARTValueTermUsingList(this);
  }

  public ARTValueTermUsingList castToMetavariable() throws ARTValueException {
    errorOp("castToMetavariable");
    return null;
  }

  // type test operations

  public ARTValueBoolean isBoolean() throws ARTValueException {
    return this instanceof ARTValueBoolean ? trueValue : falseValue;
  }

  public ARTValueBoolean isCharacter() throws ARTValueException {
    return this instanceof ARTValueCharacter ? trueValue : falseValue;
  }

  public ARTValueBoolean isInteger() throws ARTValueException {
    return this instanceof ARTValueInteger ? trueValue : falseValue;
  }

  public ARTValueBoolean isInteger32() throws ARTValueException {
    return this instanceof ARTValueInteger32 ? trueValue : falseValue;
  }

  public ARTValueBoolean isIntegerArbitrary() throws ARTValueException {
    return this instanceof ARTValueIntegerArbitrary ? trueValue : falseValue;
  }

  public ARTValueBoolean isReal() throws ARTValueException {
    return this instanceof ARTValueReal ? trueValue : falseValue;
  }

  public ARTValueBoolean isReal64() throws ARTValueException {
    return this instanceof ARTValueReal64 ? trueValue : falseValue;
  }

  public ARTValueBoolean isRealArbitrary() throws ARTValueException {
    return this instanceof ARTValueRealArbitrary ? trueValue : falseValue;
  }

  private ARTValueBoolean isNumber() {
    return this instanceof ARTValueNumber ? trueValue : falseValue;
  }

  private ARTValueBoolean isQuantity() {
    return this instanceof ARTValueQuantity ? trueValue : falseValue;
  }

  private ARTValueBoolean isComplex() {
    return this instanceof ARTValueComplex ? trueValue : falseValue;
  }

  private ARTValueBoolean isRational() {
    return this instanceof ARTValueRational ? trueValue : falseValue;
  }

  public ARTValueBoolean isString() throws ARTValueException {
    return this instanceof ARTValueString ? trueValue : falseValue;
  }

  public ARTValueBoolean isArray() throws ARTValueException {
    return this instanceof ARTValueArray ? trueValue : falseValue;
  }

  public ARTValueBoolean isTuple() throws ARTValueException {
    return this instanceof ARTValueTuple ? trueValue : falseValue;
  }

  public ARTValueBoolean isRecord() throws ARTValueException {
    return this instanceof ARTValueRecord ? trueValue : falseValue;
  }

  public ARTValueBoolean isList() throws ARTValueException {
    return this instanceof ARTValueList ? trueValue : falseValue;
  }

  public ARTValueBoolean isSet() throws ARTValueException {
    return this instanceof ARTValueSet ? trueValue : falseValue;
  }

  public ARTValueBoolean isMap() throws ARTValueException {
    return this instanceof ARTValueMap ? trueValue : falseValue;
  }

  public ARTValueBoolean isMapOrdered() throws ARTValueException {
    return this instanceof ARTValueMapOrdered ? trueValue : falseValue;
  }

  public ARTValueBoolean isMapOrderedHierarchy() throws ARTValueException {
    return this instanceof ARTValueEnvironment ? trueValue : falseValue;
  }

  public ARTValueBoolean isTerm() {
    return this instanceof ARTValueTermUsingList ? trueValue : falseValue;
  }

  public ARTValueBoolean isTermVariable() throws ARTValueException {
    return this instanceof ARTValueTermVariable ? trueValue : falseValue;
  }

  // Relational operations returning ARTValueBoolean
  public ARTValue gt(ARTValue r) throws ARTValueException {
    errorOp("gt", r);
    return null;
  }

  public ARTValue lt(ARTValue r) throws ARTValueException {
    errorOp("lt", r);
    return null;
  }

  public ARTValue ge(ARTValue r) throws ARTValueException {
    errorOp("ge", r);
    return null;
  }

  public ARTValue le(ARTValue r) throws ARTValueException {
    errorOp("le", r);
    return null;
  }

  public ARTValue eq(ARTValue r) throws ARTValueException {
    errorOp("eq", r);
    return null;
  }

  public ARTValue ne(ARTValue r) throws ARTValueException {
    errorOp("ne", r);
    return null;
  }

  // Logic operations which return booleans
  public ARTValue lnot() throws ARTValueException {
    errorOp("lnot");
    return null;
  }

  public ARTValue land(ARTValue r) throws ARTValueException {
    errorOp("land", r);
    return null;
  }

  public ARTValue lor(ARTValue r) throws ARTValueException {
    errorOp("lor", r);
    return null;
  }

  public ARTValue lxor(ARTValue r) throws ARTValueException {
    errorOp("lxor", r);
    return null;
  }

  // Logic operations which return booleans
  public ARTValue bnot() throws ARTValueException {
    errorOp("bnot");
    return null;
  }

  public ARTValue band(ARTValue r) throws ARTValueException {
    errorOp("band", r);
    return null;
  }

  public ARTValue bor(ARTValue r) throws ARTValueException {
    errorOp("bor", r);
    return null;
  }

  public ARTValue bxor(ARTValue r) throws ARTValueException {
    errorOp("bxor", r);
    return null;
  }

  // Shift operations
  public ARTValue lsh(ARTValue r) throws ARTValueException {
    errorOp("lsh", r);
    return null;
  }

  public ARTValue rsh(ARTValue r) throws ARTValueException {
    errorOp("rsh", r);
    return null;
  }

  public ARTValue ash(ARTValue r) throws ARTValueException {
    errorOp("ash", r);
    return null;
  }

  public ARTValue rol(ARTValue r) throws ARTValueException {
    errorOp("rol", r);
    return null;
  }

  public ARTValue ror(ARTValue r) throws ARTValueException {
    errorOp("ror", r);
    return null;
  }

  // Arithmetic operations
  public ARTValue add(ARTValue r) throws ARTValueException {
    errorOp("add", r);
    return null;
  }

  public ARTValue sub(ARTValue r) throws ARTValueException {
    errorOp("sub", r);
    return null;
  }

  public ARTValue mul(ARTValue r) throws ARTValueException {
    errorOp("mul", r);
    return null;
  }

  public ARTValue div(ARTValue r) throws ARTValueException {
    errorOp("div", r);
    return null;
  }

  public ARTValue mod(ARTValue r) throws ARTValueException {
    errorOp("mod", r);
    return null;
  }

  public ARTValue exp(ARTValue r) throws ARTValueException {
    errorOp("exp", r);
    return null;
  }

  public ARTValue neg() throws ARTValueException {
    errorOp("neg");
    return null;
  }

  // Collection operations
  public ARTValue cardinality() throws ARTValueException {
    errorOp("cardinality");
    return null;
  }

  public ARTValue contains(ARTValue r) throws ARTValueException {
    errorOp("contains", r);
    return null;
  }

  public ARTValue insert(ARTValue val) throws ARTValueException {
    errorOp("insert", val);
    return null;
  }

  public ARTValue insertKey(ARTValue key, ARTValue value) throws ARTValueException {
    errorOp("insert key", value);
    return null;
  }

  public ARTValue delete() throws ARTValueException {
    errorOp("delete");
    return null;
  }

  public ARTValue deleteKey(ARTValue key) throws ARTValueException {
    errorOp("delete key", key);
    return null;
  }

  public ARTValue update(ARTValue key, ARTValue value) throws ARTValueException {
    errorOp("update", value);
    return null;
  }

  public ARTValue updateOrdered(ARTValueCollection value) throws ARTValueException {
    errorOp("updateOrdered", value);
    return null;
  }

  public ARTValue value() throws ARTValueException {
    errorOp("value");
    return null;
  }

  public ARTValue valueKey(ARTValue key) throws ARTValueException {
    errorOp("value for key", key);
    return null;
  }

  public ARTValue key(ARTValue value) throws ARTValueException {
    errorOp("key for value");
    return null;
  }

  public ARTValue finalKey(ARTValue value) throws ARTValueException {
    errorOp("final key for value");
    return null;
  }

  public ARTValue union(ARTValue value) throws ARTValueException {
    errorOp("union");
    return null;
  }

  public ARTValue intersection(ARTValue value) throws ARTValueException {
    errorOp("union");
    return null;
  }

  public ARTValue difference(ARTValue value) throws ARTValueException {
    errorOp("union");
    return null;
  }

  // Procedure operations
  public ARTValueEnvironment parameters() throws ARTValueException {
    errorOp("paramaters");
    return null;
  }

  public ARTValue body() throws ARTValueException {
    errorOp("body");
    return null;
  }

  // I/O operations
  public ARTValue output(ARTValue v) throws ARTValueException {
    errorOp("output");
    return null;
  }

  public ARTValue input(ARTValue v) throws ARTValueException {
    errorOp("input");
    return null;
  }

  /* New stuff */
  public ARTValue cat(ARTValue v) throws ARTValueException {
    errorOp("cat");
    return null;
  }

  public ARTValue cnd(ARTValue v) throws ARTValueException {
    errorOp("cnd");
    return null;
  }

  public ARTValue range(ARTValue v) throws ARTValueException {
    errorOp("range");
    return null;
  }

  public ARTValue close() throws ARTValueException {
    errorOp("close");
    return null;
  }

}
