import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class RunExp {
  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd,HH:mm:ss");
  int line = 1;
  static String logFileName = "log.csv", summaryFileName = "summary.csv";

  public static void main(String[] args) throws IOException, InterruptedException {
    new RunExp(args);
    new MakeSummary(logFileName, summaryFileName);
  }

  RunExp(String[] args) throws IOException, InterruptedException {
    if (args.length < 3) fatal("Usage: java RunExp <RLC> <count> <group> <group>? ...\n"
        + "where <RLC> is the path to the ReferenceLanguageCorpora directory root\n" + "      <count> is the number of iterations per experiment\n"
        + "      <group> is a group of grammars and strings to test\n\n" + "      There must be at least one group. Standard groups include: str tok bulk");

    Files.deleteIfExists(Paths.get(logFileName));
    File logFile = new File(logFileName);

    String rlc = args[0];
    int count = Integer.parseInt(args[1]);
    Set<String> groupSet = new HashSet<>();
    for (int i = 2; i < args.length; i++) // Load groups set from command line
      groupSet.add(args[i]);

    System.out.print("RLC experimental framework scanning " + rlc + " on group" + (groupSet.size() > 1 ? "s" : ""));
    for (var a : groupSet)
      System.out.print(" " + a);
    System.out.println();

    for (var s : getFiles(rlc + "/experiments/try/scripts"))
      for (var a : groupSet) // group directories as specified on command line
        for (var l : getFiles(rlc + "/languages")) // language directories
          for (var g : getFiles(l + "/grammar")) // grammar directories for this language
            for (var gg : getFiles(g + "/" + a)) // individual file this grammar's group
              for (var c : getFiles(l + "/corpus")) // corpus directories for this language
                for (var cc : getFiles(c + "/" + a)) // individual file from this corpus' group
                  if (getFileType(s.getName()).equals("gtb")) {
                    if (!getFileType(gg.getName()).equals("gtb")) continue;
                    String toolsDir = rlc + "/experiments/try/tools/gtb";
                    var t = getFiles(toolsDir);
                    if (t.length == 0) fatal("Script " + gg + " requires GTB, but no relevant tools found in " + toolsDir);
                    fileCat("test.str", cc);
                    fileCat("test.gtb", gg, s);
                    for (var tt : t)
                      for (int i = 0; i < count; i++) {// iteration count
                        logExperiment(logFile, i, s, l, a, g, c, tt, gg, cc);
                        System.out.println("** " + tt + "test.gtb");
                        execute(logFile, tt.toString(), "test.gtb");
                      }
                  } else if (getFileType(s.getName()).equals("bat") || getFileType(s.getName()).equals("sh")) {
                    if (!getFileType(gg.getName()).equals("art")) continue;
                    String toolsDir = rlc + "/experiments/try/tools/art";
                    var t = getFiles(toolsDir); // collect tools
                    if (t.length == 0) fatal("Script " + gg + " requires ART, but no relevant tools found in " + toolsDir);
                    for (var tt : t) { // tool
                      for (int i = 0; i < count; i++) {// iteration count
                        logExperiment(logFile, i, s, l, a, g, c, tt, gg, cc);
                        Scanner scanner = new Scanner(s);
                        while (scanner.hasNext()) {
                          String ss = scanner.nextLine();
                          if (ss.startsWith("#") || ss.startsWith("rem")) continue; // skip comments
                          String ttPath = tt.getPath().replace('\\', '/');
                          String ggPath = gg.getPath().replace('\\', '/');
                          String ccPath = cc.getPath().replace('\\', '/');

                          ss = ss.replaceAll("%1", ttPath); // P1 is the tool filename (MS)
                          ss = ss.replaceAll("%2", ggPath); // P2 is the grammar filename (MS)
                          ss = ss.replaceAll("%3", ccPath); // P3 is the corpus string filename (MS)
                          ss = ss.replaceAll("$1", ttPath); // P1 is the tool filename (Un*x)
                          ss = ss.replaceAll("$2", ggPath); // P2 is the grammar filename (Un*x)
                          ss = ss.replaceAll("$3", ccPath); // P3 is the corpus string filename (Un*x)
                          System.out.println("** " + ss);
                          execute(logFile, ss.split(" "));
                        }
                      }
                    }
                  } else
                    System.out.println("Warning - skipping unknown script file type " + s.getName() + " must be one of: bat gtb sh");
  }

  void logExperiment(File log, int iteration, File s, File l, String a, File g, File c, File tt, File gg, File cc) throws IOException {
    String toolName = tt == null ? "bat" : tt.getName();
    System.out.println(line + " " + dtf.format(LocalDateTime.now(ZoneId.systemDefault())) + " " + toolName + " " + s.getName() + " " + l.getName() + " "
        + g.getName() + "/" + a + "/" + gg.getName() + " " + c.getName() + "/" + a + "/" + cc.getName() + " " + iteration);
    appendTo(log, (line++) + "," + dtf.format(LocalDateTime.now(ZoneId.systemDefault())) + "," + toolName + "," + s.getName() + "," + iteration + ","
        + l.getName() + "," + g.getName() + "/" + a + "/" + gg.getName() + "," + c.getName() + "/" + a + "/" + cc.getName() + ",");
  }

  void appendTo(File f, String string) throws IOException {
    var fw = new FileWriter(f, true);
    fw.write(string);
    fw.close();
  }

  void fileCat(String dstFilename, File... files) throws IOException {
    FileChannel dst = new FileOutputStream(dstFilename).getChannel();
    for (var file : files) {
      FileChannel src = new FileInputStream(file).getChannel();
      src.transferTo(0, src.size(), dst);
      src.close();
    }
    dst.close();
  }

  String getFileType(String filename) {
    return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
  }

  void execute(File log, String... command) throws IOException, InterruptedException {
    ProcessBuilder pb = new ProcessBuilder(command); // Launch and wait for command process
    pb.redirectErrorStream(true);
    pb.redirectOutput(Redirect.appendTo(log));
    Process p = pb.start();
    p.waitFor();
  }

  File empty[] = new File[0];

  File[] getFiles(String directory) {
    var tmp = new File(directory).listFiles();
    return tmp == null ? empty : tmp;
  }

  void fatal(String msg) {
    System.err.println(msg);
    System.exit(0);
  }
}

