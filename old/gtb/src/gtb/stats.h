/********************************************************************************
 *
 * stats.h - timing and statistics gathering support
 *
 *******************************************************************************/
#include <chrono>
#include <cstddef>
#ifndef STATS_H
#define STATS_H

using namespace std;
using namespace chrono;

extern bool artInadmissable; // set when, say, a BNF parser is called with an EBNF grammar
extern bool artIsInLanguage;

extern time_point<steady_clock> startTime;
extern time_point<steady_clock> setupTime;
extern time_point<steady_clock> lexTime;
extern time_point<steady_clock> lexChooseTime;
extern time_point<steady_clock> parseTime;
extern time_point<steady_clock> parseChooseTime;
extern time_point<steady_clock> derivationSelectTime;
extern time_point<steady_clock> termGenerateTime;
extern time_point<steady_clock> semanticsTime;

extern long endPoolAllocated;
extern int inputStringLength;
extern int inputTokenLength;
extern const char* algorithm;

void loadAlgorithm(const char* s);
void loadInputStringLength(int i);
void loadInputTokenLength(int i);
void loadSetupTime();
void loadLexTime();
void loadLexChooseTime();
void loadParseTime();
void loadParseChooseTime();
void loadDerivationSelectTime();
void loadTermGenerateTime();
void loadSemanticsTime();
void loadEndPoolAllocated(long m);
void loadAccept(bool b);

void resetStats();
void artLog();

#endif
