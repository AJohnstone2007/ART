import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 Map<String, Integer> variables = new HashMap<>(); 
   Map<String, AbstractAttributeBlock> procedures = new HashMap<>(); 
  public String name() { return "2026-03-11 23:21:37"; }

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
      case 125: ID.v = lexeme();  break;
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
      case 129: INTEGER.v = Integer.parseInt(lexeme());  break;
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
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 133: STRING_SQ.v = lexeme().translateEscapes();  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 137: e11 = new ART_C_e1(); e11.term = term; break;
      case 140: e11 = new ART_C_e1(); e11.term = term; break;
      case 142: e12 = new ART_C_e1(); e12.term = term; break;
      case 145: e11 = new ART_C_e1(); e11.term = term; break;
      case 147: e12 = new ART_C_e1(); e12.term = term; break;
      case 150: e11 = new ART_C_e1(); e11.term = term; break;
      case 152: e12 = new ART_C_e1(); e12.term = term; break;
      case 155: e11 = new ART_C_e1(); e11.term = term; break;
      case 157: e12 = new ART_C_e1(); e12.term = term; break;
      case 160: e11 = new ART_C_e1(); e11.term = term; break;
      case 162: e12 = new ART_C_e1(); e12.term = term; break;
      case 165: e11 = new ART_C_e1(); e11.term = term; break;
      case 167: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 137: return e11;
      case 140: return e11;
      case 142: return e12;
      case 145: return e11;
      case 147: return e12;
      case 150: return e11;
      case 152: return e12;
      case 155: return e11;
      case 157: return e12;
      case 160: return e11;
      case 162: return e12;
      case 165: return e11;
      case 167: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 137: e0.v = e11.v;  break;
      case 142: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 147: e0.v = e11.v <  e12.v ? 1 : 0;  break;
      case 152: e0.v = e11.v >= e12.v ? 1 : 0;  break;
      case 157: e0.v = e11.v <= e12.v ? 1 : 0;  break;
      case 162: e0.v = e11.v == e12.v ? 1 : 0;  break;
      case 167: e0.v = e11.v != e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 171: e21 = new ART_C_e2(); e21.term = term; break;
      case 174: e11 = new ART_C_e1(); e11.term = term; break;
      case 176: e21 = new ART_C_e2(); e21.term = term; break;
      case 179: e11 = new ART_C_e1(); e11.term = term; break;
      case 181: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 171: return e21;
      case 174: return e11;
      case 176: return e21;
      case 179: return e11;
      case 181: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 171: e1.v = e21.v;  break;
      case 176: e1.v = e11.v + e21.v;  break;
      case 181: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_e2 e21; ART_C_e3 e31;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 185: e31 = new ART_C_e3(); e31.term = term; break;
      case 188: e21 = new ART_C_e2(); e21.term = term; break;
      case 190: e31 = new ART_C_e3(); e31.term = term; break;
      case 193: e21 = new ART_C_e2(); e21.term = term; break;
      case 195: e31 = new ART_C_e3(); e31.term = term; break;
      case 198: e21 = new ART_C_e2(); e21.term = term; break;
      case 200: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 185: return e31;
      case 188: return e21;
      case 190: return e31;
      case 193: return e21;
      case 195: return e31;
      case 198: return e21;
      case 200: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 185: e2.v= e31.v;  break;
      case 190: e2.v = e21.v * e31.v;  break;
      case 195: e2.v = e21.v / e31.v;  break;
      case 200: e2.v = e21.v % e31.v;  break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this; int v; ART_C_e3 e31; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 204: e41 = new ART_C_e4(); e41.term = term; break;
      case 208: e31 = new ART_C_e3(); e31.term = term; break;
      case 212: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 204: return e41;
      case 208: return e31;
      case 212: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 204:e3.v = e41.v;  break;
      case 208:e3.v = e31.v;  break;
      case 212:e3.v = -e31.v;  break;
      }
    }
  }

  public class ART_C_e4 extends AbstractAttributeBlock {
    ART_C_e4 e4 = this; int v; ART_C_e5 e51; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 216: e51 = new ART_C_e5(); e51.term = term; break;
      case 219: e51 = new ART_C_e5(); e51.term = term; break;
      case 221: e41 = new ART_C_e4(); e41.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 216: return e51;
      case 219: return e51;
      case 221: return e41;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 216: e4.v = e51.v;  break;
      case 221:e4.v = (int) Math.pow(e51.v, e41.v);  break;
      }
    }
  }

  public class ART_C_e5 extends AbstractAttributeBlock {
    ART_C_e5 e5 = this; int v; ART_C_ID ID1; ART_C_e1 e11; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 225: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 228: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 232: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 225: return INTEGER1;
      case 228: return ID1;
      case 232: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 225:e5.v = INTEGER1.v;  break;
      case 228: e5.v = variables.get(ID1.v);  break;
      case 232: e5.v = e11.v;  break;
      }
    }
  }

  public class ART_C_elseOpt extends AbstractAttributeBlock {
    ART_C_elseOpt elseOpt = this; ART_C_statement statement1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 238: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 238: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
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
      case 245: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 248: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 250: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      case 253: e01 = new ART_C_e0(); e01.term = term; break;
      case 256: e01 = new ART_C_e0(); e01.term = term; break;
      case 258: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 245: return STRING_SQ1;
      case 248: return STRING_SQ1;
      case 250: return printElements1;
      case 253: return e01;
      case 256: return e01;
      case 258: return printElements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 245: System.out.print(STRING_SQ1.v);  break;
      case 248: System.out.print(STRING_SQ1.v);  break;
      case 253: System.out.print(e01.v);  break;
      case 256: System.out.print(e01.v);  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_printElements printElements1; ART_C_statement statement1; ART_C_statements statements1; ART_C_ID ID1; ART_C_e0 e01; ART_C_elseOpt elseOpt1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 262: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 264: e01 = new ART_C_e0(); e01.term = term; break;
      case 269: e01 = new ART_C_e0(); e01.term = term; break;
      case 271: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 272: elseOpt1 = new ART_C_elseOpt(); elseOpt1.term = term; break;
      case 276: e01 = new ART_C_e0(); e01.term = term; break;
      case 278: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 283: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      case 289: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 290: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 294: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 299: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 262: return ID1;
      case 264: return e01;
      case 269: return e01;
      case 271: return statement1;
      case 272: return elseOpt1;
      case 276: return e01;
      case 278: return statement1;
      case 283: return printElements1;
      case 289: return ID1;
      case 290: return statement1;
      case 294: return ID1;
      case 299: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 265: variables.put(ID1.v, e01.v);  break;
      case 272: if (e01.v != 0) interpret(statement1); 
     else interpret(elseOpt1);  break;
      case 278: interpret(e01); 
     while (e01.v != 0) { 
        interpret(statement1); 
        interpret(e01); 
      }  break;
      case 290: procedures.put(ID1.v, statement1);  break;
      case 295: interpret(procedures.get(ID1.v));  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 304: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 307: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 308: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 304: return statement1;
      case 307: return statement1;
      case 308: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
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
      case 312: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 312: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 312: System.out.println("Final variable map " + variables);  break;
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
