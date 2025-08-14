package uk.ac.rhul.cs.csle.art.script;

import uk.ac.rhul.cs.csle.art.term.TRRules;

public class ScriptValueTRRules extends ScriptValue {
  final TRRules payload;

  public ScriptValueTRRules(TRRules payload) {
    super();
    this.payload = new TRRules(payload);
  }

}
