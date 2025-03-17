import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 HashMap<String, Integer> variables = new HashMap<>(); 
  public String name() { return "2025-03-15 21:46:20"; }

  public class ART_C_ID extends AbstractAttributeBlock {
    ART_C_ID ID = this; String v;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 52: ID.v = lexeme();                     break;
      }
    }
  }

  public class ART_C_INT extends AbstractAttributeBlock {
    ART_C_INT INT = this; int v;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 56: INT.v = Integer.parseInt(lexeme());  break;
      }
    }
  }

  public class ART_C_STR extends AbstractAttributeBlock {
    ART_C_STR STR = this; String v;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 60: STR.v = lexemeAsString();            break;
      }
    }
  }

  public class ART_C_pred extends AbstractAttributeBlock {
    ART_C_pred pred = this; boolean v; ART_C_ID ID1; ART_C_INT INT1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 64: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 66: INT1 = new ART_C_INT(); INT1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 64: return ID1;
      case 66: return INT1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 66: pred.v = variables.get(ID1.v) > INT1.v;  break;
      }
    }
  }

  public class ART_C_printables extends AbstractAttributeBlock {
    ART_C_printables printables = this; ART_C_STR STR1; ART_C_printables printables1; ART_C_ID ID1; ART_C_INT INT1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 70: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 71: printables1 = new ART_C_printables(); printables1.term = term; break;
      case 74: INT1 = new ART_C_INT(); INT1.term = term; break;
      case 75: printables1 = new ART_C_printables(); printables1.term = term; break;
      case 78: STR1 = new ART_C_STR(); STR1.term = term; break;
      case 79: printables1 = new ART_C_printables(); printables1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 70: return ID1;
      case 71: return printables1;
      case 74: return INT1;
      case 75: return printables1;
      case 78: return STR1;
      case 79: return printables1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 70: System.out.print(variables.get(ID1.v));   break;
      case 74: System.out.print(INT1.v);                 break;
      case 78: System.out.print(STR1.v);                 break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_pred pred1; ART_C_printables printables1; ART_C_statements statements1; ART_C_ID ID1; ART_C_INT INT1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 88: printables1 = new ART_C_printables(); printables1.term = term; break;
      case 93: pred1 = new ART_C_pred(); pred1.term = term; break;
      case 95: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 99: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 101: INT1 = new ART_C_INT(); INT1.term = term; break;
      case 105: ID1 = new ART_C_ID(); ID1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 88: return printables1;
      case 93: return pred1;
      case 95: return statements1;
      case 99: return ID1;
      case 101: return INT1;
      case 105: return ID1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 96: interpret (pred1); while (pred1.v) { interpret(statements1); interpret(pred1); }  break;
      case 101: variables.put(ID1.v, INT1.v);  break;
      case 105: variables.put(ID1.v, variables.get(ID1.v) -  1);  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 109: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 110: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 109: return statement1;
      case 110: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_statements();
    ret.term = term;
    return ret;
  }
}
