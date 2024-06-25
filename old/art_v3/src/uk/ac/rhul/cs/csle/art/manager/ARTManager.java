package uk.ac.rhul.cs.csle.art.manager;

import java.io.FileNotFoundException;
import java.util.HashMap;

import uk.ac.rhul.cs.csle.art.alg.gll.support.ARTGLLRDT;
import uk.ac.rhul.cs.csle.art.manager.fx.ARTFXMainWindow;
import uk.ac.rhul.cs.csle.art.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.manager.module.ARTModule;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTLexer;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser;
import uk.ac.rhul.cs.csle.art.manager.parser.ARTParser.ARTAT_ARTV3_text;
import uk.ac.rhul.cs.csle.art.util.ARTException;
import uk.ac.rhul.cs.csle.art.util.graph.ARTTree;
import uk.ac.rhul.cs.csle.art.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;

/**
 * ARTManager - central manager object for a running ART system
 * <P>
 * Maintains a set of ARTModule, a set of ARTGrammar, a text handler, an ART specification parser and a default module
 *
 * @author Adrian Johnstone {@literal <a.johnstone@rhul.ac.uk>}
 * @version 3.1
 *
 */

public class ARTManager {
  // private ARTText text;
  public final ARTOptionBlock optionBlock = new ARTOptionBlock();

  private final ARTParser parser;
  private final HashMap<String, ARTModule> modules = new HashMap<String, ARTModule>();
  private final HashMap<String, ARTGrammar> grammars = new HashMap<String, ARTGrammar>();
  private ARTModule defaultMainModule = null;

  public ARTManager() throws ARTException {
    this(new ARTText(new ARTTextHandlerConsole()));
  }

  public ARTManager(ARTText text) throws ARTException {
    parser = new ARTParser(new ARTLexer());
  }

  public ARTOptionBlock getOptionBlock() {
    return optionBlock;
  }

  public ARTParser getParser() {
    return parser;
  }

  public HashMap<String, ARTModule> getModules() {
    return modules;
  }

  public HashMap<String, ARTGrammar> getGrammars() {
    return grammars;
  }

  public ARTModule getDefaultMainModule() {
    return defaultMainModule;
  }

  public ARTModule setDefaultMainModule(String id) {
    return defaultMainModule = findModule(id);
  }

  public ARTModule setDefaultMainModule(ARTModule defaultMainModule) {
    return this.defaultMainModule = defaultMainModule;
  }

  public void clearDefaultMainModule() {
    defaultMainModule = null;
  }

  public ARTModule findModule(String id) {
    ARTModule ret;

    if ((ret = modules.get(id)) == null) modules.put(id, ret = new ARTModule(this, id));
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String m : modules.keySet())
      sb.append("* Module " + modules.get(m).toString());

    if (grammars.isEmpty())
      sb.append("* No grammars\n");
    else
      for (String m : grammars.keySet())
        sb.append("* Grammar " + grammars.get(m).toString());

    return sb.toString();
  }

  public void fxMode(ARTGrammar grammar, String specificationFilename, String inputFilename, ARTTree RDT) throws ARTException {
    new ARTFXMainWindow(this, grammar, specificationFilename, RDT, inputFilename, 0, 0, 1, 1);
  }

  public void fxMode(String specificationFilename, String inputFilename) throws FileNotFoundException, ARTException {
    ARTTree rdt = parseARTSpecification(ARTText.readFile(specificationFilename));
    fxMode(addGrammar(specificationFilename, defaultMainModule, false, optionBlock), specificationFilename, inputFilename, rdt);
  }

  public ARTTree parseARTSpecification(String artSpecification) throws ARTException, FileNotFoundException {
    ARTAT_ARTV3_text textAttributes = new ARTAT_ARTV3_text();
    textAttributes.artManager = this;

    parser.artParse(artSpecification);
    if (optionBlock.verbosityLevel > 1) parser.artLexDump("artLexerOutput.txt");
    if (!parser.artIsInLanguage) throw new ARTException("Syntax error in ART specification");
    if (parser.artIsAmbiguous()) throw new ARTException("Internal specification error in artParser.art: ARTParser returns ambiguous SPPF");
    parser.artDerivationSelectFirst();
    if (optionBlock.verbosityLevel > 1) parser.artRenderSPPF("artParserSPPF.dot", parser.artRenderKindDerivation);
    ARTGLLRDT ret = parser.artEvaluator(textAttributes);
    if (optionBlock.verbosityLevel > 1) ret.printDot("artParserRDT.dot");
    return ret;
  }

  public ARTGrammar addGrammar(String id, ARTModule module, boolean augment, ARTOptionBlock artOptionBlock) throws ARTException, FileNotFoundException {
    ARTGrammar ret = new ARTGrammar(this, id, module, augment, artOptionBlock);
    // System.out.println("ART manager added grammar: ");
    // ret.prettyPrint();

    grammars.put(id, ret);
    return ret;
  }

  public void printMemory(String label) {
    Runtime instance = Runtime.getRuntime();

    System.out.println(label + " memory " + (instance.totalMemory() - instance.freeMemory()) + " ("
        + (instance.totalMemory() - instance.freeMemory()) / (1024 * 1024) + "Mbytes)");
  }

  public void forceGC(String label) {
    Runtime instance = Runtime.getRuntime();

    System.out.println(label + " - before forcing garbage collection" + " memory " + (instance.totalMemory() - instance.freeMemory()) + " ("
        + (instance.totalMemory() - instance.freeMemory()) / (1024 * 1024) + "Mbytes)");

    instance.gc();
    instance.gc();

    System.out.println(label + " - after forcing garbage collection" + " memory " + (instance.totalMemory() - instance.freeMemory()) + " ("
        + (instance.totalMemory() - instance.freeMemory()) / (1024 * 1024) + "Mbytes)");
  }
}
