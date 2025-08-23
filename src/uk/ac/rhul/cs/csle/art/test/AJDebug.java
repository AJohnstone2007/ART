package uk.ac.rhul.cs.csle.art.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import uk.ac.rhul.cs.csle.art.ART;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEoS;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalBuiltin;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCaseInsensitive;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCaseSensitive;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminalCharacter;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.module.ARTV3Module;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

/* This version of AJDebug is intended to perform regression testing between V5 and V3 first/follow set computations */
public final class AJDebug {
  /* AJ debug material below this line */
  CFGRules grammarV5; // regression V5 grammar
  ARTGrammar grammarV3; // regression V3 grammar

  ScriptTermInterpreter regressionScriptInterpreter;

  public AJDebug(String[] args) {
    Util.errorLevel = 4;
    Util.info("!! Test mode ajdebug " + args[1]);
    testStringEscapes();

    // testSetRegressions(args);
  }

  private void testSetRegressions(String[] args) {
    try {
      Path arg1AsPath = Paths.get(args[1]);
      if (args[1].endsWith(".art"))
        processFile(arg1AsPath);
      else if (Files.isDirectory(arg1AsPath)) {
        for (Path filePath : Files.list(arg1AsPath).collect(Collectors.toList()))
          if (filePath.toString().endsWith(".art")) processFile(filePath);
      } else
        Util.fatal("ajDebug: argument must be a filename ending with .art or a directory");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void testStringEscapes() {
    String str = "adri " + "\\" + "v" + "00005Aan";
    Util.info("Original string: " + str);
    Util.info("Escaped space original string: " + Util.escapeString(str, true));
    Util.info("Unescaped original string: " + Util.unescapeString(str));
  }

  private void processFile(Path filePath) throws IOException {
    boolean good = v5v3RegressionFirstAndFollowSets(Files.readString(filePath));
    Util.info("File " + filePath + " " + (good ? " Good " : "Bad"));
  }

  private boolean v5v3RegressionFirstAndFollowSets(String scriptString) {

    // System.out.print("v5v3RegressionFirstAndFollowSets");

    ART.tracing = true;
    regressionScriptInterpreter = new ScriptTermInterpreter(scriptString);

    ARTV3 artV3 = new ARTV3(scriptString);

    grammarV3 = artV3.artManager.addGrammar("Parser grammar", artV3.artManager.getDefaultMainModule(), false, artV3.artManager.artDirectives);

    // System.out.print("\n*** V3 grammar\n" + grammarV3.toString());
    grammarV5 = regressionScriptInterpreter.currentCFGRules;
    Util.info("*** Working grammar normalisation starts here");
    grammarV5.normalise();

    boolean good = true;

    // First check the nonterminals
    for (ARTGrammarElementNonterminal v3Nonterminal : grammarV3.getNonterminals()) {
      CFGElement v5Nonterminal = grammarV5.elements.get(new CFGElement(CFGKind.NONTERMINAL, v3Nonterminal.getId()));

      // Util.info(
      // "V3 nonterminal " + v3Nonterminal + " first " + new TreeSet<>(v3Nonterminal.getFirst()) + " follow " + new TreeSet<>(v3Nonterminal.getFollow()));
      // Util.info("V5 nonterminal " + v5Nonterminal + " first " + v5Nonterminal.first + " follow " + v5Nonterminal.follow + "\n");

      if (!v5v3ElementSetSame(grammarV5.first.get(v5Nonterminal), new TreeSet<>(v3Nonterminal.getFirst()), artV3.artManager.getDefaultMainModule(),
          v5Nonterminal)) {
        System.out
            .println("First for " + v5Nonterminal + " differ:\nV5 " + grammarV5.first.get(v5Nonterminal) + "\nV3 " + new TreeSet<>(v3Nonterminal.getFirst()));
        good = false;
      }

      if (!v5v3ElementSetSame(grammarV5.first.get(v5Nonterminal), new TreeSet<>(v3Nonterminal.getFollow()), artV3.artManager.getDefaultMainModule(), null)) {
        // Bug in V3? Spurious $ check
        // Set<GrammarElement> v5prime = new TreeSet<>(grammarV5.follow.get(v5Nonterminal));
        // v5prime.add(grammarV5.endOfStringElement);
        // if (!v5v3ElementSetSame(v5prime, new TreeSet<>(v3Nonterminal.getFollow()), artV3.artManager.getDefaultMainModule()))
        {

          Util.info("Follow for " + v5Nonterminal + " differ:\nV5 " + grammarV5.follow.get(v5Nonterminal) + "\nV3 " + new TreeSet<>(v3Nonterminal.getFollow()));
          Util.info("v5:v3 cardinality " + grammarV5.follow.get(v5Nonterminal).size() + " : " + v3Nonterminal.getFollow().size() + "\n");
          good = false;
        }
      }
    }

    // Now work through instance sets
    // v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec((ARTGrammarInstance) grammarV3.getInstanceTree().getRoot());
    // good &= v5v3RegressionCheckFirstAndFollowInstanceSets(grammarV5, artV3);
    return good;
  }

  private boolean v5v3RegressionCheckFirstAndFollowInstanceSetsRec(CFGNode v5, ARTV3 artV3) {
    if (v5 == null) return true;

    boolean good = true;
    String key = v5.toStringAsProduction().replaceAll("\\s", "");
    // Util.info("V5 instance " + key + " first " + v5.instanceFirst + " follow " + v5.instanceFollow);
    Set<ARTGrammarElement> v3InstanceFirst = v3InstanceFirsts.get(key), v3InstanceFollow = v3InstanceFollows.get(key);

    if (v3InstanceFirsts.get(key) == null)
      // Util.info(" v3 key is missing")
      ;
    else {
      // if (!v5v3ElementSetSame(v5.instanceFirst, v3InstanceFirst, artV3.artManager.getDefaultMainModule())) {
      // Util.info("Instance first differ: V5 " + v5.instanceFirst + " V3 " + v3InstanceFirst);
      // good = false;
      // }
      // if (v5.elm.kind == GrammarKind.N && !v5v3ElementSetSame(v5.instanceFollow, v3InstanceFollow, artV3.artManager.getDefaultMainModule())) {
      // Util.info("Instance follow differ: V5 " + v5.instanceFollow + " V3 " + v3InstanceFollow);
      // good = false;
      // }
    }
    // Util.info();
    if (v5.cfgElement.cfgKind == CFGKind.END) return good;

    good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(v5.seq, artV3);
    good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(v5.alt, artV3);

    return good;
  }

  private boolean v5v3RegressionCheckFirstAndFollowInstanceSets(CFGRules grammarV5, ARTV3 artV3) {
    boolean good = true;
    for (CFGElement e : grammarV5.elements.keySet())
      if (e.cfgKind == CFGKind.NONTERMINAL) good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(grammarV5.elementToRulesNodeMap.get(e).alt, artV3);

    return good;
  }

  Map<String, Set<ARTGrammarElement>> v3InstanceFirsts = new HashMap<>();
  Map<String, Set<ARTGrammarElement>> v3InstanceFollows = new HashMap<>();
  Set<String> checked = new HashSet<>();

  void v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(ARTGrammarInstance v3) {
    if (v3 == null) return;
    // Util.info(
    // "v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec at [" + v3.getKey() + "] " + v3.toGrammarString() + " first=" + v3.first + " follow=" +
    // v3.follow);
    if (v3 instanceof ARTGrammarInstanceSlot) {
      v3InstanceFirsts.put(v3.toGrammarString(".").replaceAll("\\s", ""), v3.first); // (v3.getSibling() == null ? v3.first : v3.getSibling().first));
      v3InstanceFollows.put(v3.toGrammarString(".").replaceAll("\\s", ""), (v3.getSibling() == null ? v3.follow : v3.getSibling().follow));
    }
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(v3.getChild());
    v5v3RegressionGatherV3FirstAndFollowInstanceSetsRec(v3.getSibling());
  }

  private boolean v5v3ElementSetSame(Set<CFGElement> v5, Set<ARTGrammarElement> v3, ARTV3Module artv3Module, CFGElement v5IgnoreElement) {
    boolean ret = true;

    for (CFGElement ve5 : v5) {
      if (ve5.equals(v5IgnoreElement)) continue;
      ret &= v3.contains(v5Element2v3Element(ve5, artv3Module));
      // Util.info("Checked v5 " + ve5 + " " + ret);
    }

    for (ARTGrammarElement ve3 : v3) {
      CFGElement ve5 = v3Element2v5Element(ve3);

      ret &= v5.contains(ve5);
      // Util.info("Checked v3 " + ve3 + " " + ret + " with v5 as " + ve5);
    }
    return ret;
  }

  CFGElement v3Element2v5Element(ARTGrammarElement elem) {
    if (elem instanceof ARTGrammarElementTerminalBuiltin) return new CFGElement(CFGKind.TRM_BI, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementTerminalCharacter) return new CFGElement(CFGKind.TRM_CH, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementEoS) return new CFGElement(CFGKind.EOS, "$");
    if (elem instanceof ARTGrammarElementEpsilon) return new CFGElement(CFGKind.EPSILON, "#");
    if (elem instanceof ARTGrammarElementNonterminal) return new CFGElement(CFGKind.NONTERMINAL, elem.toString());
    if (elem instanceof ARTGrammarElementTerminalCaseSensitive) return new CFGElement(CFGKind.TRM_CS, ((ARTGrammarElementTerminal) elem).getId());
    if (elem instanceof ARTGrammarElementTerminalCaseInsensitive) return new CFGElement(CFGKind.TRM_CI, ((ARTGrammarElementTerminal) elem).getId());

    return null;
  }

  ARTGrammarElement v5Element2v3Element(CFGElement elem, ARTV3Module artV3Module) {
    switch (elem.cfgKind) {
    case ALT, PAR, END, KLN, OPT, POS:
      return null; // These should not appear

    case TRM_BI:
      return new ARTGrammarElementTerminalBuiltin(elem.str);
    case TRM_CH:
      return new ARTGrammarElementTerminalCharacter(elem.str);
    case EOS:
      return new ARTGrammarElementEoS();
    case EPSILON:
      return new ARTGrammarElementEpsilon();
    case NONTERMINAL:
      return new ARTGrammarElementNonterminal(artV3Module, elem.str);
    case TRM_CS:
      return new ARTGrammarElementTerminalCaseSensitive(elem.str);
    case TRM_CI:
      return new ARTGrammarElementTerminalCaseInsensitive(elem.str);
    }
    return null; // To settle the Java control flow analyser - the above case list should be complete
  }
}
