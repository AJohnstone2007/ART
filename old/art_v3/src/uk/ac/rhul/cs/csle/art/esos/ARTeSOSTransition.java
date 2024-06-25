package uk.ac.rhul.cs.csle.art.esos;

import java.util.Map;

import uk.ac.rhul.cs.csle.art.value.ARTValueException;

public class ARTeSOSTransition extends ARTeSOSCondition {
  private ARTeSOSRelation relation;
  private final ARTeSOSConfiguration lhs = new ARTeSOSConfiguration();
  private final ARTeSOSConfiguration rhs = new ARTeSOSConfiguration();

  public ARTeSOSTransition() {
    super();
  }

  @Override
  public String toString() {
    return lhs + " " + relation.getLexeme() + " " + rhs;
  }

  @Override
  public String toLatexString(Map<String, String> map) throws ARTValueException {
    return lhs.toLatexString(map) + " " + relation.toLatexString(map) + " " + rhs.toLatexString(map);
  }

  public ARTeSOSRelation getRelation() {
    return relation;
  }

  public ARTeSOSConfiguration getLhs() {
    return lhs;
  }

  public ARTeSOSConfiguration getRhs() {
    return rhs;
  }

  public void setRelation(ARTeSOSRelation relation) {
    this.relation = relation;
  }
}
