#include<stdlib.h>
#include<stdio.h>
#include<ctype.h>
#define String const char*
#define public
#define private
#define static
#define final
#define boolean bool
#define null NULL
#define throws
#define FileNotFoundException
//#define ARTTRACE

//#include "ARTStaticSlotArray.cpp"
#include "ARTStaticEarleyTable.h"
#include "ARTPool.cpp"
#include "ARTLexer.cpp"
#include "ARTMain.cpp"

extern boolean artIsInLanguage;
extern bool artIsInadmissable;
extern int artStartTime;
extern int artSetupTime;
extern int artLexTime;
extern int artParseTime;
int rSetRemovals;
private final int earleyItemPerLevelBucketCount = 809;
private final int upsilonBucketCount = 2000011;

  // An Earley configuration is nfaVertex x inputIndex, that is a 2:0
  private final int earleyConfigurationNFAVertexOffset = 0;
  private final int earleyConfigurationInputIndexOffset = 1;

  // An Earley Table Chi Set element is ChiSetIndex x i X j X k, that is a 4:0
  private final int chiSetElementIndex = 0;
  private final int chiSetElementI = 1;
  private final int chiSetElementK = 2;
  private final int chiSetElementJ = 3;

  private int upsilon;
  private int inputLength;

  private int *R;
  private int *E;
  private int *EList;
  private int *rdn;

  private void add(int h, int x, int i, int k, int j, int t) {
    if (x == eoS) return;

    int* tmpChiSet = chiSetCache[epnMap[h][x]];
    if (tmpChiSet != null && *tmpChiSet != 0) { // skip if cache element is empty
      // printf("Adding to Upsilon via epn (%i, %i, %i, %i)\n", epnMap[h][x], i, k, j);
      mapFind_4_0(upsilon, epnMap[h][x], i, k, j);
      // upsilon.add(new ARTChiBSR(epnMap[h][x], i, k, j));
    }
    tmpChiSet = chiSetCache[eeMap[h][x]];
    if (tmpChiSet != null && *tmpChiSet != 0) { // skip if cache element is empty
      mapFind_4_0(upsilon, eeMap[h][x], i, j, j);
      //      printf("Adding to Upsilon via ee (%i, %i, %i, %i)\n", eeMap[h][x], i, j, j);
      // upsilon.add(new ARTChiBSR(eeMap[h][x], i, j, j));
    }

    int H = outEdgeMap[h][x];
    if (H == -1) return;
    if (!ARTSelect[H][t]) return;

    if (H != -1) {
      // ARTEarleyConfiguration tmpConfiguration = new ARTEarleyConfiguration(outEdgeMap[h][x], i);
      if (mapLookup_2(E[j], H, i) == 0) {
        // printf("Adding (%i, %i) to R[%i]\n", H, i, j);
        mapFind_2_0(E[j], H, i);
        listAdd_2(EList[j], H, i);
        listAdd_2(R[j], H, i);
      }
    }
  }

  void artParse(String stringInput, String inputFilename) {
    artParseAlgorithm = "ARTEarleyTableIndexedPool (C)";
    boolean useRDNSet = true;
    rSetRemovals = 0;

    artIsInLanguage = false;

    int *input = dynamicLexicaliseLongestMatch(stringInput, 1);

    for (inputLength = 1; input[inputLength] != 0; inputLength++)
      ;

    inputLength--;

      poolInit(21, 2048); // 1024 x 1Mlocation blocks: at 32-bit integers that 4G of memory when fully
      chiSetCacheInitialiser();
      redSetCacheInitialiser();
      rLHSInitialiser();

      // Declare arrays of sets representing R and E (curly E in document) and rdn
      R = new int[inputLength + 1];
      E = new int[inputLength + 1];
      EList = new int[inputLength + 1];
      rdn = new int[inputLength + 1];
      for (int i = 0; i < inputLength + 1; i++) {
        R[i] = listMake();
        E[i] = mapMake(earleyItemPerLevelBucketCount);
        EList[i] = listMake();
        rdn[i] = mapMake(earleyItemPerLevelBucketCount);
      }
      upsilon = mapMake(upsilonBucketCount);

      // E_0 = R_0 = { (G_0,0) }
      listAdd_2(R[0], 0, 0);
      mapFind_2_0(E[0], 0, 0);
      listAdd_2(EList[0], 0, 0);

      artSetupTime = clock();

      // for (0 \le j \= n)
      for (int j = 0; j <= inputLength; j++) {
#ifdef ARTTRACE
printf("Level %i\n",j);
#endif

        // while ( R_j \ne \emptyset)
        while (poolGet(R[j]) != 0) {
          // Remove an item (G, k) from R_j
          int c = listRemove(R[j]);
          rSetRemovals++;

          int G = poolGet(c + earleyConfigurationNFAVertexOffset);
          int k = poolGet(c + earleyConfigurationInputIndexOffset);

#ifdef ARTTRACE
printf("At index position %i, removed from R configuration G%i, %i\n", j, G, k);
#endif
          // if ( k != j)
          if (k != j) {
            // for(X \in NFA_2(G; a_{j+1}) {
            int successorElement = input[j + 1]; // successorElement = a_{j+1}
            if (successorElement == eoS) successorElement = epsilon;
//            int* tmpRed = redSetCache[redMap[G][successorElement]];
            int* tmpRed = rLHS[G];
            if (tmpRed != null) {
              for (int xi = 0; ; xi++) {
                int x = tmpRed[xi];
                if (x == 0) break; // In C, the sets are zero terminated
                // ARTEarleyRDNSetElement rdnSetElement = new ARTEarleyRDNSetElement(x, k);
                if (useRDNSet) {
                  if (mapLookup_2(rdn[j], x, k) != 0) {
                    continue;
                  }
                  mapFind_2_0(rdn[j], x, k);
                } // for ((H, i) \in E_k)

                // int offset = poolGet(e + earleyItemIndexOffset);
                // int slot = poolGet(e + earleyItemSlotOffset);

//                for (int e = mapIteratorFirst1(E[k]); e != 0; e = mapIteratorNext1()) {
                for (int e = poolGet(EList[k]); e != 0; e = poolGet(e)) {
                  // for (ARTEarleyConfiguration e : E[k].getSet()) {
                  int H = poolGet(e + 1 + earleyConfigurationNFAVertexOffset);
                  int i = poolGet(e + 1 + earleyConfigurationInputIndexOffset);
                  // ADD(H, X, i, k, j)
                  add(H, x, i, k, j, input[j+1]);
                }
              }
            }
          }
          // ADD(G, \epsilon, j, j, j)
          add(G, epsilon, j, j, j, input[j+1]);

          // if (j < n) ADD(G, a_{j+1}, k, j, j + 1)
          if (j < inputLength) {
            add(G, input[j + 1], k, j, j + 1, input[j+2]);
          }
        }
      }

      artParseTime = clock();

      artIsInLanguage = false;

      int upsilonCardinality = 0;
      for (int pp = mapIteratorFirst1(upsilon); pp != 0; pp = mapIteratorNext1()) {
/*
        int xe = poolGet(pp + chiSetElementIndex);
        int xi = poolGet(pp + chiSetElementI);
        int xk = poolGet(pp + chiSetElementK);
        int xj = poolGet(pp + chiSetElementJ);
*/
	//        printf("Acceptance testing: pp[%i]=(%i, %i, %i, %i) with inputLength = %i\n", pp, xe, xi, xk, xj, inputLength);

        upsilonCardinality++;
        if (poolGet(pp + chiSetElementI) == 0 && poolGet(pp + chiSetElementJ) == inputLength)
          for (int *ppc = chiSetCache[poolGet(pp + chiSetElementIndex)]; *ppc != 0; ppc++)
            for (const int *ae = acceptingProductions; *ae != 0; ae++) {
//            printf("Acceptance testing: pp[%i], ppc[%i] = %i\n", pp, ppc, *ppc);
            artIsInLanguage |= (*ppc == *ae);
          }
      }
//      printf("(CPP) EarleyTableIndexedPool %s in %.4fms\n", artIsInLanguage ? "accept" : "reject",  ((double) (artParseTime-artSetupTime)/ (double) CLOCKS_PER_SEC)*1000.00);
//      printf("Total removals from R =  %i\n", rSetRemovals);
//      printf("Final raw P with Chi set based BSRs: |PChi| = %i\n", upsilonCardinality);
}

