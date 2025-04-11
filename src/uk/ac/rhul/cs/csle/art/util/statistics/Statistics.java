package uk.ac.rhul.cs.csle.art.util.statistics;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.util.OutputInterface;
import uk.ac.rhul.cs.csle.art.util.Util;

public class Statistics implements OutputInterface {
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
  public void printHeader(PrintStream ps) {
    for (var k : elements.keySet())
      ps.print(k + ",");
  }

  @Override
  public void print(PrintStream ps) {
    for (var k : elements.keySet())
      ps.print(elements.get(k) + ",");
  }

}
