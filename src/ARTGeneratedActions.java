import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap; 
public class ARTGeneratedActions extends AbstractActions {
 Map<String, Double> variables = new HashMap<>(); 
   Map<String, AbstractAttributeBlock> blocks = new HashMap<>(); 
  public String name() { return "2026-02-09 21:20:02"; }

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
      case 107: ID.v = lexeme();  break;
      }
    }
  }

  public class ART_C_REAL extends AbstractAttributeBlock {
    ART_C_REAL REAL = this; double v;

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
      case 111: REAL.v = Double.parseDouble(lexeme());  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; double v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 115: e11 = new ART_C_e1(); e11.term = term; break;
      case 118: e11 = new ART_C_e1(); e11.term = term; break;
      case 120: e12 = new ART_C_e1(); e12.term = term; break;
      case 123: e11 = new ART_C_e1(); e11.term = term; break;
      case 125: e12 = new ART_C_e1(); e12.term = term; break;
      case 128: e11 = new ART_C_e1(); e11.term = term; break;
      case 130: e12 = new ART_C_e1(); e12.term = term; break;
      case 133: e11 = new ART_C_e1(); e11.term = term; break;
      case 135: e12 = new ART_C_e1(); e12.term = term; break;
      case 138: e11 = new ART_C_e1(); e11.term = term; break;
      case 140: e12 = new ART_C_e1(); e12.term = term; break;
      case 143: e11 = new ART_C_e1(); e11.term = term; break;
      case 145: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 115: return e11;
      case 118: return e11;
      case 120: return e12;
      case 123: return e11;
      case 125: return e12;
      case 128: return e11;
      case 130: return e12;
      case 133: return e11;
      case 135: return e12;
      case 138: return e11;
      case 140: return e12;
      case 143: return e11;
      case 145: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 115: e0.v = e11.v;  break;
      case 120: e0.v = e11.v >  e12.v ? 1.0 : 0;  break;
      case 125: e0.v = e11.v <  e12.v ? 1.0 : 0;  break;
      case 130: e0.v = e11.v >= e12.v ? 1.0 : 0;  break;
      case 135: e0.v = e11.v <= e12.v ? 1.0 : 0;  break;
      case 140: e0.v = e11.v == e12.v ? 1.0 : 0;  break;
      case 145: e0.v = e11.v != e12.v ? 1.0 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; double v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 149: e21 = new ART_C_e2(); e21.term = term; break;
      case 152: e11 = new ART_C_e1(); e11.term = term; break;
      case 154: e21 = new ART_C_e2(); e21.term = term; break;
      case 157: e11 = new ART_C_e1(); e11.term = term; break;
      case 159: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 149: return e21;
      case 152: return e11;
      case 154: return e21;
      case 157: return e11;
      case 159: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 149: e1.v = e21.v;  break;
      case 154: e1.v = e11.v + e21.v;  break;
      case 159: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; double v; ART_C_e2 e21; ART_C_e3 e31;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 163: e31 = new ART_C_e3(); e31.term = term; break;
      case 166: e21 = new ART_C_e2(); e21.term = term; break;
      case 168: e31 = new ART_C_e3(); e31.term = term; break;
      case 171: e21 = new ART_C_e2(); e21.term = term; break;
      case 173: e31 = new ART_C_e3(); e31.term = term; break;
      case 176: e21 = new ART_C_e2(); e21.term = term; break;
      case 178: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 163: return e31;
      case 166: return e21;
      case 168: return e31;
      case 171: return e21;
      case 173: return e31;
      case 176: return e21;
      case 178: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 163: e2.v= e31.v;  break;
      case 168: e2.v = e21.v * e31.v;  break;
      case 173: e2.v = e21.v / e31.v;  break;
      case 178: e2.v = e21.v % e31.v;  break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this; double v; ART_C_e3 e31; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 182: e41 = new ART_C_e4(); e41.term = term; break;
      case 186: e31 = new ART_C_e3(); e31.term = term; break;
      case 190: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 182: return e41;
      case 186: return e31;
      case 190: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 182:e3.v = e41.v;  break;
      case 186:e3.v = e31.v;  break;
      case 190:e3.v = -e31.v;  break;
      }
    }
  }

  public class ART_C_e4 extends AbstractAttributeBlock {
    ART_C_e4 e4 = this; double v; ART_C_e5 e51; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 194: e51 = new ART_C_e5(); e51.term = term; break;
      case 197: e51 = new ART_C_e5(); e51.term = term; break;
      case 199: e41 = new ART_C_e4(); e41.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 194: return e51;
      case 197: return e51;
      case 199: return e41;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 194: e4.v = e51.v;  break;
      case 199:e4.v = (double) Math.pow(e51.v, e41.v);  break;
      }
    }
  }

  public class ART_C_e5 extends AbstractAttributeBlock {
    ART_C_e5 e5 = this; double v; ART_C_REAL REAL1; ART_C_ID ID1; ART_C_e1 e11;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 203: REAL1 = new ART_C_REAL(); REAL1.term = term; break;
      case 206: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 210: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 203: return REAL1;
      case 206: return ID1;
      case 210: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 203:e5.v = REAL1.v;  break;
      case 206: e5.v = variables.get(ID1.v);  break;
      case 210: e5.v = e11.v;  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_statement statement1; ART_C_statements statements1; ART_C_e0 e01; ART_C_ID ID1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 219: e01 = new ART_C_e0(); e01.term = term; break;
      case 223: e01 = new ART_C_e0(); e01.term = term; break;
      case 226: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 228: e01 = new ART_C_e0(); e01.term = term; break;
      case 231: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 233: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 237: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 241: e01 = new ART_C_e0(); e01.term = term; break;
      case 242: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 246: e01 = new ART_C_e0(); e01.term = term; break;
      case 247: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 251: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 219: return e01;
      case 223: return e01;
      case 226: return ID1;
      case 228: return e01;
      case 231: return ID1;
      case 233: return statement1;
      case 237: return ID1;
      case 241: return e01;
      case 242: return statement1;
      case 246: return e01;
      case 247: return statement1;
      case 251: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 215: plugin("home");  break;
      case 219: plugin("rotate", e01.v);  break;
      case 223: plugin("go", e01.v);  break;
      case 228: variables.put(ID1.v, e01.v);  break;
      case 233: blocks.put(ID1.v, statement1);  break;
      case 237: if (blocks.get(ID1.v) == null) Util.fatal("Unknown named block " + ID1.v);  interpret(blocks.get(ID1.v));  break;
      case 242: if (e01.v != 0) interpret(statement1);  break;
      case 247: do {
       interpret(statement1); 
       interpret(e01); 
//       System.out.println("predicate evaluates to " + e01.v);
     } while (e01.v < 0.1);  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 256: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 259: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 260: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 256: return statement1;
      case 259: return statement1;
      case 260: return statements1;
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
      case 264: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 264: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 263: plugin("init");  break;
      case 264: System.out.println("Final variable map " + variables);  break;
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
