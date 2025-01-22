import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 Map<String, Integer> variables = new HashMap<>(); 
   Map<String, AbstractAttributeBlock> procedures = new HashMap<>(); 
  public String name() { return "2025-01-22 08:05:31"; }

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
      case 122: ID.v = lexeme();  break;
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
      case 126: INTEGER.v = Integer.parseInt(lexeme());  break;
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
      case 130: STRING_SQ.v = lexemeCore().translateEscapes();  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 134: e11 = new ART_C_e1(); e11.term = term; break;
      case 137: e11 = new ART_C_e1(); e11.term = term; break;
      case 139: e12 = new ART_C_e1(); e12.term = term; break;
      case 142: e11 = new ART_C_e1(); e11.term = term; break;
      case 144: e12 = new ART_C_e1(); e12.term = term; break;
      case 147: e11 = new ART_C_e1(); e11.term = term; break;
      case 149: e12 = new ART_C_e1(); e12.term = term; break;
      case 152: e11 = new ART_C_e1(); e11.term = term; break;
      case 154: e12 = new ART_C_e1(); e12.term = term; break;
      case 157: e11 = new ART_C_e1(); e11.term = term; break;
      case 159: e12 = new ART_C_e1(); e12.term = term; break;
      case 162: e11 = new ART_C_e1(); e11.term = term; break;
      case 164: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 134: return e11;
      case 137: return e11;
      case 139: return e12;
      case 142: return e11;
      case 144: return e12;
      case 147: return e11;
      case 149: return e12;
      case 152: return e11;
      case 154: return e12;
      case 157: return e11;
      case 159: return e12;
      case 162: return e11;
      case 164: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 134: e0.v = e11.v;  break;
      case 139: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 144: e0.v = e11.v <  e12.v ? 1 : 0;  break;
      case 149: e0.v = e11.v >= e12.v ? 1 : 0;  break;
      case 154: e0.v = e11.v <= e12.v ? 1 : 0;  break;
      case 159: e0.v = e11.v == e12.v ? 1 : 0;  break;
      case 164: e0.v = e11.v != e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 168: e21 = new ART_C_e2(); e21.term = term; break;
      case 171: e11 = new ART_C_e1(); e11.term = term; break;
      case 173: e21 = new ART_C_e2(); e21.term = term; break;
      case 176: e11 = new ART_C_e1(); e11.term = term; break;
      case 178: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 168: return e21;
      case 171: return e11;
      case 173: return e21;
      case 176: return e11;
      case 178: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 168: e1.v = e21.v;  break;
      case 173: e1.v = e11.v + e21.v;  break;
      case 178: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_e2 e21; ART_C_e3 e31;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 182: e31 = new ART_C_e3(); e31.term = term; break;
      case 185: e21 = new ART_C_e2(); e21.term = term; break;
      case 187: e31 = new ART_C_e3(); e31.term = term; break;
      case 190: e21 = new ART_C_e2(); e21.term = term; break;
      case 192: e31 = new ART_C_e3(); e31.term = term; break;
      case 195: e21 = new ART_C_e2(); e21.term = term; break;
      case 197: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 182: return e31;
      case 185: return e21;
      case 187: return e31;
      case 190: return e21;
      case 192: return e31;
      case 195: return e21;
      case 197: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 182: e2.v= e31.v;  break;
      case 187: e2.v = e21.v * e31.v;  break;
      case 192: e2.v = e21.v / e31.v;  break;
      case 197: e2.v = e21.v % e31.v;  break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this; int v; ART_C_e3 e31; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 201: e41 = new ART_C_e4(); e41.term = term; break;
      case 205: e31 = new ART_C_e3(); e31.term = term; break;
      case 209: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 201: return e41;
      case 205: return e31;
      case 209: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 201:e3.v = e41.v;  break;
      case 205:e3.v = e31.v;  break;
      case 209:e3.v = -e31.v;  break;
      }
    }
  }

  public class ART_C_e4 extends AbstractAttributeBlock {
    ART_C_e4 e4 = this; int v; ART_C_e5 e51; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 213: e51 = new ART_C_e5(); e51.term = term; break;
      case 216: e51 = new ART_C_e5(); e51.term = term; break;
      case 218: e41 = new ART_C_e4(); e41.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 213: return e51;
      case 216: return e51;
      case 218: return e41;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 213: e4.v = e51.v;  break;
      case 218:e4.v = (int) Math.pow(e51.v, e41.v);  break;
      }
    }
  }

  public class ART_C_e5 extends AbstractAttributeBlock {
    ART_C_e5 e5 = this; int v; ART_C_ID ID1; ART_C_e1 e11; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 222: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 225: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 229: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 222: return INTEGER1;
      case 225: return ID1;
      case 229: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 222:e5.v = INTEGER1.v;  break;
      case 225: e5.v = variables.get(ID1.v);  break;
      case 229: e5.v = e11.v;  break;
      }
    }
  }

  public class ART_C_elseOpt extends AbstractAttributeBlock {
    ART_C_elseOpt elseOpt = this; ART_C_statement statement1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 235: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 235: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_printElements extends AbstractAttributeBlock {
    ART_C_printElements printElements = this; ART_C_printElements printElements1; ART_C_e0 e01; ART_C_STRING_SQ STRING_SQ1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 242: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 245: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 247: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      case 250: e01 = new ART_C_e0(); e01.term = term; break;
      case 253: e01 = new ART_C_e0(); e01.term = term; break;
      case 255: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 242: return STRING_SQ1;
      case 245: return STRING_SQ1;
      case 247: return printElements1;
      case 250: return e01;
      case 253: return e01;
      case 255: return printElements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 242: System.out.print(STRING_SQ1.v);  break;
      case 245: System.out.print(STRING_SQ1.v);  break;
      case 250: System.out.print(e01.v);  break;
      case 253: System.out.print(e01.v);  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_printElements printElements1; ART_C_statement statement1; ART_C_statements statements1; ART_C_ID ID1; ART_C_e0 e01; ART_C_elseOpt elseOpt1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 259: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 261: e01 = new ART_C_e0(); e01.term = term; break;
      case 266: e01 = new ART_C_e0(); e01.term = term; break;
      case 268: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 269: elseOpt1 = new ART_C_elseOpt(); elseOpt1.term = term; break;
      case 273: e01 = new ART_C_e0(); e01.term = term; break;
      case 275: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 280: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      case 286: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 287: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 291: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 296: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 259: return ID1;
      case 261: return e01;
      case 266: return e01;
      case 268: return statement1;
      case 269: return elseOpt1;
      case 273: return e01;
      case 275: return statement1;
      case 280: return printElements1;
      case 286: return ID1;
      case 287: return statement1;
      case 291: return ID1;
      case 296: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 262: variables.put(ID1.v, e01.v);  break;
      case 269: if (e01.v != 0) interpret(statement1); 
     else interpret(elseOpt1);  break;
      case 275: interpret(e01); 
     while (e01.v != 0) { 
        interpret(statement1); 
        interpret(e01); 
      }  break;
      case 287: procedures.put(ID1.v, statement1);  break;
      case 292: interpret(procedures.get(ID1.v));  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 301: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 304: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 305: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 301: return statement1;
      case 304: return statement1;
      case 305: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_text extends AbstractAttributeBlock {
    ART_C_text text = this; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 309: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 309: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 309: System.out.println("Final variable map " + variables);  break;
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_text();
    ret.term = term;
    return ret;
  }
}
