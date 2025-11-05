import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2025-11-05 10:52:36"; }

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
      case 86: INTEGER.v = Integer.parseInt(lexeme());  break;
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
      case 90: STRING_SQ.v = lexeme().translateEscapes();  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 94: e11 = new ART_C_e1(); e11.term = term; break;
      case 97: e11 = new ART_C_e1(); e11.term = term; break;
      case 99: e12 = new ART_C_e1(); e12.term = term; break;
      case 102: e11 = new ART_C_e1(); e11.term = term; break;
      case 104: e12 = new ART_C_e1(); e12.term = term; break;
      case 107: e11 = new ART_C_e1(); e11.term = term; break;
      case 109: e12 = new ART_C_e1(); e12.term = term; break;
      case 112: e11 = new ART_C_e1(); e11.term = term; break;
      case 114: e12 = new ART_C_e1(); e12.term = term; break;
      case 117: e11 = new ART_C_e1(); e11.term = term; break;
      case 119: e12 = new ART_C_e1(); e12.term = term; break;
      case 122: e11 = new ART_C_e1(); e11.term = term; break;
      case 124: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 94: return e11;
      case 97: return e11;
      case 99: return e12;
      case 102: return e11;
      case 104: return e12;
      case 107: return e11;
      case 109: return e12;
      case 112: return e11;
      case 114: return e12;
      case 117: return e11;
      case 119: return e12;
      case 122: return e11;
      case 124: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 94: e0.v = e11.v;  break;
      case 99: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 104: e0.v = e11.v <  e12.v ? 1 : 0;  break;
      case 109: e0.v = e11.v >= e12.v ? 1 : 0;  break;
      case 114: e0.v = e11.v <= e12.v ? 1 : 0;  break;
      case 119: e0.v = e11.v == e12.v ? 1 : 0;  break;
      case 124: e0.v = e11.v != e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 128: e21 = new ART_C_e2(); e21.term = term; break;
      case 131: e11 = new ART_C_e1(); e11.term = term; break;
      case 133: e21 = new ART_C_e2(); e21.term = term; break;
      case 136: e11 = new ART_C_e1(); e11.term = term; break;
      case 138: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 128: return e21;
      case 131: return e11;
      case 133: return e21;
      case 136: return e11;
      case 138: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 128: e1.v = e21.v;  break;
      case 133: e1.v = e11.v + e21.v;  break;
      case 138: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_e2 e21; ART_C_e3 e31;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 142: e31 = new ART_C_e3(); e31.term = term; break;
      case 145: e21 = new ART_C_e2(); e21.term = term; break;
      case 147: e31 = new ART_C_e3(); e31.term = term; break;
      case 150: e21 = new ART_C_e2(); e21.term = term; break;
      case 152: e31 = new ART_C_e3(); e31.term = term; break;
      case 155: e21 = new ART_C_e2(); e21.term = term; break;
      case 157: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 142: return e31;
      case 145: return e21;
      case 147: return e31;
      case 150: return e21;
      case 152: return e31;
      case 155: return e21;
      case 157: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 142: e2.v= e31.v;  break;
      case 147: e2.v = e21.v * e31.v;  break;
      case 152: e2.v = e21.v / e31.v;  break;
      case 157: e2.v = e21.v % e31.v;  break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this; int v; ART_C_e3 e31; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 161: e41 = new ART_C_e4(); e41.term = term; break;
      case 165: e31 = new ART_C_e3(); e31.term = term; break;
      case 169: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 161: return e41;
      case 165: return e31;
      case 169: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 161:e3.v = e41.v;  break;
      case 165:e3.v = e31.v;  break;
      case 169:e3.v = -e31.v;  break;
      }
    }
  }

  public class ART_C_e4 extends AbstractAttributeBlock {
    ART_C_e4 e4 = this; int v; ART_C_e5 e51; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 173: e51 = new ART_C_e5(); e51.term = term; break;
      case 176: e51 = new ART_C_e5(); e51.term = term; break;
      case 178: e41 = new ART_C_e4(); e41.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 173: return e51;
      case 176: return e51;
      case 178: return e41;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 173: e4.v = e51.v;  break;
      case 178: e4.v = (int) Math.pow(e51.v, e41.v);  break;
      }
    }
  }

  public class ART_C_e5 extends AbstractAttributeBlock {
    ART_C_e5 e5 = this; int v; ART_C_e1 e11; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 182: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 186: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 182: return INTEGER1;
      case 186: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 182:e5.v = INTEGER1.v;  break;
      case 186: e5.v = e11.v;  break;
      }
    }
  }

  public class ART_C_printElements extends AbstractAttributeBlock {
    ART_C_printElements printElements = this; ART_C_printElements printElements1; ART_C_e0 e01; ART_C_STRING_SQ STRING_SQ1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 191: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 194: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 196: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      case 199: e01 = new ART_C_e0(); e01.term = term; break;
      case 202: e01 = new ART_C_e0(); e01.term = term; break;
      case 204: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 191: return STRING_SQ1;
      case 194: return STRING_SQ1;
      case 196: return printElements1;
      case 199: return e01;
      case 202: return e01;
      case 204: return printElements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 191: System.out.print(STRING_SQ1.v);  break;
      case 194: System.out.print(STRING_SQ1.v);  break;
      case 199: System.out.print(e01.v);  break;
      case 202: System.out.print(e01.v);  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_printElements printElements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 210: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 210: return printElements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_statement();
    ret.term = term;
    return ret;
  }
}
