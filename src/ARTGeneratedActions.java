import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActionsNonterminal;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2025-01-20 20:30:50"; }

  public class ART_C_B extends AbstractActionsNonterminal {
    int term; ART_C_B B = this; int v;
    public void initAttributes(int nodeNumber, int term) { this.term = term; 
      switch(nodeNumber){
      }
    }
    public AbstractActionsNonterminal getAttributes(int nodeNumber) { switch(nodeNumber){
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
    }}

    public void action(int nodeNumber) { switch(nodeNumber){
      case 17: System.out.println("Found a b"); B.v = 10;break;
    }}
  }

  public class ART_C_S extends AbstractActionsNonterminal {
    int term; ART_C_S S = this; ART_C_B B1;
    public void initAttributes(int nodeNumber, int term) { this.term = term; 
      switch(nodeNumber){
      case 22: 
      }
    }
    public AbstractActionsNonterminal getAttributes(int nodeNumber) { switch(nodeNumber){
      case 22: return B1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
    }}

    public void action(int nodeNumber) { switch(nodeNumber){
      case 21: System.out.println("Found an a"); break;
      case 22: System.out.println("At B"); break;
      case 23: System.out.println("Found a c"); 
         System.out.println("Now calling delayed attribute B1");
         interpret(B1); break;
    }}
  }
public AbstractActionsNonterminal init(AbstractInterpreter interpreter, int term) { this.interpreter = interpreter; var ret = new ART_C_S(); ret.activate(null); return ret; }
}
