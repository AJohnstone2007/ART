package uk.ac.rhul.cs.csle.art.term;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;

public class Signatures implements DisplayInterface {
  // Fields that need to be cloned
  private final Map<Integer, Map<Integer, List<Integer>>> signatures = new LinkedHashMap<>(); // Map

  // Working fields initialised by !try or normalise

  private boolean normalised = false;

  public Signatures(Signatures payload) { // Copy constructor
    // TODO: unfinished
  }

  public Signatures() {
    super();
  }

  public void buildSignature(int term) {
    int relation = ScriptInterpreter.iTerms.subterm(term, 1); // Modified 27/2/26 to retain trRelation nonterminal
    int constructorIndex = ScriptInterpreter.iTerms.termSymbolStringIndex((ScriptInterpreter.iTerms.subterm(term, 0)));
    Util.debug("Building signature " + ScriptInterpreter.iTerms.toString(term) + "\nwith relation " + ScriptInterpreter.iTerms.toString(relation)
        + "\nand constructor " + ScriptInterpreter.iTerms.getString(constructorIndex));
    if (signatures.get(relation) == null) signatures.put(relation, new LinkedHashMap<>());
    Map<Integer, List<Integer>> map = signatures.get(relation);
    if (map.get(constructorIndex) == null) map.put(constructorIndex, new LinkedList<>());
    map.get(constructorIndex).add(term);
    normalised = false;
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indent, int depthLimit) {
    for (var rel : signatures.keySet())
      for (var constructor : signatures.get(rel).keySet())
        for (var s : signatures.get(rel).get(constructor))
          outputStream.println(ScriptInterpreter.iTerms.toString(s, outputTraverser, indent, depthLimit));
  }
}
