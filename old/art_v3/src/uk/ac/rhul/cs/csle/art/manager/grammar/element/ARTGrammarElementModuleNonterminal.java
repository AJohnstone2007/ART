package uk.ac.rhul.cs.csle.art.manager.grammar.element;

import java.util.HashSet;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.manager.module.ARTModule;

public class ARTGrammarElementModuleNonterminal extends ARTGrammarElement {
  private final ARTModule module;
  private final String id;

  // HashSet<ART_GLLRDTVertex> productions = new HashSet<ART_GLLRDTVertex>();
  private final LinkedList<ARTGLLRDTVertex> productions = new LinkedList<ARTGLLRDTVertex>();
  private final HashSet<ARTGrammarElementAttribute> attributes = new HashSet<ARTGrammarElementAttribute>();
  private final HashSet<ARTGLLRDTVertex> rdtDeleters = new HashSet<ARTGLLRDTVertex>();

  public ARTGrammarElementModuleNonterminal(ARTModule module, String id) {
    super();
    this.module = module;
    this.id = id;
  }

  public void addProduction(ARTGLLRDTVertex tree) {
    // System.out.println("\nNonterminal " + this + " adding production " + tree);
    productions.add(tree);
  }

  public void addAttribute(String id, String type) {
    // System.err.printf("adding attribute called %s of type %s to nonterminal %s\n", id, type, this.id);
    attributes.add(new ARTGrammarElementAttribute(id, type));
  }

  public void addDeleter(ARTGLLRDTVertex tree) {
    rdtDeleters.add(tree);
  }

  @Override
  public String toString() {
    return module.getId() + "." + id;
  }

  public void printProductions() {
    for (ARTGLLRDTVertex v : productions) {
      System.out.print(id + "::=");
      v.print();
      System.out.print("\n");
    }
  }

  public ARTModule getModule() {
    return module;
  }

  public String getId() {
    return id;
  }

  public LinkedList<ARTGLLRDTVertex> getProductions() {
    return productions;
  }

  public HashSet<ARTGrammarElementAttribute> getAttributes() {
    return attributes;
  }

  public HashSet<ARTGLLRDTVertex> getRdtDeleters() {
    return rdtDeleters;
  }

  @Override
  public String toEnumerationString() {
    return "ARTModuleNonterminal_" + module.getId() + "_" + id;
  }

  @Override
  public String toEnumerationString(String prefix) {
    return "prefix" + "_ARTModuleNonterminal_" + module.getId() + "_" + id;
  }
}
