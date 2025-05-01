package uk.ac.rhul.cs.csle.art.cfg.gll;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.derivations.SPPF;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.stacks.GSS;
import uk.ac.rhul.cs.csle.art.util.tasks.TasksGLL;

public class GLLBaseLine extends AbstractParser {
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
    derivations = new SPPF(this);

    lexer.lex(input, cfgRules, chooseRules);
    // lexer.printTWESet(System.out, null);
    inputIndex = 0;
    cfgNode = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueProductionTasks();

    nextTask: while (nextTask())
      while (true) {
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueProductionTasks(); // Creat task descriptor for the start of each production
          continue nextTask;
        case EPS:
          updateDerivation(inputIndex); // Mustmatch, but nothing consumed, so rightExtent = inputIndex
          cfgNode = cfgNode.seq; // Next grammar node which will be an END node
          break; // continue with this sequence
        case B, T, TI, C:
          if (lexer.tweSlices[inputIndex] != null) { // Empty TWE slice
            var inputElement = lexer.tweSlices[inputIndex][0];
            if (inputElement.cfgElement == cfgNode.cfgElement) {
              updateDerivation(inputElement.rightExtent);
              inputIndex = inputElement.rightExtent; // Step over the matched TWE
              cfgNode = cfgNode.seq; // Next grammar node
              break; // continue with this sequence
            }
          }
          continue nextTask;
        case N:
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

  private void updateDerivation(int rightExtent) {
    Util.trace(8, 0, "Matched " + cfgNode.cfgElement);
    var rightNode = derivations.find(cfgRules.elementToNodeMap.get(cfgNode.cfgElement), inputIndex, rightExtent);
    derivationNode = derivations.extend(cfgNode.seq, derivationNode, rightNode);
  }

  private void queueProductionTasks() {
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