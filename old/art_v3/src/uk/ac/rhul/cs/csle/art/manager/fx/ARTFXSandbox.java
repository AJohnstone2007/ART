package uk.ac.rhul.cs.csle.art.manager.fx;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ARTFXSandbox extends Application {

  private static final String[] KEYWORDS = new String[] { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
      "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
      "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
      "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };

  private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
  private static final String PAREN_PATTERN = "\\(|\\)";
  private static final String BRACE_PATTERN = "\\{(.|\\\\R)*?\\}" + "|\\<(.|\\\\R)*?\\>";
  private static final String BRACKET_PATTERN = "\\[|\\]";
  private static final String SEMICOLON_PATTERN = "\\;";
  private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"" + "|\'([^\'\\\\]|\\\\.)*\'";
  private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/" + "|" + "\\(\\*(.|\\R)*?\\*\\)";

  private static final Pattern PATTERN = Pattern
      .compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")" + "|(?<PAREN>" + PAREN_PATTERN + ")" + "|(?<BRACKET>"
          + BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")");

  private static final String sampleCode = String.join("\n", new String[] { "(*******************************************************************************",
      "*", "* miniCalc.art - Adrian Johnstone 9 January 2016", "*", "*******************************************************************************)",
      "statement ::= 'print' '(' printElements ')' ';'                      (* print statement *)", "",
      "printElements ::= STRING_DQ { artText.printf(\"%s\", STRING_DQ1.v); } | ",
      "                  STRING_DQ { artText.printf(\"%s\", STRING_DQ1.v); } ',' printElements | ",
      "                  e0 { artText.printf(\"%d\", e01.v); } | e0 { artText.printf(\"%d\", e01.v); } ',' printElements  ", "",
      "e0 <v:int> ::= e1 { e0.v = e11.v; } | ", "               e1 '>'  e1 { e0.v = e11.v >  e12.v ? 1 : 0; } |       (* Greater than *)",
      "               e1 '<'  e1 { e0.v = e11.v <  e12.v ? 1 : 0; } |       (* Less than *)",
      "               e1 '>=' e1 { e0.v = e11.v >= e12.v ? 1 : 0; } |       (* Greater than or equals*)",
      "               e1 '<=' e1 { e0.v = e11.v <= e12.v ? 1 : 0; } |       (* Less than or equals *)",
      "               e1 '==' e1 { e0.v = e11.v == e12.v ? 1 : 0; } |       (* Equal to *)",
      "               e1 '!=' e1 { e0.v = e11.v != e12.v ? 1 : 0; }         (* Not equal to *)", "", "e1 <v:int>  ::= e2 { e1.v = e21.v; } | ",
      "                e1 '+' e2 { e1.v = e11.v + e21.v; } |                (* Add *)",
      "                e1 '-' e2 { e1.v = e11.v - e21.v; }                  (* Subtract *)", "", "e2  <v:int> ::= e3 { e2.v= e31.v; } | ",
      "                e2 '*' e3 { e2.v = e21.v * e31.v; } |                (* Multiply *)",
      "                e2 '/' e3 { e2.v = e21.v / e31.v; } |                (* Divide *)",
      "                e2 '%' e3 { e2.v = e21.v % e31.v; }                  (* Mod *)", "", "e3  <v:int> ::= e4 {e3.v = e41.v; } | ",
      "                '+' e3 {e3.v = e41.v; } |                            (* Posite *)",
      "                '-' e3 {e3.v = -e41.v; }                             (* Negate *)", "", "e4  <v:int> ::= e5 { e4.v = e51.v; } | ",
      "                e5 '**' e4 {e4.v = (int) Math.pow(e51.v, e41.v); }   (* exponentiate *)", "",
      "e5  <v:int> ::= INTEGER {e5.v = INTEGER1.v; } |                      (* Integer literal *)",
      "                '(' e1 { e5.v = e11.v; } ')'                         (* Parenthesised expression *)", "       ",
      "INTEGER <leftExtent:int rightExtent:int lexeme:String v:int> ::= ",
      "  &INTEGER {INTEGER.lexeme = artLexeme(INTEGER.leftExtent, INTEGER.rightExtent); INTEGER.v = artLexemeAsInteger(INTEGER.leftExtent, INTEGER.rightExtent); }  ",
      "", "STRING_DQ <leftExtent:int rightExtent:int lexeme:String v:String> ::= ",
      "  &STRING_DQ {STRING_DQ.lexeme = artLexeme(STRING_DQ.leftExtent, STRING_DQ.rightExtent); STRING_DQ.v = artLexemeAsString(STRING_DQ.leftExtent, STRING_DQ.rightExtent); }  ",

      "package com.example;", "", "import java.util.*;", "", "public class Foo extends Bar implements Baz {", "", "    /*", "     * multi-line comment",
      "     */", "    public static void main(String[] args) {", "        // single-line comment", "        for(String arg: args) {",
      "            if(arg.length() != 0)", "                System.out.println(arg);", "            else",
      "                System.err.println(\"Warning: empty string as argument\");", "        }", "    }", "", "}" });

  public static void main(String[] args) {
    launch(args);
  }

  private CodeArea codeArea;
  private ExecutorService executor;

  @Override
  public void start(Stage primaryStage) {
    executor = Executors.newSingleThreadExecutor();
    codeArea = new CodeArea();
    codeArea.setStyle("-fx-font-family: consolas");
    // codeArea.setStyle("-fx-font-size:" + 28);

    codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    Subscription cleanupWhenDone = codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500)).supplyTask(this::computeHighlightingAsync)
        .awaitLatest(codeArea.multiPlainChanges()).filterMap(t -> {
          if (t.isSuccess()) {
            return Optional.of(t.get());
          } else {
            t.getFailure().printStackTrace();
            return Optional.empty();
          }
        }).subscribe(this::applyHighlighting);

    // call when no longer need it: `cleanupWhenFinished.unsubscribe();`

    codeArea.replaceText(0, 0, sampleCode);

    Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(codeArea)), 1000, 600);
    scene.getStylesheets().add(ARTFXSandbox.class.getResource("java-keywords.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.setTitle("ARTFX language specification");
    primaryStage.show();
  }

  @Override
  public void stop() {
    executor.shutdown();
  }

  private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
    String text = codeArea.getText();
    Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
      @Override
      protected StyleSpans<Collection<String>> call() throws Exception {
        return computeHighlighting(text);
      }
    };
    executor.execute(task);
    return task;
  }

  private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
    codeArea.setStyleSpans(0, highlighting);
  }

  private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    Matcher matcher = PATTERN.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    while (matcher.find()) {
      String styleClass = matcher.group("KEYWORD") != null ? "keyword"
          : matcher.group("PAREN") != null ? "paren"
              : matcher.group("BRACE") != null ? "brace"
                  : matcher.group("BRACKET") != null ? "bracket"
                      : matcher.group("SEMICOLON") != null ? "semicolon"
                          : matcher.group("STRING") != null ? "string" : matcher.group("COMMENT") != null ? "comment" : null;
      /* never happens */ assert styleClass != null;
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastKwEnd = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    return spansBuilder.create();
  }
}
