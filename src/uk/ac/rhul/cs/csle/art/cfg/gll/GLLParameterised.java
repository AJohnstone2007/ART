package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.io.PrintStream;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGElement;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
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
public class GLLParameterised extends AbstractParser {
  private final boolean isMGLL;
  private final boolean isRecogniser;
  private CFGNode cfgNode; // current Context Free Grammar Node
  private AbstractStackNode stackNode; // current top of stack node
  private AbstractDerivationNode derivationNode; // current derivation forest node

  public GLLParameterised(boolean isMGLL, boolean isRecogniser) {
    super();
    this.isMGLL = isMGLL;
    this.isRecogniser = isRecogniser;
  }

  @Override
  protected String name() { // Unpack the control booleans to give the canonical algorithm name
    return (isMGLL ? "M" : "") + "GLL" + (isRecogniser ? "Recogniser" : "");
  }

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    inLanguage = false;
    this.input = input;
    this.cfgRules = cfgRules;
    this.lexer = lexer;
    tasks = new TasksGLL();
    stacks = new GSSGLL(cfgRules);
    if (isRecogniser)
      derivations = new SPPFDummyForRecognisers(this);
    else
      derivations = new SPPF(this);

    if (!lexer.lex(input, cfgRules, chooseRules)) return; // Nothing to do if the lexer fails
    inputIndex = 0;
    cfgNode = cfgRules.elementToRulesNodeMap.get(cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueAlternateTasks();
    nextTask: while (nextTask())
      nextCFGNode: while (true) {
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueAlternateTasks(); // Create task descriptor for the start of each production
          continue nextTask;
        case EPSILON:
          derivationNode = updateDerivation(inputIndex); // Must match, but nothing consumed, so rightExtent = inputIndex
          cfgNode = cfgNode.seq; // Next grammar node which will be an END node
          continue nextCFGNode; // Continue with this sequence
        case SOS, TRM_BI, TRM_CS, TRM_CI, TRM_CH: // Look for exact instance
          var trmSlice = lexer.tweSlices[inputIndex];
          if (trmSlice != null) // Nothing todo for empty TWE slices
            nextSliceElement: for (int sliceIndex = 0; sliceIndex < trmSlice.length; sliceIndex++) // Iterate over the TWE set elements in this slice
            if (!trmSlice[sliceIndex].suppressed && matchTerminal(trmSlice[sliceIndex].cfgElement)) { // Ignore suppressed TWE set elements
              // Util.debug("Matched " + cfgNode.toStringAsProduction());
              if (isMGLL) // MGLL only: createdescriptors for any other match in this TWE set slice
                for (int restOfIndex = sliceIndex + 1; restOfIndex < trmSlice.length; restOfIndex++)
                if (!trmSlice[sliceIndex].suppressed && matchTerminal(trmSlice[sliceIndex].cfgElement))
                  tasks.queue(trmSlice[restOfIndex].rightExtent, cfgNode.seq, stackNode, updateDerivation(trmSlice[restOfIndex].rightExtent));

              derivationNode = updateDerivation(trmSlice[sliceIndex].rightExtent);
              inputIndex = trmSlice[sliceIndex].rightExtent; // Step over the matched TWE
              cfgNode = cfgNode.seq; // Next grammar node
              continue nextCFGNode; // Continue with this sequence
            }
          continue nextTask;
        case TRM_CH_SET, TRM_CH_ANTI_SET: // Look for character TWE set element whose string is in the set
          var setSlice = lexer.tweSlices[inputIndex];
          if (setSlice != null) // Nothing todo for empty TWE slices
            nextSliceElement: for (int sliceIndex = 0; sliceIndex < setSlice.length; sliceIndex++) // Iterate over the TWE set elements in this slice
            if (!setSlice[sliceIndex].suppressed && matchTerminal(setSlice[sliceIndex].cfgElement)) { // Ignore suppressed TWE set elements
              // Util.debug("Matched " + cfgNode.toStringAsProduction());
              if (isMGLL) // MGLL only: createdescriptors for any other match in this TWE set slice
                for (int restOfIndex = sliceIndex + 1; restOfIndex < setSlice.length; restOfIndex++)
                if (!setSlice[sliceIndex].suppressed && matchTerminal(setSlice[sliceIndex].cfgElement))
                  tasks.queue(setSlice[restOfIndex].rightExtent, cfgNode.seq, stackNode, updateDerivation(setSlice[restOfIndex].rightExtent));

              derivationNode = updateDerivation(setSlice[sliceIndex].rightExtent);
              inputIndex = setSlice[sliceIndex].rightExtent; // Step over the matched TWE
              cfgNode = cfgNode.seq; // Next grammar node
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
    derivations.choose(chooseRules);
  }

  private boolean matchTerminal(CFGElement cfgElement) {
    switch (cfgNode.cfgElement.cfgKind) {
    case TRM_CH_SET:
      return cfgElement.cfgKind == CFGKind.TRM_CH && cfgNode.cfgElement.set.contains(cfgElement.str.charAt(0));
    case TRM_CH_ANTI_SET:
      return cfgElement.cfgKind == CFGKind.TRM_CH && !cfgNode.cfgElement.set.contains(cfgElement.str.charAt(0));
    default:
      return (cfgElement == cfgNode.cfgElement);
    }
  }

  private AbstractDerivationNode updateDerivation(int rightExtent) {
    var tmp = cfgRules.elementToRulesNodeMap.get(cfgNode.cfgElement);
    var rightNode = derivations.find(tmp, inputIndex, rightExtent);
    return derivations.extend(cfgNode.seq, derivationNode, rightNode);
  }

  private void queueAlternateTasks() {
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
    for (CFGNode p = cfgRules.elementToRulesNodeMap.get(cfgNode.cfgElement).alt; p != null; p = p.alt)
      tasks.queue(inputIndex, p.seq, newStackNode, null);
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

  @Override
  public void printCardinalities(PrintStream outputStream) {
    outputStream.println(name() + ": characters:" + fmt(input.length()) + " TWEs:" + fmt(lexer.cardinality()) + " tasks:" + fmt(tasks.cardinality())
        + " stackNodes:" + fmt(stacks.nodeCardinality()) + " stackEdges:" + fmt(stacks.edgeCardinality()) + " pops:" + fmt(stacks.popCardinality()) + " BSRs:"
        + fmt(derivations.bsrCardinality()));
  }

  private String fmt(int n) {
    return String.format("%,d", n);
  }
}
