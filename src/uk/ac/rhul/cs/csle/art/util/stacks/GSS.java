package uk.ac.rhul.cs.csle.art.util.stacks;

import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;

public class GSS {
  public Map<GSSNode, GSSNode> nodes = new HashMap<>();
  public GSSNode root;

  public GSS(CFGRules cfgRules) {
    root = new GSSNode(cfgRules.endOfStringNode, 0);
    nodes.put(root, root);
  }

  public GSSNode find(CFGNode grammarNode, int i) {
    GSSNode gssNode = new GSSNode(grammarNode, i);
    if (nodes.get(gssNode) == null) nodes.put(gssNode, gssNode);
    return nodes.get(gssNode);
  }
}
