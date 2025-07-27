package uk.ac.rhul.cs.csle.art.cfg.gll;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPFDummyForRecognisers;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS;
import uk.ac.rhul.cs.csle.art.util.tasks.TasksGLL;

/**
 * This is the 'master' GLL/MGLL implementation which has selectable features based on final booleans, which we hope get compiled into macro-style code switches
 * - to be evaluated!
 */
public class GLLParameterised extends AbstractParser {
  final boolean isMGLL;
  final boolean isRecogniser;

  public GLLParameterised(boolean isMGLL, boolean isRecogniser) {
    super();
    this.isMGLL = isMGLL;
    this.isRecogniser = isRecogniser;
  }

  @Override
  protected String name() {
    return (isMGLL ? "M" : "") + "GLL" + (isRecogniser ? "Recogniser" : "");
  }

  CFGNode cfgNode;
  AbstractStackNode stackNode;
  AbstractDerivationNode derivationNode;

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    inLanguage = false;
    this.input = input;
    this.cfgRules = cfgRules;
    this.lexer = lexer;
    tasks = new TasksGLL();
    stacks = new GSS(cfgRules);
    if (isRecogniser)
      derivations = new SPPFDummyForRecognisers(this);
    else
      derivations = new SPPF(this);

    if (!lexer.lex(input, cfgRules, chooseRules)) return;
    inputIndex = 0;
    cfgNode = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueAlternateTasks();
    nextTask: while (nextTask())
      nextCFGNode: while (true) {
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueAlternateTasks(); // Create task descriptor for the start of each production
          continue nextTask;
        case EPS:
          derivationNode = updateDerivation(inputIndex); // Must match, but nothing consumed, so rightExtent = inputIndex
          cfgNode = cfgNode.seq; // Next grammar node which will be an END node
          continue nextCFGNode; // continue with this sequence
        case SOS, TRM_BI, TRM_CS, TRM_CI, TRM_CH:
          var slice = lexer.tweSlices[inputIndex];
          if (slice != null) {// Nothing todo for empty TWE slices
            for (int sliceIndex = 0; sliceIndex < slice.length; sliceIndex++) // Iterate over the TWE set elements in this slice
              if (!slice[sliceIndex].suppressed && slice[sliceIndex].cfgElement == cfgNode.cfgElement) { // Does this element match the current grammar position
                // Util.debug("Matched " + cfgNode.toStringAsProduction());
                if (isMGLL) for (int restOfIndex = sliceIndex + 1; restOfIndex < slice.length; restOfIndex++) // Queue tasks for any subsequent matching TWEs in
                                                                                                              // this slice
                  if (!slice[sliceIndex].suppressed && slice[restOfIndex].cfgElement == cfgNode.cfgElement)
                    tasks.queue(slice[restOfIndex].rightExtent, cfgNode.seq, stackNode, updateDerivation(slice[restOfIndex].rightExtent));
                derivationNode = updateDerivation(slice[sliceIndex].rightExtent);
                inputIndex = slice[sliceIndex].rightExtent; // Step over the matched TWE
                cfgNode = cfgNode.seq; // Next grammar node
                continue nextCFGNode; // continue with this sequence
              }
          }
          continue nextTask;
        case NON:
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

  private AbstractDerivationNode updateDerivation(int rightExtent) {
    // Util.trace(8, 0, "Matched " + cfgNode.cfgElement);
    // Util.debug("*** Update derivation " + rightExtent);
    var rightNode = derivations.find(cfgRules.elementToNodeMap.get(cfgNode.cfgElement), inputIndex, rightExtent);
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
    inputIndex = task.tokenIndex;
    cfgNode = task.cfgNode;
    stackNode = task.stackNode;
    derivationNode = task.derivationNode;
    return true;
  }

  private void call(CFGNode cfgNode) {
    var newStackNode = stacks.push(derivations, tasks, inputIndex, cfgNode, stackNode, derivationNode);
    for (CFGNode p = cfgRules.elementToNodeMap.get(cfgNode.cfgElement).alt; p != null; p = p.alt)
      tasks.queue(inputIndex, p.seq, newStackNode, null);
  }

  private void retrn() {
    if (stackNode.equals(stacks.getRoot())) {
      if (cfgRules.acceptingNodeNumbers.contains(cfgNode.num) && (inputIndex == lexer.tweSlices.length - 1)) {
        derivations.setRoot(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), lexer.tweSlices.length - 1);
        inLanguage = true;
      }
      return;
    }
    stacks.pop(derivations, tasks, inputIndex, stackNode, derivationNode);
  }
}