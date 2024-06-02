package uk.ac.rhul.cs.csle.art.fx;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditorWithConsoleWindow {
  public CodeArea codeArea = new CodeArea();
  public final TextArea console = new TextArea();

  public EditorWithConsoleWindow(Stage stage, double x, double y, double width, double height, String title, MenuBuilder menuBuilder, String contents) {

    codeArea.getVisibleParagraphs().addModificationObserver(new VisibleParagraphStyler<>(codeArea, this::computeHighlighting));

    // auto-indent: insert previous line's indents on enter
    final Pattern whiteSpace = Pattern.compile("^\\s+");
    codeArea.addEventHandler(KeyEvent.KEY_PRESSED, KE -> {
      if (KE.getCode() == KeyCode.ENTER) {
        int caretPosition = codeArea.getCaretPosition();
        int currentParagraph = codeArea.getCurrentParagraph();
        Matcher m0 = whiteSpace.matcher(codeArea.getParagraph(currentParagraph - 1).getSegments().get(0));
        if (m0.find()) Platform.runLater(() -> codeArea.insertText(caretPosition, m0.group()));
      }
    });

    codeArea.replaceText(0, 0, contents);

    console.setWrapText(true);
    console.setEditable(false);

    final var menuStack = new VBox();
    final var editorArea = new VirtualizedScrollPane<>(codeArea);
    final var splitPane = new SplitPane(editorArea, console);
    splitPane.setOrientation(Orientation.VERTICAL);
    splitPane.setPrefHeight(height);
    splitPane.setDividerPositions(0.9);

    resetConsole();

    menuStack.getChildren().addAll(menuBuilder.buildMenuBar(), splitPane);

    Scene scene = new Scene(menuStack);
    // scene.getStylesheets().add(Alero.class.getResource("alero-highlights.css").toExternalForm());
    stage.setScene(scene);
    stage.setTitle(title);
    stage.setX(x);
    stage.setY(y);
    stage.setWidth(width);
    stage.setHeight(height);
    stage.show();
  }

  public void resetConsole() {
    console.clear();
    console.setText("Console opened " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()).toString() + "\n");
  }

  public void printConsole(String s) {
    console.appendText(s);
  }

  private StyleSpans<Collection<String>> computeHighlighting(String text) {
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

  /*
   * Styling handler below here
   */
  private final String[] KEYWORDS = new String[] { "stl", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
      "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
      "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "", "strictfp", "super", "switch", "synchronized",
      "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "fn", "lom", "system" };

  private final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
  private final String PAREN_PATTERN = "\\(|\\)";
  private final String BRACE_PATTERN = "\\{|\\}";
  private final String BRACKET_PATTERN = "\\[|\\]";
  private final String SEMICOLON_PATTERN = "\\;";
  private final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
  private final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/" // for whole text processing (text blocks)
      + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)"; // for visible paragraph processing (line by line)

  private final Pattern PATTERN = Pattern
      .compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>" + PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>"
          + BRACKET_PATTERN + ")" + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

  private class VisibleParagraphStyler<PS, SEG, S> implements Consumer<ListModification<? extends Paragraph<PS, SEG, S>>> {
    private final GenericStyledArea<PS, SEG, S> area;
    private final Function<String, StyleSpans<S>> computeStyles;
    private int prevParagraph, prevTextLength;

    public VisibleParagraphStyler(GenericStyledArea<PS, SEG, S> area, Function<String, StyleSpans<S>> computeStyles) {
      this.computeStyles = computeStyles;
      this.area = area;
    }

    @Override
    public void accept(ListModification<? extends Paragraph<PS, SEG, S>> lm) {
      if (lm.getAddedSize() > 0) {
        int paragraph = Math.min(area.firstVisibleParToAllParIndex() + lm.getFrom(), area.getParagraphs().size() - 1);
        String text = area.getText(paragraph, 0, paragraph, area.getParagraphLength(paragraph));

        if (paragraph != prevParagraph || text.length() != prevTextLength) {
          int startPos = area.getAbsolutePosition(paragraph, 0);
          Platform.runLater(() -> area.setStyleSpans(startPos, computeStyles.apply(text)));
          prevTextLength = text.length();
          prevParagraph = paragraph;
        }
      }
    }
  }

  public void menuAction(String s) {
    switch (s) {
    case "_Run":
      consoleln("Run");
      break;
    case "E_xit":
      Platform.exit();
      break;
    default:
      printConsole("Action " + s + " not yet implemented\n");
    }
  }

  public void console(String str) {
    printConsole(str);
  }

  public void consoleln(String str) {
    printConsole(str);
    printConsole("\n");
  }
}
