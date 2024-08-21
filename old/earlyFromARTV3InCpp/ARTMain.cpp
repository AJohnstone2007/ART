/********************************************************************************
 * ARTMain.cpp - mainline function ART generated parsers
 *
 *******************************************************************************/
#include<stdio.h>
#include <stdlib.h>
#include<time.h>
#include<string.h>
#define String const char*

extern void artParse(String stringInput, String inputFilename);

bool artIsInLanguage;
bool artIsInadmissable;
int artStartTime = 0;
int artSetupTime = 0;
int artLexTime = 0;
int artParseTime = 0;
String artParseAlgorithm = "";
int artTokenCount = 0;

char * fileRead(char* filename) {
  FILE *f = fopen(filename, "r");
  if (f == NULL) {
    printf("Unable to open file '%s'", filename);
    exit(1);
  }

// Size the file
  int c = ' ';
  int size = 0;
  while (c != EOF) {
    size++;
    c = getc(f);
  }

  size--;

  // printf("Inpuut contains %i characters\n", size);

  char* buffer = (char*) malloc(sizeof(char) * (size + 1));
  f = fopen(filename, "r");

  for (int i = 0; i < size; ++i) {
    buffer[i] = (char) getc(f);
  }
  buffer[size] = 0;

  return buffer;
}

  int findFinalDot(String s) {
    char *tmp;
    
    for (tmp = ((char*)s) + strlen(s); tmp>=s && *tmp != '.'; tmp--)
      ;
    if (tmp<s) return strlen(s);

    return tmp - s;
  }

  bool suffixIs_acc(String s){
    int dotPosition = findFinalDot(s);
    int length = strlen(s);

    return dotPosition + 3 < length && s[dotPosition + 1] == 'a' && s[dotPosition + 2] == 'c' && s[dotPosition + 3] == 'c';
  }

  bool suffixIs_rej(String s){
    int dotPosition = findFinalDot(s);
    int length = strlen(s);

    return dotPosition + 3 < length && s[dotPosition + 1] == 'r' && s[dotPosition + 2] == 'e' && s[dotPosition + 3] == 'j';
  }

  void artLog(String inputFilename) {
    FILE *logStream = fopen("log.csv", "w");

    String status = "--";

    if (suffixIs_acc(inputFilename) || suffixIs_rej(inputFilename)) {
      status = "bad";
      if ((suffixIs_acc(inputFilename) && artIsInLanguage) || (suffixIs_rej(inputFilename) && !artIsInLanguage)) status = "good";
    }

    time_t currentTime = time(NULL);
    char* timeString = ctime(&currentTime);
    timeString[strlen(timeString) - 1] = 0;
    fprintf(logStream, "%s,BNF,%s,%s,%s,%s,%i,Setup, %.3f,Lex, %.3f,Parse,%3f\n",
      artParseAlgorithm, inputFilename,
        artIsInLanguage ? "accept" : "reject", status, timeString, artTokenCount, ((double)artSetupTime - (double) artLexTime)/(double) CLOCKS_PER_SEC, ((double)artLexTime - (double) artStartTime)/(double) CLOCKS_PER_SEC, ((double)artParseTime - (double)-artSetupTime)/(double) CLOCKS_PER_SEC);

      printf("%s,BNF,%s,%s,%s,%s,%i,Setup, %.3f,Lex, %.3f,Parse,%3f\n",
      artParseAlgorithm, inputFilename,
        artIsInLanguage ? "accept" : "reject", status, timeString, artTokenCount, ((double)artSetupTime - (double) artLexTime)/(double) CLOCKS_PER_SEC, ((double)artLexTime - (double) artStartTime)/(double) CLOCKS_PER_SEC, ((double)artParseTime - (double)-artSetupTime)/(double) CLOCKS_PER_SEC);

    fclose(logStream);
}

int main (int argc, char *argv[]) {

  if (argc < 2){
    printf("No input filename supplied");
    exit(1);
  }

  char* input = fileRead(argv[1]);

  // printf("File %s contains\n%s\n\n", argv[1], input);
  artParse(input, argv[1]);
  artLog(argv[1]);
}

