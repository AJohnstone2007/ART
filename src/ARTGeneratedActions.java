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
    return "2025-01-21 15:56:22";
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
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 120:
        ID.v = lexemeCore();
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
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 124:
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
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 128:
        STRING_SQ.v = lexemeCore().translateEscapes();
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
      case 132:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 135:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 137:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 140:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 142:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 145:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 147:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 150:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 152:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 155:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 157:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      case 160:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 162:
        e12 = new ART_C_e1();
        e12.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 132:
        return e11;
      case 135:
        return e11;
      case 137:
        return e12;
      case 140:
        return e11;
      case 142:
        return e12;
      case 145:
        return e11;
      case 147:
        return e12;
      case 150:
        return e11;
      case 152:
        return e12;
      case 155:
        return e11;
      case 157:
        return e12;
      case 160:
        return e11;
      case 162:
        return e12;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 132:
        e0.v = e11.v;
        break;
      case 137:
        e0.v = e11.v > e12.v ? 1 : 0;
        break;
      case 142:
        e0.v = e11.v < e12.v ? 1 : 0;
        break;
      case 147:
        e0.v = e11.v >= e12.v ? 1 : 0;
        break;
      case 152:
        e0.v = e11.v <= e12.v ? 1 : 0;
        break;
      case 157:
        e0.v = e11.v == e12.v ? 1 : 0;
        break;
      case 162:
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
      case 166:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 169:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 171:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 174:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      case 176:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 166:
        return e21;
      case 169:
        return e11;
      case 171:
        return e21;
      case 174:
        return e11;
      case 176:
        return e21;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 166:
        e1.v = e21.v;
        break;
      case 171:
        e1.v = e11.v + e21.v;
        break;
      case 176:
        e1.v = e11.v - e21.v;
        break;
      }
    }
  }

  public class ART_C_e2 extends AbstractAttributeBlock {
    ART_C_e2 e2 = this;
    int v;
    ART_C_e2 e21;
    ART_C_e3 e31;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 180:
        e31 = new ART_C_e3();
        e31.term = term;
        break;
      case 183:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 185:
        e31 = new ART_C_e3();
        e31.term = term;
        break;
      case 188:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 190:
        e31 = new ART_C_e3();
        e31.term = term;
        break;
      case 193:
        e21 = new ART_C_e2();
        e21.term = term;
        break;
      case 195:
        e31 = new ART_C_e3();
        e31.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 180:
        return e31;
      case 183:
        return e21;
      case 185:
        return e31;
      case 188:
        return e21;
      case 190:
        return e31;
      case 193:
        return e21;
      case 195:
        return e31;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 180:
        e2.v = e31.v;
        break;
      case 185:
        e2.v = e21.v * e31.v;
        break;
      case 190:
        e2.v = e21.v / e31.v;
        break;
      case 195:
        e2.v = e21.v % e31.v;
        break;
      }
    }
  }

  public class ART_C_e3 extends AbstractAttributeBlock {
    ART_C_e3 e3 = this;
    int v;
    ART_C_e3 e31;
    ART_C_e4 e41;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 199:
        e41 = new ART_C_e4();
        e41.term = term;
        break;
      case 203:
        e31 = new ART_C_e3();
        e31.term = term;
        break;
      case 207:
        e31 = new ART_C_e3();
        e31.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 199:
        return e41;
      case 203:
        return e31;
      case 207:
        return e31;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 199:
        e3.v = e41.v;
        break;
      case 203:
        e3.v = e31.v;
        break;
      case 207:
        e3.v = -e31.v;
        break;
      }
    }
  }

  public class ART_C_e4 extends AbstractAttributeBlock {
    ART_C_e4 e4 = this;
    int v;
    ART_C_e5 e51;
    ART_C_e4 e41;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 211:
        e51 = new ART_C_e5();
        e51.term = term;
        break;
      case 214:
        e51 = new ART_C_e5();
        e51.term = term;
        break;
      case 216:
        e41 = new ART_C_e4();
        e41.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 211:
        return e51;
      case 214:
        return e51;
      case 216:
        return e41;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 211:
        e4.v = e51.v;
        break;
      case 216:
        e4.v = (int) Math.pow(e51.v, e41.v);
        break;
      }
    }
  }

  public class ART_C_e5 extends AbstractAttributeBlock {
    ART_C_e5 e5 = this;
    int v;
    ART_C_ID ID1;
    ART_C_e1 e11;
    ART_C_INTEGER INTEGER1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 220:
        INTEGER1 = new ART_C_INTEGER();
        INTEGER1.term = term;
        break;
      case 223:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 227:
        e11 = new ART_C_e1();
        e11.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 220:
        return INTEGER1;
      case 223:
        return ID1;
      case 227:
        return e11;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 220:
        e5.v = INTEGER1.v;
        break;
      case 223:
        e5.v = variables.get(ID1.v);
        break;
      case 227:
        e5.v = e11.v;
        break;
      }
    }
  }

  public class ART_C_elseOpt extends AbstractAttributeBlock {
    ART_C_elseOpt elseOpt = this;
    ART_C_statement statement1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 233:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 233:
        return statement1;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      }
    }
  }

  public class ART_C_printElements extends AbstractAttributeBlock {
    ART_C_printElements printElements = this;
    ART_C_printElements printElements1;
    ART_C_e0 e01;
    ART_C_STRING_SQ STRING_SQ1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 240:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 243:
        STRING_SQ1 = new ART_C_STRING_SQ();
        STRING_SQ1.term = term;
        break;
      case 245:
        printElements1 = new ART_C_printElements();
        printElements1.term = term;
        break;
      case 248:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 251:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 253:
        printElements1 = new ART_C_printElements();
        printElements1.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 240:
        return STRING_SQ1;
      case 243:
        return STRING_SQ1;
      case 245:
        return printElements1;
      case 248:
        return e01;
      case 251:
        return e01;
      case 253:
        return printElements1;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 240:
        System.out.print(STRING_SQ1.v);
        break;
      case 243:
        System.out.print(STRING_SQ1.v);
        break;
      case 248:
        System.out.print(e01.v);
        break;
      case 251:
        System.out.print(e01.v);
        break;
      }
    }
  }

  public class ART_C_statement extends AbstractAttributeBlock {
    ART_C_statement statement = this;
    ART_C_printElements printElements1;
    ART_C_statement statement1;
    ART_C_statements statements1;
    ART_C_ID ID1;
    ART_C_e0 e01;
    ART_C_elseOpt elseOpt1;

    @Override
    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch (nodeNumber) {
      case 257:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 259:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 264:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 266:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 267:
        elseOpt1 = new ART_C_elseOpt();
        elseOpt1.term = term;
        break;
      case 271:
        e01 = new ART_C_e0();
        e01.term = term;
        break;
      case 273:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 278:
        printElements1 = new ART_C_printElements();
        printElements1.term = term;
        break;
      case 284:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 285:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 289:
        ID1 = new ART_C_ID();
        ID1.term = term;
        break;
      case 294:
        statements1 = new ART_C_statements();
        statements1.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 257:
        return ID1;
      case 259:
        return e01;
      case 264:
        return e01;
      case 266:
        return statement1;
      case 267:
        return elseOpt1;
      case 271:
        return e01;
      case 273:
        return statement1;
      case 278:
        return printElements1;
      case 284:
        return ID1;
      case 285:
        return statement1;
      case 289:
        return ID1;
      case 294:
        return statements1;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 260:
        variables.put(ID1.v, e01.v);
        break;
      case 267:
        if (e01.v != 0)
          interpret(statement1);
        else
          interpret(elseOpt1);
        break;
      case 273:
        interpret(e01);
        while (e01.v != 0) {
          interpret(statement1);
          interpret(e01);
        }
        break;
      case 285:
        procedures.put(ID1.v, statement.statement1);
        break;
      case 290:
        interpret(procedures.get(ID1.v));
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
      case 299:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 302:
        statement1 = new ART_C_statement();
        statement1.term = term;
        break;
      case 303:
        statements1 = new ART_C_statements();
        statements1.term = term;
        break;
      }
    }

    @Override
    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch (nodeNumber) {
      case 299:
        return statement1;
      case 302:
        return statement1;
      case 303:
        return statements1;
      default:
        Util.fatal("getAttributes: unknown node " + nodeNumber);
        return null;
      }
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
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
