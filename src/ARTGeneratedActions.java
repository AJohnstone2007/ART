import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActionsNonterminal;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTGeneratedActions extends AbstractActions {
  @Override
  public String name() {
    return "2025-01-15 19:42:34";
  }

  public class ART_C_S extends AbstractActionsNonterminal {

    ART_C_S S;
    ART_C_X X1;

    public void activate(ART_C_S parent) {
      S = parent;
      X1 = new ART_C_X();
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 24:
        System.out.println("Found an a");
        break;
      case 26:
        System.out.println("Found an @");
        System.out.println("Xcount: " + X1.xCount);
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 25:
        X1.activate(X1);
        return X1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_X extends AbstractActionsNonterminal {
    int xCount;
    ART_C_X X;
    ART_C_X X1;

    public void activate(ART_C_X parent) {
      X = parent;
      X1 = new ART_C_X();
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 31:
        System.out.println("Found an x");
        X.xCount = X1.xCount + 1;
        break;
      case 33:
        X.xCount = 0;
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 31:
        X1.activate(X1);
        return X1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  @Override
  public AbstractActionsNonterminal init() {
    var ret = new ART_C_S();
    ret.activate(null);
    return ret;
  }
}
