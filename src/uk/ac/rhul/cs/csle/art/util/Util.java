package uk.ac.rhul.cs.csle.art.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {

  public static int traceLevel = 0;

  public static void trace(int level, int indent, String string) {
    if (traceLevel >= level) {
      for (int i = 0; i < indent; i++)
        System.out.print("  ");
      System.out.print(string);
    }
  }

  public static void info(String msg) {
    info(System.out, msg);
  }

  public static void info(PrintStream stream, String msg) {
    stream.println("*** " + msg);
  }

  public static void warning(String msg) {
    info(System.out, msg);
  }

  public static void warning(PrintStream stream, String msg) {
    stream.println("*** Warning: " + msg);
  }

  public static void error(String msg) {
    info(System.out, msg);
  }

  public static void error(PrintStream stream, String msg) {
    stream.println("*** Error: " + msg);
  }

  public static void fatal(String msg) {
    info(System.out, msg);
  }

  public static void fatal(PrintStream stream, String msg) {
    stream.println("*** Fatal: " + msg);
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
        // System.out.println("Appending " + buffer.charAt(i) + "[" + ((int) buffer.charAt(i)) + "]");
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

  public static String escapeString(String string) {
    return string.replaceAll("\"", "\\\\\"");
  }

  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static String timestamp() {
    return dateFormat.format(Calendar.getInstance().getTime());
  }

}
