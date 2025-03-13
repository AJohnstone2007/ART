import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2025-03-13 07:36:21"; }

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
      case 83: INTEGER.v = Integer.parseInt(lexeme());  break;
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
      case 87: STRING_SQ.v = lexemeCore().translateEscapes();  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 91: e11 = new ART_C_e1(); e11.term = term; break;
      case 94: e11 = new ART_C_e1(); e11.term = term; break;
      case 96: e12 = new ART_C_e1(); e12.term = term; break;
      case 99: e11 = new ART_C_e1(); e11.term = term; break;
      case 101: e12 = new ART_C_e1(); e12.term = term; break;
      case 104: e11 = new ART_C_e1(); e11.term = term; break;
      case 106: e12 = new ART_C_e1(); e12.term = term; break;
      case 109: e11 = new ART_C_e1(); e11.term = term; break;
      case 111: e12 = new ART_C_e1(); e12.term = term; break;
      case 114: e11 = new ART_C_e1(); e11.term = term; break;
      case 116: e12 = new ART_C_e1(); e12.term = term; break;
      case 119: e11 = new ART_C_e1(); e11.term = term; break;
      case 121: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 91: return e11;
      case 94: return e11;
      case 96: return e12;
      case 99: return e11;
      case 101: return e12;
      case 104: return e11;
      case 106: return e12;
      case 109: return e11;
      case 111: return e12;
      case 114: return e11;
      case 116: return e12;
      case 119: return e11;
      case 121: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 91: e0.v = e11.v;  break;
      case 96: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 101: e0.v = e11.v <  e12.v ? 1 : 0;  break;
      case 106: e0.v = e11.v >= e12.v ? 1 : 0;  break;
      case 111: e0.v = e11.v <= e12.v ? 1 : 0;  break;
      case 116: e0.v = e11.v == e12.v ? 1 : 0;  break;
      case 121: e0.v = e11.v != e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 125: e21 = new ART_C_e2(); e21.term = term; break;
      case 128: e11 = new ART_C_e1(); e11.term = term; break;
      case 130: e21 = new ART_C_e2(); e21.term = term; break;
      case 133: e11 = new ART_C_e1(); e11.term = term; break;
      case 135: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 125: return e21;
      case 128: return e11;
      case 130: return e21;
      case 133: return e11;
      case 135: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 125: e1.v = e21.v;  break;
      case 130: e1.v = e11.v + e21.v;  break;
      case 135: e1.v = e11.v - e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_e2 e21; ART_C_e3 e31;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 139: e31 = new ART_C_e3(); e31.term = term; break;
      case 142: e21 = new ART_C_e2(); e21.term = term; break;
      case 144: e31 = new ART_C_e3(); e31.term = term; break;
      case 147: e21 = new ART_C_e2(); e21.term = term; break;
      case 149: e31 = new ART_C_e3(); e31.term = term; break;
      case 152: e21 = new ART_C_e2(); e21.term = term; break;
      case 154: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 139: return e31;
      case 142: return e21;
      case 144: return e31;
      case 147: return e21;
      case 149: return e31;
      case 152: return e21;
      case 154: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 139: e2.v= e31.v;  break;
      case 144: e2.v = e21.v * e31.v;  break;
      case 149: e2.v = e21.v / e31.v;  break;
      case 154: e2.v = e21.v % e31.v;  break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this; int v; ART_C_e3 e31; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 158: e41 = new ART_C_e4(); e41.term = term; break;
      case 162: e31 = new ART_C_e3(); e31.term = term; break;
      case 166: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 158: return e41;
      case 162: return e31;
      case 166: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 158:e3.v = e41.v;  break;
      case 162:e3.v = e31.v;  break;
      case 166:e3.v = -e31.v;  break;
      }
    }
  }

  public class ART_C_e4 extends AbstractAttributeBlock {
    ART_C_e4 e4 = this; int v; ART_C_e5 e51; ART_C_e4 e41;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 170: e51 = new ART_C_e5(); e51.term = term; break;
      case 173: e51 = new ART_C_e5(); e51.term = term; break;
      case 175: e41 = new ART_C_e4(); e41.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 170: return e51;
      case 173: return e51;
      case 175: return e41;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 170: e4.v = e51.v;  break;
      case 175: e4.v = (int) Math.pow(e51.v, e41.v);  break;
      }
    }
  }

  public class ART_C_e5 extends AbstractAttributeBlock {
    ART_C_e5 e5 = this; int v; ART_C_e1 e11; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 179: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 183: e11 = new ART_C_e1(); e11.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 179: return INTEGER1;
      case 183: return e11;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 179:e5.v = INTEGER1.v;  break;
      case 183: e5.v = e11.v;  break;
      }
    }
  }

  public class ART_C_printElements extends AbstractAttributeBlock {
    ART_C_printElements printElements = this; ART_C_printElements printElements1; ART_C_e0 e01; ART_C_STRING_SQ STRING_SQ1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 188: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 191: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 193: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      case 196: e01 = new ART_C_e0(); e01.term = term; break;
      case 199: e01 = new ART_C_e0(); e01.term = term; break;
      case 201: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 188: return STRING_SQ1;
      case 191: return STRING_SQ1;
      case 193: return printElements1;
      case 196: return e01;
      case 199: return e01;
      case 201: return printElements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 188: System.out.print(STRING_SQ1.v);  break;
      case 191: System.out.print(STRING_SQ1.v);  break;
      case 196: System.out.print(e01.v);  break;
      case 199: System.out.print(e01.v);  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_printElements printElements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 207: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 207: return printElements1;
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
    var ret = new ART_C_statement();
    ret.term = term;
    return ret;
  }
}
