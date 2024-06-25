#ifndef DEFAULTTEXTHANDLER_H
#define DEFAULTTEXTHANDLR_H
class DefaultTextHandler: public TextHandler {
  int fatalCount;
  int errorCount;
  int warningCount;

  public:

  DefaultTextHandler() {
    fatalCount = 0;
    errorCount = 0;
    warningCount = 0;
  }

  void errorReport() {
    printf("%i error%s and %i warning%s\n", errorCount, (errorCount == 1 ? "" : "s"), warningCount, (warningCount == 1 ? "" : "s"));
  }

  void text(TextLevel level, int index, char* buffer, const char *msg) {
    switch (level) {
    case TL_FATAL:
    case TL_FATAL_ECHO:
      fatalCount++;
      printf("Fatal: %s", msg);
      errorReport();
      exit(1);
      break;
    case TL_ERROR:
    case TL_ERROR_ECHO:
      errorCount++;
      printf("Error: %s", msg);
      break;
    case TL_WARNING:
    case TL_WARNING_ECHO:
      warningCount++;
      printf("Warning: %s", msg);
      break;
    case TL_INFO:
    case TL_INFO_ECHO:
      printf("%s", msg);
      break;
    case TL_TRACE:
    case TL_TRACE_ECHO:
      fprintf(stderr, "%s", msg);
      break;
    case TL_OUTPUT:
    case TL_OUTPUT_ECHO:
      printf("%s", msg);
      break;
    }
  }
};
#endif

