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

import uk.ac.rhul.cs.csle.art.util.Util;

class RunExp {

  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd,HH:mm:ss");
  int line = 1;
  static String logFileName = "log.csv", timeSummaryFileName = "timeSummary.csv", spaceSummaryFileName = "spaceSummary.csv";

  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length < 2)
      fatal("Usage: java RunExp <RLC> <count> <group> <group>? ...\n" + "where <RLC> is the path to the ReferenceLanguageCorpora directory root\n"
          + "      <count> is the number of iterations per experiment\n" + "      <group> is a group of grammars and strings to test\n\n"
          + "      If countis zero, then skip the experimetal run, otherwise there must be at least one group. Standard groups include: org cws tok amb");
    if (Integer.parseInt(args[1]) > 0)
      new RunExp(args);
    else
      Util.info("Count is zero - skipping experimental run and recomputing summaries");
    new MakeTimeSummary(logFileName, timeSummaryFileName);
    new MakeSpaceSummary(logFileName, spaceSummaryFileName);
  }

  RunExp(String[] args) throws IOException, InterruptedException {

    Files.deleteIfExists(Paths.get(logFileName));
    File logFile = new File(logFileName);
    appendTo(logFile,
        "line,date,time,tool,script,iter,language,grammar,string,length,algorithm,result,status,TSetup,TLex,TLChoose,TParse,TPChoose,TTerm,TSem,twe N,twe E,lexes,"
            + "Desc,GSS N,GGS E,Pops,SPPF Eps,SPPF T,SPPF NonT,SPPF Inter,SPPF SymInter,SPPF Pack,SPPF Amb,SPPF Edge,SPPF Cyc SCC,Deriv N,Deriv Amb,Mem,Pool,H0,H1,H2,H3,H4,H5,H6+\n");
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
                    if (t.length == 0) {
                      Util.info("Warning - script " + gg + " requires GTB, but no relevant tools found in " + toolsDir);
                      continue;
                    }
                    fileCat("test.str", cc);
                    fileCat("test.gtb", gg, s);
                    for (var tt : t)
                      for (int i = 1; i <= count; i++) {// iteration count
                        logExperiment(logFile, i, s, l, a, g, c, tt, gg, cc);
                        Util.info("   " + tt + "test.gtb");
                        execute(logFile, tt.toString(), "test.gtb");
                      }
                  } else if (getFileType(s.getName()).equals("bat") || getFileType(s.getName()).equals("sh")) {
                    if (!getFileType(gg.getName()).equals("art")) continue;
                    String toolsDir = rlc + "/experiments/try/tools/art";
                    var t = getFiles(toolsDir); // collect tools
                    if (t.length == 0) {
                      Util.info("Warning - script " + gg + " requires ART, but no relevant tools found in " + toolsDir);
                      continue;
                    }
                    for (var tt : t) { // tool
                      for (int i = 1; i <= count; i++) {// iteration count
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
                          Util.info("   " + ss);
                          execute(logFile, ss.split(" "));
                        }
                      }
                    }
                  } else
                    Util.info("Warning - skipping unknown script file type " + s.getName() + " must be one of: bat gtb sh");
  }

  static void fatal(String msg) {
    Util.info(msg);
    System.exit(0);
  }

  void logExperiment(File log, int iteration, File s, File l, String a, File g, File c, File tt, File gg, File cc) throws IOException {
    String toolName = tt == null ? "bat" : tt.getName();
    Util.info(line + " " + dtf.format(LocalDateTime.now(ZoneId.systemDefault())) + " " + toolName + " " + s.getName() + " " + l.getName() + " " + g.getName()
        + "/" + a + "/" + gg.getName() + " " + c.getName() + "/" + a + "/" + cc.getName() + " " + iteration);
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

}

class SummaryKey {
  String tool, script, language, grammar, string, tokens, algorithm, result;

  public SummaryKey(String tool, String script, String language, String grammar, String string, String tokens, String algorithm, String result) {
    super();
    this.tool = tool;
    this.script = script;
    this.language = language;
    this.grammar = grammar;
    this.string = string;
    this.tokens = tokens;
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
    result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
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
    if (tokens == null) {
      if (other.tokens != null) return false;
    } else if (!tokens.equals(other.tokens)) return false;
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
    builder.append(tokens);
    builder.append(",");
    builder.append(algorithm);
    builder.append(",");
    builder.append(result);
    return builder.toString();
  }

}

class MakeTimeSummary {
  final int lineCol = 0, dateCol = 1, timeCol = 2, toolCol = 3, scriptCol = 4, iterCol = 5, languageCol = 6, grammarCol = 7, stringCol = 8, lengthCol = 9,
      algorithmCol = 10, resultCol = 11, statusCol = 12, TSetupCol = 13, TLexCol = 14, TLChooseCol = 15, TParseCol = 16, TPChooseCol = 17, TTermCol = 18,
      TSemCol = 19, tweNCol = 20, tweECol = 21, lexesCol = 22, DescCol = 23, GSSNCol = 24, GGSECol = 25, PopsCol = 26, SPPFEpsCol = 27, SPPFTCol = 28,
      SPPFNonTCol = 29, SPPFInterCol = 30, SPPFSymInterCol = 31, SPPFPackCol = 32, SPPFAmbCol = 33, SPPFEdgeCol = 34, SPPFCycCol = 35; // SCC Deriv N Deriv Amb
                                                                                                                                       // Mem Pool H0 H1 H2 H3
                                                                                                                                       // H4 H5 H6+;

