package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.lexer.TWESetElement;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFDummyForRecognisers;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSGLL;
import uk.ac.rhul.cs.csle.art.util.tasks.TasksGLL;

/**
 * This is the modal (M)GLL implementation which has selectable optimisations
 */
public class GLLModal extends AbstractParser {
  private CFGNode cfgNode; // current Context Free Grammar Node
  private AbstractStackNode stackNode; // current top of stack node
  private AbstractDerivationNode derivationNode; // current derivation forest node

  @Override
  public void parse(AbstractLexicalisations lexicalisations) {
    inLanguage = false;
    this.lexicalisations = lexicalisations;
    // this.lexer = lexer;
    tasks = new TasksGLL();
    stacks = new GSSGLL(lexicalisations.cfgRules);
    derivations = ScriptInterpreter.currentModes.contains("recogniser") ? new SPPFDummyForRecognisers(lexicalisations) : new SPPF(lexicalisations);

    int loops = 0;

    inputIndex = 0;
    cfgNode = lexicalisations.cfgRules.elementToRulesNodeMap.get(lexicalisations.cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueProductionTasks();
    nextTask: while (nextTask()) {
      if ((++loops) % 1000000 == 0 && Util.traceLevel >= 8) printReport(System.out, loops);
      nextCFGNode: while (true)
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueProductionTasks(); // Create task descriptor for the start of each production - is this needed for BNF?
          continue nextTask;
        case EPSILON:
          derivationNode = updateDerivation(inputIndex); // Must match, but nothing consumed, so rightExtent = inputIndex
          cfgNode = cfgNode.seq; // Next grammar node which will be an END node
          continue nextCFGNode; // Continue with this sequence
        case SOS, TRM_BI, TRM_CS, TRM_CI, TRM_CH, TRM_CH_SET, TRM_CH_ANTI_SET: // Look for exact instance
          var slice = lexicalisations.getSlice(inputIndex);
          if (slice == null) continue nextTask;// Nothing todo for empty TWE slices
          for (int sliceIndex = 0; sliceIndex < slice.length; sliceIndex++) // Iterate over the TWE set elements in this slice
            if (!slice[sliceIndex].suppressed && matchTerminal(slice[sliceIndex].cfgElement)) { // Ignore suppressed TWE set elements
              // Util.debug("Matched " + cfgNode.toStringAsProduction());
              if (ScriptInterpreter.currentModes.contains("mgll")) // MGLL only: create continuation task descriptors for all subsequent matches in this slice
                for (int restOfIndex = sliceIndex + 1; restOfIndex < slice.length; restOfIndex++)
                if (!slice[restOfIndex].suppressed && matchTerminal(slice[restOfIndex].cfgElement))
                  tasks.queue(slice[restOfIndex].rightExtent, cfgNode.seq, stackNode, updateDerivation(slice[restOfIndex].rightExtent));

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
          if (lookaheadFollow("returnlookahead", cfgNode.seq.cfgElement)) retrn();
          continue nextTask;
        default:
          Util.fatal("Unexpected CFGNode kind " + cfgNode.cfgElement.cfgKind + " in " + getClass().getSimpleName());
        }
    }

    derivations.numberNodes();
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
    var rightNode = derivations.find(lexicalisations.cfgRules.elementToRulesNodeMap.get(cfgNode.cfgElement), inputIndex, rightExtent);
    return derivations.extend(cfgNode.seq, derivationNode, rightNode);
  }

  private void queueProductionTasks() {
    for (CFGNode production = cfgNode; production != null; production = production.alt)
      if (lookaheadInstanceFirst("productionlookahead", production.seq)) tasks.queue(inputIndex, production.seq, stackNode, derivationNode);
  }

  private boolean nextTask() {
    var task = tasks.next();
    // Util.debug("Processing task " + task);
    if (task == null) return false;
    inputIndex = task.inputIndex;
    cfgNode = task.cfgNode;
    stackNode = task.stackNode;
    derivationNode = task.derivationNode;
    return true;
  }

  private void call(CFGNode cfgNode) {
    var newStackNode = stacks.push(derivations, tasks, inputIndex, cfgNode, stackNode, derivationNode);
    for (CFGNode p = lexicalisations.cfgRules.elementToRulesNodeMap.get(cfgNode.cfgElement).alt; p != null; p = p.alt)
      if (lookaheadInstanceFirst("productionlookahead", p.seq)) tasks.queue(inputIndex, p.seq, newStackNode, null);
  }

  private void retrn() {
    if (stackNode.equals(stacks.getRoot())) {
      if (lexicalisations.cfgRules.acceptingNodeNumbers.contains(cfgNode.num) && (inputIndex == lexicalisations.inputString.length() - 1)) {
        derivations.setRoot(lexicalisations.cfgRules.elementToRulesNodeMap.get(lexicalisations.cfgRules.startNonterminal),
            lexicalisations.inputString.length() - 1);
        inLanguage = true;
      }
      return;
    }
    stacks.pop(derivations, tasks, inputIndex, stackNode, derivationNode);
  }

  private boolean lookaheadInstanceFirst(String mode, CFGNode cfgNode) {
    if (!ScriptInterpreter.currentModes.contains(mode)) return true;

    var set = lexicalisations.cfgRules.instanceFirst.get(cfgNode);
    var slice = lexicalisations.getSlice(inputIndex);
    // Util.debug("lookaheadInstanceFirst() on node " + cfgNode + " with instance first " + set + " and slice ");
    // for (var s : slice)
    // Util.info(s.toString());

    for (TWESetElement s : slice)
      if (!s.suppressed) {
        if (set.contains(s.cfgElement)) return true;
        if (set.contains(lexicalisations.cfgRules.epsilonElement)
            && lexicalisations.cfgRules.follow.get(lexicalisations.cfgRules.lhsOf.get(cfgNode)).contains(s.cfgElement))
          return true;
      }
    // Util.debug("lookahead() false");
    return false;
  }

  private boolean lookaheadFollow(String mode, CFGElement cfgElement) {
    if (!ScriptInterpreter.currentModes.contains(mode)) return true;

    var set = lexicalisations.cfgRules.follow.get(cfgElement);
    var slice = lexicalisations.getSlice(inputIndex);
    // Util.debug("lookaheadFollow() on element " + cfgElement + " with set " + set + " and slice ");
    // for (var s : slice)
    // Util.info(s.toString());

    for (TWESetElement s : slice)
      if (!s.suppressed && set.contains(s.cfgElement)) return true;
    // Util.debug("lookahead() false");
    return false;
  }

  @Override
  public void printCardinalities(PrintStream outputStream) {
    outputStream.println(">> " + name() + ": characters:" + fmt(lexicalisations.inputString.length()) + " TWEs:" + fmt(lexicalisations.cardinality())
        + " tasks:" + fmt(tasks.cardinality()) + " stackNodes:" + fmt(stacks.nodeCardinality()) + " stackEdges:" + fmt(stacks.edgeCardinality()) + " pops:"
        + fmt(stacks.popCardinality()) + " BSRs:" + fmt(derivations.bsrCardinality()));
  }

  private String fmt(int n) {
    return String.format("%,d", n);
  }

  public void printReport(PrintStream outputStream, int count) {
    outputStream.println(">> After " + count + " parser loops: " + ScriptInterpreter.currentStatistics.currentTime() + "s elapsed, tasks:"
        + fmt(tasks.cardinality()) + " stackNodes:" + fmt(stacks.nodeCardinality()) + " stackEdges:" + fmt(stacks.edgeCardinality()) + " pops:"
        + fmt(stacks.popCardinality()) + " BSRs:" + fmt(derivations.bsrCardinality()));
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
