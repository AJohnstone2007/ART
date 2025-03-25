import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTGeneratedActions extends AbstractActions {
  Map<String, Integer> variables = new HashMap<>();
  Map<String, AbstractAttributeBlock> procedures = new HashMap<>();

  @Override
  public String name() {
    return "2025-03-23 13:35:07";
  }

  public class ART_C_ID extends AbstractAttributeBlock {
    ART_C_ID ID = this;
    String v;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 71:
        ID.v = lexeme();
        break;
      }
    }
  }

  public class ART_C_INTEGER extends AbstractAttributeBlock {
    ART_C_INTEGER INTEGER = this;
    int v;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 75:
        INTEGER.v = Integer.parseInt(lexeme());
        break;
      }
    }
  }

  public class ART_C_STRING_SQ extends AbstractAttributeBlock {
    ART_C_STRING_SQ STRING_SQ = this;
    int v;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 79:
        STRING_SQ.v = 1;
        break;
      }
    }
  }

  public class ART_C_e0 extends AbstractAttributeBlock {
    ART_C_e0 e0 = this;
    int v;
    ART_C_e1 e11;
    ART_C_e1 e12;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 83:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 86:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 88:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 91:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 93:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 83:
        return e11;
      case 86:
        return e11;
      case 88:
        return e12;
      case 91:
        return e11;
      case 93:
        return e12;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 83:
        e0.v = e11.v;
        break;
      case 88:
        e0.v = e11.v > e12.v ? 1 : 0;
        break;
      case 93:
        e0.v = e11.v != e12.v ? 1 : 0;
        break;
      }
    }
  }

  public class ART_C_e1 extends AbstractAttributeBlock {
    ART_C_e1 e1 = this;
    int v;
    ART_C_e1 e11;
    ART_C_e2 e21;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 97:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 100:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 102:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 97:
        return e21;
      case 100:
        return e11;
      case 102:
        return e21;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 97:
        e1.v = e21.v;
        break;
      case 102:
        e1.v = e11.v - e21.v;
        break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this;
    int v;
    ART_C_ID ID1;
    ART_C_e0 e01;
    ART_C_STRING_SQ STRING_SQ1;
    ART_C_e1 e11;
    ART_C_INTEGER INTEGER1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 106:
        INTEGER1 = new ART_C_INTEGER();
        INTEGER1.term = term;
        break;
      case 109:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 112:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 116:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 122:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 124:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 106:
        return INTEGER1;
      case 109:
        return STRING_SQ1;
      case 112:
        return ID1;
      case 116:
        return e11;
      case 122:
        return STRING_SQ1;
      case 124:
        return e01;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 106:
        e2.v = INTEGER1.v;
        break;
      case 109:
        e2.v = STRING_SQ1.v;
        break;
      case 112:
        e2.v = variables.get(ID1.v);
        break;
      case 116:
        e2.v = e11.v;
        break;
      case 125:
        e2.v = (int) plugin(STRING_SQ1.v, e01.v);
        break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this;
    ART_C_statement statement1;
    ART_C_statement statement2;
    ART_C_ID ID1;
    ART_C_e0 e01;
    ART_C_e0 e02;
    ART_C_e0 e03;
    ART_C_STRING_SQ STRING_SQ1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 129:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 131:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 136:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 138:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 140:
        statement2 = new ART_C_statement();
        statement2.term = term;
        break;
      case 144:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 146:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 151:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 158:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 160:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 167:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 169:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 171:
        e02 = new ART_C_e0();
        e02.term = term;
        break;
      case 178:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 180:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 182:
        e02 = new ART_C_e0();
        e02.term = term;
        break;
      case 184:
        e03 = new ART_C_e0();
        e03.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 129:
        return ID1;
      case 131:
        return e01;
      case 136:
        return e01;
      case 138:
        return statement1;
      case 140:
        return statement2;
      case 144:
        return e01;
      case 146:
        return statement1;
      case 151:
        return STRING_SQ1;
      case 158:
        return STRING_SQ1;
      case 160:
        return e01;
      case 167:
        return STRING_SQ1;
      case 169:
        return e01;
      case 171:
        return e02;
      case 178:
        return STRING_SQ1;
      case 180:
        return e01;
      case 182:
        return e02;
      case 184:
        return e03;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 132:
        variables.put(ID1.v, e01.v);
        break;
      case 140:
        if (e01.v != 0)
          interpret(statement1);
        else
          interpret(statement2);
        break;
      case 146:
        interpret(e01);
        while (e01.v != 0) {
          interpret(statement1);
          interpret(e01);
        }
        break;
      case 153:
        plugin(STRING_SQ1.v);
        break;
      case 162:
        plugin(STRING_SQ1.v, e01.v);
        break;
      case 173:
        plugin(STRING_SQ1.v, e01.v, e02.v);
        break;
      case 186:
        plugin(STRING_SQ1.v, e01.v, e02.v, e03.v);
        break;
      }
    }
  }

  public class ART_C_statements extends AbstractAttributeBlock {
    ART_C_statements statements = this;
    ART_C_statement statement1;
    ART_C_statements statements1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 190:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 191:
        statements1 = new ART_C_statements();
        statements1.term = term;
        break;
      case 194:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 190:
        return statement1;
      case 191:
        return statements1;
      case 194:
        return statement1;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 194:
        System.out.println("Final variable map " + variables);
        break;
      }
    }
  }

  @Override
  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_statements();
    ret.term = term;
    return ret;
  }
}
