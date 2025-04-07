package uk.ac.rhul.cs.csle.art.cfg;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivations;
import uk.ac.rhul.cs.csle.art.util.process.AbstractProcesses;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStacks;

public abstract class AbstractParser {
  public AbstractLexer lexer;
  protected int tokenIndex; // Current input index
  public AbstractProcesses processes = null;
  public AbstractDerivations derivations = null;
  public AbstractStacks stacks = null;
  public CFGRules cfgRules;
  public boolean inLanguage;

  public void parse(AbstractLexer lexer) {
    System.out.println("parse(lexer) not implemented for parser class " + this.getClass().getSimpleName());
  }

  public CFGNode getLHS(CFGNode gn) {
    return cfgRules.elementToNodeMap.get(gn.element);
  }

  /* Statistics support for RunExp below this line ***************************/
  private long startTime, setupTime, lexTime, lexChooseTime, parseTime, parseChooseTime, reachabilityTime, cycleBreakTime, termGenerateTime, semanticsTime;
  private long tweNodeCount, tweEdgeCount, tweLexCount;
  private long descriptorCount;
  private long gssNodeCount, gssEdgeCount, popCount;
  private long sppfEpsilonNodeCount, sppfTerminalNodeCount, sppfNonterminalNodeCount, sppfIntermediateNodeCount, sppfSymbolPlusIntermediateNodeCount,
      sppfPackNodeCount, sppfAmbiguityCount, sppfEdgeCount, sppfCyclicSCCCount;
  private long derivationNodeCount, derivationAmbiguityNodeCount;
  private long startMemory, endMemory;
  private long poolAllocated, h0, h1, h2, h3, h4, h5, h6more;

  public void loadSetupTime() {
    setupTime = System.nanoTime();
  }

  public void loadLexTime() {
    lexTime = System.nanoTime();
  }

  public void loadLexChooseTime() {
    lexChooseTime = System.nanoTime();
  }

  public void loadParseTime() {
    parseTime = System.nanoTime();
  }

  public void loadParseChooseTime() {
    parseChooseTime = System.nanoTime();
  }

  public void loadParseReachabilityTime() {
    reachabilityTime = System.nanoTime();
  }

  public void loadcycleBreakTime() {
    cycleBreakTime = System.nanoTime();
  }

  public void loadTermGenerateTime() {
    termGenerateTime = System.nanoTime();
  }

  public void loadSemanticsTime() {
    semanticsTime = System.nanoTime();
  }

  public void loadTWECounts(long tweNodeCount, long tweEdgeCount, long tweLexCount) {
    this.tweNodeCount = tweNodeCount;
    this.tweEdgeCount = tweEdgeCount;
    this.tweLexCount = tweLexCount;
  }

  public void loadGSSCounts(long descriptorCount, long gssNodeCount, long gssEdgeCount, long popCount) {
    this.descriptorCount = descriptorCount;
    this.gssNodeCount = gssNodeCount;
    this.gssEdgeCount = gssEdgeCount;
    this.popCount = popCount;
  }

  public void loadSPPFCounts(long sppfEpsilonNodeCount, long sppfTerminalNodeCount, long sppfNonterminalNodeCount, long sppfIntermediateNodeCount,
      long sppfSymbolPlusIntermediateNodeCount, long sppfPackNodeCount, long sppfAmbiguityCount, long sppfEdgeCount) {
    this.sppfEpsilonNodeCount = sppfEpsilonNodeCount;
    this.sppfTerminalNodeCount = sppfTerminalNodeCount;
    this.sppfNonterminalNodeCount = sppfNonterminalNodeCount;
    this.sppfIntermediateNodeCount = sppfIntermediateNodeCount;
    this.sppfSymbolPlusIntermediateNodeCount = sppfSymbolPlusIntermediateNodeCount;
    this.sppfPackNodeCount = sppfPackNodeCount;
    this.sppfAmbiguityCount = sppfAmbiguityCount;
    this.sppfEdgeCount = sppfEdgeCount;
  }

  public void loadSPPFCyclicSCCCount(long sppfCyclicSCCCount) {
    this.sppfCyclicSCCCount = sppfCyclicSCCCount;
  }

