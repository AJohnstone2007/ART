package uk.ac.rhul.cs.csle.art.script;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;

public class ScriptValueCFGRules extends ScriptValue {
  final CFGRules payload;

  public ScriptValueCFGRules(CFGRules payload) {
    super();
    this.payload = new CFGRules(payload, payload.cfgRulesKind, false, false, false, false, false);
  }

}
