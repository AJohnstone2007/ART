import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 import java.util.Map; import java.util.HashMap;import java.util.List;import java.util.ArrayList;  
public class ARTGeneratedActions extends AbstractActions {
 Map<Integer, Integer> variables = new HashMap<>();
   ArrayList<HashMap<String, Integer>> rhos = new ArrayList<>() {{ add(new HashMap<>()); }};
   Map<String, AbstractAttributeBlock> procedures = new HashMap<>();
   
   void assign(String id,int val) {
    variables.put(variables.size(), val); rhos.get(rhos.size()-1).put(id ,variables.size()-1); 
   }
   Integer deref(String ID ){
      for (int i = rhos.size() - 1; i >= 0; i--) {
          Integer   res = variables.get(rhos.get(i).get(ID));
          if (res != null){
             return  res;
          }
       }
   return null;    
   }

  public String name() { return "2025-03-22 13:12:12"; }

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
      case 118: ID.v = lexeme();  break;
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
      case 122: INTEGER.v = Integer.parseInt(lexeme());  break;
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
      case 126: STRING_SQ.v = lexemeCore().translateEscapes();  break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this; int v; ART_C_e1 e11; ART_C_e1 e12;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 130: e11 = new ART_C_e1(); e11.term = term; break;
      case 133: e11 = new ART_C_e1(); e11.term = term; break;
      case 135: e12 = new ART_C_e1(); e12.term = term; break;
      case 138: e11 = new ART_C_e1(); e11.term = term; break;
      case 140: e12 = new ART_C_e1(); e12.term = term; break;
      case 143: e11 = new ART_C_e1(); e11.term = term; break;
      case 145: e12 = new ART_C_e1(); e12.term = term; break;
      case 148: e11 = new ART_C_e1(); e11.term = term; break;
      case 150: e12 = new ART_C_e1(); e12.term = term; break;
      case 153: e11 = new ART_C_e1(); e11.term = term; break;
      case 155: e12 = new ART_C_e1(); e12.term = term; break;
      case 158: e11 = new ART_C_e1(); e11.term = term; break;
      case 160: e12 = new ART_C_e1(); e12.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 130: return e11;
      case 133: return e11;
      case 135: return e12;
      case 138: return e11;
      case 140: return e12;
      case 143: return e11;
      case 145: return e12;
      case 148: return e11;
      case 150: return e12;
      case 153: return e11;
      case 155: return e12;
      case 158: return e11;
      case 160: return e12;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 130: e0.v = e11.v;  break;
      case 135: e0.v = e11.v >  e12.v ? 1 : 0;  break;
      case 140: e0.v = e11.v <  e12.v ? 1 : 0;  break;
      case 145: e0.v = e11.v >=  e12.v ? 1 : 0;  break;
      case 150: e0.v = e11.v <=  e12.v ? 1 : 0;  break;
      case 155: e0.v = e11.v != e12.v ? 1 : 0;  break;
      case 160: e0.v = e11.v == e12.v ? 1 : 0;  break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this; int v; ART_C_e1 e11; ART_C_e2 e21;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 164: e21 = new ART_C_e2(); e21.term = term; break;
      case 167: e11 = new ART_C_e1(); e11.term = term; break;
      case 169: e21 = new ART_C_e2(); e21.term = term; break;
      case 172: e11 = new ART_C_e1(); e11.term = term; break;
      case 174: e21 = new ART_C_e2(); e21.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 164: return e21;
      case 167: return e11;
      case 169: return e21;
      case 172: return e11;
      case 174: return e21;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 164: e1.v = e21.v;  break;
      case 169: e1.v = e11.v - e21.v;  break;
      case 174: e1.v = e11.v + e21.v;  break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this; int v; ART_C_e2 e21; ART_C_e3 e31;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 178: e31 = new ART_C_e3(); e31.term = term; break;
      case 181: e21 = new ART_C_e2(); e21.term = term; break;
      case 183: e31 = new ART_C_e3(); e31.term = term; break;
      case 186: e21 = new ART_C_e2(); e21.term = term; break;
      case 188: e31 = new ART_C_e3(); e31.term = term; break;
      case 191: e21 = new ART_C_e2(); e21.term = term; break;
      case 193: e31 = new ART_C_e3(); e31.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 178: return e31;
      case 181: return e21;
      case 183: return e31;
      case 186: return e21;
      case 188: return e31;
      case 191: return e21;
      case 193: return e31;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 178: e2.v = e31.v;  break;
      case 183: e2.v = e21.v * e31.v;  break;
      case 188: e2.v = e21.v / e31.v;   break;
      case 193: e2.v = e21.v % e31.v;   break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this; int v; ART_C_ID ID1; ART_C_e0 e01; ART_C_e1 e11; ART_C_STRING_SQ STRING_SQ1; ART_C_INTEGER INTEGER1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 197: INTEGER1 = new ART_C_INTEGER(); INTEGER1.term = term; break;
      case 200: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 204: e11 = new ART_C_e1(); e11.term = term; break;
      case 210: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 212: e01 = new ART_C_e0(); e01.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 197: return INTEGER1;
      case 200: return ID1;
      case 204: return e11;
      case 210: return STRING_SQ1;
      case 212: return e01;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 197:e3.v = INTEGER1.v;  break;
      case 200: e3.v = deref(ID1.v); break;
      case 204: e3.v = e11.v;  break;
      case 213: e3.v = (int) plugin(STRING_SQ1.v, e01.v);  break;
      }
    }
  }

  public class ART_C_printElements extends AbstractAttributeBlock {
    ART_C_printElements printElements = this; ART_C_e0 e01; ART_C_STRING_SQ STRING_SQ1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 217: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 220: e01 = new ART_C_e0(); e01.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 217: return STRING_SQ1;
      case 220: return e01;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 217: System.out.print(STRING_SQ1.v);  break;
      case 220: System.out.print(e01.v);  break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; int v; ART_C_printElements printElements1; ART_C_statement statement1; ART_C_statement statement2; ART_C_statement statement3; ART_C_ID ID1; ART_C_e0 e01;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 227: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 229: e01 = new ART_C_e0(); e01.term = term; break;
      case 234: e01 = new ART_C_e0(); e01.term = term; break;
      case 236: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 238: statement2 = new ART_C_statement(); statement2.term = term; break;
      case 242: e01 = new ART_C_e0(); e01.term = term; break;
      case 244: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 248: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 249: e01 = new ART_C_e0(); e01.term = term; break;
      case 251: statement2 = new ART_C_statement(); statement2.term = term; break;
      case 253: statement3 = new ART_C_statement(); statement3.term = term; break;
      case 258: printElements1 = new ART_C_printElements(); printElements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 227: return ID1;
      case 229: return e01;
      case 234: return e01;
      case 236: return statement1;
      case 238: return statement2;
      case 242: return e01;
      case 244: return statement1;
      case 248: return statement1;
      case 249: return e01;
      case 251: return statement2;
      case 253: return statement3;
      case 258: return printElements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 224: variables = new HashMap<>(); new ArrayList<>() {{ add(new HashMap<>()); }};procedures = new HashMap<>(); break;
      case 230: assign(ID1.v,e01.v);  break;
      case 238: if (e01.v != 0) interpret(statement1); else interpret(statement2);  break;
      case 244: interpret(e01); while (e01.v != 0) { interpret(statement1); interpret(e01); }  break;
      case 253:   interpret(e01); while (e01.v != 0) { interpret(statement3); interpret(statement2); interpret(e01);  }  break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 264: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 265: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 268: statement1 = new ART_C_statement(); statement1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 264: return statement1;
      case 265: return statements1;
      case 268: return statement1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 268: System.out.println("Final variable map " + variables + rhos);  break;
      }
    }
  }

  public class ART_C_switchCase extends AbstractAttributeBlock {
    ART_C_switchCase switchCase = this; int v; ART_C_switchCase switchCase1; ART_C_statements statements1; ART_C_statements statements2; ART_C_e0 e01;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 274: e01 = new ART_C_e0(); e01.term = term; break;
      case 275: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 277: switchCase1 = new ART_C_switchCase(); switchCase1.term = term; break;
      case 282: e01 = new ART_C_e0(); e01.term = term; break;
      case 283: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 287: statements2 = new ART_C_statements(); statements2.term = term; break;
      case 289: switchCase1 = new ART_C_switchCase(); switchCase1.term = term; break;
      case 294: e01 = new ART_C_e0(); e01.term = term; break;
      case 295: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 300: switchCase1 = new ART_C_switchCase(); switchCase1.term = term; break;
      case 305: e01 = new ART_C_e0(); e01.term = term; break;
      case 310: switchCase1 = new ART_C_switchCase(); switchCase1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 274: return e01;
      case 275: return statements1;
      case 277: return switchCase1;
      case 282: return e01;
      case 283: return statements1;
      case 287: return statements2;
      case 289: return switchCase1;
      case 294: return e01;
      case 295: return statements1;
      case 300: return switchCase1;
      case 305: return e01;
      case 310: return switchCase1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 276:if(e01.v ==  switchCase.v){interpret(statements1);} switchCase1.v = switchCase.v; break;
      }
    }
  }

  public class ART_C_tryCatch extends AbstractAttributeBlock {
    ART_C_tryCatch tryCatch = this; int v; ART_C_tryCatch tryCatch1; ART_C_statement statement1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 317: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 318: tryCatch1 = new ART_C_tryCatch(); tryCatch1.term = term; break;
      case 324: tryCatch1 = new ART_C_tryCatch(); tryCatch1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 317: return statement1;
      case 318: return tryCatch1;
      case 324: return tryCatch1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 318: tryCatch.v = tryCatch1.v + tryCatch.v; break;
      case 323: tryCatch.v = 1; break;
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
