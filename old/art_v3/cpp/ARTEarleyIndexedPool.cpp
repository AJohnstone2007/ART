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

#include "ARTStaticSlotArray.h"
#include "ARTPool.cpp"
#include "ARTMain.cpp"

extern boolean artIsInLanguage;
extern bool artIsInadmissable;
extern int artStartTime;
extern int artSetupTime;
extern int artLexTime;
extern int artParseTime;

  private int inputLength;
  private int epsilonSPPFNode;

  // An accepting slot is a single integer index into the tables in the
  // ARTSlotArray structure: that is a 1:0
  private int acceptingSlotsSet;

  // An Earley item is a slot x input position x SPPF node : nothing, that is a
  // 3:0
  private final int earleyItemSlotOffset = 0; // An element of the enumeration
  private final int earleyItemIndexOffset = 1;
  private final int earleyItemSPPFNodeOffset = 2;

  private int* eSets; // An integer array holding an array of pool sets
  private int qSet, qPrimeSet, rSet, vSet;

  // An hMap is a map from nonterminal |-> SPPF node, that is a 1:1
  private final int hMapNonterminalOffset = 0;
  private final int hMapSPPFNodeOffset = 1;

  private int hMap;

  // An SPPF node is slot/nonterminal/terminal/epsilon x leftExtent x rightExtent
  // : (small) set of packedNodes, that is a 3:1
  private final int sppfNodeLabelOffset = 0; // An element of the enumeration
  private final int sppfNodeLeftExtentOffset = 1; // An integer offset
  private final int sppfNodeRightExtentOffset = 2; // An integer offset
  private final int sppfFamilyOffset = 3; // A table map with the packed nodes in it

  // A packed node is a pair of SPPF nodes, that is a 2:0
  private final int packedNodeLeftChildOffset = 0;
  private final int packedNodeRightChildOffset = 1;
  private int sppf;

  // Hash table sizes
  private final int acceptingSlotsBucketCount = 20;
  private final int sppfNodePerLevelBucketCount = 300;
  private final int sppfNodeFullBucketCount = 500000;
  private final int sppfNodeFamilyBucketCount = 5;
  private final int earleyItemPerLevelBucketCount = 40;
  private final int qPrimeBucketCount = 40;

  // For C...

  public boolean isNonterminal(int symbol) {
   return symbol > epsilon && symbol < firstSlotNumber;
  }

  public boolean isNonterminalOrEpsilon(int symbol) {
   return symbol == 0 || (symbol >= epsilon && symbol < firstSlotNumber);
  }

  public boolean isTerminal(int symbol) {
   return symbol > eoS && symbol < epsilon;
  }

  // End of language customisation

  private boolean inSigmaN(int p) {
    return isNonterminalOrEpsilon(slotRightSymbols[p]);
  }

  // This is a quick and dirty lexicaliser which does not support ART's special
  // lexical features or character level tokens - startIndex specifies the first
  // live
  // elemeng of ret[]h
  // Used for quick test of simple parsers

  #if (0)
  public int strlen(const char* str) {
    int ret = 0;
    if (str == null) return 0;
    while (str[ret] != 0)
      ret++;
    return ret;
  }
  #endif

  private boolean isSubstringAt(const char* full, int start, const char* sub) {
    int subStart = 0;

    while (full[start] != 0 && sub[subStart] != 0 && full[start] == sub[subStart]) {
      start++;
      subStart++;
    }

    return sub[subStart] == 0;
  }

  private String charsToString(const char* c) {
    String ret = "";
    for (int i = 0; c[i] != 0; i++)
      ret += c[i];
    return ret;
  }

    int inputLengthFromLexer;
    int* dynamicLexicaliseLongestMatch(String input, int startIndex) {
    // End of language customisation

    int inputStringLength = strlen(input);
    int* ret = null;
    int stringStart, retIndex;

    for (int pass = 0; pass < 2; pass++) {
      retIndex = stringStart = 0;
      for (int i = startIndex; i > 0; i--) {
        if (ret != null) ret[retIndex] = eoS; // Dummy EoS at element zero which is not used for Earley
        retIndex++;
      }

      int longestTerminal, longestTerminalLength;

      while (stringStart < inputStringLength && isspace(input[stringStart]))
        stringStart++;

      while (stringStart < inputStringLength) {
        longestTerminal = longestTerminalLength = 0;
        for (int t = eoS + 1; t < epsilon; t++) {

          if (isSubstringAt(input, stringStart, symbolStrings[t])) {
            if (strlen(symbolStrings[t]) > longestTerminalLength) {
              longestTerminal = t;
              longestTerminalLength = strlen(symbolStrings[t]);
            }
          }
        }
        if (ret != null) ret[retIndex] = longestTerminal;
        if (longestTerminal == 0) return null; // lexicalisation error
        stringStart += strlen(symbolStrings[longestTerminal]);
        // Just do whitespace for all terminals in this version... if (!(ret[retIndex]
        // instanceof ARTGrammarElementTerminalCharacter))
        while (stringStart < inputStringLength && isspace(input[stringStart]))
          stringStart++;
        retIndex++;
      }
      // set a_{n+1} = $
      if (ret != null) ret[retIndex] = eoS;
      retIndex++;

      if (ret == null) ret = new int[retIndex];
    }
    inputLengthFromLexer = retIndex;
    return ret;
  }

  // MAKE_NODE(B ::= αx · β, j, i, w, v, V) {
  int makeNode(int betaSlot, int j, int i, int w, int v, int vSet) {// vSet2 is reallt SPPF?

    // if β = epsilon { let s = B } else { let s = (B ::= αx · β) }
    int s;
    if (slotRightSymbols[betaSlot] == 0)
      s = slotLHSSymbols[betaSlot];
    else
      s = betaSlot;

    // if α = epsilon and β != epsilon { let y = v }
    int y; // SPPFNode
    int postAlphaSlot = betaSlot - 2;
    if (slotRightSymbols[postAlphaSlot] == 0 && slotRightSymbols[betaSlot] != 0) {
      y = v;
    }
    // else {
    else {
      // if there is no node y in V labelled (s, j, i) create one and add it to V
      y = mapFind_3_1(sppf, s, j, i);

      // if w = null and y does not have a family of children (v) add one
      // First, ensure y has a set...
      if (poolGet(y + sppfFamilyOffset) == 0) // Is there a set?
        poolPut(y + sppfFamilyOffset, mapMake(sppfNodeFamilyBucketCount));

      if (w == 0)
        mapFind_2_0(poolGet(y + sppfFamilyOffset), 0, v);
      // if w != null and y does not have a family of children (w, v) add one }
      else
        mapFind_2_0(poolGet(y + sppfFamilyOffset), w, v);
    }

    // return y
    return y;
  }

  void artParse(String stringInput, String inputFilename) {
    artParseAlgorithm = "ARTEarleyIndexedPool (C)";
    artStartTime = clock();
    int* input = dynamicLexicaliseLongestMatch(stringInput, 1);

    if (input == null) {
      printf("ARTEarleyIndexedPool: reject lexical");

      return;
    }

    inputLength = inputLengthFromLexer - 2; // input[0] is not used and input[n+1] is $

    artTokenCount = inputLength;
    // printf("ARTEarleyIndexedPool runnng on %i tokens\n", inputLength);

    artLexTime = clock();

    poolInit(20, 1024); // 1024 x 1Mlocation blocks: at 32-buit integers that 4G of memory when fully
                                  // allocated
    epsilonSPPFNode = poolAllocate(4);
    poolPut(epsilonSPPFNode, epsilon);

    int p;
    acceptingSlotsSet = mapMake(acceptingSlotsBucketCount); // One per production of the start rule, so
                                                                             // just a small set
    for (int productionNumber = 0; (p = slotIndex[startSymbol][productionNumber]) != 0; productionNumber++) {
      // Kick past production
      p++;
      // !! update p to be the accepting slot!
      while (slotRightSymbols[p] != 0)
        p++;

      mapFind_1_0(acceptingSlotsSet, p);
    }

    eSets = new int[inputLength + 1];
    for (int i = 0; i < inputLength + 1; i++)
      eSets[i] = mapMake(earleyItemPerLevelBucketCount);
    rSet = mapMake(earleyItemPerLevelBucketCount);
    qSet = mapMake(earleyItemPerLevelBucketCount);
    qPrimeSet = mapMake(qPrimeBucketCount);
    vSet = mapMake(sppfNodePerLevelBucketCount);
    hMap = mapMake(sppfNodePerLevelBucketCount);
    sppf = mapMake(sppfNodeFullBucketCount);

    artSetupTime = clock();

    // for all (S ::= α) ∈ P { if α ∈ ΣN add (S ::= ·α, 0, null) to E0
    // if α = a1 α′ add (S ::= ·α, 0, null) to Q′ } !! Q' is now Q[0] for this
    // initialisation phase
    for (int productionNumber = 0; (p = slotIndex[startSymbol][productionNumber]) != 0; productionNumber++) {
    if (!mergedSets[slotGuardSetAddresses[p + 1]][input[1]]) continue;
/** lookahead refactoring here ***/
      if (inSigmaN(p))
        mapFind_3_0(eSets[0], p + 1, 0, 0);
      else
        mapFind_3_0(qPrimeSet, p + 1, 0, 0);
    }
    // for 0 ≤ i ≤ n {
    for (int i = 0; i <= inputLength; i++) {
      // printSets();
      // H = ∅, R = Ei , Q = Q′
      mapClear(hMap);
      mapAssign(rSet, eSets[i]);
      mapAssign(qSet, qPrimeSet);

      // Q′ = ∅
      mapClear(qPrimeSet);

      // while R ! = ∅ {
      while (mapCardinality(rSet) != 0) {

        // remove an element, Λ say, from R
        int lambda = mapRemove(rSet);

        int c = slotRightSymbols[poolGet(lambda + earleyItemSlotOffset)];
        int h = poolGet(lambda + earleyItemIndexOffset);
        int w = poolGet(lambda + earleyItemSPPFNodeOffset);

        // if Λ = (B ::= α · Cβ, h, w) {
        if (isNonterminal(c)) {
          // for all (C ::= δ) ∈ P {
          int deltaSlot;
          for (int productionNumber = 0; (deltaSlot = slotIndex[c][productionNumber]) != 0; productionNumber++) {
            // if δ ∈ ΣN and (C ::= ·δ, i, null) ! ∈ Ei {
            deltaSlot++; // Move to first child
            if (!mergedSets[slotGuardSetAddresses[deltaSlot]][input[i+1]]) continue;
/** lookahead refactoring here ***/
            if (inSigmaN(deltaSlot)) {
              if (mapLookup_3(eSets[i], deltaSlot, i, 0) == 0) {
                // add (C ::= ·δ, i, null) to Ei and R }
                mapFind_3_0(eSets[i], deltaSlot, i, 0);
                mapFind_3_0(rSet, deltaSlot, i, 0);
              }
            } else
                mapFind_3_0(qSet, deltaSlot, i, 0);
          }

          // if ((C, v) ∈ H) {
          int v;
          int hMapElement;
          int betaPreSlot = poolGet(lambda + earleyItemSlotOffset) + 1;
          if ((hMapElement = mapLookup_1(hMap, c)) != 0 && mergedSets[slotGuardSetAddresses[betaPreSlot]][input[i+1]]) {
            v = poolGet(hMapElement + hMapSPPFNodeOffset);
            // let y = MAKE_NODE(B ::= αC · β, h, i, w, v, V)
            int y = makeNode(betaPreSlot, h, i, w, v, vSet);
            // if β ∈ ΣN and (B ::= αC · β, h, y) ! ∈ Ei {
/** lookahead refactoring here ***/
            if (inSigmaN(betaPreSlot)) {
              if (mapLookup_3(eSets[i], betaPreSlot, h, y) == 0) {
                // add (C ::= ·δ, i, null) to Ei and R }
                mapFind_3_0(eSets[i], betaPreSlot, h, y);
                mapFind_3_0(rSet, betaPreSlot, h, y);
              }
            } else
                mapFind_3_0(qSet, betaPreSlot, h, y);
          }
        }

        // if Λ = (D ::= α·, h, w) {
        int D = slotLHSSymbols[poolGet(lambda + earleyItemSlotOffset)];
        if (slotRightSymbols[poolGet(lambda + earleyItemSlotOffset)] == 0
            || slotRightSymbols[poolGet(lambda + earleyItemSlotOffset)] == epsilon) {
          // if w = null {
          if (w == 0) {
            // if there is no node v in V labelled (D, i, i) create one
            int v = mapFind_3_1(sppf, D, i, i);

            // set w = v
            w = v;
            // if w does not have family (epsilon) add one }
            if (poolGet(w + sppfFamilyOffset) == 0) // Is there a set?
              poolPut(w + sppfFamilyOffset, mapMake(sppfNodeFamilyBucketCount));
            mapFind_2_0(poolGet(w + sppfFamilyOffset), epsilonSPPFNode, 0);
          }
          // if h = i { add (D, w) to H }
          if (h == i) mapFind_1_1(hMap, D, w);

          // for all (A ::= τ · Dδ, k, z) in Eh {
          for (int e = mapIteratorFirst1(eSets[h]); e != 0; e = mapIteratorNext1()) {
            int deltaPreSlot = poolGet(e + earleyItemSlotOffset) + 1;
            if (!mergedSets[slotGuardSetAddresses[deltaPreSlot]][input[i+1]]) continue;
            // major inefficiency here: rework as a set of hashmaps from D to items
            if (slotRightSymbols[poolGet(e + earleyItemSlotOffset)] == D) {
              // let y = MAKE_NODE(A ::= τ D · δ, k, i, z, w, V)
              int k = poolGet(e + earleyItemIndexOffset);
              int z = poolGet(e + earleyItemSPPFNodeOffset);
              int y = makeNode(deltaPreSlot, k, i, z, w, vSet);
              // if δ ∈ ΣN and (A ::= τ D · δ, k, y) ! ∈ Ei {
/** lookahead refactoring here ***/
              if (inSigmaN(deltaPreSlot)) {
                if (mapLookup_3(eSets[i], deltaPreSlot, k, y) == 0) {
                  // add (C ::= ·δ, i, null) to Ei and R }
                  mapFind_3_0(eSets[i], deltaPreSlot, k, y);
                  mapFind_3_0(rSet, deltaPreSlot, k, y);
                }
              } else
                  mapFind_3_0(qSet, deltaPreSlot, k, y);
            }
          }
        }
      }
      // V=∅
      mapClear(vSet);
      // create an SPPF node v labelled (ai+1 , i, i + 1)
      if (i != inputLength) {
        int v = mapFind_3_1(sppf, input[i + 1], i, i + 1);
        // while Q ! = ∅ {
        while (mapCardinality(qSet) != 0) {
          // remove an element, Λ = (B ::= α · ai+1 β, h, w) say, from Q
          int lambda = mapRemove(qSet);

          int postAlphaSlot = poolGet(lambda + earleyItemSlotOffset);
          int h = poolGet(lambda + earleyItemIndexOffset);
          int w = poolGet(lambda + earleyItemSPPFNodeOffset);
          // let y = MAKE_NODE(B ::= α ai+1 · β, h, i + 1, w, v, V)
          int preBetaSlot = postAlphaSlot + 1;
          if (!mergedSets[slotGuardSetAddresses[preBetaSlot]][input[i+2]]) continue;

          int y = makeNode(preBetaSlot, h, i + 1, w, v, vSet);
          // if β ∈ ΣN { add (B ::= α ai+1 · β, h, y) to Ei+1 }
/** lookahead refactoring here ***/
          if (inSigmaN(preBetaSlot))
            mapFind_3_0(eSets[i + 1], preBetaSlot, h, y);
          else
            mapFind_3_0(qPrimeSet, preBetaSlot, h, y);
          

#if 0
          if (inSigmaN(preBetaSlot)) {
            mapFind_3_0(eSets[i + 1], preBetaSlot, h, y);
          }
          // if β = ai+2 β′ { add (B ::= α ai+1 · β, h, y) to Q′ }
          if (slotRightSymbols[preBetaSlot] != 0 && slotRightSymbols[preBetaSlot] == input[i + 2]) {
            mapFind_3_0(qPrimeSet, preBetaSlot, h, y);
          }
#endif
        }
      }
    }

    // if (S ::= τ ·, 0, w) ∈ En return w

    artParseTime = clock();
    // Scan eSets.get(inputLength) to look for accepting slots and some w, then
    // return w
    for (int e = mapIteratorFirst1(eSets[inputLength]); e != 0; e = mapIteratorNext1()) {
      int offset = poolGet(e + earleyItemIndexOffset);
      int slot = poolGet(e + earleyItemSlotOffset);

      if (offset == 0 && mapLookup_1(acceptingSlotsSet, slot) != 0) {
        artIsInLanguage = true;
        printf("(CPP) EarleyIndexedPool %s in %.4fms\n", artIsInLanguage ? "accept" : "reject",  ((double) (artParseTime-artSetupTime)/ (double) CLOCKS_PER_SEC)*1000.00);
        return;
      }
    }
    // else return failure
    {
      printf("(CPP) EarleyIndexedPool %s in %.4fms\n", (artIsInLanguage ? "accept" : "reject"), ((double)(artParseTime-artSetupTime)/ (double) CLOCKS_PER_SEC)*1000.00);
      return;
    }
  }

