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
import java.util.Stack;

public class ARTGeneratedActions extends AbstractActions {
 
  Map<String, Map<String, String>> macros = new HashMap<>();
  Stack<String> macrosInUse = new Stack<>();
  Set<String> macrosNestable = new HashSet<>();
  String currentMacroName;
  Map<String, String> currentMacro;
  String expansionString;
  boolean trace = true;

  void fatal(String str) {
    System.err.println(str);
    System.exit(1);
  }

  String argumentsToString(LinkedList<String> arguments) {
    StringBuilder sb = new StringBuilder();
    for (var a : arguments)
      sb.append("{" + a + "}");
    return sb.toString();
  }

  void expand(PrintStream printStream, String str, Map<String, String> parameterBindings) {
    printStream.print(expand(str, parameterBindings));
  }

  String expand(String str, Map<String, String> parameterBindings) {
    StringBuilder sb = new StringBuilder(str);
    while (true) {
      if (trace) System.err.println("Expanding " + str);
    
      // 1. Find first instance
      int start = sb.indexOf("^");
      if (start == -1) {
        if (trace) System.err.println("Returning " + sb);
        return sb.toString(); // No instances left; nothing to do
      }
      // 2. Collect instance id
      int end = start + 1;
      while (end < sb.length() && Character.isJavaIdentifierPart(sb.charAt(end)))
        end++;
      String id = sb.substring(start + 1, end);
      if (trace) System.err.println("Found macro '^" + id + "' at index " + start + " " + sb.substring(start));

      // 3. Check to see if this instance is a parameter; if so simple replacement as there can be no parameters to a parameter
      String replacement = parameterBindings == null ? null : parameterBindings.get(id);
      if (replacement != null) {
        if (trace) System.err.println(id + "is a parameter, so replacing parameter with binding " + replacement);
        sb.replace(start, end, replacement);
        continue;
      }

      // 4. Collect any instance arguments
      LinkedList<String> arguments = new LinkedList<>();
      if (end < sb.length()) {
        char c = sb.charAt(end);

        while (end < sb.length() && sb.charAt(end) == '{') { // Iterate over repeated arguments
          int argumentStart = end + 1;

          int nestLevel = 0;
          do {
            if (end >= sb.length()) fatal("^" + id + " unterminated argument");
            if (sb.charAt(end) == '{') nestLevel++;
            if (sb.charAt(end) == '}') nestLevel--;
            if (sb.charAt(end) == '\\') end++; // Allow escapes
            end++;
          } while (nestLevel > 0);

          String arg = sb.substring(argumentStart, end - 1); // Pull out the 'lexeme'
          // Util.debug("Found argument " + arg);
          arguments.add(arg);
        }
      }

      // 5. Check for system macros
      switch (id) {
      case "OUT": {
        String filename = "???";
        try { expand(new PrintStream(filename = expand(arguments.get(0), parameterBindings)), arguments.get(1), parameterBindings);
        } catch (FileNotFoundException e) { fatal("Unable to open output file: " + filename); }
        sb.replace(start, end, "");
        } continue;
      case "NOW":
        sb.replace(start, end, Util.timestamp());
        continue;
      case "PAR": {
        String arg = arguments.getFirst();
        String exp = expand(arg, parameterBindings);
        String rep = exp.replaceAll("\r?\n\r?\n", "</p>\n<p>");
        sb.replace(start, end, "<p>" + rep + "</p>");
        } continue;
      case "HAT":{
        sb.replace(start, end, "^" + expand(arg, parameterBindings));      
        } continue;
      }

      // 6. Check for undefined macro, recursive instance and arity match
      if (!macros.keySet().contains(id)) fatal("Macro " + "'^" + id + argumentsToString(arguments) + "' not defined");
      if (!macrosNestable.contains(id) && macrosInUse.contains(id)) fatal("Recursive macro call ^" + id + "\nMacro call stack " + macrosInUse + "\nNestable macros" + macrosNestable);
      macrosInUse.push(id);
      if (macros.get(id).size() != arguments.size() + 1) // Add one for the body definition
        fatal("^" + id + argumentsToString(arguments) + " incorrect arity " + arguments.size() + ": expecting " + (macros.get(id).size() - 1));

      // 7. Bind expanded arguments to macro parameters - note we do not allow recursion so only one set of bindings is sufficient
      Map<String, String> newParameterBindings = new HashMap<>();
      int i = 0;
      for (var m : macros.get(id).keySet())
        if (!m.equals("")) newParameterBindings.put(m, expand(arguments.get(i++), parameterBindings)); // Last element keyed on "" is the macro body, not a
                                                                                                       // parameter

      // 8. Expand instance, and then go round again...
      replacement = expand(macros.get(id).get(""), newParameterBindings);
      sb.replace(start, end, replacement);
    
      macrosInUse.pop();
    }
  }

