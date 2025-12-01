package uk.ac.rhul.cs.csle.art.util.tasks;

import java.io.PrintStream;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class TasksGLL extends AbstractTasks {
  private final Set<ConfigurationGLL> tasks;
  private final Deque<ConfigurationGLL> taskQueue;

  public TasksGLL() {
    tasks = new HashSet<>(20000000);
    taskQueue = new LinkedList<>();
  }

  @Override
  public void queue(int tokenIndex, CFGNode cfgNode, AbstractStackNode stackNode, AbstractDerivationNode derivationNode) {
    ConfigurationGLL tmp = new ConfigurationGLL(tokenIndex, cfgNode, stackNode, derivationNode);
    if (tasks.add(tmp)) {
      taskQueue.addFirst(tmp);
      // Util.debug("enqueue task " + tmp);
    }
  }

  public void loadstatistics(Statistics statistics) {
    statistics.put("descriptorCount", tasks.size());
  }

  @Override
  public ConfigurationGLL next() {
    var ret = taskQueue.poll();
    // Util.debug("dequeue task " + ret);
    return ret;
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    for (var d : tasks)
      Util.info(d.toString());
  }

  @Override
  public void show(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

  }

  @Override
  public void statistics(Statistics currentstatistics, PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full,
      boolean indented) {
    // TODO Auto-generated method stub
  }

  @Override
  public long cardinality() {
    return tasks.size();
  }
}
