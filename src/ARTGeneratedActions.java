import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActionsNonterminal;
import uk.ac.rhul.cs.csle.art.util.Util;
import java.util.HashMap;
public class ARTGeneratedActions extends AbstractActions {
  HashMap<String, Integer> symbols = new HashMap<>(); 
  public String name() { return "2025-01-15 21:28:56"; }

 public class ART_C_INTEGER extends AbstractActionsNonterminal {
  int v;
    ART_C_INTEGER INTEGER;public void activate(ART_C_INTEGER parent) {  INTEGER = parent;}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 83: INTEGER.v = Integer.parseInt(lexeme()); break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_STRING_DQ extends AbstractActionsNonterminal {
  String v;
    ART_C_STRING_DQ STRING_DQ;public void activate(ART_C_STRING_DQ parent) {  STRING_DQ = parent;}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 87: STRING_DQ.v = lexeme(); break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_e0 extends AbstractActionsNonterminal {
  int v;
    ART_C_e0 e0; ART_C_e1 e11; ART_C_e1 e12;public void activate(ART_C_e0 parent) {  e0 = parent;e11 = new ART_C_e1();e12 = new ART_C_e1();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 91: e0.v = e11.v; break;
      case 96: e0.v = e11.v >  e12.v ? 1 : 0; break;
      case 101: e0.v = e11.v <  e12.v ? 1 : 0; break;
      case 106: e0.v = e11.v >= e12.v ? 1 : 0; break;
      case 111: e0.v = e11.v <= e12.v ? 1 : 0; break;
      case 116: e0.v = e11.v == e12.v ? 1 : 0; break;
      case 121: e0.v = e11.v != e12.v ? 1 : 0; break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 91: e11.activate(e11); return e11;
      case 94: e11.activate(e11); return e11;
      case 96: e12.activate(e12); return e12;
      case 99: e11.activate(e11); return e11;
      case 101: e12.activate(e12); return e12;
      case 104: e11.activate(e11); return e11;
      case 106: e12.activate(e12); return e12;
      case 109: e11.activate(e11); return e11;
      case 111: e12.activate(e12); return e12;
      case 114: e11.activate(e11); return e11;
      case 116: e12.activate(e12); return e12;
      case 119: e11.activate(e11); return e11;
      case 121: e12.activate(e12); return e12;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_e1 extends AbstractActionsNonterminal {
  int v;
    ART_C_e1 e1; ART_C_e1 e11; ART_C_e2 e21;public void activate(ART_C_e1 parent) {  e1 = parent;e11 = new ART_C_e1();e21 = new ART_C_e2();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 125: e1.v = e21.v; break;
      case 130: e1.v = e11.v + e21.v; break;
      case 135: e1.v = e11.v - e21.v; break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 125: e21.activate(e21); return e21;
      case 128: e11.activate(e11); return e11;
      case 130: e21.activate(e21); return e21;
      case 133: e11.activate(e11); return e11;
      case 135: e21.activate(e21); return e21;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_e2 extends AbstractActionsNonterminal {
  int v;
    ART_C_e2 e2; ART_C_e2 e21; ART_C_e3 e31;public void activate(ART_C_e2 parent) {  e2 = parent;e21 = new ART_C_e2();e31 = new ART_C_e3();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 139: e2.v= e31.v; break;
      case 144: e2.v = e21.v * e31.v; break;
      case 149: e2.v = e21.v / e31.v; break;
      case 154: e2.v = e21.v % e31.v; break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 139: e31.activate(e31); return e31;
      case 142: e21.activate(e21); return e21;
      case 144: e31.activate(e31); return e31;
      case 147: e21.activate(e21); return e21;
      case 149: e31.activate(e31); return e31;
      case 152: e21.activate(e21); return e21;
      case 154: e31.activate(e31); return e31;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_e3 extends AbstractActionsNonterminal {
  int v;
    ART_C_e3 e3; ART_C_e3 e31; ART_C_e4 e41;public void activate(ART_C_e3 parent) {  e3 = parent;e31 = new ART_C_e3();e41 = new ART_C_e4();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 158:e3.v = e41.v; break;
      case 162:e3.v = e31.v; break;
      case 166:e3.v = -e31.v; break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 158: e41.activate(e41); return e41;
      case 162: e31.activate(e31); return e31;
      case 166: e31.activate(e31); return e31;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_e4 extends AbstractActionsNonterminal {
  int v;
    ART_C_e4 e4; ART_C_e5 e51; ART_C_e4 e41;public void activate(ART_C_e4 parent) {  e4 = parent;e51 = new ART_C_e5();e41 = new ART_C_e4();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 170: e4.v = e51.v; break;
      case 175: e4.v = (int) Math.pow(e51.v, e41.v); break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 170: e51.activate(e51); return e51;
      case 173: e51.activate(e51); return e51;
      case 175: e41.activate(e41); return e41;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_e5 extends AbstractActionsNonterminal {
  int v;
    ART_C_e5 e5; ART_C_e1 e11; ART_C_INTEGER INTEGER1;public void activate(ART_C_e5 parent) {  e5 = parent;e11 = new ART_C_e1();INTEGER1 = new ART_C_INTEGER();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 179:e5.v = INTEGER1.v; break;
      case 183: e5.v = e11.v; break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 179: INTEGER1.activate(INTEGER1); return INTEGER1;
      case 183: e11.activate(e11); return e11;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_printElements extends AbstractActionsNonterminal {
 
    ART_C_printElements printElements; ART_C_printElements printElements1; ART_C_STRING_DQ STRING_DQ1; ART_C_e0 e01;public void activate(ART_C_printElements parent) {  printElements = parent;printElements1 = new ART_C_printElements();STRING_DQ1 = new ART_C_STRING_DQ();e01 = new ART_C_e0();}
    public void action(int nodeNumber) { switch(nodeNumber){
      case 188: System.out.print(STRING_DQ1.v); break;
      case 191: System.out.print(STRING_DQ1.v); break;
      case 196: System.out.print(e01.v); break;
      case 199: System.out.print(e01.v); break;
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 188: STRING_DQ1.activate(STRING_DQ1); return STRING_DQ1;
      case 191: STRING_DQ1.activate(STRING_DQ1); return STRING_DQ1;
      case 193: printElements1.activate(printElements1); return printElements1;
      case 196: e01.activate(e01); return e01;
      case 199: e01.activate(e01); return e01;
      case 201: printElements1.activate(printElements1); return printElements1;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }

 public class ART_C_statement extends AbstractActionsNonterminal {
 
    ART_C_statement statement; ART_C_printElements printElements1;public void activate(ART_C_statement parent) {  statement = parent;printElements1 = new ART_C_printElements();}
    public void action(int nodeNumber) { switch(nodeNumber){
    }}

    public AbstractActionsNonterminal call(int nodeNumber) { switch(nodeNumber){
      case 207: printElements1.activate(printElements1); return printElements1;
      default: Util.fatal("Unknown call node " + nodeNumber); return null;
    }}
  }
public AbstractActionsNonterminal init(AbstractInterpreter interpreter) { this.interpreter = interpreter; var ret = new ART_C_statement(); ret.activate(null); return ret; }
}
