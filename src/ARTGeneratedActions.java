import uk.ac.rhul.cs.csle.art.interpret.AbstractInterpreter;
import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.interpret.AbstractAttributeBlock;
import uk.ac.rhul.cs.csle.art.util.Util;
 
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ARTGeneratedActions extends AbstractActions {
 
  Map<String, Map<String, String>> macros = new HashMap<>();
  Map<String, String> currentMacro, emptyMap = new HashMap<>();
  Set<String> macrosInUse = new HashSet<>();
  String outputFilename = "amp.out";
  PrintStream out;

  public ARTGeneratedActions() {
    try {
      out = new PrintStream(outputFilename);
    } catch (FileNotFoundException e) {
      fatal("Unable to open output file " + outputFilename);
    }
  }

  void fatal(String str) {
    System.err.println(str);
    System.exit(1);
  }

  String expand(String str, Map<String, String> parameterBindings) {
    // Util.info("Expanding " + str + " " + parameterBindings);
    StringBuilder sb = new StringBuilder(str);
    while (true) {
      int start = sb.indexOf("^");
      if (start == -1) return sb.toString(); // No instances left
      int end = start + 1;
      while (end < sb.length() && Character.isJavaIdentifierPart(sb.charAt(end)))
        end++;
      String id = sb.substring(start + 1, end);

      String argument = parameterBindings.get(id);
      if (argument != null)
        sb.replace(start, end, argument);
      else { // Collect any arguments following the macro
        LinkedList<String> arguments = new LinkedList<>();
        if (end < sb.length()) {
          char c = sb.charAt(end);

          while (end < sb.length() && sb.charAt(end) == '{') {
            int argumentStart = end + 1;
            while (end < sb.length() && sb.charAt(end) != '}')
              end++;
            if (sb.charAt(end) != '}') fatal("Instance " + id + "unterminated argument");
            String arg = sb.substring(argumentStart, end);
            // Util.debug("Found argument " + arg);
            arguments.add(arg);
            end++; // skip terminating }
          }
        }
        if (macros.keySet().contains(id))
          sb.replace(start, end, expand(id, arguments));
        else
          switch (id) {
          case "NOW":
            sb.replace(start, end, Util.timestamp());
            break;
          case "PAR":
            String arg = arguments.getFirst();
            String exp = expand(arg, new HashMap<String, String>());
            String rep = exp.replaceAll("\r?\n\r?\n", "\n</P>\n<P>");

            sb.replace(start, end, "<P>" + rep + "</P>");
            break;
          case "OUT":
            sb.replace(start, end, "Redirect output");
            break;
          default:
            fatal("Macro " + "'^" + id + argumentsToString(arguments) + "' not defined");
          }
      }
    }
  }

  String expand(String id, LinkedList<String> arguments) {
    if (macrosInUse.contains(id)) fatal("^" + id + argumentsToString(arguments) + " macro already in us - recursive macro calls not allowed");
    macrosInUse.add(id);
    if (macros.get(id).size() != arguments.size() + 1) // Add one for the body definition
      fatal("^" + id + argumentsToString(arguments) + " incorrect arity " + arguments.size() + ": expecting " + (macros.get(id).size() - 1));

    Map<String, String> parameterBindings = new HashMap<>();
    int i = 0;
    for (var m : macros.get(id).keySet())
      if (!m.equals("")) parameterBindings.put(m, arguments.get(i++));
    macrosInUse.remove(id);
    return expand(macros.get(id).get(""), parameterBindings);
  }

  String argumentsToString(LinkedList<String> arguments) {
    StringBuilder sb = new StringBuilder();
    for (var a : arguments)
      sb.append("{" + a + "}");
    return sb.toString();
  }

  public String name() { return "2025-11-12 20:29:49"; }

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
      case 54: ID.v= lexeme();  break;
      }
    }
  }

  public class ART_C_STRING_BRACE_NEST extends AbstractAttributeBlock {
    ART_C_STRING_BRACE_NEST STRING_BRACE_NEST = this; String v;

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
      case 58: STRING_BRACE_NEST.v = lexeme().translateEscapes();  break;
      }
    }
  }

  public class ART_C_arguments extends AbstractAttributeBlock {
    ART_C_arguments arguments = this; LinkedList<String> v; ART_C_STRING_BRACE_NEST STRING_BRACE_NEST1; ART_C_arguments arguments1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 62: STRING_BRACE_NEST1 = new ART_C_STRING_BRACE_NEST(); STRING_BRACE_NEST1.term = term; break;
      case 63: arguments1 = new ART_C_arguments(); arguments1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 62: return STRING_BRACE_NEST1;
      case 63: return arguments1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 62: arguments.v.add(STRING_BRACE_NEST1.v); arguments1.v = arguments.v;  break;
      }
    }
  }

  public class ART_C_define extends AbstractAttributeBlock {
    ART_C_define define = this; ART_C_paramatersOpt paramatersOpt1; ART_C_STRING_BRACE_NEST STRING_BRACE_NEST1; ART_C_ID ID1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 71: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 72: paramatersOpt1 = new ART_C_paramatersOpt(); paramatersOpt1.term = term; break;
      case 73: STRING_BRACE_NEST1 = new ART_C_STRING_BRACE_NEST(); STRING_BRACE_NEST1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 71: return ID1;
      case 72: return paramatersOpt1;
      case 73: return STRING_BRACE_NEST1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 71: macros.put(ID1.v, new LinkedHashMap<>()); currentMacro = macros.get(ID1.v);  break;
      case 73: currentMacro.put("", STRING_BRACE_NEST1.v);  break;
      }
    }
  }

  public class ART_C_instance extends AbstractAttributeBlock {
    ART_C_instance instance = this; ART_C_arguments arguments1; ART_C_ID ID1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 78: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 79: arguments1 = new ART_C_arguments(); arguments1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 78: return ID1;
      case 79: return arguments1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 78: arguments1.v = new LinkedList<String>();  break;
      case 79: out.println(expand(ID1.v, arguments1.v));  break;
      }
    }
  }

  public class ART_C_mainline extends AbstractAttributeBlock {
    ART_C_mainline mainline = this; String str; ART_C_text text1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 83: text1 = new ART_C_text(); text1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 83: return text1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_paramatersOpt extends AbstractAttributeBlock {
    ART_C_paramatersOpt paramatersOpt = this; ART_C_parameters parameters1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 88: parameters1 = new ART_C_parameters(); parameters1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 88: return parameters1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_parameters extends AbstractAttributeBlock {
    ART_C_parameters parameters = this; ART_C_ID ID1; ART_C_parameters parameters1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 96: ID1 = new ART_C_ID(); ID1.term = term; break;
      case 97: parameters1 = new ART_C_parameters(); parameters1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 96: return ID1;
      case 97: return parameters1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 96: currentMacro.put(ID1.v, null);  break;
      }
    }
  }

  public class ART_C_text extends AbstractAttributeBlock {
    ART_C_text text = this; ART_C_instance instance1; ART_C_define define1; ART_C_text text1; ART_C_undefine undefine1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 104: undefine1 = new ART_C_undefine(); undefine1.term = term; break;
      case 105: text1 = new ART_C_text(); text1.term = term; break;
      case 108: define1 = new ART_C_define(); define1.term = term; break;
      case 109: text1 = new ART_C_text(); text1.term = term; break;
      case 112: instance1 = new ART_C_instance(); instance1.term = term; break;
      case 113: text1 = new ART_C_text(); text1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 104: return undefine1;
      case 105: return text1;
      case 108: return define1;
      case 109: return text1;
      case 112: return instance1;
      case 113: return text1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_undefine extends AbstractAttributeBlock {
    ART_C_undefine undefine = this; ART_C_ID ID1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 121: ID1 = new ART_C_ID(); ID1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 121: return ID1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 121: macros.remove(ID1.v);  break;
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_mainline();
    ret.term = term;
    return ret;
  }
}
