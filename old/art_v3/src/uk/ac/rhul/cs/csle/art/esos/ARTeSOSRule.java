package uk.ac.rhul.cs.csle.art.esos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.value.ARTValueException;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermVariable;

public class ARTeSOSRule {
  private String label;
  private ARTeSOSName priority;
  private boolean isGathered = false;
  private final List<ARTeSOSCondition> conditions = new ArrayList<ARTeSOSCondition>();
  private ARTeSOSTransition conclusion;
  Set<ARTValueTermVariable> metavariables;

  public ARTeSOSTransition getConclusion() {
    return conclusion;
  }

  public void setConclusion(ARTeSOSTransition v) {
    this.conclusion = v;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String string) {
    this.label = string;
  }

  public void setLabelNumeric() {
    this.label = ARTeSOSSpecification.getNextLabelNumber().toString();
  }

  public List<ARTeSOSCondition> getConditions() {
    return conditions;
  }

  @Override
  public String toString() {
    String ret = "\n\n";
    ret += "[" + label + " " + priority + "] ";
    ret += "meta-variables " + metavariables + "\n";
    if (!conditions.isEmpty()) {
      boolean first = true;

      for (ARTeSOSCondition p : conditions) {
        if (first)
          first = false;
        else
          ret += "\n";

        ret += p.toString();
      }

      ret += "\n---\n";
    }

    ret += conclusion.toString();

    return ret;
  }

  public String toLatexString(Map<String, String> map) throws ARTValueException {
    String ret = "\\begin{equation}\n";
    ret += "\\tag*{[" + label + "\\mbox{\\ \\tiny\\em " + priority + "\\ }]}\n";

    ret += "\\frac{\n";
    if (isGathered) ret += "\\begin{gathered}\n";

    if (conditions.isEmpty())
      ret += "~";
    else {
      boolean first = true;

      for (ARTeSOSCondition p : conditions) {
        if (first)
          first = false;
        else if (isGathered)
          ret += "\\\\\n";
        else
          ret += "\\quad\n";

        ret += p.toLatexString(map);
      }
    }
    if (isGathered) ret += "\\end{gathered}";
    ret += "\n}{\n";
    ret += conclusion.toLatexString(map);
    ret += "\n}\n";
    ret += "\\end{equation}\n";

    return ret;
  }

  public void setPriority(ARTeSOSName name) {
    this.priority = name;
  }

  public ARTeSOSName getPriority() {
    return priority;
  }

  public void setGathered(boolean isGathered) {
    this.isGathered = isGathered;

  }
}
