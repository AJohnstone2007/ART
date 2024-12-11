package uk.ac.rhul.cs.csle.alero.language;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

import uk.ac.rhul.cs.csle.adl.ADLException;
import uk.ac.rhul.cs.csle.alero.Alero;
import uk.ac.rhul.cs.csle.alero.AleroException;
import uk.ac.rhul.cs.csle.art.cfg.ParserBase;
import uk.ac.rhul.cs.csle.art.cfg.gll.GLLBaseLine;
import uk.ac.rhul.cs.csle.art.cfg.lexer.LexerSingletonLongestMatch;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.ITermsLowLevelAPI;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.ValueException;
import uk.ac.rhul.cs.csle.art.term.__char;
import uk.ac.rhul.cs.csle.art.term.__empty;
import uk.ac.rhul.cs.csle.art.term.__int32;
import uk.ac.rhul.cs.csle.art.term.__list;
import uk.ac.rhul.cs.csle.art.term.__mapChain;
import uk.ac.rhul.cs.csle.art.term.__mesh;
import uk.ac.rhul.cs.csle.art.term.__proc;
import uk.ac.rhul.cs.csle.art.term.__quote;
import uk.ac.rhul.cs.csle.art.term.__real64;
import uk.ac.rhul.cs.csle.art.term.__string;
import uk.ac.rhul.cs.csle.art.term.mesh.AleroMesh;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ADLInterpreter {
  private final ITerms iTerms;
  private final LexerSingletonLongestMatch adlLexer;
  private final ParserBase adlParser;
  Scanner keyboard = new Scanner(System.in);
  public __mapChain topLevelSymbols = new __mapChain();

  public static void mainx(String[] args) {
    System.out.println("ADL0_Alero standalone interpreter");
    ADLInterpreter adlInterpreter = new ADLInterpreter(new ITermsLowLevelAPI());

    if (args[0] != null) try {
      System.out.print(args[0] + ": ");
      adlInterpreter.print(System.out, adlInterpreter.adlInterpret(Files.readString(Paths.get(args[0]))));
    } catch (IOException e) {
      Util.fatal("Unable to read ADL input file " + args[0]);
    }
    while (true) {
      System.out.print("\n> ");
      String input = adlInterpreter.keyboard.nextLine();
      if (input.length() == 0) {
        System.out.print("Exit");
        System.exit(0);
      }
      adlInterpreter.print(System.out, adlInterpreter.adlInterpret(input));
    }
  }

  public ADLInterpreter(ITerms iTerms) {
    this.iTerms = iTerms;
    ScriptTermInterpreter artScriptInterpreter = new ScriptTermInterpreter(iTerms);
    try {
      artScriptInterpreter
          .interpretARTScript(new String(getClass().getClassLoader().getResourceAsStream("uk/ac/rhul/cs/csle/alero/language/ADL0_Alero.art").readAllBytes()));
    } catch (IOException e) {
      Util.fatal("Unable to read ADL specification");
    }
    adlLexer = new LexerSingletonLongestMatch();
    adlParser = new GLLBaseLine();
    adlParser.cfgRules = artScriptInterpreter.currentCFGRules;
    adlParser.cfgRules.normalise();
  }

  public Value adlInterpret(String inputString) {
    try {
      adlParser.inLanguage = false;
      adlLexer.lex(inputString, adlParser.cfgRules.lexicalKindsArray(), adlParser.cfgRules.lexicalStringsArray(), adlParser.cfgRules.whitespacesArray());

      if (adlLexer.tokens != null) {
        adlParser.inputString = inputString;
        adlParser.input = adlLexer.tokens;
        adlParser.positions = adlLexer.positions;
        adlParser.parse();
      }

      if (adlParser.inLanguage) {
        adlParser.chooseLongestMatch();
        int derivationTerm = adlParser.derivationAsTerm();
        System.out.println("ADL derivation: " + iTerms.toString(derivationTerm, true, -1, null));
        return adlInterpretRec(derivationTerm, topLevelSymbols);
      }
      throw new AleroException("Syntax error");
    } catch (

    ValueException e) {
      Alero.consoleln(e.getMessage());
      return new __empty();
    }
  }

  private Value adlInterpretRec(int term, __mapChain env) {
    System.out.println("adlInterpretRec at " + iTerms.toString(term));
    String termSymbolString = iTerms.termSymbolString(term);
    int children[] = iTerms.termChildren(term);
    Value ret; // temporary which is used in some switch cases
  //@formatter:off
    switch (termSymbolString) { // constructors that do their own subtree handling
    case "true": return iTerms.valueBoolTrue;
    case "false": return iTerms.valueBoolFalse;
    case "__char": return new __char(term);
    case "__int32": return new __int32(term);
    case "__real64": return new __real64(term);
    case "__string": return new __string(term);
    case "skip": return iTerms.valueDone;
    case "adl": return iTerms.valueEmpty; // Special case - only appears in derivation if the interpeted string is empty or has only whitespace
    case "list": ret = new __list(); for (int c : children) ret.__put(adlInterpretRec(c, env)); return ret;
    case "fix": return env.__put(new __quote(iTerms.subterm(children[0], 0)), ret = adlInterpretRec(children[1], env), true);
    case "assign": return env.__put(new __quote(iTerms.subterm(children[0], 0)), ret = adlInterpretRec(children[1], env), false);
    case "derefValue": return env.__get(new __quote(children[0]));
    case "derefBinding": return new __quote(children[0]);
    case "lambda": return createProcedure(env, children[0], children[1]);
    case "syscall": return systemCall(adlInterpretRec(children[0],env), adlInterpretRec(children[1],env));
    }

    Value left = children.length > 0 ? adlInterpretRec(children[0], env) : null;
    Value right = children.length > 1 ? adlInterpretRec(children[1], env) : null;
    switch (termSymbolString) {  // constructors that use left and right variables
    case "seq": if (left instanceof __proc) return applyProcedure(left, right, env, children); else return right;
    case "rotate": var mesh = (AleroMesh) left.javaValue(); var r = collectVector3(right); mesh.rotateX(r[0]); mesh.rotateY(r[1]); mesh.rotateZ(r[2]); return left;
    case "or": return left.__or(right);
    case "xor": return left.__xor(right);
    case "and": return left.__and(right);
    case "eq": return left.__eq(right);
    case "ne": return left.__ne(right);
    case "ge": return left.__ge(right);
    case "gt": return left.__gt(right);
    case "le": return left.__le(right);
    case "lt": return left.__lt(right);
    case "cat": return left.__cat(right);
    case "lsh": return left.__lsh(right);
    case "rsh": return left.__rsh(right);
    case "rol": return left.__rol(right);
    case "ror": return left.__ror(right);
    case "ash": return left.__ash(right);
    case "add": if (left instanceof __mesh && right instanceof __mesh) return left.__union(right);
                if (left instanceof __mesh) { var s = collectVector3(right, true); ((AleroMesh) left.javaValue()).scale(s[0], s[1], s[2]); }
                return left.__add(right);
    case "sub": if (left instanceof __mesh && right instanceof __mesh) return left.__difference(right); else return left.__sub(right);
    case "mul": if (left instanceof __mesh && right instanceof __mesh) return left.__intersection(right); else return left.__mul(right);
    case "div": return left.__div(right);
    case "mod": return left.__mod(right);
    case "exp": return left.__exp(right);
    case "pos": return left.__pos();
    case "neg": return left.__neg();
    case "not": if (left instanceof __mesh) { Alero.addMesh((AleroMesh) left.javaValue()); return left; } else return left.__not();
    }
    throw new AleroException("in ADL derivation term, unimplemented constructor '" + iTerms.termSymbolString(term) + "'");
  //@formatter:on
  }

  private Value createProcedure(__mapChain env, int parameterTerm, int bodyTerm) {
    System.out.println("Create procedure");
    LinkedHashMap<Value, Value> parameters = new LinkedHashMap<>();
    for (int i : iTerms.termChildren(parameterTerm)) {// work our way through the bindings in the map under children[0]
      System.out.println("Processing parameter " + iTerms.toString(i));
      Value key = adlInterpretRec(iTerms.subterm(i, 0), env);
      System.out.println("key is  " + key);

      Value boundValue = iTerms.termArity(i) == 1 ? iTerms.valueEmpty : adlInterpretRec(iTerms.subterm(i, 0, 1), env);
      System.out.println("boundValue is  " + boundValue);

      parameters.put(key, boundValue);
    }

    var ret = new __proc(parameters, bodyTerm);
    System.out.println("Returning " + ret);
    return ret;
  }

  // Watch out for functions that return functions, and thus need repeated application work
  private Value applyProcedure(Value left, Value right, __mapChain env, int[] children) {
    System.out.println("Apply " + left + " to " + right);
    Value ret = iTerms.valueEmpty;
    for (int c : children) {
      if (ret instanceof __proc) { // ret is result of previous child
        // System.out.println("Applying procedure");
        __mapChain callEnv = new __mapChain(env);
        Value argument = adlInterpretRec(c, env);
        Iterator<Value> parameterIterator = ((__proc) ret).getParameters().keySet().iterator();

        if (argument instanceof __list) {
          Iterator<Value> argumentIterator = ((__list) argument).javaValue().iterator();
          while (argumentIterator.hasNext())
            callEnv.__put(parameterIterator.next(), argumentIterator.next());
        } else
          callEnv.__put(parameterIterator.next(), argument); // singleton case

        // System.out.println("apply " + ret + " with argument environment " + callEnv.toStringLocal());

        ret = adlInterpretRec(((__proc) ret).getBodyTerm(), callEnv);
      } else
        ret = adlInterpretRec(c, env);
    }
    return ret; // Result of expression is the last thing computed
  }

  private Value systemCall(Value opcode, Value argument) {
    System.out.println("System call with opcode " + opcode + " and argument " + argument);
    if (!(opcode instanceof __int32)) throw new AleroException("First argument of sys must be an integer");
    int oc = ((__int32) opcode).javaValue;
    switch (oc) {
    case 0:
      throw new AleroException("Plugin access not yet implemented"); // Plugin
    case 1:
      Alero.tw.printConsole("Sys 1: " + argument + "\n");
      return iTerms.valueDone;
    // case 1:
    // return print(System.out, argument);
    case 2:
      return print(System.err, argument);
    case 3:
      return new __string(keyboard.nextLine());
    default:
      throw new AleroException("Illegal system opcode " + oc);
    }
  }

  /*
   * This convenience method is used to collect a vector (x,y,z) represented as float ret[3] from a value
   *
   * The value might be a __list, in which case if it is of length 3 then we use it all, <3 we pad fron the left and >3 we throw an error
   *
   * The value might be a mumber, in which case we put it in the z position and pad with zeroes
   */
  private float[] collectVector3(Value value) {
    return collectVector3(value, false);
  }

  private float[] collectVector3(Value value, boolean fill) {
    float[] ret = new float[3];
    if (value instanceof __list) {
      @SuppressWarnings("unchecked")
      LinkedList<Value> jl = (LinkedList<Value>) value.javaValue();
      switch (jl.size()) {
      case 3:
        ret[0] = valueAsFloat(jl.get(0));
        ret[1] = valueAsFloat(jl.get(1));
        ret[2] = valueAsFloat(jl.get(2));
        break;

      case 2:
        ret[0] = valueAsFloat(jl.get(0));
        ret[1] = valueAsFloat(jl.get(1));
        ret[2] = 0;
        break;

      case 1:
        ret[0] = valueAsFloat(jl.get(0));
        ret[1] = fill ? ret[0] : 0;
        ret[2] = fill ? ret[0] : 0;
        break;

      default:
        throw new ADLException("Collect vector expects a list of length 1, 2 or 3");
      }
    } else {
      ret[0] = valueAsFloat(value);
      ret[1] = fill ? ret[0] : 0;
      ret[2] = fill ? ret[0] : 0;
    }

    return ret;
  }

  private float valueAsFloat(Value value) {
    if (value instanceof __real64) return (float) value.javaValue();
    if (value instanceof __int32) {
      int ret1 = (int) value.javaValue();
      float ret = ret1;
      return ret;
    }
    throw new ADLException("Expected an __int32 or a __real64; found a " + value.getClass().getSimpleName());
  }

  private Value print(PrintStream ps, Value argument) {
    LinkedList<Value> list = argument instanceof __list ? ((__list) argument).javaValue() : null;
    if (list == null)
      ps.print(argument.toValueString());
    else
      for (Value v : list)
        System.out.print(v.toValueString());
    return iTerms.valueEmpty;
  }
}
