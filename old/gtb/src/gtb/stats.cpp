/********************************************************************************
 *
 * stats.cpp - timing and statistics gathering support
 *
 *******************************************************************************/
#include <chrono>
#include <cstddef>
#include <iomanip>
#include <iostream>
#include "stats.h"
 
using namespace std;
using namespace chrono;

bool artInadmissable; // set when, say, a BNF parser is called with an EBNF grammar
bool artAccept;

time_point<steady_clock> startTime;
time_point<steady_clock> setupTime;
time_point<steady_clock> lexTime;
time_point<steady_clock> lexChooseTime;
time_point<steady_clock> parseTime;
time_point<steady_clock> parseChooseTime;
time_point<steady_clock> derivationSelectTime;
time_point<steady_clock> termGenerateTime;
time_point<steady_clock> semanticsTime;

long endPoolAllocated;
int inputStringLength;
int inputTokenLength;
const char* algorithm;

void loadAlgorithm(const char* s) { algorithm = s; }
void loadInputStringLength(int i) { inputStringLength = i; }
void loadInputTokenLength(int i) { inputTokenLength = i; }
void loadSetupTime() { setupTime = steady_clock::now(); }
void loadLexTime() { lexTime = steady_clock::now(); }
void loadLexChooseTime() { lexChooseTime = steady_clock::now(); }
void loadParseTime() { parseTime = steady_clock::now(); }
void loadParseChooseTime() { parseChooseTime = steady_clock::now(); }
void loadDerivationSelectTime() { derivationSelectTime = steady_clock::now(); }
void loadTermGenerateTime() { termGenerateTime = steady_clock::now(); }
void loadSemanticsTime() { semanticsTime = steady_clock::now(); }
void loadEndPoolAllocated(long m) { endPoolAllocated = m; }
void loadAccept(bool b) { artAccept = b; }

void normaliseTimes() {
  if (setupTime < startTime) setupTime = startTime;
  if (lexTime == startTime) lexTime = setupTime;
  if (lexChooseTime == startTime) lexChooseTime = lexTime;
  if (parseTime == startTime) parseTime = lexChooseTime;
  if (parseChooseTime == startTime) parseChooseTime = parseTime;
  if (derivationSelectTime == startTime) derivationSelectTime = parseChooseTime;
  if (termGenerateTime == startTime) termGenerateTime = derivationSelectTime;
  if (semanticsTime == startTime) semanticsTime = termGenerateTime;
}

void resetStats() {
  startTime = steady_clock::now();
  setupTime = lexTime = lexChooseTime = parseTime = parseChooseTime = derivationSelectTime = termGenerateTime = semanticsTime = startTime;
  endPoolAllocated = 0;
  algorithm = "---";
}

double interval(time_point<steady_clock> start, time_point<steady_clock> end){
  return duration<double, milli> {end - start}.count();
}

void artLog() {
  normaliseTimes();

    printf("%i,%s,%s,OK,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%i,%i,1,%li\n",
            inputStringLength,
            algorithm,
            (artAccept ? "accept" : "reject"),
            interval(startTime, setupTime),
            interval(setupTime, lexTime),
            interval(lexTime, lexChooseTime), 
            interval(lexChooseTime, parseTime),
            interval(parseTime, parseChooseTime),
            interval(parseChooseTime, derivationSelectTime),
            interval(derivationSelectTime, termGenerateTime),
            interval(termGenerateTime, semanticsTime),
            inputTokenLength,
            (inputTokenLength - 1),
            endPoolAllocated);
  }
