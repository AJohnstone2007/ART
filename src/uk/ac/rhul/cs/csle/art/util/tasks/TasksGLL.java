package uk.ac.rhul.cs.csle.art.util.tasks;

import java.io.PrintStream;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGNode;
import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.derivations.AbstractDerivationNode;
import uk.ac.rhul.cs.csle.art.util.stacks.AbstractStackNode;
import uk.ac.rhul.cs.csle.art.util.statistics.Statistics;

public class TasksGLL extends AbstractTasks {
  private final Set<DescriptorGLL> descriptors;
  private final Deque<DescriptorGLL> descriptorQueue;

  public TasksGLL() {
    descriptors = new HashSet<>();
    descriptorQueue = new LinkedList<>();
  }

  @Override
  public void queue(int tokenIndex, CFGNode cfgNode, AbstractStackNode stackNode, AbstractDerivationNode derivationNode) {
    DescriptorGLL tmp = new DescriptorGLL(tokenIndex, cfgNode, stackNode, derivationNode);
    if (descriptors.add(tmp)) {
      descriptorQueue.addFirst(tmp);
      // Util.debug("Queued GLL descriptor " + tmp);
    }
  }

  public void loadstatistics(Statistics statistics) {
    statistics.put("descriptorCount", descriptors.size());
  }

  @Override
  public DescriptorGLL next() {
    return descriptorQueue.poll();
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    // TODO Auto-generated method stub

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
}
