package uk.ac.rhul.cs.csle.art.script;

import uk.ac.rhul.cs.csle.art.choose.ChooseRules;

public class ScriptValueChooseRules extends ScriptValue {
  final ChooseRules payload;

  public ScriptValueChooseRules(ChooseRules payload) {
    super();
    this.payload = new ChooseRules(payload);
  }

}
