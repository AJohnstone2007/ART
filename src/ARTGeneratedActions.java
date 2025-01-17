import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActionsNonterminal;
import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTGeneratedActions extends AbstractActions {
  Map<String, Integer> variables = new HashMap<>();
  Map<String, Integer> procedures = new HashMap<>();

  @Override
  public String name() {
    return "2025-01-16 22:05:33";
  }

  public class ART_C_ID extends AbstractActionsNonterminal {
    String v;
    ART_C_ID ID;

    public void activate(ART_C_ID parent) {
      ID = parent;
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 120:
        ID.v = lexemeCore();
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_INTEGER extends AbstractActionsNonterminal {
    int v;
    ART_C_INTEGER INTEGER;

    public void activate(ART_C_INTEGER parent) {
      INTEGER = parent;
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 124:
        INTEGER.v = Integer.parseInt(lexeme());
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_STRING_SQ extends AbstractActionsNonterminal {
    String v;
    ART_C_STRING_SQ STRING_SQ;

    public void activate(ART_C_STRING_SQ parent) {
      STRING_SQ = parent;
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 128:
        STRING_SQ.v = lexemeCore().translateEscapes();
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_e0 extends AbstractActionsNonterminal {
    int v;
    ART_C_e0 e0;
    ART_C_e1 e11;
    ART_C_e1 e12;

    public void activate(ART_C_e0 parent) {
      e0 = parent;
      e11 = new ART_C_e1();
      e12 = new ART_C_e1();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 132:
        e11.activate(e11);
        return e11;
      case 135:
        e11.activate(e11);
        return e11;
      case 137:
        e12.activate(e12);
        return e12;
      case 140:
        e11.activate(e11);
        return e11;
      case 142:
        e12.activate(e12);
        return e12;
      case 145:
        e11.activate(e11);
        return e11;
      case 147:
        e12.activate(e12);
        return e12;
      case 150:
        e11.activate(e11);
        return e11;
      case 152:
        e12.activate(e12);
        return e12;
      case 155:
        e11.activate(e11);
        return e11;
      case 157:
        e12.activate(e12);
        return e12;
      case 160:
        e11.activate(e11);
        return e11;
      case 162:
        e12.activate(e12);
        return e12;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_e1 extends AbstractActionsNonterminal {
    int v;
    ART_C_e1 e1;
    ART_C_e1 e11;
    ART_C_e2 e21;

    public void activate(ART_C_e1 parent) {
      e1 = parent;
      e11 = new ART_C_e1();
      e21 = new ART_C_e2();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 166:
        e21.activate(e21);
        return e21;
      case 169:
        e11.activate(e11);
        return e11;
      case 171:
        e21.activate(e21);
        return e21;
      case 174:
        e11.activate(e11);
        return e11;
      case 176:
        e21.activate(e21);
        return e21;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_e2 extends AbstractActionsNonterminal {
    int v;
    ART_C_e2 e2;
    ART_C_e2 e21;
    ART_C_e3 e31;

    public void activate(ART_C_e2 parent) {
      e2 = parent;
      e21 = new ART_C_e2();
      e31 = new ART_C_e3();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 180:
        e31.activate(e31);
        return e31;
      case 183:
        e21.activate(e21);
        return e21;
      case 185:
        e31.activate(e31);
        return e31;
      case 188:
        e21.activate(e21);
        return e21;
      case 190:
        e31.activate(e31);
        return e31;
      case 193:
        e21.activate(e21);
        return e21;
      case 195:
        e31.activate(e31);
        return e31;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_e3 extends AbstractActionsNonterminal {
    int v;
    ART_C_e3 e3;
    ART_C_e3 e31;
    ART_C_e4 e41;

    public void activate(ART_C_e3 parent) {
      e3 = parent;
      e31 = new ART_C_e3();
      e41 = new ART_C_e4();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 199:
        e41.activate(e41);
        return e41;
      case 203:
        e31.activate(e31);
        return e31;
      case 207:
        e31.activate(e31);
        return e31;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_e4 extends AbstractActionsNonterminal {
    int v;
    ART_C_e4 e4;
    ART_C_e5 e51;
    ART_C_e4 e41;

    public void activate(ART_C_e4 parent) {
      e4 = parent;
      e51 = new ART_C_e5();
      e41 = new ART_C_e4();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 211:
        e51.activate(e51);
        return e51;
      case 214:
        e51.activate(e51);
        return e51;
      case 216:
        e41.activate(e41);
        return e41;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_e5 extends AbstractActionsNonterminal {
    int v;
    ART_C_e5 e5;
    ART_C_ID ID1;
    ART_C_e1 e11;
    ART_C_INTEGER INTEGER1;

    public void activate(ART_C_e5 parent) {
      e5 = parent;
      ID1 = new ART_C_ID();
      e11 = new ART_C_e1();
      INTEGER1 = new ART_C_INTEGER();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 220:
        INTEGER1.activate(INTEGER1);
        return INTEGER1;
      case 223:
        ID1.activate(ID1);
        return ID1;
      case 227:
        e11.activate(e11);
        return e11;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_elseOpt extends AbstractActionsNonterminal {

    ART_C_elseOpt elseOpt;
    ART_C_statement statement1;

    public void activate(ART_C_elseOpt parent) {
      elseOpt = parent;
      statement1 = new ART_C_statement();
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 233:
        statement1.activate(statement1);
        return statement1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_printElements extends AbstractActionsNonterminal {

    ART_C_printElements printElements;
    ART_C_printElements printElements1;
    ART_C_e0 e01;
    ART_C_STRING_SQ STRING_SQ1;

    public void activate(ART_C_printElements parent) {
      printElements = parent;
      printElements1 = new ART_C_printElements();
      e01 = new ART_C_e0();
      STRING_SQ1 = new ART_C_STRING_SQ();
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

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 240:
        STRING_SQ1.activate(STRING_SQ1);
        return STRING_SQ1;
      case 243:
        STRING_SQ1.activate(STRING_SQ1);
        return STRING_SQ1;
      case 245:
        printElements1.activate(printElements1);
        return printElements1;
      case 248:
        e01.activate(e01);
        return e01;
      case 251:
        e01.activate(e01);
        return e01;
      case 253:
        printElements1.activate(printElements1);
        return printElements1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_statement extends AbstractActionsNonterminal {
    // int e01; int statement1; int elseOpt1;
    ART_C_statement statement;
    ART_C_printElements printElements1;
    ART_C_statement statement1;
    ART_C_statements statements1;
    ART_C_ID ID1;
    ART_C_e0 e01;
    ART_C_elseOpt elseOpt1;

    public void activate(ART_C_statement parent) {
      statement = parent;
      printElements1 = new ART_C_printElements();
      statement1 = new ART_C_statement();
      statements1 = new ART_C_statements();
      ID1 = new ART_C_ID();
      e01 = new ART_C_e0();
      elseOpt1 = new ART_C_elseOpt();
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      case 260:
        variables.put(ID1.v, e01.v);
        break;
      case 267:
        if (e01.v != 0)
          interpret(statement.statement1, statement1);
        else
          interpret(statement.elseOpt1, elseOpt1);
        break;
      case 273:
        interpret(statement.e01, e01);
        while (e01.v != 0) {
          interpret(statement.statement1, statement1);
          interpret(statement.e01, e01);
        }
        break;
      case 285:
        // procedures.put(ID1.v, statement.statement1);
        break;
      case 290:
        // artEvaluate(procedures.get(ID1.v), null);
        break;
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 257:
        ID1.activate(ID1);
        return ID1;
      case 259:
        e01.activate(e01);
        return e01;
      case 264:
        e01.activate(e01);
        return e01;
      case 266:
        statement1.activate(statement1);
        return statement1;
      case 267:
        elseOpt1.activate(elseOpt1);
        return elseOpt1;
      case 271:
        e01.activate(e01);
        return e01;
      case 273:
        statement1.activate(statement1);
        return statement1;
      case 278:
        printElements1.activate(printElements1);
        return printElements1;
      case 284:
        ID1.activate(ID1);
        return ID1;
      case 285:
        statement1.activate(statement1);
        return statement1;
      case 289:
        ID1.activate(ID1);
        return ID1;
      case 294:
        statements1.activate(statements1);
        return statements1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  public class ART_C_statements extends AbstractActionsNonterminal {

    ART_C_statements statements;
    ART_C_statement statement1;
    ART_C_statements statements1;

    public void activate(ART_C_statements parent) {
      statements = parent;
      statement1 = new ART_C_statement();
      statements1 = new ART_C_statements();
    }

    @Override
    public void action(int nodeNumber) {
      switch (nodeNumber) {
      }
    }

    @Override
    public AbstractActionsNonterminal call(int nodeNumber) {
      switch (nodeNumber) {
      case 299:
        statement1.activate(statement1);
        return statement1;
      case 302:
        statement1.activate(statement1);
        return statement1;
      case 303:
        statements1.activate(statements1);
        return statements1;
      default:
        Util.fatal("Unknown call node " + nodeNumber);
        return null;
      }
    }
  }

  @Override
  public AbstractActionsNonterminal init(AbstractInterpreter interpreter) {
    this.interpreter = interpreter;
    var ret = new ART_C_statements();
    ret.activate(null);
    return ret;
  }
}
