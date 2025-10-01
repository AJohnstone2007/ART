package uk.ac.rhul.cs.csle.art.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {
  public static PrintStream console = System.out;
  public static int traceLevel = 3;
  public static int errorLevel = 3;

  public static void trace(int level, int indent, String msg) {
    if (traceLevel >= level) {
      console.print(">> ");
      for (int i = 0; i < indent; i++)
        console.print("  ");
      console.println(msg);
    }
  }

  public static void trace(int level, String msg) {
    trace(level, 0, msg);
  }

  public static void debug(String msg) {
    debug(console, msg);
  }

  public static void debug(PrintStream stream, String msg) {
    stream.println("!! " + msg);
  }

  public static void info(String msg) {
    info(console, msg);
  }

  public static void info(PrintStream stream, String msg) {
    if (errorLevel >= 3) stream.println("** " + msg);
  }

  public static void warning(String msg) {
    warning(console, msg);
  }

  public static void warning(PrintStream stream, String msg) {
    if (errorLevel >= 2) stream.println("** Warning: " + msg);
  }

  public static void error(String msg) {
    error(console, msg);
  }

  public static void error(PrintStream stream, String msg) {
    if (errorLevel >= 1) stream.println("** Error: " + msg);
  }

  public static void fatal(String msg) {
    fatal(console, msg);
  }

  public static void fatal(PrintStream stream, String msg) {
    stream.println("** Fatal: " + msg);
    System.exit(1);
  }

  public static String echo(String message, int index, String buffer) {
    StringBuilder sb = new StringBuilder();
    int lineNumber = Util.lineNumber(index, buffer), columnNumber = Util.columnNumber(index, buffer);
    sb.append(lineNumber + "," + columnNumber + " " + message + "\n");
    if (buffer == null || index < 0) return sb.toString();

    // sb.append(String.format("%5d: ", lineNumber));

    int echoColumn = columnNumber;

    Util.appendLine(sb, lineNumber - 1, buffer);
    Util.appendLine(sb, lineNumber, buffer);
    // int echoIndex = index - echoColumn;
    // do {
    // sb.append(buffer.charAt(echoIndex++));
    // } while (echoIndex < buffer.length() && buffer.charAt(echoIndex) != '\n' && buffer.charAt(echoIndex) != '\0');

    sb.append("\n-------"); // Print pointer line
    for (int tmp = 0; tmp < echoColumn; tmp++)
      sb.append("-");
    sb.append("^\n");
    Util.appendLine(sb, lineNumber + 1, buffer);
    sb.append("\n");

    return sb.toString();
  }

  public static void appendLine(StringBuilder sb, int lineNumber, String buffer) {
    if (lineNumber < 1) return;
    int length = buffer.length();
    int tmp = lineNumber;
    int i;
    // Locate line
    for (i = 0; tmp > 1 && i < length; i++)
      if (buffer.charAt(i) == '\n') tmp--;

    if (i >= length) return;

    sb.append(String.format("%5d: ", lineNumber));
    if (lineNumber == 1 && buffer.charAt(0) == '\n')
      sb.append("\n"); // Special case: at start of buffer and empty line
    else
      for (; i < length && buffer.charAt(i) != '\n'; i++) {
        // console.println("Appending " + buffer.charAt(i) + "[" + ((int) buffer.charAt(i)) + "]");
        sb.append(buffer.charAt(i));
      }

  }

  public static int columnNumber(int index, String buffer) { // Return x coordinate: note that the first column is column zero!
    int columnCount = 0;
    if (buffer == null || index < 0) return 0;
    if (index >= buffer.length()) index = buffer.length() - 1;
    if (index == 0) return 0;
    do {
      index--;
      columnCount++;
    } while (index > 0 && buffer.charAt(index) != '\n');

    if (index != 0) columnCount--; // If we did not terminate on start of buffer, then we must have terminated on \n so step forward 1
    return columnCount;
  }

  /* Support for echoing the current line */
  public static int lineNumber(int index, String buffer) {
    if (buffer == null || index < 0) return 0;
    if (index >= buffer.length()) index = buffer.length() - 1;
    int lineCount = 1;
    for (int tmp = 0; tmp < index; tmp++)
      if (buffer.charAt(tmp) == '\n') lineCount++;
    return lineCount;
  }

  public static String unescapeString(String str) {
    return unescapeString(str, 0, 0);
  }

  public static String unescapeString(String str, int leftDelimiterWidth, int rightDelimiterWidth) {
    StringBuilder sb = new StringBuilder();

    for (int i = leftDelimiterWidth; i < str.length() - rightDelimiterWidth; i++)
      if (str.charAt(i) == '\\')
        switch (str.charAt(i + 1)) {
        case '\\':
          sb.append('\\');
          i++;
          break;
        case 'b':
          sb.append('\b');
          i++;
          break;
        case 'f':
          sb.append('\f');
          i++;
          break;
        case 'n':
          sb.append('\n');
          i++;
          break;
        case 'r':
          sb.append('\r');
          i++;
          break;
        case 't':
          sb.append('\t');
          i++;
          break;
        case '"':
          sb.append('"');
          i++;
          break;
        case '\'':
          sb.append('\'');
          i++;
          break;
        case '}':
          sb.append('}');
          i++;
          break;
        case '$':
          sb.append('$');
          i++;
          break;
        case 'u':
          try {
            int bmpCP = Integer.parseInt(str.substring(i + 2, i + 6), 16);
            sb.appendCodePoint(bmpCP);
            i += 5;
          } catch (NumberFormatException nfe) {
            Util.fatal("escape sequence \\" + str.substring(i + 1, i + 6) + "contains non-hexadecial digit");
          }
          break;
        case 'v':
          try {
            int bmpCP = Integer.parseInt(str.substring(i + 2, i + 8), 16);
            sb.appendCodePoint(bmpCP);
            i += 7;
          } catch (NumberFormatException nfe) {
            Util.fatal("escape sequence \\" + str.substring(i + 1, i + 8) + "contains non-hexadecial digit");
          }
          break;
        default:
          warning(" unrecognised escape sequence \\" + str.charAt(i) + " - unescaping to itself");
          sb.append(str.charAt(i));
        }
      else
        sb.append(str.charAt(i));
    return sb.toString();
  }

  public static String escapeString(String str) {
    return escapeString(str, false);
  }

  public static String escapeString(String str, boolean escapeSpace) {
    StringBuilder sb = new StringBuilder();
    char[] chars = str.toCharArray();

    for (var c : chars)
      switch (c) {
      case '\b':
        sb.append("\\b");
        break;
      case '\f':
        sb.append("\\f");
        break;
      case '\n':
        sb.append("\\n");
        break;
      case '\r':
        sb.append("\\r");
        break;
      case '\t':
        sb.append("\\t");
        break;
      case '\\':
        sb.append("\\\\");
        break;
      case '\'':
        sb.append("\\'");
        break;
      case '"':
        sb.append("\\\"");
        break;
      case ' ':
        if (escapeSpace) sb.append("\\");
        sb.append(c);
        break;
      default:
        sb.append(c);
      }

    return sb.toString();
  }

  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static String timestamp() {
    return dateFormat.format(Calendar.getInstance().getTime());
  }

  public static String timeAsMilliseconds(long startTime, long stopTime) {
    return String.format("%.3f", (stopTime - startTime) * 1E-6);
  }

  public static void notImplemented(String methodName, Class<?> classObject) {
    error(methodName + " not implemented for class " + classObject.getSimpleName());
  }

  private static String asciiIdentifiers[] = { "_NUL", "_SOH", "_STX", "_ETX", "_EOT", "_ENQ", "_ACK", "_BEL", "_BS", "_HT", "_LF", "_VT", "_FF", "_CR", "_SO",
      "_SI", "_DLE", "_DC1", "_DC2", "_DC3", "_DC4", "_NAK", "_SYN", "_ETB", "_CAN", "_EM", "_SUB", "_ESC", "_FS", "_GS", "_RS", "_", "_SPACE", "_SHREIK",
      "_DBLQUOTE", "_HASH", "_DOLLAR", "_PERCENT", "_AMPERSAND", "_QUOTE", "_LPAR", "_RPAR", "_STAR", "_PLUS", "_COMMA", "_MINUS", "_PERIOD", "_SLASH", "0",
      "1", "2", "3", "4", "5", "6", "7", "8", "9", "_COLON", "_SEMICOLON", "_LT", "_EQUAL", "_GT", "_QUERY", "_AT", "A", "B", "C", "D", "E", "F", "G", "H", "I",
      "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "_LBRACK", "_BACKSLASH", "_RBRACK", "_UPARROW", "_", "_BACKQUOTE",
      "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "_LBRACE", "_BAR",
      "_RBRACE", "_TILDE", "_DEL" };

  private static String asciiLiteralStrings[] = { "\\000", "\\001", "\\002", "\\003", "\\004", "\\005", "\\006", "\\007", "\\010", "\\t", "\\n", "\\013",
      "\\014", "\\r", "\\016", "\\017", "\\020", "\\021", "\\022", "\\023", "\\024", "\\025", "\\026", "\\027", "\\030", "\\031", "\\032", "\\033", "\\034",
      "\\035", "\\036", "\\037", " ", "!", "\\\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8",
      "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
      "X", "Y", "Z", "[", "\\\\", "]", "^", "_", "`", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
      "v", "w", "x", "y", "z", "{", "|", "}", "~", "_DEL" };

  private static String asciiQuotedLiteralStrings[] = { "\\\\\\\\000", "\\\\\\\\001", "\\\\\\\\002", "\\\\\\\\003", "\\\\\\\\004", "\\\\\\\\005", "\\\\\\\\006",
      "\\\\\\\\007", "\\\\\\\\010", "\\\\\\\\t", "\\\\\\\\n", "\\\\\\\\013", "\\\\\\\\014", "\\\\\\\\r", "\\\\\\\\016", "\\\\\\\\017", "\\\\\\\\020",
      "\\\\\\\\021", "\\\\\\\\022", "\\\\\\\\023", "\\\\\\\\024", "\\\\\\\\025", "\\\\\\\\026", "\\\\\\\\027", "\\\\\\\\030", "\\\\\\\\031", "\\\\\\\\032",
      "\\\\\\\\033", "\\\\\\\\034", "\\\\\\\\035", "\\\\\\\\036", "\\\\\\\\037", " ", "!", "\\\\\\\"", "#", "$", "%", "&", "\\\\\\\\'", "(", ")", "*", "+", ",",
      "-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", ";", "<", "=", ">", "?", "@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
      "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "[", "\\\\\\\\\\\\\\\\", "]", "^", "_", "\\\\\\\\`", "a", "b", "c", "d", "e",
      "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "{", "|", "}", "~", "\\\\\\\\0177" };

  private static String toString(String string, String[] table) {
    String ret = "";

    if (string != null) for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      if (c > 0 && c < 128)
        ret += table[c];
      else
        ret += ' ';
    }

    return ret;
  }

  public static String toIdentifier(String string) {
    return toString(string, asciiIdentifiers);
  }

  public static String toLiteralString(String string) {
    return toString(string, asciiLiteralStrings);
  }

  public static String toQuotedLiteralString(String string) {
    return toString(string, asciiQuotedLiteralStrings);
  }

  public static boolean containsWhitespace(String string) {
    for (var c : string.toCharArray())
      switch (c) {
      case ' ', '\t', '\r', '\n':
        return true;
      }
    return false;
  }

  public static boolean containsNewline(String string) {
    for (var c : string.toCharArray())
      switch (c) {
      case '\r', '\n':
        return true;
      }
    return false;
  }
}
