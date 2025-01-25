import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 Map<String, Integer> variables = new HashMap<>(); 
   Map<String, AbstractAttributeBlock> procedures = new HashMap<>(); 
  public String name() { return "2025-01-25 11:16:13"; }

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
      case 59: ID.v = lexeme();  break;
      }
    }
  }

  public class ART_C_INTEGER extends AbstractAttributeBlock {
    ART_C_INTEGER INTEGER = this; int v;

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
      case 63: INTEGER.v = Integer.parseInt(lexeme());  break;
      }
    }
  }

  public class ART_C_STRING_SQ extends AbstractAttributeBlock {
    ART_C_STRING_SQ STRING_SQ = this; String v;

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
      case 67: STRING_SQ.v = lexemeCore().translateEscapes();  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 71: e11 = new ART_C_e1(); e11.term = term; break;
      case 74: e11 = new ART_C_e1(); e11.term = term; break;
      case 76: e12 = new ART_C_e1(); e12.term = term; break;
      case 79: e11 = new ART_C_e1(); e11.term = term; break;
      case 81: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 71: return e11;
      case 74: return e11;
      case 76: return e12;
      case 79: return e11;
      case 81: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 71: e0.v = e11.v;  break;
      case 76: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 81: e0.v = e11.v != e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 85: e21 = new ART_C_e2(); e21.term = term; break;
      case 88: e11 = new ART_C_e1(); e11.term = term; break;
      case 90: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 85: return e21;
      case 88: return e11;
      case 90: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 85: e1.v = e21.v;  break;
      case 90: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_ID ID1; ART_C_e1 e11; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 94: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 97: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 101: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 94: return INTEGER1;
      case 97: return ID1;
      case 101: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 94:e2.v = INTEGER1.v;  break;
      case 97: e2.v = variables.get(ID1.v);  break;
      case 101: e2.v = e11.v;  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_statement statement1; ART_C_statement statement2; ART_C_ID ID1; ART_C_e0 e01;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 106: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 108: e01 = new ART_C_e0(); e01.term = term; break;
      case 113: e01 = new ART_C_e0(); e01.term = term; break;
      case 114: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 116: statement2 = new ART_C_statement(); statement2.term = term; break;
      case 120: e01 = new ART_C_e0(); e01.term = term; break;
      case 122: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 106: return ID1;
      case 108: return e01;
      case 113: return e01;
      case 114: return statement1;
      case 116: return statement2;
      case 120: return e01;
      case 122: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 109: variables.put(ID1.v, e01.v);  break;
      case 116: if (e01.v != 0) interpret(statement1); else interpret(statement2);  break;
      case 122: interpret(e01); while (e01.v != 0) { interpret(statement1); interpret(e01); }  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 126: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 127: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 130: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 126: return statement1;
      case 127: return statements1;
      case 130: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 130: System.out.println("Final variable map " + variables);  break;
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
