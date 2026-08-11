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
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElement;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

/* This version of AJDebug is intended to perform regression testing between V5 and V3 first/follow set computations */

/* General notes from July 2026 analysis
 *
 * 1. Seems to be able to work across a set of files in a directory - that's nice and seems to be working well
 *
 * 2. Thoughts - can we not massivley simplify all this by just working with the string reprsentation of the grammar element?
 *
 */
public final class AJDebug {
  /* AJ debug material below this line */
  CFGRules grammarV5; // regression V5 grammar
  ARTGrammar grammarV3; // regression V3 grammar

  ScriptInterpreter regressionScriptInterpreter;

  public AJDebug(String[] args) {
    Util.errorLevel = 4;
    Util.info("!! ajdebug " + args[1]);
    // testStringEscapes();

    testSetRegressions(args);
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
    Util.info("");
    Util.info("File " + filePath);
    boolean good = v5v3RegressionFirstAndFollowSets(Files.readString(filePath));
    Util.info("File " + filePath + " " + (good ? " Good " : "Bad"));
  }

  private boolean v5v3RegressionFirstAndFollowSets(String scriptString) {
    ART.tracing = true;
    regressionScriptInterpreter = new ScriptInterpreter(scriptString);

    ARTV3 artV3 = new ARTV3(scriptString);

    grammarV3 = artV3.artManager.addGrammar("Parser grammar", artV3.artManager.getDefaultMainModule(), false, artV3.artManager.artDirectives);
    grammarV5 = ScriptInterpreter.currentCFGRules;
    grammarV5.normalise();

    boolean good = true;

    // First check the nonterminals
    for (ARTGrammarElementNonterminal v3Nonterminal : grammarV3.getNonterminals()) {
      CFGElement v5Nonterminal = grammarV5.elements.get(new CFGElement(CFGElementKind.NONTERMINAL, v3Nonterminal.getId()));

      Set<String> v3FirstSet = new TreeSet<>(), v5FirstSet = new TreeSet<>();
      for (var f : v3Nonterminal.first)
        v3FirstSet.add(f.toString());

      for (var f : grammarV5.first.get(v5Nonterminal))
        v5FirstSet.add(f.toString());

      boolean goodFirst = v3FirstSet.equals(v5FirstSet);
      good &= goodFirst;
      Util.info("First " + (goodFirst ? "" : "mis") + "match for " + v3Nonterminal + " V3: " + v3FirstSet + " V5: " + v5FirstSet);

      Set<String> v3FollowSet = new TreeSet<>(), v5FollowSet = new TreeSet<>();
      for (var f : v3Nonterminal.follow)
        v3FollowSet.add(f.toString());

      for (var f : grammarV5.follow.get(v5Nonterminal))
        v5FollowSet.add(f.toString());

      boolean goodFollow = v3FollowSet.equals(v5FollowSet);
      good &= goodFollow;
      Util.info("Follow " + (goodFollow ? "" : "mis") + "match for " + v3Nonterminal + " V3: " + v3FollowSet + " V5: " + v5FollowSet);

    }

    // Now work through instance sets
    v5v3RegressionGatherV3InstanceSetsRec((ARTGrammarInstance) grammarV3.getInstanceTree().getRoot());
    good &= v5v3RegressionCheckFirstAndFollowInstanceSets(grammarV5, artV3);
    return good;
  }

  private boolean v5v3RegressionCheckFirstAndFollowInstanceSets(CFGRules grammarV5, ARTV3 artV3) {
    boolean good = true;
    for (CFGElement e : grammarV5.elements.keySet())
      if (e.cfgKind == CFGElementKind.NONTERMINAL) {
        good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(grammarV5.elementToRulesNodeMap.get(e).alt, artV3);
      }

    return good;
  }

  Set<CFGElementKind> scaffoldingKinds = Set.of(CFGElementKind.ALT, CFGElementKind.KLN, CFGElementKind.OPT, CFGElementKind.PAR, CFGElementKind.POS);

