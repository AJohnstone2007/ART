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

    tokenIndex = 0;
    cfgNode = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).alt;
    stackNode = stacks.getRoot();
    derivationNode = null;
    queueProductionTasks();

    nextTask: while (nextTask())
      while (true) {
        switch (cfgNode.cfgElement.cfgKind) {
        case ALT:
          queueProductionTasks();
          continue nextTask;
        case EPS:
          updateDerivation(0);
          cfgNode = cfgNode.seq;
          break; // continue with this sequence
        case B, T, TI, C:
          var inputElement = lexer.tweSlices[tokenIndex][0].cfgElement;
          if (inputElement == cfgNode.cfgElement) {
            updateDerivation(1);
            tokenIndex++;
            cfgNode = cfgNode.seq;
            break; // continue with this sequence
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

  private void updateDerivation(int size) {
    Util.trace(8, 0, "Matched " + cfgNode.cfgElement);
    var rightNode = derivations.find(cfgRules.elementToNodeMap.get(cfgNode.cfgElement), tokenIndex, tokenIndex + size);
    derivationNode = derivations.extend(cfgNode.seq, derivationNode, rightNode);
  }

  private void queueProductionTasks() {
    for (CFGNode production = cfgNode; production != null; production = production.alt)
      tasks.queue(tokenIndex, production.seq, stackNode, derivationNode);
  }

  private boolean nextTask() {
    var task = tasks.next();
    if (task == null) return false;
    tokenIndex = task.tokenIndex;
    cfgNode = task.cfgNode;
    stackNode = task.stackNode;
    derivationNode = task.derivationNode;
    return true;
  }

  private void call(CFGNode cfgNode) {
    var newStackNode = stacks.push(derivations, tasks, tokenIndex, cfgNode, stackNode, derivationNode);
    for (CFGNode p = cfgRules.elementToNodeMap.get(cfgNode.cfgElement).alt; p != null; p = p.alt)
      tasks.queue(tokenIndex, p.seq, newStackNode, null);
  }

  private void retrn() {
    if (stackNode.equals(stacks.getRoot())) {
      if (cfgRules.acceptingNodeNumbers.contains(cfgNode.num) && (tokenIndex == lexer.tweSlices.length - 1)) {
        derivations.setRoot(cfgRules.elementToNodeMap.get(cfgRules.startNonterminal), lexer.tweSlices.length - 1);
        inLanguage = true;
      }
      return;
    }
    stacks.pop(derivations, tasks, tokenIndex, stackNode, derivationNode);
  }
}