  public void loadDerivationCounts(long derivationNodeCount, long derivationAmbiguityNodeCount) {
    this.derivationNodeCount = derivationNodeCount;
    this.derivationAmbiguityNodeCount = derivationAmbiguityNodeCount;
  }

  public void loadStartMemory() {
    // startMemory = memoryUsed();
  }

  public void loadEndMemory() {
    // endMemory = memoryUsed();
  }

  private long memoryUsed() {
    System.gc();
    System.gc();
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  public void loadPoolAllocated(long m) {
    poolAllocated = m;
  }

  public void loadHashCounts(long h0, long h1, long h2, long h3, long h4, long h5, long h6more) {
    this.h0 = h0;
    this.h1 = h1;
    this.h2 = h2;
    this.h3 = h3;
    this.h4 = h4;
    this.h5 = h5;
    this.h6more = h6more;
  }

  private void normaliseTimes() {
    if (setupTime < startTime) setupTime = startTime;
    if (lexTime < setupTime) lexTime = setupTime;
    if (lexChooseTime < lexTime) lexChooseTime = lexTime;
    if (parseTime < lexChooseTime) parseTime = lexChooseTime;
    if (parseChooseTime < parseTime) parseChooseTime = parseTime;
    if (termGenerateTime < parseChooseTime) termGenerateTime = parseChooseTime;
    if (semanticsTime < termGenerateTime) semanticsTime = termGenerateTime;
  }

  private String artGetTimes() {
    return Util.timeAsMilliseconds(startTime, setupTime) + "," + Util.timeAsMilliseconds(setupTime, lexTime) + ","
        + Util.timeAsMilliseconds(lexTime, lexChooseTime) + "," + Util.timeAsMilliseconds(lexChooseTime, parseTime) + ","
        + Util.timeAsMilliseconds(parseTime, parseChooseTime) + "," + Util.timeAsMilliseconds(parseChooseTime, termGenerateTime) + ","
        + Util.timeAsMilliseconds(termGenerateTime, semanticsTime);
  }

  private String getTWECounts() {
    return tweNodeCount + "," + tweEdgeCount + "," + tweLexCount;
  }

  private String getGSSCounts() {
    return descriptorCount + "," + gssNodeCount + "," + gssEdgeCount + "," + popCount;
  }

  private String getSPPFCounts() {
    return sppfEpsilonNodeCount + "," + sppfTerminalNodeCount + "," + sppfNonterminalNodeCount + "," + sppfIntermediateNodeCount + ","
        + sppfSymbolPlusIntermediateNodeCount + "," + sppfPackNodeCount + "," + sppfAmbiguityCount + "," + sppfEdgeCount + "," + sppfCyclicSCCCount;
  }

  private String getDerivationCounts() {
    return derivationNodeCount + "," + derivationAmbiguityNodeCount;
  }

  private String getPoolCounts() {
    return poolAllocated + "," + h0 + "," + h1 + "," + h2 + "," + h3 + "," + h4 + "," + h5 + "," + h6more;
  }

  public void resetStatistics() {
    startTime = System.nanoTime();
    setupTime = lexTime = lexChooseTime = parseTime = parseChooseTime = termGenerateTime = semanticsTime = 0;
    tweNodeCount = tweEdgeCount = tweLexCount = -1;
    descriptorCount = gssNodeCount = gssEdgeCount = popCount = -1;
    sppfEpsilonNodeCount = sppfTerminalNodeCount = sppfNonterminalNodeCount = sppfIntermediateNodeCount = sppfSymbolPlusIntermediateNodeCount = sppfPackNodeCount = sppfAmbiguityCount = sppfEdgeCount = sppfCyclicSCCCount = -1;
    startMemory = endMemory = poolAllocated = h0 = h1 = h2 = h3 = h4 = h5 = h6more = -1;
    derivationNodeCount = derivationAmbiguityNodeCount = -1;
  }

  public String statisticsToString() {
    normaliseTimes();
    return lexer.inputString.length() + "," + getClass().getSimpleName() + "," + (inLanguage ? "accept" : "reject") + ",OK," + artGetTimes() + ","
        + getTWECounts() + "," + getGSSCounts() + "," + getSPPFCounts() + "," + getDerivationCounts() + "," + (endMemory - startMemory) + "," + getPoolCounts();
  }

}
