package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGKind;
import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.hashpool.HashPool;
import uk.ac.rhul.cs.csle.art.cfg.lexer.AbstractLexer;
import uk.ac.rhul.cs.csle.art.choose.ChooseRules;
import uk.ac.rhul.cs.csle.art.script.ScriptTermInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;

public class GLLHashPool extends HashPool {
  // Tabular copies of grammar information installed by setGrammar()
  int endOfStringNodeNi;
  int startNonterminalNodeNi;
  int kindOf[];
  int altOf[][];
  int seqOf[];
  int targetOf[];
  int elementOf[];
  // !!! Alert - this must have changed because of SOS
  final int T = 1; // This is a bit weird - these constants must be compile time so that we can use them in a switch statement, and GKind.T.ordinal() is
                   // runtime, so we hardcode and check for agreement with the runtime values below
  final int EPS = 5;
  final int N = 6;
  final int END = 8;

  /** Constant field offsets. In each case, offset zero links to next in hash chain ********/
  final int gssNode_gn = 1; // Key
  final int gssNode_i = 2; // Key
  final int gssNode_edgeList = 3;
  final int gssNode_popList = 4;
  final int gssNode_SIZE = 5; // Key size 2

  final int gssEdge_src = 1; // Key
  final int gssEdge_dst = 2; // Key
  final int gssEdge_dn = 3; // Key
  final int gssEdge_edgeList = 4;
  final int gssEdge_SIZE = 5; // Key size 3

  final int popElement_i = 1; // Key
  final int popElement_sn = 2; // Key
  final int popElement_dn = 3; // Key
  final int popElement_popList = 4;
  final int popElement_SIZE = 5; // Key size 3

  final int sppfNode_gn = 1; // Key
  final int sppfNode_leftExt = 2; // Key
  final int sppfNode_rightExt = 3; // Key
  final int sppfNode_packNodeList = 4;
  final int sppfNode_SIZE = 5; // Key size 3

  final int sppfPackNode_parent = 1; // Key
  final int sppfPackNode_gn = 2; // Key
  final int sppfPackNode_pivot = 3; // Key
  final int sppfPackNode_leftChild = 4;
  final int sppfPackNode_rightChild = 5;
  final int sppfPackNode_packNodeList = 6;
  final int sppfPackNode_SIZE = 7; // Key size 3

  final int descriptor_gn = 1; // Key
  final int descriptor_i = 2; // Key
  final int descriptor_sn = 3; // Key
  final int descriptor_dn = 4; // Key
  final int descriptor_queue = 5; // zero once processed, otherwise next in the queue of awaited descriptors
  final int descriptor_SIZE = 6; // Key size 4

  private final int loadFactor = 2;

  // DescriptorMax data from ansi_c and gtb_src; primes set for load factor 2. This is the experimental setup for the SLE23 paper
  int sizeKludge = 1; // Leave this at one for normal operations. Can be used to zoom up the bucket count, but probably loses coprime property
  final int descriptorMax = 6_578_603;
  final int descriptorPrime = 13_157_231;
  final int descriptorBucketInitialCount = descriptorPrime * sizeKludge;
  int descriptorBucketCount;
  int descriptorBuckets[];

  final int gssNodeMax = 946_975;
  final int gssNodePrime = 1_893_967;
  final int gssNodeBucketInitialCount = gssNodePrime * sizeKludge;
  int gssNodeBucketCount;
  int gssNodeBuckets[];

  final int gssEdgeMax = 2_989_166;
  final int gssEdgePrime = 5_978_341;
  final int gssEdgeBucketInitialCount = gssEdgePrime * sizeKludge;
  int gssEdgeBucketCount;
  int gssEdgeBuckets[];

  final int PopElementMax = 776_934;
  final int popElementPrime = 1_553_869;
  final int popElementBucketInitialCount = popElementPrime * sizeKludge;
  int popElementBucketCount;
  int popElementBuckets[];

  final int sppfNodeMax = 881_128;
  final int sppfNodePrime = 1_762_259;
  final int sppfNodeBucketInitialCount = sppfNodePrime * sizeKludge;
  int sppfNodeBucketCount;
  int sppfNodeBuckets[];

  final int sppfPackNodeMax = 829_463;
  final int sppfPackNodePrime = 1_658_927;
  final int sppfPackNodeBucketInitialCount = sppfPackNodePrime * sizeKludge;
  int sppfPackNodeBucketCount;
  int sppfPackNodeBuckets[];

