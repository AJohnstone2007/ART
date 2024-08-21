/********************************************************************************
 *
 * stats.h - timing and statistics gathering support DUMMY
 *
 *******************************************************************************/
#ifndef STATS_H
#define STATS_H
void loadAlgorithm(const char* s){printf("Algorithm %s\n", s); }
void loadInputStringLength(int i){}
void loadInputTokenLength(int i){}
void loadSetupTime(){}
void loadLexTime(){}
void loadLexChooseTime(){}
void loadParseTime(){}
void loadParseChooseTime(){}
void loadDerivationSelectTime(){}
void loadTermGenerateTime(){}
void loadSemanticsTime(){}
void loadEndPoolAllocated(long m){}
void loadAccept(bool b){printf("Accept: %s\n", b?"true":"false"); }

void resetStats(){}
void artLog(){printf("ARTlog");}

#endif
