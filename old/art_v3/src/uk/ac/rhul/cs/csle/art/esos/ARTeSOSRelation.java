package uk.ac.rhul.cs.csle.art.esos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.value.ARTValueException;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;

public class ARTeSOSRelation {
  // Use LinkedHashMap to preseve iteration order
  final ARTeSOSConfiguration configuration = new ARTeSOSConfiguration();
  final String lexeme;
  final private Map<ARTeSOSName, Integer> thetaTerminalNames = new HashMap<ARTeSOSName, Integer>(); // Value names mapped to their arity
  final private Map<ARTeSOSName, LinkedList<ARTeSOSRule>> ruleMap = new HashMap<>();
  boolean isStarred = false;

  ARTeSOSRelation(String lexeme) throws ARTException {
    this.lexeme = lexeme;
    configuration.addEntity(ARTeSOSSpecification.termKey, new ARTeSOSEntityProgramTerm(ARTeSOSSpecification.termKey));
  }

  @Override
  public String toString() {
    return lexeme + " " + configuration + " " + thetaTerminalNames;
  }

  public String toLatexString(Map<String, String> map) {
    return map.containsKey(lexeme) ? map.get(lexeme) : lexeme;
  }

  public ARTeSOSConfiguration getConfiguration() {
    return configuration;
  }

  public Map<ARTeSOSName, LinkedList<ARTeSOSRule>> getRuleMap() {
    return ruleMap;
  }

  public String getLexeme() {
    return lexeme;
  }

  public Map<ARTeSOSName, Integer> getThetaTerminalNames() {
    return thetaTerminalNames;
  }

  public void addEntity(ARTeSOSName name, ARTValueTermUsingList initialTerm) throws ARTValueException {
    configuration.insertKey(name, initialTerm);
  }

  public void addThetaTerminal(ARTValueTermUsingList v) {
    if (!(v.getPayload() instanceof ARTeSOSName)) ARTeSOSSpecification.error("internal error: attempt to add a terminal name which is not a name");
    thetaTerminalNames.put((ARTeSOSName) v.getPayload(), v.getChildren().size());
    ARTeSOSSpecification.addLaTeXName(v.toString(), "\\textsf{\\textit{" + v.toString() + "}}");
  }

  public boolean isTerminalTheta(ARTValueTermUsingList term) {
    if (term.getPayload() instanceof ARTeSOSName)
      return getThetaTerminalNames().containsKey(term.getPayload());
    else
      return true;
  }

  public void setIsStarred(boolean isStarred) {
    this.isStarred = isStarred;

  }
}
