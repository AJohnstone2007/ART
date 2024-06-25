/*******************************************************************************
*
* gllTest.cpp - test harness for ART generated GLL parsers
*
* (c) Adrian Johnstone 2013
*
*******************************************************************************/
#include "bccgcc.h"
#include "GLLParser.h"

#if defined(PAGEFAULTS)
#include <sys/resource.h>
#endif

#if defined(NANOSECOND)
static double compositeDelay(timespec startTime, timespec stopTime){
  double compositeStartTime = (double) startTime.tv_sec + (1E-9 * (double) startTime.tv_nsec);
  double compositeStopTime = (double) stopTime.tv_sec + (1E-9 * (double) stopTime.tv_nsec);

  return compositeStopTime - compositeStartTime;
}
#endif

static void log(char* inputFilename, GLLParser* parser, bool initial, bool console) {
  const char* inputFiletype = inputFilename + strlen(inputFilename);

  while (*inputFiletype != '.' && inputFiletype != inputFilename)
    inputFiletype--;

  if (*inputFiletype == '.')
    inputFiletype++;

  if (!console && !((strncmp(inputFiletype, "acc", 3) == 0) || (strncmp(inputFiletype, "rej", 3) == 0)))
      return;

  const char* shortInputFilename = inputFilename + strlen(inputFilename);
  while (*shortInputFilename != '\\' && *shortInputFilename != '/' && shortInputFilename != inputFilename)
    shortInputFilename--;

  if (*shortInputFilename != '\\' || *shortInputFilename != '/')
    shortInputFilename++;
    
  FILE* output = console ? stdout : fopen("accept_reject.csv", "w");

  const char* status = "good";
  if (((strncmp(inputFiletype, "acc", 3) == 0) && !parser->inLanguage) || ((strncmp(inputFiletype, "rej", 3) == 0) && parser->inLanguage))
    status = "bad";
  if (initial)
    status = "crash";

  time_t now = time(0);
  char *dateTime = ctime(&now);
  dateTime[strlen(dateTime) - 1] = 0;  //Suppress terminating \n

  fprintf(output, "%s,%s,%s,%s,%s,%s,%s,%d,%f",
          parser->grammarName,
          parser->startSymbolName,
          shortInputFilename,
          parser->buildOptions,
          parser->inLanguage ? "accept": "reject",
          initial ? "crash" : status,
          dateTime,
          parser->inputLength - 1, //suppress terminating EOS

          ((double) parser->stopTime - parser->startTime) / CLOCKS_PER_SEC);

#if defined(NANOSECOND)
  fprintf(output, ",%f,%f",
          compositeDelay(parser->startTime_CLOCK_MONOTONIC, parser->stopTime_CLOCK_MONOTONIC),
          compositeDelay(parser->startTime_CLOCK_PROCESS_CPUTIME_ID, parser->stopTime_CLOCK_PROCESS_CPUTIME_ID));
#else
  fprintf(output, ",0,0");
#endif

#if defined(PAGEFAULTS)
  rusage usage;

  if (getrusage(RUSAGE_SELF, &usage) != 0)
    printf("!!! Error in getrusage() function\n");

  fprintf(output, ",%ld,%ld,%ld", (long int) usage.ru_maxrss, (long int) usage.ru_minflt, (long int) usage.ru_majflt);
#else
  fprintf(output, ",0,0,0");
#endif

#if defined(STATISTICS)
  if (!initial)
  fprintf(output, ",%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld,%ld",
          parser->sppfNodeHistogram->weightedSumBuckets(),
          parser->sppfPackNodeHistogram->weightedSumBuckets(),
          parser->gssNodeHistogram->weightedSumBuckets(),
          parser->gssEdgeHistogram->weightedSumBuckets(),
          parser->popElementHistogram->weightedSumBuckets(),
          parser->descriptorHistogram->weightedSumBuckets(),
          parser->testRepeatElementHistogram->weightedSumBuckets(),
          parser->sppfNodeFinds,
          parser->sppfPackNodeFinds,
          parser->gssNodeFinds,
          parser->gssEdgeFinds,
          parser->popElementFinds,
          parser->descriptorFinds,
          parser->testRepeatElementFinds);
#endif

#if defined(HISTOGRAMS)
  if (!initial) {
    fprintf(output, ","); parser->sppfNodeHistogram->print(output, false);
    fprintf(output, ","); parser->sppfPackNodeHistogram->print(output, false);
    fprintf(output, ","); parser->gssNodeHistogram->print(output, false);
    fprintf(output, ","); parser->gssEdgeHistogram->print(output, false);
    fprintf(output, ","); parser->popElementHistogram->print(output, false);
    fprintf(output, ","); parser->descriptorHistogram->print(output, false);
    fprintf(output, ","); parser->testRepeatElementHistogram->print(output, false);
  }
#endif

  fprintf(output, "\n");

  if (!console)
    fclose(output);

  if (strstr(inputFilename, "deliberatelyCrash") != NULL) // This is used to test the test system: it artificially creates an abnormal termination
    exit(1);
}

int main (int argc, char *argv[]){
  // Open the file named by the first parameter
  if (argc < 2) {
    printf("Fatal error: no input file specified\n");
    exit(1);
  }

  char *inputFilename = argv[1];

  ifstream inputFile(inputFilename);
#if defined(__BORLANDC__)
  if (inputFile == NULL) {
#else
  if (!inputFile.is_open()) {
#endif
    printf("Fatal error: unable to open input file '%s'\n", inputFilename);;
    exit(1);
  }

  //Walk input to see how long it is
  int length = 0;
  while (!inputFile.eof()) {
    length++;
    inputFile.get();
  }
  length--; // supppress EOF character

  char *inputString = new char[length + 1];  // space for terminating 0
  inputFile.close();

  // Walk again, reading in file contents
  ifstream readFile(inputFilename);

  for (int temp = 0; !readFile.eof(); temp++)
    inputString[temp] = (char) readFile.get();

  inputString[length] = 0;
  readFile.close();

#define DISPLAY_LENGTH 50
  printf("Input length %i characters: ", length);
  int temp;
  for (temp = 0; temp < DISPLAY_LENGTH && temp < length; temp++)
    printf("%c", inputString[temp]);
  if (temp == DISPLAY_LENGTH)
    printf("...");
  printf("\n");

  GLLParser *parser = new GLLParser();
  log(argv[1], parser, true, false);
  parser->parse(inputString);
  parser->computeStatistics();
  log(argv[1], parser, false, true);
  log(argv[1], parser, false, false);
}