  /* Parser ******************************************************************/
  private void initialise() {
    // 1. Make local references to grammar tables
    endOfStringNodeNi = cfgRules.endOfStringNode.num;
    startNonterminalNodeNi = cfgRules.elementToNodeMap.get(cfgRules.startNonterminal).num;
    kindOf = cfgRules.makeKindsArray();
    altOf = cfgRules.makeAltsArray();
    seqOf = cfgRules.makeSeqsArray();
    targetOf = cfgRules.makeCallTargetsArray();
    elementOf = cfgRules.makeElementOfArray();
    // Defensive programming - make sure we've not messed up the enumeration value
    if (T != CFGKind.TRM_CS.ordinal()) Util.fatal("Enumeration mismatch for T - check ParserHashPool.java for consistency with Kind enumeration");
    if (EPS != CFGKind.EPSILON.ordinal()) Util.fatal("Enumeration mismatch for EPS - check ParserHashPool.java for consistency with Kind enumeration");
    if (N != CFGKind.NONTERMINAL.ordinal()) Util.fatal("Enumeration mismatch for N - check ParserHashPool.java for consistency with Kind enumeration");
    if (END != CFGKind.END.ordinal()) Util.fatal("Enumeration mismatch for END - check ParserHashPool.java for consistency with Kind enumeration");

    // 1a. (Debug): print precomputed tables
    // Util.debug("CFGRules:" + cfgRules);
    // for (int i = 0; i < kindOf.length; i++)
    // Util.debug("kindOf[" + i + "]=" + kindOf[i] + " ");
    // for (int i = 0; i < altOf.length; i++) {
    // Util.debug("altOf[" + i + "]=" + (altOf[i] == null ? "null" : ""));
    // if (altOf[i] != null) for (int j = 0; j < altOf[i].length; j++)
    // Util.debug(altOf[i][j] + " ");
    // }
    // for (int i = 0; i < targetOf.length; i++)
    // Util.debug("targetOf[" + i + "]=" + targetOf[i]);
    // for (int i = 0; i < elementOf.length; i++)
    // Util.debug("elementOf[" + i + "]=" + elementOf[i] + " ");

    // 2. Clean hash pool and tables
    initialisehashPool();
    descriptorBucketCount = descriptorBucketInitialCount;
    descriptorBuckets = clean(descriptorBuckets, descriptorBucketCount);

    gssNodeBucketCount = gssNodeBucketInitialCount;
    gssNodeBuckets = clean(gssNodeBuckets, gssNodeBucketCount);

    gssEdgeBucketCount = gssEdgeBucketInitialCount;
    gssEdgeBuckets = clean(gssEdgeBuckets, gssEdgeBucketCount);

    popElementBucketCount = popElementBucketInitialCount;
    popElementBuckets = clean(popElementBuckets, popElementBucketCount);

    sppfNodeBucketCount = sppfNodeBucketInitialCount;
    sppfNodeBuckets = clean(sppfNodeBuckets, sppfNodeBucketCount);

    sppfPackNodeBucketCount = sppfPackNodeBucketInitialCount;
    sppfPackNodeBuckets = clean(sppfPackNodeBuckets, sppfPackNodeBucketCount);

    // 3. Initialise parser data structures
    find(gssNodeBuckets, gssNodeBucketCount, gssNode_SIZE, endOfStringNodeNi, 0);
    int gssRoot = findIndex;
    inputIndex = 0;
    sn = gssRoot;
    dn = 0;
    enqueueDescriptorsFor(startNonterminalNodeNi, inputIndex, sn, dn);
  }

  @Override
  public void parse(String input, CFGRules cfgRules, AbstractLexer lexer, ChooseRules chooseRules) {
    inLanguage = false;
    this.input = input;
    this.cfgRules = cfgRules;
    this.lexer = lexer;

    initialise();

    if (!lexer.lex(input, cfgRules, chooseRules)) return;

    // Util.debug(lexer.tweSet.toString());

    nextDescriptor: while (dequeueDescriptor())
      while (true) {
        switch (kindOf[gn]) {
        case T:
          if (lexer.tweSlices[inputIndex][0].cfgElement.number == elementOf[gn]) {
            // Util.debug("Matched " + lexer.firstLexicalisation.get(tokenIndex).element);
            d(1);
            inputIndex++;
            gn++;
            break;
          } else
            continue nextDescriptor;
        case N:
          call(gn);
          continue nextDescriptor;
        case EPS:
          d(0);
          gn++;
          break;
        case END:
          retrn();
          continue nextDescriptor;
        default:
          Util.fatal("internal error - unexpected grammar node in gllHashPool");
        }
      }
    // loadCounts(); // This is very slow!
  }

