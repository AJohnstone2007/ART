import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompressSMLIdentifiers {
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage - CompressSMLIdentifiers <filename>");
      System.exit(0);
    }

    Set keywords = Set.of("abstype", "and", "andalso", "as", "case", "datatype", "do", "else", "end", "eqtype", "exception", "fn", "fun", "functor", "handle",
        "if", "in", "include", "infix", "infixr", "let", "local", "nonfix", "of", "op", "open", "orelse", "raise", "rec", "sharing", "sig", "require",
        "signature", "struct", "structure", "then", "type", "val", "where", "while", "with", "withtype");

    String str = Files.readString(Paths.get(args[0]));

    Matcher idMatcher = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*").matcher(str);

    int previousEnd = 0;
    while (idMatcher.find()) {
      String lexeme = idMatcher.group();
      System.out.print(str.substring(previousEnd, idMatcher.start()) + (keywords.contains(lexeme) ? lexeme : "I"));
      previousEnd = idMatcher.end();
    }
    System.out.print(str.substring(previousEnd));
  }
}
