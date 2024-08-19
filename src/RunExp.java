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
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class RunExp {
  int line = 1;
  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-dd,HH:mm:ss");
  static String logFileName = "RunExpLog.csv", expScriptFileName = "RunExpScr.bat";
  static File log = null, expScr = null, empty[] = new File[0]; // When expScr is non-null, execute() writes out commands rather than directly performing them
  static Set<String> groups = new HashSet<>();

  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length < 3) fatal("Usage: java RunExp <RLC> <count> <group> <group>? ...\n"
        + "where <RLC> is the path to the ReferenceLanguageCorpora directory root\n" + "      <count> is the number of iterations per experiment\n"
        + "      <group> is a group of grammars and strings to test\n\n" + "      If <count> is negative then write the commands needed for this run to file "
        + expScriptFileName + " rather than running them directly\n" + "      If <count> is non-negative then run directly, with output logged to "
        + logFileName + "\n" + "      There must be at least one group. Standard groups include: str tok bulk");
    int count = Integer.parseInt(args[1]);

    if (count < 0) { // Output commands; so delete any existing file and open file expScr
      Files.deleteIfExists(Paths.get(expScriptFileName));
      expScr = new File(expScriptFileName);
      count = -count;
    }

    for (int i = 2; i < args.length; i++) // Load groups set from command line
      groups.add(args[i]);

    new RunExp(args[0], count);
  }

  RunExp(String rlc, int count) throws IOException, InterruptedException {
    System.out.print("RLC experimental framework scanning " + rlc + " on group" + (groups.size() > 1 ? "s" : ""));
    for (var a : groups)
      System.out.print(" " + a);
    System.out.println();

    if (expScr == null) { // If we are directly executing then delete the old log, open file log.csv and write header line
      Files.deleteIfExists(Paths.get(logFileName));
      log = new File(logFileName);
      appendTo(log,
          "line,date,time,tool,script,iter,language,grammar,string,length,algorithm,result,status,"
              + "TSetup,TLex,TLChoose,TParse,TPChoose,TSelect,TTerm,TSem,tweN,tweE,lexes,Pool,H0,H1,H2,H3,H4,H5,H6+,"
              + "GSS SN,GSS EN,GGS E,SPPF Eps,SPPF T,SPPF NT,SPPF Inter,SPPF PN,SPPF Edge\n");
    }

    for (var s : getFiles(rlc + "/experiments/try/scripts"))
      for (var l : getFiles(rlc + "/languages"))
        for (var g : getFiles(l + "/grammar"))
          for (var c : getFiles(l + "/corpus"))
            for (var a : groups)
              for (var gg : getFiles(g + "/" + a))
                for (var cc : getFiles(c + "/" + a))
                  for (int i = 0; i < count; i++)
                    switch (s.toString().substring(s.toString().lastIndexOf('.') + 1).toLowerCase()) {
                    case "art":
                      if (!gg.toString().endsWith("art")) continue;
                      fileCat("test.str", cc);
                      fileCat("test.art", gg, s);
                      var tf = getFiles(rlc + "/experiments/try/tools/art");
                      if (tf.length == 0) fatal("Script " + gg + " requires ART, but no relevant tools found");
                      for (var t : tf) {
                        logExperiment(i, s, l, a, g, c, gg, cc, t);
                        execute(log, "java", "-jar", t.toString(), "test.art");
                      }
                      break;
                    case "artv3":
                      if (!gg.toString().endsWith("art")) continue;
                      fileCat("test.str", cc);
                      fileCat("test.art", gg, s);
                      tf = getFiles(rlc + "/experiments/try/tools/art");
                      if (tf.length == 0) fatal("Script " + gg + " requires ART, but no relevant tools found");
                      for (var t : tf) {
                        logExperiment(i, s, l, a, g, c, gg, cc, t);
                        execute(log, "java", "-jar", t.toString(), "v3", "test.art", "test.str");
                      }
                      break;
                    case "gtb":
                      if (!gg.toString().endsWith("gtb")) continue;
                      fileCat("test.str", cc);
                      fileCat("test.gtb", gg, s);
                      tf = getFiles(rlc + "/experiments/try/tools/gtb");
                      if (tf.length == 0) fatal("Script " + gg + " requires GTB, but no relevant tools found");
                      for (var t : getFiles(rlc + "/experiments/try/tools/gtb")) {
                        logExperiment(i, s, l, a, g, c, gg, cc, t);
                        execute(log, t.toString(), "test.gtb");
                      }
                      break;
                    case "bat":
                      if (!gg.toString().endsWith("art")) continue;
                      fileCat("test.str", cc);
                      fileCat("test.art", gg);
                      logExperiment(i, s, l, a, g, c, gg, cc, null);
                      Scanner scanner = new Scanner(s);
                      while (scanner.hasNext()) {
                        String ss = scanner.nextLine();
                        ss = ss.replaceAll("%1", "test.art");
                        ss = ss.replaceAll("%2", "test.str");
                        execute(log, ss.split(" "));
                      }
                      break;
                    default:
                      fatal("Unknown script file type " + s.getName() + " must be one of: art gtb bat");
                    }
    Files.deleteIfExists(Paths.get("test.gtb"));
    Files.deleteIfExists(Paths.get("test.art"));
    Files.deleteIfExists(Paths.get("test.str"));
  }

  public void logExperiment(int iteration, File s, File l, String a, File g, File c, File gg, File cc, File t) throws IOException {
    System.out.println(line + " " + dtf.format(LocalDateTime.now(ZoneId.systemDefault())) + " " + (t == null ? "script" : t.getName()) + " " + s.getName() + " "
        + l.getName() + " " + g.getName() + "/" + a + "/" + gg.getName() + " " + c.getName() + "/" + a + "/" + cc.getName() + " " + iteration);
    if (log != null)
      appendTo(log, (line++) + "," + dtf.format(LocalDateTime.now(ZoneId.systemDefault())) + "," + (t == null ? "script" : t.getName()) + "," + s.getName()
          + "," + iteration + "," + l.getName() + "," + g.getName() + "/" + a + "/" + gg.getName() + "," + c.getName() + "/" + a + "/" + cc.getName() + ",");
  }

  private void appendTo(File f, String string) throws IOException {
    var fw = new FileWriter(f, true);
    fw.write(string);
    fw.close();
  }

  private void fileCat(String dstFilename, File... files) throws IOException {
    if (expScr != null) {
      appendTo(expScr, "cat");
      for (var f : files)
        appendTo(expScr, " " + f);
      appendTo(expScr, " > " + dstFilename + "\n");
    } else {
      FileChannel dst = new FileOutputStream(dstFilename).getChannel();
      for (var file : files) {
        FileChannel src = new FileInputStream(file).getChannel();
        src.transferTo(0, src.size(), dst);
        src.close();
      }
      dst.close();
    }
  }

  private void execute(File log, String... command) throws IOException, InterruptedException {
    if (expScr != null) {
      for (var s : command)
        appendTo(expScr, s + " ");
      appendTo(expScr, "\n");
    } else {
      ProcessBuilder pb = new ProcessBuilder(command); // Launch and wait for command process
      pb.redirectErrorStream(true);
      pb.redirectOutput(Redirect.appendTo(log));
      Process p = pb.start();
      p.waitFor();
    }
  }

  static File[] getFiles(String directory) {
    var tmp = new File(directory).listFiles();
    return tmp == null ? empty : tmp;
  }

  static void fatal(String msg) {
    System.err.println(msg);
    System.exit(0);
  }
}