  /* Stack handling **********************************************************/
  private void call(int nonterminalNi) {
    // Util.debug("Calling nonterminal " + nonterminalNi);
    find(gssNodeBuckets, gssNodeBucketCount, gssNode_SIZE, gn + 1, inputIndex);
    int parentGSSNode = findIndex;

    find(gssEdgeBuckets, gssEdgeBucketCount, gssEdge_SIZE, parentGSSNode, sn, dn);
    int gssEdge = findIndex;

    if (findMadeNew) {
      poolSet(gssEdge + gssEdge_edgeList, poolGet(parentGSSNode + gssNode_edgeList));
      poolSet(parentGSSNode + gssNode_edgeList, gssEdge);
      for (int contingent = poolGet(parentGSSNode + gssNode_popList); contingent != 0; contingent = poolGet(contingent + popElement_popList)) {
        int rightChild = poolGet(contingent + popElement_dn);
        enqueueDescriptor(gn + 1, poolGet(rightChild + sppfNode_rightExt), sn, derivationUpdate(gn + 1, dn, rightChild));
      }
      enqueueDescriptorsFor(targetOf[nonterminalNi], inputIndex, parentGSSNode, 0);
    }
  }

  private void retrn() {
    // Util.debug("Return");
    if (poolGet(sn + gssNode_gn) == endOfStringNodeNi) {
      if (cfgRules.acceptingNodeNumbers.contains(gn)) inLanguage |= (inputIndex == lexer.tweSlices.length - 1); // Make gni to boolean array for
                                                                                                                // acceptance
      // testing
      // Util.debug("Acceptance test returns " + inLanguage);
      return;
    }
    find(popElementBuckets, popElementBucketCount, popElement_SIZE, inputIndex, sn, dn);
    if (findMadeNew) {
      poolSet(findIndex + popElement_popList, poolGet(sn + gssNode_popList));
      poolSet(sn + gssNode_popList, findIndex);
    }

    for (int e = poolGet(sn + gssNode_edgeList); e != 0; e = poolGet(e + gssEdge_edgeList))
      enqueueDescriptor(poolGet(sn + gssNode_gn), inputIndex, poolGet(e + gssEdge_dst),
          derivationUpdate(poolGet(sn + gssNode_gn), poolGet(e + gssEdge_dn), dn));
  }

  /* Derivation handling *****************************************************/
  private int derivationFindNode(int dni, int leftExt, int rightExt) {
    find(sppfNodeBuckets, sppfNodeBucketCount, sppfNode_SIZE, dni, leftExt, rightExt);
    return findIndex;
  }

  private int derivationUpdate(int gni, int leftChild, int rightChild) {
    int symbolNode = derivationFindNode(kindOf[gni] == END ? seqOf[gni] : gni,
        leftChild == 0 ? poolGet(rightChild + sppfNode_leftExt) : poolGet(leftChild + sppfNode_leftExt), poolGet(rightChild + sppfNode_rightExt));

    find(sppfPackNodeBuckets, sppfPackNodeBucketCount, sppfPackNode_SIZE, // Parent for uniqueness because there is a single set of packed nodes?
        symbolNode, gni, leftChild == 0 ? poolGet(rightChild + sppfNode_leftExt) : poolGet(leftChild + sppfNode_rightExt)); /* pivot */
    poolSet(findIndex + sppfPackNode_leftChild, leftChild);
    poolSet(findIndex + sppfPackNode_rightChild, rightChild);
    if (findMadeNew) { // New packed node: add to this SPPFnode's packed node list
      poolSet(findIndex + sppfPackNode_packNodeList, poolGet(symbolNode + sppfNode_packNodeList));
      poolSet(symbolNode + sppfNode_packNodeList, findIndex);
    }
    return symbolNode;
  }

  private void d(int width) {
    dn = derivationUpdate(gn + 1, dn, derivationFindNode(gn, inputIndex, inputIndex + width));
  }

  /* Thread handling *********************************************************/
  int gn;
  int sn;
  int dn;
  int descriptorQueue = 0;

  private boolean dequeueDescriptor() { // load current descriptor
    if (descriptorQueue == 0) return false;
    gn = poolGet(descriptorQueue + descriptor_gn);
    inputIndex = poolGet(descriptorQueue + descriptor_i);
    sn = poolGet(descriptorQueue + descriptor_sn);
    dn = poolGet(descriptorQueue + descriptor_dn);

    descriptorQueue = poolGet(descriptorQueue + descriptor_queue);
    // Util.debug("dequeued descriptor (" + gn + ", " + tokenIndex + ", " + sn + ", " + dn + ")");
    return true;
  }

  private void enqueueDescriptor(int gn, int i, int sn, int dn) {
    // Util.debug("enqueueDescriptor(" + gni + "," + i + "," + sni + "," + dni + ")");
    find(descriptorBuckets, descriptorBucketCount, descriptor_SIZE, gn, i, sn, dn);
    if (findMadeNew) {
      poolSet(findIndex + descriptor_queue, descriptorQueue);
      descriptorQueue = findIndex;
    }
  }

