package uk.ac.rhul.cs.csle.art.util.statistics;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.util.DisplayInterface;
import uk.ac.rhul.cs.csle.art.util.Util;

public class Statistics implements DisplayInterface {
  private final Map<String, Object> elements = new LinkedHashMap<>();

  public Statistics(String... elementKeys) {
    for (var k : elementKeys)
      elements.put(k, null);
  }

  public void put(String k, Object v) {
    if (!elements.keySet().contains(k))
      // Util.warning("Statistics.put() passed unknown key " + k)
      ;
    else
      elements.put(k, v);
  }

  public void putTime(String key) {
    put(key, System.nanoTime());
  }

  public Object get(String k) {
    if (!elements.keySet().contains(k)) {
      Util.warning("Statistics.get() passed unknown key " + k);
      return null;
    } else
      return elements.get(k);
  }

  @Override
  public void print(PrintStream outputStream, TermTraverserText outputTraverser, boolean indexed, boolean full, boolean indented) {
    for (var k : elements.keySet())
      outputStream.print(k + ",");

    for (var k : elements.keySet())
      outputStream.print(elements.get(k) + ",");
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
