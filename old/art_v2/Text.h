#ifndef TEXT_H
#define TEXT_H
#include "TextLevel.h"
#include "TextHandler.h"
#include "DefaultTextHandler.h"


class Text {
  TextHandler *handler;

  public:
   Text() {
    this->handler = new DefaultTextHandler();
  }

  Text(TextHandler *handler) {
    this->handler = handler;
  }

  /* Support for echoing the current line */
  int lineNumber(int index, const char* buffer) {
    if (buffer == NULL || index < 0) return 0;

    if (index >= strlen(buffer)) index = strlen(buffer) - 1;

    int lineCount = 1;

    for (int tmp = 0; tmp < index; tmp++)
      if (buffer[tmp] == '\n') lineCount++;

    return lineCount;

  }

  int columnNumber(int index, const char* buffer) {
    int columnCount = 1;

    if (buffer == NULL || index < 0) return 0;

    if (index >= strlen(buffer)) index = strlen(buffer) - 1;

    while (index > 0 && index != '\n') {
      index--;
      columnCount++;
    }

    return columnCount;
  }

  void echo(int index, const char* buffer) {
    if (buffer == NULL || index < 0) return;

    cout << lineNumber(index, buffer);

    int echoColumn = columnNumber(index, buffer);

    for (int tmp = index + 1 - echoColumn; buffer[tmp] != '\n' && buffer[tmp] != '\0'; tmp++)
      cout << buffer[tmp];

    cout << "\n------";

    for (int tmp = 0; tmp < echoColumn; tmp++)
      cout << '-';

    cout << "^\n";
  }

  void conditionalEcho(TextLevel level, int index, const char* buffer) {
    if (level == TL_FATAL_ECHO || level == TL_ERROR_ECHO || level == TL_WARNING_ECHO || level == TL_INFO_ECHO
        || level == TL_TRACE_ECHO || level == TL_OUTPUT_ECHO) echo(index, buffer);
  }

  /* Output functions with buffer and index paramaters intended to allow error location in messages */

  void print(TextLevel level, int index, const char* buffer, const char* msg) {
    conditionalEcho(level, index, buffer);
//    handler->text(level, index, buffer, msg);
  }

  void println(TextLevel level, int index, const char* buffer, const char* msg) {
    conditionalEcho(level, index, buffer);
//    handler->text(level, index, buffer, msg + "\n");
  }

  void printf(TextLevel level, int index, const char* buffer, const char* formatString, int args) {
    conditionalEcho(level, index, buffer);
//    handler->text(level, index, buffer, const char*.format(formatString, args));
  }

  /* Output functions for simple messages */

  void print(TextLevel level, const char* msg) {
//    printf(level, 0, NULL, msg);
  }

  void println(TextLevel level, const char* msg) {
//    printf(level, 0, NULL, msg + "\n");
  }

  void printf(TextLevel level, const char* formatString, int args) {
//    printf(level, 0, NULL, const char*.format(formatString, args));
  }

  /* Convenience functions that just send strings to the standard output */

  void print(const char* msg) {
//    printf(OUTPUT, msg);
  }

  void println(const char* msg) {
//    printf(OUTPUT, msg + "\n");
  }

  void printf(const char* formatString, int args) {
//    printf(OUTPUT, const char*.format(formatString, args));
  }

  /* Some useful static methods that are used by the ART parser generator to avoid language clashes */

#if 0
  bool isValidJavaID(const char* string) {
    int len = strlen(string);
    for (int i = 0; i < len; i++)
      if (!Character.isJavaIdentifierPart(string.charAt(i))) return false;
    return true;
  }

  const char* toJavaID(const char* string) {
    if (isValidJavaID(string)) return string;

    const char* retString = "";

    int len = string.length();
    for (int i = 0; i < len; i++) {
      char c = string.charAt(i);

      if (Character.isJavaIdentifierPart(c))
        retString += string.charAt(i);
      else
        retString += ("_" + i);
    }

    return retString;
  }

  bool isValidJavaLiteral(const char* string) {
    int len = string.length();
    for (int i = 0; i < len; i++) {
      char c = string.charAt(i);

      if (c == '\\' || c < 32 || c > 0x7E) return false;
    }
    return true;
  }

  const char* toJavaLiteral(const char* string) {
    if (isValidJavaLiteral(string)) return string;

    const char* retString = "";

    int len = string.length();
    for (int i = 0; i < len; i++) {
      char c = string.charAt(i);
      if (c == '\\')
        retString += "\\\\";
      else if (c < 32)
        switch (c) {
        case '\n':
          retString += "\\n";
          break;
        case '\r':
          retString += "\\r";
          break;
        case '\t':
          retString += "\\t";
          break;
        default:
          retString += "\\x" + Integer.toHexString(c);
          break;
        }
      else if (c < 0x7F)
        retString += c;
      else if (c == 0x7F)
        retString += "\\x7F";
      else
        retString += "\\u" + Integer.toHexString(c);
    }
    return retString;
  }
#endif
};
#endif

