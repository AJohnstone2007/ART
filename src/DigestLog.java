import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DigestLog {

  static String digestFileName = "RunExpDigest.csv";
  static String logFileName = "RunExpLog.csv";
  static File digest;

  class Key {
    String tool, script, language, grammar, string, length, algorithm, result;

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
      Key other = (Key) obj;
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

    public Key(String tool, String script, String language, String grammar, String string, String length, String algorithm, String result) {
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

    public String toString1() {
      StringBuilder builder = new StringBuilder();
      builder.append("Key [tool=");
      builder.append(tool);
      builder.append(", script=");
      builder.append(script);
      builder.append(", language=");
      builder.append(language);
      builder.append(", grammar=");
      builder.append(grammar);
      builder.append(", string=");
      builder.append(string);
      builder.append(", length=");
      builder.append(length);
      builder.append(", algorithm=");
      builder.append(algorithm);
      builder.append(", result=");
      builder.append(result);
      builder.append("]");
      return builder.toString();
    }
  }

  public static void main(String[] args) throws IOException {
    new DigestLog();
  }

  DigestLog() throws IOException {
    Files.deleteIfExists(Paths.get(digestFileName));
    digest = new File(digestFileName);
    var fw = new FileWriter(digest, true);
    fw.write("tool,script,language,grammar,string,length,algorithm,result," + "Runs,TParseMin,TParseMax,TParseMean,TParseBestFiveMean,,Results...\n");

    Scanner scanner = new Scanner(new File(logFileName));
    String header = scanner.nextLine();
    Map<Key, ArrayList<Double>> map = new HashMap<>();
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      var fields = line.split(",");
      if (fields.length < 17) {
        System.out.println("Bad format: " + line);
        continue;
      }
      Key key = new Key(fields[3], fields[4], fields[6], fields[7], fields[8], fields[9], fields[10], fields[11]);
      // System.out.println(key);
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
