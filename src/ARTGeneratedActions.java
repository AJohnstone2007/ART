import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 Map<String, Integer> variables = new HashMap<>(); 
  public String name() { return "2026-03-12 23:02:39"; }

  public class ART_C_addOps extends AbstractAttributeBlock {
    ART_C_addOps addOps = this; int v; ART_C_addOps addOps1; ART_C_operand operand1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 60: operand1 = new ART_C_operand(); operand1.term = term; break;
      case 63: addOps1 = new ART_C_addOps(); addOps1.term = term; break;
      case 65: operand1 = new ART_C_operand(); operand1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 60: return operand1;
      case 63: return addOps1;
      case 65: return operand1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 60: addOps.v = operand1.v;  break;
      case 65: addOps.v = addOps1.v - operand1.v;  break;
      }
    }
  }

  public class ART_C_id extends AbstractAttributeBlock {
    ART_C_id id = this; String v;

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
      case 69: id.v = lexeme();  break;
      }
    }
  }

  public class ART_C_int32 extends AbstractAttributeBlock {
    ART_C_int32 int32 = this; int v;

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
      case 73: int32.v = Integer.parseInt(lexeme());  break;
      }
    }
  }

  public class ART_C_operand extends AbstractAttributeBlock {
    ART_C_operand operand = this; int v; ART_C_addOps addOps1; ART_C_int32 int321; ART_C_id id1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 77: int321 = new ART_C_int32(); int321.term = term; break;
      case 80: id1 = new ART_C_id(); id1.term = term; break;
      case 84: addOps1 = new ART_C_addOps(); addOps1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 77: return int321;
      case 80: return id1;
      case 84: return addOps1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 77:operand.v = int321.v;  break;
      case 80: operand.v = variables.get(id1.v);  break;
      case 85: operand.v = addOps1.v;  break;
      }
    }
  }

  public class ART_C_pred extends AbstractAttributeBlock {
    ART_C_pred pred = this; int v; ART_C_addOps addOps1; ART_C_addOps addOps2;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 89: addOps1 = new ART_C_addOps(); addOps1.term = term; break;
      case 92: addOps1 = new ART_C_addOps(); addOps1.term = term; break;
      case 94: addOps2 = new ART_C_addOps(); addOps2.term = term; break;
      case 97: addOps1 = new ART_C_addOps(); addOps1.term = term; break;
      case 99: addOps2 = new ART_C_addOps(); addOps2.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 89: return addOps1;
      case 92: return addOps1;
      case 94: return addOps2;
      case 97: return addOps1;
      case 99: return addOps2;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 89: pred.v = addOps1.v;  break;
      case 94: pred.v = addOps1.v >  addOps2.v ? 1 : 0;  break;
      case 99: pred.v = addOps1.v != addOps2.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_pred pred1; ART_C_statement statement1; ART_C_statement statement2; ART_C_id id1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 103: id1 = new ART_C_id(); id1.term = term; break;
      case 105: pred1 = new ART_C_pred(); pred1.term = term; break;
      case 110: pred1 = new ART_C_pred(); pred1.term = term; break;
      case 112: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 114: statement2 = new ART_C_statement(); statement2.term = term; break;
      case 118: pred1 = new ART_C_pred(); pred1.term = term; break;
      case 120: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 103: return id1;
      case 105: return pred1;
      case 110: return pred1;
      case 112: return statement1;
      case 114: return statement2;
      case 118: return pred1;
      case 120: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 106: variables.put(id1.v, pred1.v);  break;
      case 114: if (pred1.v != 0) interpret(statement1); else interpret(statement2);  break;
      case 120: interpret(pred1); while (pred1.v != 0) { interpret(statement1); interpret(pred1); }  break;
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
