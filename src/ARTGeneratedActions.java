import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
  import java.util.Map;
    import java.util.HashMap;
    import java.util.Scanner;
    import java.util.List;
    import java.util.LinkedList; 
public class ARTGeneratedActions extends AbstractActions {
  Map<Integer, Object> store = new HashMap<>(); 
    int scope = 0;
    Map<Integer, Map<String, Integer>> environments = new HashMap<>(Map.of(0, new HashMap<String, Integer>()));
    Scanner reader = new Scanner(System.in);
    boolean returned = false;
    
    void assign(String name, Object value) {
        Map<String, Integer> currentEnv = environments.get(scope);
        if (!currentEnv.containsKey(name)) {
            currentEnv.put(name, store.size());}
        store.put(currentEnv.get(name), value);
    }
    Object deref(String name) {
        return store.get(environments.get(scope).get(name));
    }
    void scopeUp() {
        scope++;
        environments.put(scope, new HashMap(environments.get(scope-1)));
    }
    void scopeDown() {
        environments.remove(scope);
        scope--;
    }
    
  public String name() { return "2026-05-05 11:38:54"; }

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
      case 214: ID.v = lexeme();  break;
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
      case 218: REAL.v = Double.parseDouble(lexeme());  break;
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
      case 222: STRING_SQ.v = lexeme().translateEscapes();  break;
      }
    }
  }

  public class ART_C_anyExp extends AbstractAttributeBlock {
    ART_C_anyExp anyExp = this; Object v; ART_C_load load1; ART_C_boolExp boolExp1; ART_C_STRING_SQ STRING_SQ1; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 226: boolExp1 = new ART_C_boolExp(); boolExp1.term = term; break;
      case 229: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      case 232: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 235: load1 = new ART_C_load(); load1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 226: return boolExp1;
      case 229: return STRING_SQ1;
      case 232: return numExp1;
      case 235: return load1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 226: anyExp.v = boolExp1.v;  break;
      case 229: anyExp.v = STRING_SQ1.v;  break;
      case 232: anyExp.v = numExp1.v;  break;
      case 235: anyExp.v = load1.v;  break;
      }
    }
  }

  public class ART_C_assign extends AbstractAttributeBlock {
    ART_C_assign assign = this; ART_C_ID ID1; ART_C_anyExp anyExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 239: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 241: anyExp1 = new ART_C_anyExp(); anyExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 239: return ID1;
      case 241: return anyExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 241:
    assign(ID1.v, anyExp1.v);
 break;
      }
    }
  }

  public class ART_C_boolExp extends AbstractAttributeBlock {
    ART_C_boolExp boolExp = this; boolean v; ART_C_boolExp boolExp1; ART_C_numExp numExp1; ART_C_numExp numExp2;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 245: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 247: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      case 250: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 252: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      case 255: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 257: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      case 260: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 262: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      case 265: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 267: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      case 270: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 272: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      case 276: boolExp1 = new ART_C_boolExp(); boolExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 245: return numExp1;
      case 247: return numExp2;
      case 250: return numExp1;
      case 252: return numExp2;
      case 255: return numExp1;
      case 257: return numExp2;
      case 260: return numExp1;
      case 262: return numExp2;
      case 265: return numExp1;
      case 267: return numExp2;
      case 270: return numExp1;
      case 272: return numExp2;
      case 276: return boolExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 247: boolExp.v = (double) numExp1.v > (double) numExp2.v;  break;
      case 252: boolExp.v = (double) numExp1.v < (double) numExp2.v;  break;
      case 257: boolExp.v = (double) numExp1.v >= (double) numExp2.v;  break;
      case 262: boolExp.v = (double) numExp1.v <= (double) numExp2.v;  break;
      case 267: boolExp.v = (double) numExp1.v == (double) numExp2.v;  break;
      case 272: boolExp.v = (double) numExp1.v != (double) numExp2.v;  break;
      case 276: boolExp.v = ! boolExp1.v;  break;
      }
    }
  }

  public class ART_C_call extends AbstractAttributeBlock {
    ART_C_call call = this; Object v; ART_C_listExps listExps1; ART_C_ID ID1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 281: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 283: listExps1 = new ART_C_listExps(); listExps1.term = term; break;
      case 288: ID1 = new ART_C_ID(); ID1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 281: return ID1;
      case 283: return listExps1;
      case 288: return ID1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 284: 
    scopeUp();
    List<String> variables = (LinkedList<String>) deref(ID1.v + "args");
    for (int i = 0; i < variables.size(); i++) {
        assign(variables.get(i), listExps1.list.get(i));
    }
    returned = false;
    interpret( (AbstractAttributeBlock) deref(ID1.v + "program"));
    if (returned) {
        call.v = deref("reservedforreturn");
        returned = false;
    }
    scopeDown();
 break;
      case 288:
        scopeUp();
        returned = false;
        interpret( (AbstractAttributeBlock) deref(ID1.v + "program"));
        if (returned) {
            call.v = deref("reservedforreturn");
            returned = false;
        }
        scopeDown();
 break;
      }
    }
  }

  public class ART_C_cases extends AbstractAttributeBlock {
    ART_C_cases cases = this; List<AbstractAttributeBlock> listStmnt; List<Double> listExp; ART_C_cases cases1; ART_C_statements statements1; ART_C_statements statements2; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 293: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 295: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 297: cases1 = new ART_C_cases(); cases1.term = term; break;
      case 301: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 303: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 306: statements2 = new ART_C_statements(); statements2.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 293: return numExp1;
      case 295: return statements1;
      case 297: return cases1;
      case 301: return numExp1;
      case 303: return statements1;
      case 306: return statements2;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 297:
        cases.listExp = cases1.listExp;
        cases.listExp.add(0, (double) numExp1.v);
        cases.listStmnt = cases1.listStmnt;
        cases.listStmnt.add(0,statements1);
     break;
      case 307:
        cases.listExp = new LinkedList<Double>();
        cases.listExp.add((double) numExp1.v);
        cases.listStmnt = new LinkedList<AbstractAttributeBlock>();
        cases.listStmnt.add(statements1);
        cases.listStmnt.add(statements2);
 break;
      }
    }
  }

  public class ART_C_define extends AbstractAttributeBlock {
    ART_C_define define = this; ART_C_statements statements1; ART_C_ID ID1; ART_C_listVars listVars1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 312: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 314: listVars1 = new ART_C_listVars(); listVars1.term = term; break;
      case 318: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 323: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 326: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 312: return ID1;
      case 314: return listVars1;
      case 318: return statements1;
      case 323: return ID1;
      case 326: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 319:
        assign(ID1.v+"args", listVars1.list);
        assign(ID1.v+"program", statements1);
 break;
      case 327: 
        assign(ID1.v+"program", statements1);
 break;
      }
    }
  }

  public class ART_C_forr extends AbstractAttributeBlock {
    ART_C_forr forr = this; ART_C_statement statement1; ART_C_statement statement2; ART_C_boolExp boolExp1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 333: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 334: boolExp1 = new ART_C_boolExp(); boolExp1.term = term; break;
      case 336: statement2 = new ART_C_statement(); statement2.term = term; break;
      case 339: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 333: return statement1;
      case 334: return boolExp1;
      case 336: return statement2;
      case 339: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 340:
    scopeUp();
    interpret(statement1);
    interpret(boolExp1);
    while (boolExp1.v) {
        interpret(statements1);
        interpret(statement2);
        interpret(boolExp1); }
    scopeDown();
 break;
      }
    }
  }

  public class ART_C_iff extends AbstractAttributeBlock {
    ART_C_iff iff = this; ART_C_boolExp boolExp1; ART_C_statements statements1; ART_C_statements statements2;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 345: boolExp1 = new ART_C_boolExp(); boolExp1.term = term; break;
      case 347: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 352: boolExp1 = new ART_C_boolExp(); boolExp1.term = term; break;
      case 354: statements1 = new ART_C_statements(); statements1.term = term; break;
      case 356: statements2 = new ART_C_statements(); statements2.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 345: return boolExp1;
      case 347: return statements1;
      case 352: return boolExp1;
      case 354: return statements1;
      case 356: return statements2;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 348:
    if (boolExp1.v) {interpret(statements1);}
 break;
      case 357:
    if (boolExp1.v) {interpret(statements1);}
    else {interpret(statements2);}
 break;
      }
    }
  }

  public class ART_C_init extends AbstractAttributeBlock {
    ART_C_init init = this;

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
      case 361: plugin("init");  break;
      }
    }
  }

  public class ART_C_invert extends AbstractAttributeBlock {
    ART_C_invert invert = this; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 366: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 366: return numExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 366: plugin("invert", numExp1.v);  break;
      }
    }
  }

  public class ART_C_listExps extends AbstractAttributeBlock {
    ART_C_listExps listExps = this; List<Object> list; ART_C_listExps listExps1; ART_C_anyExp anyExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 370: anyExp1 = new ART_C_anyExp(); anyExp1.term = term; break;
      case 373: listExps1 = new ART_C_listExps(); listExps1.term = term; break;
      case 375: anyExp1 = new ART_C_anyExp(); anyExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 370: return anyExp1;
      case 373: return listExps1;
      case 375: return anyExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 370:
    listExps.list = new LinkedList<Object>();
    listExps.list.add(anyExp1.v);
 break;
      case 375:
        listExps.list = listExps1.list;
        listExps.list.add(anyExp1.v);
 break;
      }
    }
  }

  public class ART_C_listVars extends AbstractAttributeBlock {
    ART_C_listVars listVars = this; List<String> list; ART_C_ID ID1; ART_C_listVars listVars1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 379: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 382: listVars1 = new ART_C_listVars(); listVars1.term = term; break;
      case 384: ID1 = new ART_C_ID(); ID1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 379: return ID1;
      case 382: return listVars1;
      case 384: return ID1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 379:
    listVars.list = new LinkedList<String>();
    listVars.list.add(ID1.v);
 break;
      case 384:
        listVars.list = listVars1.list;
        listVars.list.add(ID1.v);
 break;
      }
    }
  }

  public class ART_C_load extends AbstractAttributeBlock {
    ART_C_load load = this; double v; ART_C_STRING_SQ STRING_SQ1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 389: STRING_SQ1 = new ART_C_STRING_SQ(); STRING_SQ1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 389: return STRING_SQ1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 389: load.v = (double) plugin("load", STRING_SQ1.v);  break;
      }
    }
  }

  public class ART_C_medianFilter extends AbstractAttributeBlock {
    ART_C_medianFilter medianFilter = this; ART_C_numExp numExp1; ART_C_numExp numExp2;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 394: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 395: numExp2 = new ART_C_numExp(); numExp2.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 394: return numExp1;
      case 395: return numExp2;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 395: plugin("medianFilter", numExp1.v, numExp2.v);  break;
      }
    }
  }

  public class ART_C_numExp extends AbstractAttributeBlock {
    ART_C_numExp numExp = this; Object v; ART_C_p2op p2op1; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 399: p2op1 = new ART_C_p2op(); p2op1.term = term; break;
      case 402: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 404: p2op1 = new ART_C_p2op(); p2op1.term = term; break;
      case 407: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 409: p2op1 = new ART_C_p2op(); p2op1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 399: return p2op1;
      case 402: return numExp1;
      case 404: return p2op1;
      case 407: return numExp1;
      case 409: return p2op1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 399: numExp.v = p2op1.v;  break;
      case 404: numExp.v = (double) numExp1.v + (double) p2op1.v;  break;
      case 409: numExp.v = (double) numExp1.v - (double) p2op1.v;  break;
      }
    }
  }

  public class ART_C_p2op extends AbstractAttributeBlock {
    ART_C_p2op p2op = this; Object v; ART_C_p2op p2op1; ART_C_p3op p3op1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 413: p3op1 = new ART_C_p3op(); p3op1.term = term; break;
      case 416: p2op1 = new ART_C_p2op(); p2op1.term = term; break;
      case 418: p3op1 = new ART_C_p3op(); p3op1.term = term; break;
      case 421: p2op1 = new ART_C_p2op(); p2op1.term = term; break;
      case 423: p3op1 = new ART_C_p3op(); p3op1.term = term; break;
      case 426: p2op1 = new ART_C_p2op(); p2op1.term = term; break;
      case 428: p3op1 = new ART_C_p3op(); p3op1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 413: return p3op1;
      case 416: return p2op1;
      case 418: return p3op1;
      case 421: return p2op1;
      case 423: return p3op1;
      case 426: return p2op1;
      case 428: return p3op1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 413: p2op.v = p3op1.v;  break;
      case 418: p2op.v = (double) p2op1.v * (double) p3op1.v;  break;
      case 423: p2op.v = (double) p2op1.v / (double) p3op1.v;  break;
      case 428: p2op.v = (double) p2op1.v % (double) p3op1.v;  break;
      }
    }
  }

  public class ART_C_p3op extends AbstractAttributeBlock {
    ART_C_p3op p3op = this; Object v; ART_C_p4op p4op1; ART_C_p3op p3op1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 432: p4op1 = new ART_C_p4op(); p4op1.term = term; break;
      case 435: p4op1 = new ART_C_p4op(); p4op1.term = term; break;
      case 437: p3op1 = new ART_C_p3op(); p3op1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 432: return p4op1;
      case 435: return p4op1;
      case 437: return p3op1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 432: p3op.v = p4op1.v;  break;
      case 437: p3op.v = Math.pow( (double) p4op1.v, (double) p3op1.v);  break;
      }
    }
  }

  public class ART_C_p4op extends AbstractAttributeBlock {
    ART_C_p4op p4op = this; Object v; ART_C_call call1; ART_C_REAL REAL1; ART_C_statement statement1; ART_C_ID ID1; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 441: REAL1 = new ART_C_REAL(); REAL1.term = term; break;
      case 444: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 447: call1 = new ART_C_call(); call1.term = term; break;
      case 454: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 458: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 460: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 465: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 441: return REAL1;
      case 444: return ID1;
      case 447: return call1;
      case 454: return numExp1;
      case 458: return statement1;
      case 460: return numExp1;
      case 465: return numExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 441: p4op.v = REAL1.v;  break;
      case 444: p4op.v = deref(ID1.v);  break;
      case 447: p4op.v = call1.v;  break;
      case 450:
        String input = reader.nextLine();
        try {p4op.v = Double.parseDouble(input);}
        catch (NumberFormatException e) {p4op.v = input;}
     break;
      case 454: p4op.v = (double) numExp1.v + 1.0;  break;
      case 461: p4op.v = numExp1.v;  break;
      case 466: p4op.v = numExp1.v;  break;
      }
    }
  }

  public class ART_C_print extends AbstractAttributeBlock {
    ART_C_print print = this; ART_C_anyExp anyExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 471: anyExp1 = new ART_C_anyExp(); anyExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 471: return anyExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 471: System.out.println(anyExp1.v);  break;
      }
    }
  }

  public class ART_C_returnn extends AbstractAttributeBlock {
    ART_C_returnn returnn = this; ART_C_anyExp anyExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 476: anyExp1 = new ART_C_anyExp(); anyExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 476: return anyExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 476:
    assign("reservedforreturn", anyExp1.v);
    returned = true;
 break;
      }
    }
  }

  public class ART_C_show extends AbstractAttributeBlock {
    ART_C_show show = this; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 482: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 487: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 482: return numExp1;
      case 487: return numExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 482: 
    plugin("show", "right", numExp1.v);
 break;
      case 487:
    plugin("show", "left", numExp1.v);
 break;
      }
    }
  }

  public class ART_C_sobelOperator extends AbstractAttributeBlock {
    ART_C_sobelOperator sobelOperator = this; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 492: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 492: return numExp1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 492: plugin("sobelOperator", numExp1.v);  break;
      }
    }
  }

  public class ART_C_start extends AbstractAttributeBlock {
    ART_C_start start = this; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 496: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 496: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 496:
    System.out.println("Store: " + store + "\nEnvironments: " + environments);
 break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this; ART_C_init init1; ART_C_invert invert1; ART_C_returnn returnn1; ART_C_iff iff1; ART_C_show show1; ART_C_medianFilter medianFilter1; ART_C_whilee whilee1; ART_C_switchh switchh1; ART_C_call call1; ART_C_print print1; ART_C_define define1; ART_C_forr forr1; ART_C_sobelOperator sobelOperator1; ART_C_assign assign1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 500: assign1 = new ART_C_assign(); assign1.term = term; break;
      case 504: whilee1 = new ART_C_whilee(); whilee1.term = term; break;
      case 507: forr1 = new ART_C_forr(); forr1.term = term; break;
      case 510: iff1 = new ART_C_iff(); iff1.term = term; break;
      case 513: show1 = new ART_C_show(); show1.term = term; break;
      case 517: invert1 = new ART_C_invert(); invert1.term = term; break;
      case 521: medianFilter1 = new ART_C_medianFilter(); medianFilter1.term = term; break;
      case 525: init1 = new ART_C_init(); init1.term = term; break;
      case 529: define1 = new ART_C_define(); define1.term = term; break;
      case 532: call1 = new ART_C_call(); call1.term = term; break;
      case 536: switchh1 = new ART_C_switchh(); switchh1.term = term; break;
      case 539: print1 = new ART_C_print(); print1.term = term; break;
      case 543: sobelOperator1 = new ART_C_sobelOperator(); sobelOperator1.term = term; break;
      case 547: returnn1 = new ART_C_returnn(); returnn1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 500: return assign1;
      case 504: return whilee1;
      case 507: return forr1;
      case 510: return iff1;
      case 513: return show1;
      case 517: return invert1;
      case 521: return medianFilter1;
      case 525: return init1;
      case 529: return define1;
      case 532: return call1;
      case 536: return switchh1;
      case 539: return print1;
      case 543: return sobelOperator1;
      case 547: return returnn1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this; ART_C_statement statement1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 552: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 555: statement1 = new ART_C_statement(); statement1.term = term; break;
      case 556: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 552: return statement1;
      case 555: return statement1;
      case 556: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_switchh extends AbstractAttributeBlock {
    ART_C_switchh switchh = this; ART_C_cases cases1; ART_C_numExp numExp1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 562: numExp1 = new ART_C_numExp(); numExp1.term = term; break;
      case 564: cases1 = new ART_C_cases(); cases1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 562: return numExp1;
      case 564: return cases1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 564: 
    for (int i = 0; i < cases1.listExp.size(); i++) {
        if ((double) numExp1.v == cases1.listExp.get(i)) {
            interpret(cases1.listStmnt.get(i));
            break;
        }
        else if (i == cases1.listExp.size() - 1) {
            interpret(cases1.listStmnt.get(i+1));
        }
    }
 break;
      }
    }
  }

  public class ART_C_whilee extends AbstractAttributeBlock {
    ART_C_whilee whilee = this; ART_C_boolExp boolExp1; ART_C_statements statements1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 569: boolExp1 = new ART_C_boolExp(); boolExp1.term = term; break;
      case 571: statements1 = new ART_C_statements(); statements1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 569: return boolExp1;
      case 571: return statements1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 572:
    scopeUp();
    interpret(boolExp1);
    while (boolExp1.v) {
        interpret(statements1);
        interpret(boolExp1); }
    scopeDown();
 break;
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_start();
    ret.term = term;
    return ret;
  }
}