  private void enqueueDescriptorsFor(int n, int i, int parentGSSNode, int sppfNode) {
    int[] productions = altOf[n];
    int p = 0;
    while (productions[p] != 0) // enqueue the start nonterminl's productions
      enqueueDescriptor(productions[p++] + 1, i, parentGSSNode, sppfNode);
  }

  /* Statistics support ******************************************************/
  private void loadCounts() {
    ScriptTermInterpreter.currentStatistics.put("tweNodeCount", lexer.tweSlices.length);
    ScriptTermInterpreter.currentStatistics.put("tweEdgeCount", lexer.tweSlices.length - 1);
    ScriptTermInterpreter.currentStatistics.put("tweLexCount", 1);
    ScriptTermInterpreter.currentStatistics.put("descriptorCount", cardinality(descriptorBuckets));
    ScriptTermInterpreter.currentStatistics.put("gssNodeCount", cardinality(gssNodeBuckets));
    ScriptTermInterpreter.currentStatistics.put("gssEdgeCount", cardinality(gssEdgeBuckets));
    ScriptTermInterpreter.currentStatistics.put("popCount", cardinality(popElementBuckets));

    ScriptTermInterpreter.currentStatistics.put("sppfSymbolPlusIntermediateNodeCount", cardinality(sppfNodeBuckets));
    ScriptTermInterpreter.currentStatistics.put("sppfPackNodeCount", cardinality(sppfPackNodeBuckets));
    ScriptTermInterpreter.currentStatistics.put("poolAllocated", getFirstUnusedElement());

    Map<Integer, Integer> hist = new HashMap<>();
    hist.put(0, 0);
    hist.put(1, 0);
    hist.put(2, 0);
    hist.put(3, 0);
    hist.put(4, 0);
    hist.put(5, 0);
    hist.put(-1, 0); // Anything else goes in the -1 bucket

    accumulateOccupancies(hist, descriptorBuckets);
    accumulateOccupancies(hist, gssNodeBuckets);
    accumulateOccupancies(hist, gssEdgeBuckets);
    accumulateOccupancies(hist, popElementBuckets);
    accumulateOccupancies(hist, sppfNodeBuckets);
    accumulateOccupancies(hist, sppfPackNodeBuckets);

    ScriptTermInterpreter.currentStatistics.put("h0", hist.get(0));
    ScriptTermInterpreter.currentStatistics.put("h1", hist.get(1));
    ScriptTermInterpreter.currentStatistics.put("h2", hist.get(2));
    ScriptTermInterpreter.currentStatistics.put("h3", hist.get(3));
    ScriptTermInterpreter.currentStatistics.put("h4", hist.get(4));
    ScriptTermInterpreter.currentStatistics.put("h5", hist.get(5));
    ScriptTermInterpreter.currentStatistics.put("h6more", hist.get(-1));
  }

  int sppfAmbiguityCount() {
    int count = 0;
    for (int bucket : sppfNodeBuckets)
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        if (poolGet(poolGet(chain + sppfNode_packNodeList) + sppfPackNode_packNodeList) != 0) count++;
    return count;
  }

  int sppfEdgeCount() {
    int count = 0;
    for (int bucket : sppfNodeBuckets)
      for (int chain = bucket; chain != 0; chain = poolGet(chain)) {
        for (int packNode = poolGet(chain + sppfNode_packNodeList); packNode != 0; packNode = poolGet(packNode + sppfPackNode_packNodeList)) {
          count++; // Inedge
          if (poolGet(packNode + sppfPackNode_leftChild) != 0) count++;
          if (poolGet(packNode + sppfPackNode_rightChild) != 0) count++;
          // Util.debug("SPPF node " + toStringSPPFNode(chain) + " pack node " + toStringSPPFPackNode(packNode) + ": " + "["
          // + toStringSPPFNode(poolGet(packNode + sppfPackNode_leftChild)) + "] " + "[" + toStringSPPFNode(poolGet(packNode + sppfPackNode_rightChild))
          // + "]");

        }
      }
    return count;
  }

  String toStringSPPFNode(int n) {
    if (n == 0) return "null SPPF node";
    int gn = poolGet(n + sppfNode_gn);
    int leftExtent = poolGet(n + sppfNode_leftExt);
    int rightExtent = poolGet(n + sppfNode_rightExt);

    return cfgRules.numberToNodeMap.get(gn).toStringAsProduction() + ", " + leftExtent + ", " + rightExtent;
  }

  String toStringSPPFPackNode(int n) {
    if (n == 0) return "null SPPF pack node";
    int gn = poolGet(n + sppfPackNode_gn);
    int pivot = poolGet(n + sppfPackNode_pivot);

    return cfgRules.numberToNodeMap.get(gn).toStringAsProduction() + ", " + pivot;
  }
}
