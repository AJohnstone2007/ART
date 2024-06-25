package uk.ac.rhul.cs.csle.art.manager.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.manager.ARTManager;
import uk.ac.rhul.cs.csle.art.manager.grammar.element.ARTGrammarElementModuleNonterminal;
import uk.ac.rhul.cs.csle.art.value.ARTValueTerm;

public class ARTModule {
  private final ARTManager artManager;
  private final String id;

  private ARTGrammarElementModuleNonterminal defaultStartNonterminal = null;
  private final Set<ARTImport> imports = new HashSet<ARTImport>();
  private final Map<String, ARTGrammarElementModuleNonterminal> nonterminals = new HashMap<String, ARTGrammarElementModuleNonterminal>();
  private final List<ARTGrammarElementModuleNonterminal> nonterminalList = new LinkedList<ARTGrammarElementModuleNonterminal>();
  private final Set<ARTValueTerm> whitespaceTerminals = new HashSet<>();
  private final Set<ARTValueTerm> nonterminalsDeclaredAsTerminals = new HashSet<>();
  private final Map<ARTValueTerm, Set<ARTValueTerm>> higher = new HashMap<>();
  private final Map<ARTValueTerm, Set<ARTValueTerm>> longer = new HashMap<>();
  private final Set<String> preludeStrings = new HashSet<String>();
  private final Set<String> supportStrings = new HashSet<String>();
  public boolean seenWhitespaceDeclaration = false;

  public ARTManager getArtManager() {
    return artManager;
  }

  public ARTModule(ARTManager artManager, String id) {
    super();
    this.artManager = artManager;
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public Set<ARTImport> getImports() {
    return imports;
  }

  public void addImport(ARTImport anImport) {
    imports.add(anImport);
  }

  public ARTGrammarElementModuleNonterminal findNonterminal(String id) {
    ARTGrammarElementModuleNonterminal ret = nonterminals.get(id);

    if (ret == null) {
      nonterminals.put(id, ret = new ARTGrammarElementModuleNonterminal(this, id));
      nonterminalList.add(ret);
    }
    return ret;
  }

  public void addProduction(String id, ARTGLLRDTVertex tree) {
    findNonterminal(id).addProduction(tree);
  }

  public void addAttribute(String id, String attributeID, String attributeType) {
    findNonterminal(id).addAttribute(attributeID, attributeType);
  }

  public void addDeleter(String id, ARTGLLRDTVertex tree) {
    findNonterminal(id).addDeleter(tree);
  }

  public void addWhiteSpaceTerminal(ARTValueTerm artValueTerm) {
    whitespaceTerminals.add(artValueTerm);
  }

  public void addDeclaredTerminal(ARTValueTerm artValueTerm) {
    nonterminalsDeclaredAsTerminals.add(artValueTerm);
  }

  public void addLonger(ARTValueTerm lhs, ARTValueTerm rhs) {
    if (getLonger().get(lhs) == null) getLonger().put(lhs, new HashSet<ARTValueTerm>());
    getLonger().get(lhs).add(rhs);
  }

  public void addHigher(ARTValueTerm lhs, ARTValueTerm rhs) {
    // System.out.println("Extending Higher relation with " + lhs + " > " + rhs);
    if (getHigher().get(lhs) == null) getHigher().put(lhs, new HashSet<ARTValueTerm>());
    getHigher().get(lhs).add(rhs);
  }

  public Map<String, ARTGrammarElementModuleNonterminal> getNonterminals() {
    return nonterminals;
  }

  public List<ARTGrammarElementModuleNonterminal> getNonterminalList() {
    return nonterminalList;
  }

  public ARTGrammarElementModuleNonterminal getDefaultStart() {
    return defaultStartNonterminal;
  }

  public void setDefaultStart(String id) {
    this.defaultStartNonterminal = findNonterminal(id);
  }

  public void setDefaultStart(ARTGrammarElementModuleNonterminal start) {
    this.defaultStartNonterminal = start;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ARTModule other = (ARTModule) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    return true;
  }

  public void addPreludeString(String preludeString) {
    preludeStrings.add(preludeString);
  }

  public void addSupportString(String supportString) {
    getSupportStrings().add(supportString);
  }

  public Set<String> getPreludeStrings() {
    return preludeStrings;
  }

  public Set<String> getSupportStrings() {
    return supportStrings;
  }

  public ARTGrammarElementModuleNonterminal getDefaultStartNonterminal() {
    return defaultStartNonterminal;
  }

  public void setDefaultStartNonterminal(ARTGrammarElementModuleNonterminal defaultStartNonterminal) {
    this.defaultStartNonterminal = defaultStartNonterminal;
  }

  public Set<ARTValueTerm> getWhitespaceTerminals() {
    return whitespaceTerminals;
  }

  public Set<ARTValueTerm> getDeclaredTerminals() {
    return nonterminalsDeclaredAsTerminals;
  }

  @Override
  public String toString() {
    return id + "\ndefaultStartNonterminal=" + defaultStartNonterminal + "\nimports=" + imports + "\nnonterminals=" + nonterminals + "\nwhitespaceTerminals="
        + whitespaceTerminals + "\ndeclaredTerminals=" + nonterminalsDeclaredAsTerminals + "\nhigher=" + higher + "\nlonger=" + longer + "\npreludeStrings="
        + preludeStrings + "\nsupportStrings=" + supportStrings + "\n";
  }

  public Map<ARTValueTerm, Set<ARTValueTerm>> getLonger() {
    return longer;
  }

  public Map<ARTValueTerm, Set<ARTValueTerm>> getHigher() {
    return higher;
  }
}
