/* EarleyTable generated byARTEarleyTableDataIndexed*/

#include<stdlib.h>
/* Slot Array generated by ARTSLotArray.java */

const int eoS = 0;
const int epsilon = 2;
const int startSymbol = 4;
const int firstSlotNumber = 6;
const char* symbolStrings[21] = {
  "$",	// 0
  "b",	// 1
  "#",	// 2
  "null",	// 3
  "ART_AUGMENTED",	// 4
  "S",	// 5
  "Production ART_AUGMENTED ::= S ",	// 6
  "ART_AUGMENTED ::= . S ",	// 7
  "ART_AUGMENTED ::= S .",	// 8
  "Production S ::= S S S ",	// 9
  "S ::= . S S S ",	// 10
  "S ::= S . S S ",	// 11
  "S ::= S S . S ",	// 12
  "S ::= S S S .",	// 13
  "Production S ::= S S ",	// 14
  "S ::= . S S ",	// 15
  "S ::= S . S ",	// 16
  "S ::= S S .",	// 17
  "Production S ::= 'b' ",	// 18
  "S ::= . 'b' ",	// 19
  "S ::= 'b' .",	// 20
};

const int acceptingProductions[] = {
  9,
  14,
  18,
 0};
const int outEdgeMap[5][21] = {
  {
    -1,
    1,
    -1,
    -1,
    -1,
    2,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
  },
  {
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
  },
  {
    -1,
    -1,
    0,
    -1,
    -1,
    3,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
  },
  {
    -1,
    -1,
    0,
    -1,
    -1,
    4,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
  },
  {
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
    -1,
  },
};
const int epnMap[5][21] = {
  {
    0,
    2,
    1,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    3,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    4,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
};
const int eeMap[5][21] = {
  {
    0,
    1,
    1,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
};
const int redMap[5][21] = {
  {
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    1,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    1,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
  {
    0,
    1,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
  },
};
const bool ARTSelect[5][21] = {
  {
    false,
    true,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
  },
  {
    true,
    true,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
  },
  {
    false,
    true,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
  },
  {
    true,
    true,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
  },
  {
    true,
    true,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
  },
};
int **chiSetCache;
void chiSetCacheInitialiser(void){
  chiSetCache = (int**) calloc(5, sizeof(int*));
  chiSetCache[1] = (int*) calloc(1, sizeof(int));
  chiSetCache[2] = (int*) calloc(2, sizeof(int));
  chiSetCache[2][0] = 18;
  chiSetCache[3] = (int*) calloc(3, sizeof(int));
  chiSetCache[3][0] = 12;
  chiSetCache[3][1] = 14;
  chiSetCache[4] = (int*) calloc(2, sizeof(int));
  chiSetCache[4][0] = 9;
};
int **redSetCache;
void redSetCacheInitialiser(void){
  redSetCache = (int**) calloc(2, sizeof(int*));
  redSetCache[1] = (int*) calloc(2, sizeof(int));
  redSetCache[1][0] = 5;
};
int **rLHS;
void rLHSInitialiser(void){
  rLHS = (int**) calloc(6, sizeof(int*));
  rLHS[0] = (int*) calloc(1, sizeof(int));
  rLHS[1] = (int*) calloc(2, sizeof(int));
  rLHS[1][0] = 5;
  rLHS[2] = (int*) calloc(1, sizeof(int));
  rLHS[3] = (int*) calloc(2, sizeof(int));
  rLHS[3][0] = 5;
  rLHS[4] = (int*) calloc(2, sizeof(int));
  rLHS[4][0] = 5;
};