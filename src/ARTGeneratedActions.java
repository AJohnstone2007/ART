import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActionsNonterminal;
import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTGeneratedActions extends AbstractActions {
  @Override
  public String name() {
    return "2025-01-19 15:31:18";
  }

  public class ART_C_B extends AbstractActionsNonterminal {
    int term;
    int v;
    ART_C_B B;

    public void activate(ART_C_B parent, int instance) {
      term = instance;
      B = parent;
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 17:
        System.out.println("Found a b");
        B.v = 10;
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber, int term) {
      switch (nodeNumber) {
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }

    public void interpret() {
      interpreter.interpretUsingDerivationTermRec(0, term, this);
    }
  }

  public class ART_C_S extends AbstractActionsNonterminal {
    int term;

    ART_C_S S;
    ART_C_B B1;

    public void activate(ART_C_S parent, int instance) {
      term = instance;
      S = parent;
      B1 = new ART_C_B();
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 21:
        System.out.println("Found an a\n");
        break;
      case 22:
        System.out.println("At B");
        break;
      case 23:
        System.out.println("Found a c");
        System.out.println("Now calling delayed attribute B1");
        B1.interpret();
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 22:
        B1.activate(B1, term);
        return B1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  @Override
  public AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_S();
    ret.activate(null, term);
    return ret;
  }
}