  public String name() { return "2026-02-05 21:15:30"; }

  public class ART_C_argumentsOpt extends AbstractAttributeBlock {
    ART_C_argumentsOpt argumentsOpt = this; ART_C_argumentsOpt argumentsOpt1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 50: argumentsOpt1 = new ART_C_argumentsOpt(); argumentsOpt1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 50: return argumentsOpt1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 49: expansionString += "{" + lexeme() + "}";  break;
      }
    }
  }

  public class ART_C_define extends AbstractAttributeBlock {
    ART_C_define define = this; ART_C_paramatersOpt paramatersOpt1; ART_C_starOpt starOpt1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 55: starOpt1 = new ART_C_starOpt(); starOpt1.term = term; break;
      case 56: paramatersOpt1 = new ART_C_paramatersOpt(); paramatersOpt1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 55: return starOpt1;
      case 56: return paramatersOpt1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 54: currentMacroName = lexeme(); 
                         if(macros.keySet().contains(currentMacroName)) fatal("Doubly defined macro " + currentMacroName);
                         macros.put(currentMacroName, new LinkedHashMap<String, String>()); currentMacro = macros.get(lexeme());  break;
      case 57: currentMacro.put("", lexeme());  break;
      }
    }
  }

  public class ART_C_expand extends AbstractAttributeBlock {
    ART_C_expand expand = this; ART_C_argumentsOpt argumentsOpt1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 63: argumentsOpt1 = new ART_C_argumentsOpt(); argumentsOpt1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 63: return argumentsOpt1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 62: expansionString = "^" + lexeme();  break;
      case 63: expand(System.out, expansionString, null);  break;
      }
    }
  }

  public class ART_C_paramatersOpt extends AbstractAttributeBlock {
    ART_C_paramatersOpt paramatersOpt = this; ART_C_parameters parameters1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 71: parameters1 = new ART_C_parameters(); parameters1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 71: return parameters1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public class ART_C_parameters extends AbstractAttributeBlock {
    ART_C_parameters parameters = this; ART_C_parameters parameters1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 77: parameters1 = new ART_C_parameters(); parameters1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 77: return parameters1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      case 76: currentMacro.put(lexeme(), null);  break;
      }
    }
  }

  public class ART_C_starOpt extends AbstractAttributeBlock {
    ART_C_starOpt starOpt = this;

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
      case 87: macrosNestable.add(currentMacroName);  break;
      }
    }
  }

  public class ART_C_text extends AbstractAttributeBlock {
    ART_C_text text = this; ART_C_expand expand1; ART_C_define define1; ART_C_text text1;

    public void initRHSAttributeBlock(int nodeNumber, int term) {
      switch(nodeNumber){
      case 91: define1 = new ART_C_define(); define1.term = term; break;
      case 92: text1 = new ART_C_text(); text1.term = term; break;
      case 95: expand1 = new ART_C_expand(); expand1.term = term; break;
      case 96: text1 = new ART_C_text(); text1.term = term; break;
      }
    }

    public AbstractAttributeBlock getAttributes(int nodeNumber) {
      switch(nodeNumber){
      case 91: return define1;
      case 92: return text1;
      case 95: return expand1;
      case 96: return text1;
      default: Util.fatal("getAttributes: unknown node " + nodeNumber + ". Probable out-of-date ARTGeneratedActions - regenerate and recompile"); return null;
      }
    }

    public void action(int nodeNumber) {
      switch(nodeNumber){
      }
    }
  }

  public AbstractAttributeBlock init(AbstractInterpreter interpreter, int term) {
    this.interpreter = interpreter;
    var ret = new ART_C_text();
    ret.term = term;
    return ret;
  }
}
