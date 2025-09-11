package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.cfg.lexer.TWESetElement;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFDummyForRecognisers;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSGLL;
import uk.ac.rhul.cs.csle.art.util.tasks.TasksGLL;

/**
 * This is the 'master' GLL/MGLL implementation which has selectable features based on final booleans
 */
public class GLLModal extends AbstractParser {
  private CFGNode cfgNode; // current Context Free Grammar Node
  private AbstractStackNode stackNode; // current top of stack node
  private AbstractDerivationNode derivationNode; // current derivation forest node

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    inLanguage = false;
    this.input = input;
    this.cfgRules = cfgRules;
    this.lexer = lexer;
    tasks = new TasksGLL();
    stacks = new GSSGLL(cfgRules);
    if (ScriptInterpreter.currentModes.contains("recogniser"))
      derivations = new SPPFDummyForRecognisers(this);
    else
      derivations = new SPPF(this);

    lexer.lex(input, cfgRules, chooseRules);

    inputIndex = 0;
    cfgNode = cfgRules.elementToRulesNodeMap.get(cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueProductionTasks();
    nextTask: while (nextTask())
      nextCFGNode: while (true) {
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueProductionTasks(); // Create task descriptor for the start of each production
          continue nextTask;
        case EPSILON:
          derivationNode = updateDerivation(inputIndex); // Must match, but nothing consumed, so rightExtent = inputIndex
          cfgNode = cfgNode.seq; // Next grammar node which will be an END node
          continue nextCFGNode; // Continue with this sequence
        case SOS, TRM_BI, TRM_CS, TRM_CI, TRM_CH, TRM_CH_SET, TRM_CH_ANTI_SET: // Look for exact instance
          var slice = lexer.tweSlices[inputIndex];
          if (slice == null) continue nextTask;// Nothing todo for empty TWE slices
          nextSliceElement: for (int sliceIndex = 0; sliceIndex < slice.length; sliceIndex++) // Iterate over the TWE set elements in this slice
            if (!slice[sliceIndex].suppressed && matchTerminal(slice[sliceIndex].cfgElement)) { // Ignore suppressed TWE set elements
              // Util.debug("Matched " + cfgNode.toStringAsProduction());
              if (ScriptInterpreter.currentModes.contains("mgll")) { // MGLL only: create continuation task descriptors for all subsequent matches in this slice
                for (int restOfIndex = sliceIndex + 1; restOfIndex < slice.length; restOfIndex++)
                  if (!slice[sliceIndex].suppressed && matchTerminal(slice[sliceIndex].cfgElement))
                    tasks.queue(slice[restOfIndex].rightExtent, cfgNode.seq, stackNode, updateDerivation(slice[restOfIndex].rightExtent));
              }
              derivationNode = updateDerivation(slice[sliceIndex].rightExtent); // Now process the first-found slice element
              inputIndex = slice[sliceIndex].rightExtent; // Step input past the matched TWE
              cfgNode = cfgNode.seq; // Step to next grammar node
              continue nextCFGNode; // Continue with this sequence
            }
          continue nextTask;
        case NONTERMINAL:
          call(cfgNode);
          continue nextTask;
        case END:
          var lhs = cfgNode.seq.cfgElement;
          if (lookaheadFollow("returnlookahead", cfgNode.seq.cfgElement)) retrn();
          continue nextTask;
        default:
          Util.fatal("Unexpected CFGNode kind " + cfgNode.cfgElement.cfgKind + " in " + getClass().getSimpleName());
        }
      }
  }

  private boolean matchTerminal(CFGElement cfgElement) {
    switch (cfgNode.cfgElement.cfgKind) {
    case TRM_CH_SET:
      return cfgElement.cfgKind == CFGElementKind.TRM_CH && cfgNode.cfgElement.set.contains(cfgElement.str.charAt(0));
    case TRM_CH_ANTI_SET:
      return cfgElement.cfgKind == CFGElementKind.TRM_CH && !cfgNode.cfgElement.set.contains(cfgElement.str.charAt(0));
    default:
      return (cfgElement == cfgNode.cfgElement);
    }
  }

  private AbstractDerivationNode updateDerivation(int rightExtent) {
    var tmp = cfgRules.elementToRulesNodeMap.get(cfgNode.cfgElement);
    var rightNode = derivations.find(tmp, inputIndex, rightExtent);
    return derivations.extend(cfgNode.seq, derivationNode, rightNode);
  }

  private void queueProductionTasks() {
    for (CFGNode production = cfgNode; production != null; production = production.alt)
      if (lookaheadInstanceFirst("productionlookahead", production.seq)) tasks.queue(inputIndex, production.seq, stackNode, derivationNode);
  }

  private boolean nextTask() {
    var task = tasks.next();
    Util.debug("Processing task " + task);
    if (task == null) return false;
    inputIndex = task.inputIndex;
    cfgNode = task.cfgNode;
    stackNode = task.stackNode;
    derivationNode = task.derivationNode;
    return true;
  }

  private void call(CFGNode cfgNode) {
    Util.debug("Call on nonterminal node " + cfgNode);
    var newStackNode = stacks.push(derivations, tasks, inputIndex, cfgNode, stackNode, derivationNode);
    for (CFGNode p = cfgRules.elementToRulesNodeMap.get(cfgNode.cfgElement).alt; p != null; p = p.alt) {
      Util.debug("Production " + p.toStringAsProduction());
      if (lookaheadInstanceFirst("productionlookahead", p.seq)) tasks.queue(inputIndex, p.seq, newStackNode, null);
    }
  }

  private void retrn() {
    if (stackNode.equals(stacks.getRoot())) {
      if (cfgRules.acceptingNodeNumbers.contains(cfgNode.num) && (inputIndex == lexer.tweSlices.length - 1)) {
        derivations.setRoot(cfgRules.elementToRulesNodeMap.get(cfgRules.startNonterminal), lexer.tweSlices.length - 1);
        inLanguage = true;
      }
      return;
    }
    stacks.pop(derivations, tasks, inputIndex, stackNode, derivationNode);
  }

  private boolean lookaheadInstanceFirst(String mode, CFGNode cfgNode) {
    if (!ScriptInterpreter.currentModes.contains(mode)) return true;

    var set = cfgRules.instanceFirst.get(cfgNode);
    var slice = lexer.tweSlices[inputIndex];
    Util.debug("lookaheadInstanceFirst() on node " + cfgNode + " with instance first " + set + " and slice ");
    for (var s : slice)
      Util.info(s.toString());

    for (TWESetElement s : slice)
      if (!s.suppressed && set.contains(s.cfgElement)) return true;
    Util.debug("lookahead() false");
    return false;
  }

  private boolean lookaheadFollow(String mode, CFGElement cfgElement) {
    if (!ScriptInterpreter.currentModes.contains(mode)) return true;

    var set = cfgRules.follow.get(cfgElement);
    var slice = lexer.tweSlices[inputIndex];
    // Util.debug("lookaheadFollow() on element " + cfgElement + " with set " + set + " and slice ");
    // for (var s : slice)
    // Util.info(s.toString());

    for (TWESetElement s : slice)
      if (!s.suppressed && set.contains(s.cfgElement)) return true;
    Util.debug("lookahead() false");
    return false;
  }

  @Override
  public void printCardinalities(PrintStream outputStream) {
    outputStream.println(name() + ": characters:" + fmt(input.length()) + " TWEs:" + fmt(lexer.cardinality()) + " tasks:" + fmt(tasks.cardinality())
        + " stackNodes:" + fmt(stacks.nodeCardinality()) + " stackEdges:" + fmt(stacks.edgeCardinality()) + " pops:" + fmt(stacks.popCardinality()) + " BSRs:"
        + fmt(derivations.bsrCardinality()));
  }

  private String fmt(int n) {
    return String.format("%,d", n);
  }

  @Override
  protected String name() {
    StringBuilder sb = new StringBuilder();
    sb.append("GLLModal( ");
    for (var m : ScriptInterpreter.currentModes)
      sb.append(m + " ");
    sb.append(")");
    return sb.toString();
  }
}