  private boolean v5v3RegressionCheckFirstAndFollowInstanceSetsRec(CFGNode v5, ARTV3 artV3) {
    if (v5 == null) return true;

    // Util.debug("v5v3RegressionCheckFirstAndFollowInstanceSetsRec at " + v5.toStringDot());
    boolean good = true;
    String key = v5.toStringAsProduction().replaceAll("\\s", "");
    Set<ARTGrammarElement> v3InstanceFirst = v3InstanceFirsts.get(key), v3InstanceGuard = v3InstanceGuards.get(key),
        v3InstanceFollow = v3InstanceFollows.get(key);
    Set<CFGElement> v5InstanceFirst = grammarV5.instanceFirst.get(v5), v5InstanceGuard = grammarV5.instanceGuard.get(v5),
        v5InstanceFollow = grammarV5.instanceFollow.get(v5);

    if (!scaffoldingKinds.contains(v5.cfgElement.cfgKind)) {
      if (v3InstanceFirsts.get(key) == null) {
        Util.info(" v3 key is missing: " + key);
        return false;
      }

      Set<String> v3FirstSet = new TreeSet<>(), v5FirstSet = new TreeSet<>();
      for (var f : v3InstanceFirst)
        v3FirstSet.add(f.toString());

      for (var f : v5InstanceFirst)
        v5FirstSet.add(f.toString());

      boolean goodFirst = v3FirstSet.equals(v5FirstSet);
      good &= goodFirst;

      Util.info("Instance first " + (goodFirst ? "" : "mis") + "match at " + key + " V3: " + v3FirstSet + " V5: " + v5FirstSet);

      Set<String> v3GuardSet = new TreeSet<>(), v5GuardSet = new TreeSet<>();
      for (var f : v3InstanceGuard)
        v3GuardSet.add(f.toString());

      for (var f : v5InstanceGuard)
        v5GuardSet.add(f.toString());

      boolean goodGuard = v3GuardSet.equals(v5GuardSet);
      good &= goodGuard;

      Util.info("Instance Guard " + (goodGuard ? "" : "mis") + "match at " + key + " V3: " + v3GuardSet + " V5: " + v5GuardSet);

      Set<String> v3FollowSet = new TreeSet<>(), v5FollowSet = new TreeSet<>();
      for (var f : v3InstanceFollow)
        v3FollowSet.add(f.toString());

      for (var f : v5InstanceFollow)
        v5FollowSet.add(f.toString());

      boolean goodFollow = v3FollowSet.equals(v5FollowSet);
      if (!goodFollow) Util.info("Instance follow " + (goodFollow ? "" : "mis") + "match at " + key + " V3: " + v3FollowSet + " V5: " + v5FollowSet);

      good &= goodFollow;

      if (v5.cfgElement.cfgKind == CFGElementKind.END) return good;
    }

    good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(v5.seq, artV3);
    good &= v5v3RegressionCheckFirstAndFollowInstanceSetsRec(v5.alt, artV3);

    return good;
  }

  Map<String, TreeSet<ARTGrammarElement>> v3InstanceFirsts = new HashMap<>();
  Map<String, TreeSet<ARTGrammarElement>> v3InstanceFollows = new HashMap<>();
  Map<String, TreeSet<ARTGrammarElement>> v3InstanceGuards = new HashMap<>();
  Set<String> checked = new HashSet<>();

  void v5v3RegressionGatherV3InstanceSetsRec(ARTGrammarInstance v3) {
    if (v3 == null) return;
    // Util.debug("v5v3RegressionGatherV3InstanceSetsRec at [" + v3.getKey() + "] " + v3.toGrammarString(".") + " first=" + v3.getFirst()
    // + " follow=" + v3.getFollow());
    // Note: replaceAll on \s takes out whitespace
    if (v3 instanceof ARTGrammarInstanceSlot) {
      var key = v3.toGrammarString(".").replaceAll("\\s", "");
      v3InstanceFirsts.put(key, new TreeSet<>(v3.first));
      v3InstanceGuards.put(key, new TreeSet<>(v3.guard));
      v3InstanceFollows.put(key, new TreeSet<>(v3.follow));
    }
    v5v3RegressionGatherV3InstanceSetsRec(v3.getChild());
    v5v3RegressionGatherV3InstanceSetsRec(v3.getSibling());
  }
}
