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
    int v;
    ART_C_B B;

    @Override
    public AbstractActionsNonterminal init(int nodeNumber, int term) {
      switch (nodeNumber) {
      default:
        Util.fatal("init: unknown node " + nodeNumber);
        return null;
      }
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
  }

  public class ART_C_S extends AbstractActionsNonterminal {

    ART_C_S S;
    ART_C_B B1;

    @Override
    public AbstractActionsNonterminal init(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 22:
        B1.term = term;
        B1.B = B1;
        return B1;
      default:
        Util.fatal("init: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 21:
        System.out.println("Found an a");
        break;
      case 22:
        System.out.println("At B");
        break;
      case 23:
        System.out.println("Found a c");
        System.out.println("Now calling delayed attribute B1");
        interpret(B1);
        break;
      }
    }
  }

  @Override
  public AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_S();
    ret.term = term;
    ret.S = null;
    ret.B1 = new ART_C_B();
    return ret;
  }
}
