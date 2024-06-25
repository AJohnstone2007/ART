package uk.ac.rhul.cs.csle.art.esos;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.value.ARTValueString;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;

public class ARTeSOSTest {
  String name;
  int traceLevel;
  ARTeSOSRelation relation;
  ARTValueTermUsingList lhs;
  ARTValueTermUsingList rhs = new ARTValueTermUsingList(new ARTValueString("dummy"));

  public ARTeSOSTest(String name, int traceLevel, ARTeSOSRelation relation, ARTValueTermUsingList lhs, ARTValueTermUsingList rhs) {
    this.name = name;
    this.traceLevel = traceLevel;
    this.relation = relation;
    this.lhs = lhs;
    // this.rhs = rhs;
  }

  public String getName() {
    return name;
  }

  public int getTraceLevel() {
    return traceLevel;
  }

  public ARTeSOSRelation getRelation() {
    return relation;
  }

  public ARTValueTermUsingList getLhs() {
    return lhs;
  }

  public ARTValueTermUsingList getRhs() {
    return rhs;
  }

  @Override
  public String toString() {
    return "\ntest \"" + name + "\" " + traceLevel + " " + lhs + relation + rhs;
  }

  public String toLatexString(Map<String, String> map) {
    return "\n{\\bf test} {\\em " + name + "} " + traceLevel + " " + lhs.toLatexString(map) + relation.toLatexString(map) + rhs.toLatexString(map);
  }
}
