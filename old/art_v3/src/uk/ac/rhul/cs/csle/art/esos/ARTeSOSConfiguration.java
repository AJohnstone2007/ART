package uk.ac.rhul.cs.csle.art.esos;

import java.util.Iterator;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.value.ARTValue;
import uk.ac.rhul.cs.csle.art.value.ARTValueException;
import uk.ac.rhul.cs.csle.art.value.ARTValueList;
import uk.ac.rhul.cs.csle.art.value.ARTValueRecord;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;
import uk.ac.rhul.cs.csle.art.value.ARTValueTermVariable;

public class ARTeSOSConfiguration extends ARTValueRecord {

  public ARTeSOSConfiguration() {
  }

  public ARTeSOSConfiguration(ARTeSOSConfiguration configuration) throws ARTException {
    super(configuration);
  }

  @Override
  public String toString() {
    String ret = "< ";
    for (Iterator<ARTValue> iter = payload.keySet().iterator(); iter.hasNext();) {
      ARTValue key = iter.next();
      ARTeSOSName name = (ARTeSOSName) key;
      ret += key + " = " + payload.get(key);
      ret += iter.hasNext() ? ", " : "";
    }
    return ret + " >";
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    String ret = "\\langle ";
    for (Iterator<ARTValue> iter = ARTeSOSSpecification.getFullconfiguration().payload.keySet().iterator(); iter.hasNext();) {
      ARTValue k = iter.next();
      if (!payload.containsKey(k)) continue;
      // Uncomment the two lines below to see the field names
      // ARTeSOSName name = (ARTeSOSName) k;
      // ret += name.toLatexString(map) + ": ";
      ARTValue value = payload.get(k);
      if (value == null)
        ret += "unmapped";
      else
        ret += value.toLatexString(map);
      ret += iter.hasNext() ? "," : "";
    }
    ret += "\\rangle";

    return ret;
  }

  public String toLatexStringKeys(Map<String, String> map) {
    String ret = "\\langle ";
    for (Iterator<ARTValue> iter = ARTeSOSSpecification.getFullconfiguration().payload.keySet().iterator(); iter.hasNext();) {
      ARTValue k = iter.next();
      if (!payload.containsKey(k)) continue;
      ret += ((ARTeSOSName) k).toLatexString(map);
      ret += iter.hasNext() ? "," : "";
    }
    ret += "\\rangle";

    return ret;
  }

  public void update(ARTValueList list) throws ARTValueException {
    Iterator<ARTValue> li = list.iterator();
    for (ARTValue k : payload.keySet())
      if (li.hasNext()) super.update(k, li.next());
  }

  public void updateFromTerm(ARTValueTermUsingList term) throws ARTValueException {
    Iterator<ARTValueTermUsingList> li = term.getChildren().iterator();
    for (ARTValue k : payload.keySet())
      if (li.hasNext()) super.update(k, li.next());
  }

  public boolean match(ARTeSOSConfiguration pattern, Map<ARTValueTermVariable, ARTValueTermUsingList> bindings) throws ARTValueException, ARTException {
    ARTeSOSSpecification.trace(5, "Configuration.match on " + this + " against pattern " + pattern);

    for (ARTValue key : this)
      if (!((ARTValueTermUsingList) valueKey(key)).closedMatchPatternSingletonBindings((ARTValueTermUsingList) pattern.valueKey(key), bindings)) return false;

    return true;
  }

  public ARTeSOSConfiguration substitute(Map<ARTValueTermVariable, ARTValueTermUsingList> bindings) throws ARTException {
    ARTeSOSConfiguration ret = new ARTeSOSConfiguration();
    for (ARTValue key : this)
      ret.insertKey(key, ((ARTValueTermUsingList) valueKey(key)).substitute(bindings));

    return ret;
  }

  public void addEntity(ARTeSOSName entityName, ARTValueTermUsingList entityValue) throws ARTException {
    if (payload.containsKey(entityName) && !(payload.get(entityName)).equals(entityValue))
      throw new ARTException("Entity " + entityName + " is declared as both type " + payload.get(entityName).getClass() + " and " + entityValue.getClass());
    else
      payload.put(entityName, entityValue);
  }

  public void addEntity(ARTValueTermUsingList v) throws ARTException {
    // Infer which entity this is meant to be...
    ARTeSOSName termPayload = (ARTeSOSName) v.getPayload();

    if (termPayload instanceof ARTeSOSName)
      addEntity(termPayload.base(), v); // Map base name to term
    else
      throw new ARTException("unable to infer entity name for term " + v);
  }

}
