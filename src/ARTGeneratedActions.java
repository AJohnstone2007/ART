import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
public class ARTGeneratedActions extends AbstractActions {
  public String name() { return "2026-01-28 15:19:29"; }

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
      case 25: break;
      }
    }
  }

  public class ART_C_P extends AbstractAttributeBlock {
    ART_C_P P = this; int bound; int ok; ART_C_X X1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 29: X1 = new ART_C_X(); X1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 29: return X1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 29: break;
      }
    }
  }

  public class ART_C_S extends AbstractAttributeBlock {
    ART_C_S S = this; int v; int ok; ART_C_P P1; ART_C_I I1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 33: I1 = new ART_C_I(); I1.term = term; break;
      case 34: P1 = new ART_C_P(); P1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 33: return I1;
      case 34: return P1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 33: break;
      case 34: break;
      }
    }
  }

  public class ART_C_SPrime extends AbstractAttributeBlock {
    ART_C_SPrime SPrime = this; ART_C_S S1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 38: S1 = new ART_C_S(); S1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 38: return S1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 38: System.out.println(S1.v);  break;
      }
    }
  }

  public class ART_C_X extends AbstractAttributeBlock {
    ART_C_X X = this; int count; ART_C_X X1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 43: X1 = new ART_C_X(); X1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 43: return X1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 43: break;
      case 45: break;
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_SPrime();
    ret.term = term;
    return ret;
  }
}
