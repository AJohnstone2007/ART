import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2026-03-02 12:15:09"; }

  public class ART_C_I extends AbstractAttributeBlock {
    ART_C_I I = this; int v;

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
      case 29: I.v = 1;  break;
      case 32: I.v = 2;  break;
      case 35: I.v = 3;  break;
      }
    }
  }

  public class ART_C_S extends AbstractAttributeBlock {
    ART_C_S S = this; boolean v; ART_C_T T1; ART_C_I I1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 39: I1 = new ART_C_I(); I1.term = term; break;
      case 40: T1 = new ART_C_T(); T1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 39: return I1;
      case 40: return T1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 39: T1.b = I1.v;  break;
      case 40: S.v = T1.v;  System.out.println("S.v = " + S.v);  break;
      }
    }
  }

  public class ART_C_T extends AbstractAttributeBlock {
    ART_C_T T = this; int b; boolean v; ART_C_X X1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 44: X1 = new ART_C_X(); X1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 44: return X1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 44: 	T.v = X1.c <= T.b;  break;
      }
    }
  }

  public class ART_C_X extends AbstractAttributeBlock {
    ART_C_X X = this; int c; ART_C_X X1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 49: X1 = new ART_C_X(); X1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 49: return X1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 49: X.c = X1.c+1;  break;
      case 52: X.c = 1;  break;
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_S();
    ret.term = term;
    return ret;
  }
}
