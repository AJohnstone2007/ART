import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProcessFiles {
  public static void main(String[] args) throws IOException {
    File[] files = new File("C:\\csle\\dev\\art\\loc\\versionAnalysis\\easBAtch").listFiles();
    Map<String, String> contents = new HashMap<>();
    for (var f : files)
      contents.put(f.getName(), readContents(f));

    // Print out the filenames in the directory
    // for (var k : contents.keySet())
    // System.out.println(k + "\n" + contents.get(k) + "\n\n");

    // Produce an independent sorted list so that we can manipulate the contents map
    var filenames = new ArrayList<String>(contents.keySet());
    Collections.sort(filenames);

    // n^2 scan of contents to remove filenames that have duplicate contents
    for (var n : filenames) {
      var contentsN = contents.get(n);
      for (var m : filenames) {
        var contentsM = contents.get(m);
        if (contentsM != null && !n.equals(m) && contents.get(m).equals(contents.get(n))) {
          System.out.println("del \"" + n + "\"");
          contents.remove(n);
        }
      }
    }

    // Re-sort keys
    filenames = new ArrayList<String>(contents.keySet());
    Collections.sort(filenames);

    // n^2 scan of contents to remove filenames that do not have a ! in theor contents
    for (var n : filenames) {
      var contentsN = contents.get(n);
      if (contentsN != null && !contentsN.contains("!")) {
        System.out.println("del \"" + n + "\"");
        contents.remove(n);
      }
    }

    // Re-sort keys
    filenames = new ArrayList<String>(contents.keySet());
    Collections.sort(filenames);

    // print files in sorted name order
    // System.out.println("Remaining files");
    // for (var n : filenames)
    // // System.out.println("*** " + n + "\n" + contents.get(n));

    // print directives
    Set<String> directives = new HashSet();
    for (var n : filenames) {
      String c = contents.get(n);

      for (var index = 0; index != -1; index = c.indexOf("!", index + 1)) {
        // System.out.println(n + ": " + index);
        var start = index;
        while (!Character.isWhitespace(c.charAt(index++)))
          ;
        if (start != 0) directives.add(c.substring(start, index));
      }
    }

    for (var s : directives)
      System.out.println(s);
  }

  private static String readContents(File f) throws IOException {
    var sa = Files.readAllLines(Paths.get(f.getAbsolutePath()));
    StringBuilder sb = new StringBuilder();
    for (var s : sa) {
      var ss = s.strip();
      if (!ss.equals("")) {
        sb.append(ss);
        sb.append("\n");
      }
    }
    return sb.toString();
  }
}
