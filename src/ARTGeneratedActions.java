import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2026-02-17 11:38:26"; }

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
      case 23: break;
      }
    }
  }

  public class ART_C_P extends AbstractAttributeBlock {
    ART_C_P P = this; int bound; int ok; ART_C_X X1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 27: X1 = new ART_C_X(); X1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 27: return X1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 27: break;
      }
    }
  }

  public class ART_C_S extends AbstractAttributeBlock {
    ART_C_S S = this; int ok; ART_C_P P1; ART_C_I I1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 31: I1 = new ART_C_I(); I1.term = term; break;
      case 32: P1 = new ART_C_P(); P1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 31: return I1;
      case 32: return P1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 31: break;
      case 32: break;
      }
    }
  }

  public class ART_C_X extends AbstractAttributeBlock {
    ART_C_X X = this; int count; ART_C_X X1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 37: X1 = new ART_C_X(); X1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 37: return X1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 37: break;
      case 39: break;
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