  MakeTimeSummary(String logFileName, String summaryFileName) throws IOException {
    Files.deleteIfExists(Paths.get(summaryFileName));
    var fw = new FileWriter(new File(summaryFileName), true);
    fw.write("tool,script,language,grammar,string,tokens,algorithm,result,"
        + "Runs,TMin,TMax,TMean,TStdev,TMedian,Outlier threshold,Dropped,TMeanAfterDrop,TStdedAfterDrop,Results...\n");

    var scanner = new Scanner(new File(logFileName));
    var header = scanner.nextLine();
    Map<SummaryKey, ArrayList<Double>> map = new HashMap<>();
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      Util.info(line);
      var fields = line.split(",");
      if (fields.length < 17) {
        Util.info("Bad format: " + line);
        continue;
      }
      var key = new SummaryKey(fields[toolCol], fields[scriptCol], fields[languageCol], fields[grammarCol], fields[stringCol], fields[tweECol],
          fields[algorithmCol], fields[resultCol]);
      if (map.get(key) == null) map.put(key, new ArrayList<Double>());
      map.get(key).add(Double.parseDouble(fields[TParseCol])); // Add parse time
      // map.get(key).add(Double.parseDouble(fields[17])); // Add parse chooser time
    }

    for (var k : map.keySet()) {
      ArrayList<Double> list = map.get(k);
      Collections.sort(list);

      double mean = 0;
      for (var l : list)
        mean += l;

      mean /= list.size();

      double stdDev = 0;
      for (var l : list)
        stdDev += (l - mean) * (l - mean);

      stdDev = Math.sqrt(stdDev / (list.size() - 1));

      double quartile1 = list.get(list.size() / 4);
      double median = list.get(list.size() / 2);
      double quartile3 = list.get(3 * list.size() / 4);
      double iqr = quartile3 - quartile1;
      double outlierThreshold = quartile3 + 1.5 * iqr;

      int highWaterMark;
      for (highWaterMark = list.size() - 1; highWaterMark > 0; highWaterMark--)
        if (list.get(highWaterMark) < outlierThreshold) break;

      double meanAfterDrop = 0;
      for (int i = 0; i <= highWaterMark; i++)
        meanAfterDrop += list.get(i);

      meanAfterDrop /= highWaterMark;

      double stdDevAfterDrop = 0;
      for (int i = 0; i <= highWaterMark; i++)
        stdDevAfterDrop += (list.get(i) - meanAfterDrop) * (list.get(i) - meanAfterDrop);

      stdDevAfterDrop = Math.sqrt(stdDevAfterDrop / (highWaterMark - 1));

      fw.write(k + "," + list.size() + "," + list.get(0) + "," + list.get(list.size() - 1) + "," + String.format("%6.3f", mean) + ","
          + String.format("%6.3f", stdDev) + "," + String.format("%6.3f", median) + "," + String.format("%6.3f", outlierThreshold) + ","
          + (list.size() - highWaterMark) + "," + String.format("%6.3f", meanAfterDrop) + "," + String.format("%6.3f", stdDevAfterDrop));
      fw.write(",***,");
      // for (var l : list)
      // fw.write(l + ",");
      fw.write("\n");
    }
    fw.close();
  }
}

class MakeSpaceSummary {
  MakeSpaceSummary(String logFileName, String summaryFileName) throws IOException {
    // Files.deleteIfExists(Paths.get(summaryFileName));
    // var fw = new FileWriter(new File(summaryFileName), true);
    // fw.write("tool,script,language,grammar,string,tokens,algorithm,result," + "Runs,...\n");
    //
    // var scanner = new Scanner(new File(logFileName));
    // var header = scanner.nextLine();
    // Map<SummaryKey, ArrayList<Double>> map = new HashMap<>();
    // while (scanner.hasNext()) {
    // String line = scanner.nextLine();
    // var fields = line.split(",");
    // if (fields.length < 17) {
    // Util.info("Bad format: " + line);
    // continue;
    // }
    // var key = new SummaryKey(fields[3], fields[4], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]);
    // if (map.get(key) == null) map.put(key, new ArrayList<Double>());
    // map.get(key).add(Double.parseDouble(fields[16])); // Add parse time
    // }
    //
    // for (var k : map.keySet()) {
    // double mean = 0;
    // double meanOfBestFive = 0;
    //
    // ArrayList<Double> list = map.get(k);
    // Collections.sort(list);
    //
    // for (var l : list)
    // mean += l;
    //
    // for (int i = 0; i < 5 && i < list.size(); i++)
    // meanOfBestFive += list.get(i);
    //
    // fw.write(k + "," + list.size() + "," + list.get(0) + "," + list.get(list.size() - 1) + "," + String.format("%6.3f", mean / list.size()) + ","
    // + String.format("%6.3f", meanOfBestFive / 5));
    // fw.write(",***,");
    // for (var l : list)
    // fw.write(l + ",");
    // fw.write("\n");
    // }
    // fw.close();
  }
}
