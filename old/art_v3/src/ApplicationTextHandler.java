import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandler;
import uk.ac.rhul.cs.csle.art.util.text.ARTTextLevel;

public class ApplicationTextHandler extends ARTTextHandler {
  @Override
  protected void text(ARTTextLevel level, int index, String buffer, String msg) {
    System.out.printf("Message level %s at index %d: %s\n", level, index, msg);
  }

  @Override
  public void close() {
  }

}
