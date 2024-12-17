package uk.ac.rhul.cs.csle.art.old.v4.util.text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import uk.ac.rhul.cs.csle.art.old.v4.core.ARTUncheckedException;

public class ARTTextHandlerFile extends ARTTextHandler {
  PrintWriter printWriter;

  public ARTTextHandlerFile(String filename) throws ARTUncheckedException {
    try {
      printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("Unable to open text handler on file '" + filename + "'");
    } catch (UnsupportedEncodingException e) {
      throw new ARTUncheckedException("UTF-8 unsupported for writing to file '" + filename + "'");
    }
  }

  @Override
  public void close() {
    printWriter.close();
  }

  @Override
  protected void text(ARTTextLevel level, int index, String buffer, String msg) {
    switch (level) {
    case FATAL:
    case FATAL_ECHO:
      printWriter.print("Fatal: " + msg);
      errorReport();
      System.exit(1);
      break; // Quiten dataflow analysis
    case ERROR, ERROR_ECHO:
      errorCount++;
      printWriter.print("Error: " + msg);
      break;
    case WARNING, WARNING_ECHO:
      warningCount++;
      printWriter.print("Warning: " + msg);
      break;
    case INFO, INFO_ECHO:
      printWriter.print(msg);
      break;
    case TRACE, TRACE_ECHO:
      printWriter.print(msg);
      break;
    case OUTPUT, OUTPUT_ECHO:
      printWriter.print(msg);
      break;
    }
  }
}
