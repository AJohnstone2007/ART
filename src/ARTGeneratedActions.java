import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 Map<String, Integer> variables = new HashMap<>(); 
   Map<String, AbstractAttributeBlock> procedures = new HashMap<>(); 
  public String name() { return "2026-02-11 18:31:11"; }

  public class ART_C_ID extends AbstractAttributeBlock {
    ART_C_ID ID = this; String v;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 60: ID.v = lexeme();  break;
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
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 64: INTEGER.v = Integer.parseInt(lexeme());  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 68: e11 = new ART_C_e1(); e11.term = term; break;
      case 71: e11 = new ART_C_e1(); e11.term = term; break;
      case 73: e12 = new ART_C_e1(); e12.term = term; break;
      case 76: e11 = new ART_C_e1(); e11.term = term; break;
      case 78: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 68: return e11;
      case 71: return e11;
      case 73: return e12;
      case 76: return e11;
      case 78: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 68: e0.v = e11.v;  break;
      case 73: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 78: e0.v = e11.v != e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 82: e21 = new ART_C_e2(); e21.term = term; break;
      case 85: e11 = new ART_C_e1(); e11.term = term; break;
      case 87: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 82: return e21;
      case 85: return e11;
      case 87: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 82: e1.v = e21.v;  break;
      case 87: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_ID ID1; ART_C_e1 e11; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 91: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 94: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 98: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 91: return INTEGER1;
      case 94: return ID1;
      case 98: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 91:e2.v = INTEGER1.v;  break;
      case 94: e2.v = variables.get(ID1.v);  break;
      case 98: e2.v = e11.v;  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_statement statement1; ART_C_statement statement2; ART_C_ID ID1; ART_C_e0 e01;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 103: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 105: e01 = new ART_C_e0(); e01.term = term; break;
      case 110: e01 = new ART_C_e0(); e01.term = term; break;
      case 112: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 114: statement2 = new ART_C_statement(); statement2.term = term; break;
      case 118: e01 = new ART_C_e0(); e01.term = term; break;
      case 120: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 103: return ID1;
      case 105: return e01;
      case 110: return e01;
      case 112: return statement1;
      case 114: return statement2;
      case 118: return e01;
      case 120: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 106: variables.put(ID1.v, e01.v);  break;
      case 114: if (e01.v != 0) interpret(statement1); else interpret(statement2);  break;
      case 120: interpret(e01); while (e01.v != 0) { interpret(statement1); interpret(e01); }  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 124: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 125: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 128: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 124: return statement1;
      case 125: return statements1;
      case 128: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 128: System.out.println("Final variable map " + variables);  break;
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