class SummaryKey {
  String tool, script, language, grammar, string, length, algorithm, result;

  public SummaryKey(String tool, String script, String language, String grammar, String string, String length, String algorithm, String result) {
    super();
    this.tool = tool;
    this.script = script;
    this.language = language;
    this.grammar = grammar;
    this.string = string;
    this.length = length;
    this.algorithm = algorithm;
    this.result = result;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
    result = prime * result + ((grammar == null) ? 0 : grammar.hashCode());
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    result = prime * result + ((length == null) ? 0 : length.hashCode());
    result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
    result = prime * result + ((script == null) ? 0 : script.hashCode());
    result = prime * result + ((string == null) ? 0 : string.hashCode());
    result = prime * result + ((tool == null) ? 0 : tool.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SummaryKey other = (SummaryKey) obj;
    if (algorithm == null) {
      if (other.algorithm != null) return false;
    } else if (!algorithm.equals(other.algorithm)) return false;
    if (grammar == null) {
      if (other.grammar != null) return false;
    } else if (!grammar.equals(other.grammar)) return false;
    if (language == null) {
      if (other.language != null) return false;
    } else if (!language.equals(other.language)) return false;
    if (length == null) {
      if (other.length != null) return false;
    } else if (!length.equals(other.length)) return false;
    if (result == null) {
      if (other.result != null) return false;
    } else if (!result.equals(other.result)) return false;
    if (script == null) {
      if (other.script != null) return false;
    } else if (!script.equals(other.script)) return false;
    if (string == null) {
      if (other.string != null) return false;
    } else if (!string.equals(other.string)) return false;
    if (tool == null) {
      if (other.tool != null) return false;
    } else if (!tool.equals(other.tool)) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(tool);
    builder.append(",");
    builder.append(script);
    builder.append(",");
    builder.append(language);
    builder.append(",");
    builder.append(grammar);
    builder.append(",");
    builder.append(string);
    builder.append(",");
    builder.append(length);
    builder.append(",");
    builder.append(algorithm);
    builder.append(",");
    builder.append(result);
    return builder.toString();
  }
}

class MakeSummary {
  MakeSummary(String logFileName, String summaryFileName) throws IOException {
    Files.deleteIfExists(Paths.get(summaryFileName));
    var fw = new FileWriter(new File(summaryFileName), true);
    fw.write("tool,script,language,grammar,string,length,algorithm,result," + "Runs,TParseMin,TParseMax,TParseMean,TParseBestFiveMean,,Results...\n");

    var scanner = new Scanner(new File(logFileName));
    var header = scanner.nextLine();
    Map<SummaryKey, ArrayList<Double>> map = new HashMap<>();
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      var fields = line.split(",");
      if (fields.length < 17) {
        System.out.println("Bad format: " + line);
        continue;
      }
      var key = new SummaryKey(fields[3], fields[4], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]);
      if (map.get(key) == null) map.put(key, new ArrayList<Double>());
      map.get(key).add(Double.parseDouble(fields[16])); // Add parse time
    }

    for (var k : map.keySet()) {
      double mean = 0;
      double meanOfBestFive = 0;

      ArrayList<Double> list = map.get(k);
      Collections.sort(list);

      for (var l : list)
        mean += l;

      for (int i = 0; i < 5 && i < list.size(); i++)
        meanOfBestFive += list.get(i);

      fw.write(k + "," + list.size() + "," + list.get(0) + "," + list.get(list.size() - 1) + "," + String.format("%6.3f", mean / list.size()) + ","
          + String.format("%6.3f", meanOfBestFive / 5));
      fw.write(",***,");
      for (var l : list)
        fw.write(l + ",");
      fw.write("\n");
    }
    fw.close();
  }
}
