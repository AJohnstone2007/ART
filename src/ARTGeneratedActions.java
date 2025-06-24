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
    return "2025-03-26 18:19:56";
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
      case 70:
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
      case 74:
        INTEGER.v = Integer.parseInt(lexeme());
        break;
      }
    }
  }

  public class ART_C_STRING_SQ extends AbstractAttributeBlock {
    ART_C_STRING_SQ STRING_SQ = this;
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
      case 78:
        STRING_SQ.v = lexeme().translateEscapes();
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
      case 82:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 85:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 87:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 90:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 92:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 82:
        return e11;
      case 85:
        return e11;
      case 87:
        return e12;
      case 90:
        return e11;
      case 92:
        return e12;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 82:
        e0.v = e11.v;
        break;
      case 87:
        e0.v = e11.v > e12.v ? 1 : 0;
        break;
      case 92:
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
      case 96:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 99:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 101:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 96:
        return e21;
      case 99:
        return e11;
      case 101:
        return e21;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 96:
        e1.v = e21.v;
        break;
      case 101:
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
    ART_C_e1 e11;
    ART_C_STRING_SQ STRING_SQ1;
    ART_C_INTEGER INTEGER1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 105:
        INTEGER1 = new ART_C_INTEGER();
        INTEGER1.term = term;
        break;
      case 108:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 112:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 118:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 120:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 105:
        return INTEGER1;
      case 108:
        return ID1;
      case 112:
        return e11;
      case 118:
        return STRING_SQ1;
      case 120:
        return e01;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 105:
        e2.v = INTEGER1.v;
        break;
      case 108:
        e2.v = variables.get(ID1.v);
        break;
      case 112:
        e2.v = e11.v;
        break;
      case 121:
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
      case 125:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 127:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 132:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 134:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 136:
        statement2 = new ART_C_statement();
        statement2.term = term;
        break;
      case 140:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 142:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 147:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 154:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 156:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 163:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 165:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 167:
        e02 = new ART_C_e0();
        e02.term = term;
        break;
      case 174:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 176:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 178:
        e02 = new ART_C_e0();
        e02.term = term;
        break;
      case 180:
        e03 = new ART_C_e0();
        e03.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 125:
        return ID1;
      case 127:
        return e01;
      case 132:
        return e01;
      case 134:
        return statement1;
      case 136:
        return statement2;
      case 140:
        return e01;
      case 142:
        return statement1;
      case 147:
        return STRING_SQ1;
      case 154:
        return STRING_SQ1;
      case 156:
        return e01;
      case 163:
        return STRING_SQ1;
      case 165:
        return e01;
      case 167:
        return e02;
      case 174:
        return STRING_SQ1;
      case 176:
        return e01;
      case 178:
        return e02;
      case 180:
        return e03;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 128:
        variables.put(ID1.v, e01.v);
        break;
      case 136:
        if (e01.v != 0)
          interpret(statement1);
        else
          interpret(statement2);
        break;
      case 142:
        interpret(e01);
        while (e01.v != 0) {
          interpret(statement1);
          interpret(e01);
        }
        break;
      case 149:
        plugin(STRING_SQ1.v);
        break;
      case 158:
        plugin(STRING_SQ1.v, e01.v);
        break;
      case 169:
        plugin(STRING_SQ1.v, e01.v, e02.v);
        break;
      case 182:
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
      case 186:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 187:
        statements1 = new ART_C_statements();
        statements1.term = term;
        break;
      case 190:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 186:
        return statement1;
      case 187:
        return statements1;
      case 190:
        return statement1;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile");
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 190:
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
