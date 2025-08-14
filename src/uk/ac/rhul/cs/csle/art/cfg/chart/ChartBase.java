package uk.ac.rhul.cs.csle.art.cfg.chart;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.cfg.AbstractParser;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;

public class ChartBase extends AbstractParser {
  protected ArrayList<ArrayList<Set<Integer>>> chart;

  void chartInit(int n) {
    chart = new ArrayList<>(n + 1);

    for (int x = 0; x < n; x++) {
      ArrayList<Set<Integer>> column;
      chart.add(column = new ArrayList<>(x));
      for (int y = 0; y <= x; y++)
        column.add(new LinkedHashSet<>());
    }
  }

  void chartUnion(int x, int y, int t) {
    chart.get(x).get(y).add(t);
  }

  Set<Integer> chartGet(int x, int y) {
    return chart.get(x).get(y);
  }

  String chartToString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Chart\n");
    for (int y = 0; y < chart.size(); y++) {
      sb.append(String.format("%2d", y) + ": ");
      for (int x = 0; x < y; x++)
        sb.append("                 ");
      for (int x = y; x < chart.size(); x++)
        sb.append("t[" + String.format("%2d", x) + "," + String.format("%2d", y) + "]=" + chartCellToString(x, y) + " ");
      sb.append("\n");
    }
    return sb.toString();
  }

  String chartCellToString(int x, int y) {
    Set<Integer> cell = chart.get(x).get(y);
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (int s : cell) {
      if (first)
        first = false;
      else
        sb.append(",");
      sb.append(String.format("%2d", s));
    }
    sb.append("}");
    return "{" + String.format("%4s", sb.toString());
  }

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    // TODO Auto-generated method stub

  }
}
