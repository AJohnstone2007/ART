package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElementKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSSGLL;
import uk.ac.rhul.cs.csle.art.util.tasks.TasksGLL;

/**
 * This is the simple (M)GLL implementation with no optimisations
 */
public class GLLBaseLine extends AbstractParser {
  private boolean isMGLL = true;

  public GLLBaseLine(boolean isMGLL) {
    super();
    this.isMGLL = isMGLL;
  }

  private CFGNode cfgNode; // current Context Free Grammar Node
  private AbstractStackNode stackNode; // current top of stack node
  private AbstractDerivationNode derivationNode; // current derivation forest node

  @Override
  public void parse(AbstractLexicalisations lexicalisations) {

    // public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    inLanguage = false;
    tasks = new TasksGLL();
    stacks = new GSSGLL(lexicalisations.cfgRules);
    derivations = new SPPF(lexicalisations);

    inputIndex = 0;
    cfgNode = lexicalisations.cfgRules.elementToRulesNodeMap.get(lexicalisations.cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueProductionTasks();
    nextTask: while (nextTask())
      nextCFGNode: while (true) {
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueProductionTasks(); // Create task descriptor for the start of each production - is this needed for BNF?
          continue nextTask;
        case EPSILON:
          derivationNode = updateDerivation(inputIndex); // Must match, but nothing consumed, so rightExtent = inputIndex
          cfgNode = cfgNode.seq; // Next grammar node which will be an END node
          continue nextCFGNode; // Continue with this sequence
        case SOS, TRM_BI, TRM_CS, TRM_CI, TRM_CH, TRM_CH_SET, TRM_CH_ANTI_SET: // Look for exact instance
          // var slice = lexicalisations.tweSlices[inputIndex];
          var slice = lexicalisations.getSlice(inputIndex);
          if (slice == null) continue nextTask;// Nothing todo for empty TWE slices
          for (int sliceIndex = 0; sliceIndex < slice.length; sliceIndex++) // Iterate over the TWE set elements in this slice
            if (!slice[sliceIndex].suppressed && matchTerminal(slice[sliceIndex].cfgElement)) { // Ignore suppressed TWE set elements
              // Util.debug("Matched " + cfgNode.toStringAsProduction());
              if (isMGLL) // MGLL only: create continuation task descriptors for all subsequent matches in this slice
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
          retrn();
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
      tasks.queue(inputIndex, production.seq, stackNode, derivationNode);
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
      tasks.queue(inputIndex, p.seq, newStackNode, null);
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

  @Override
  public void printCardinalities(PrintStream outputStream) {
    outputStream.println(name() + ": characters:" + fmt(lexicalisations.inputString.length()) + " TWEs:" + fmt(lexicalisations.cardinality()) + " tasks:"
        + fmt(tasks.cardinality()) + " stackNodes:" + fmt(stacks.nodeCardinality()) + " stackEdges:" + fmt(stacks.edgeCardinality()) + " pops:"
        + fmt(stacks.popCardinality()) + " BSRs:" + fmt(derivations.bsrCardinality()));
  }

  private String fmt(int n) {
    return String.format("%,d", n);
  }

  @Override
  protected String name() {
    return isMGLL ? "MGLL" : "GLL";
  }

}