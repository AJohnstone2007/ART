package uk.ac.rhul.cs.csle.art.cfg.gll;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import uk.ac.rhul.cs.csle.art.cfg.cfgRules.CFGRules;
import uk.ac.rhul.cs.csle.art.cfg.hashpool.HashPool;
import uk.ac.rhul.cs.csle.art.script.ScriptInterpreter;
import uk.ac.rhul.cs.csle.art.util.Util;
import uk.ac.rhul.cs.csle.art.util.lexicalisations.AbstractLexicalisations;

public class GLLHashPool extends HashPool {
  int loops;
  // Tabular copies of grammar information installed by setGrammar()
  int endOfStringNodeNi;
  int startNonterminalNodeNi;
  int kindOf[];
  int altOf[][];
  int seqOf[];
  int targetOf[];
  int elementOf[];
  boolean firstOf[][];
  boolean followOf[][];
  boolean secondOf[];

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

  // These primes are for non-lookahead recognition on shifts.sml
  int sizeKludge = 1; // Leave this at one for normal operations. Can be used to zoom up the bucket count, but probably loses coprime property
  final int descriptorPrime = 80_000_029;
  final int descriptorBucketInitialCount = descriptorPrime * sizeKludge;
  int descriptorBucketCount;
  int descriptorBuckets[];

  final int gssNodePrime = 500_003;
  final int gssNodeBucketInitialCount = gssNodePrime * sizeKludge;
  int gssNodeBucketCount;
  int gssNodeBuckets[];

  final int gssEdgePrime = 47_000_017;
  final int gssEdgeBucketInitialCount = gssEdgePrime * sizeKludge;
  int gssEdgeBucketCount;
  int gssEdgeBuckets[];

  final int popElementPrime = 25_000_019;
  final int popElementBucketInitialCount = popElementPrime * sizeKludge;
  int popElementBucketCount;
  int popElementBuckets[];

  final int sppfNodePrime = 25_000_019; // Guess
  final int sppfNodeBucketInitialCount = sppfNodePrime * sizeKludge;
  int sppfNodeBucketCount;
  int sppfNodeBuckets[];

  final int sppfPackNodePrime = 700_000_007; // Guess
  final int sppfPackNodeBucketInitialCount = sppfPackNodePrime * sizeKludge;
  int sppfPackNodeBucketCount;
  int sppfPackNodeBuckets[];

  /* Parser ******************************************************************/
  private void initialise() {
    // 0. Defensive programming - make sure we've not messed up the enumeration value

    // 1. Make local references to grammar tables
    endOfStringNodeNi = lexicalisations.cfgRules.endOfStringNode.num;
    startNonterminalNodeNi = lexicalisations.cfgRules.elementToRulesNodeMap.get(lexicalisations.cfgRules.startNonterminal).num;
    kindOf = lexicalisations.cfgRules.makeKindsArray();
    altOf = lexicalisations.cfgRules.makeAltsArray();
    seqOf = lexicalisations.cfgRules.makeSeqsArray();
    targetOf = lexicalisations.cfgRules.makeCallTargetsArray();
    elementOf = lexicalisations.cfgRules.makeElementOfArray();
    firstOf = lexicalisations.cfgRules.makeFirstOfArray();
    followOf = lexicalisations.cfgRules.makeFollowOfArray();
    secondOf = lexicalisations.cfgRules.makeSecondOfArray();

    // 1a. (Debug): print precomputed tables
    // Util.debug("CFGRules:" + lexicalisations.cfgRules);
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
    descriptorBuckets = clear(descriptorBuckets, descriptorBucketCount);

    gssNodeBucketCount = gssNodeBucketInitialCount;
    gssNodeBuckets = clear(gssNodeBuckets, gssNodeBucketCount);

    gssEdgeBucketCount = gssEdgeBucketInitialCount;
    gssEdgeBuckets = clear(gssEdgeBuckets, gssEdgeBucketCount);

    popElementBucketCount = popElementBucketInitialCount;
    popElementBuckets = clear(popElementBuckets, popElementBucketCount);

    sppfNodeBucketCount = sppfNodeBucketInitialCount;
    sppfNodeBuckets = clear(sppfNodeBuckets, sppfNodeBucketCount);

    sppfPackNodeBucketCount = sppfPackNodeBucketInitialCount;
    sppfPackNodeBuckets = clear(sppfPackNodeBuckets, sppfPackNodeBucketCount);

    // 3. Initialise parser data structures
    find(gssNodeBuckets, gssNodeBucketCount, gssNode_SIZE, endOfStringNodeNi, 0);
    int gssRoot = findIndex;
    inputIndex = 0;
    sn = gssRoot;
    dn = 0;
    enqueueDescriptorsFor(startNonterminalNodeNi, inputIndex, sn, dn);
  }

  @Override
  public void parse(AbstractLexicalisations lexicalisations) {
    loops = 0;

    inLanguage = false;
    this.lexicalisations = lexicalisations;

    initialise();

    nextDescriptor: while (dequeueDescriptor()) {
      if ((++loops) % 1000000 == 0 && Util.traceLevel >= 8) printReport(System.out, loops);
      while (true) {
        switch (kindOf[gn]) {
        case CFGRules.TRM_CS, CFGRules.TRM_CI, CFGRules.TRM_BI, CFGRules.TRM_CH, CFGRules.TRM_CH_UIB, CFGRules.TRM_CH_UOB, CFGRules.TRM_CH_SET, CFGRules.TRM_CH_ANTI_SET:
          if (lexicalisations.getSlice(inputIndex)[0].cfgElement.number == elementOf[gn]) {
            // Util.debug("At input " + inputIndex + " matched " + lexicalisations.getSlice(inputIndex)[0].cfgElement.number);
            d(1);
            inputIndex++;
            gn++;
            break;
          } else
            continue nextDescriptor;
        case CFGRules.NONTERMINAL:
          call(gn);
          continue nextDescriptor;
        case CFGRules.EPSILON:
          d(0);
          gn++;
          break;
        case CFGRules.END:
          retrn();
          continue nextDescriptor;
        default:
          Util.fatal("internal error - unexpected grammar node in gllHashPool");
        }
      }
    }
  }

  /* Stack handling **********************************************************/
  private void call(int nonterminalNi) {
    // Util.debug("Calling nonterminal " + nonterminalNi);
    find(gssNodeBuckets, gssNodeBucketCount, gssNode_SIZE, gn + 1, inputIndex);
    int parentGSSNode = findIndex;

    find(gssEdgeBuckets, gssEdgeBucketCount, gssEdge_SIZE, parentGSSNode, sn, dn);
    // Util.debug("Push with GSS edge labelled " + toStringSPPFNode(dn));

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
    if (poolGet(sn + gssNode_gn) == endOfStringNodeNi) { // Are we popping the basenode
      if (lexicalisations.cfgRules.acceptingNodeNumbers.contains(gn)) inLanguage |= (inputIndex == lexicalisations.inputString.length() - 1); // Make gni to
      // Util.debug("Acceptance test returns " + inLanguage);
      return;
    }

    find(popElementBuckets, popElementBucketCount, popElement_SIZE, inputIndex, sn, dn);
    if (findMadeNew) {
      poolSet(findIndex + popElement_popList, poolGet(sn + gssNode_popList));
      poolSet(sn + gssNode_popList, findIndex);
    }

    for (int e = poolGet(sn + gssNode_edgeList); e != 0; e = poolGet(e + gssEdge_edgeList)) {
      // Util.debug("Popping using edge labelled " + toStringSPPFNode(poolGet(e + gssEdge_dn)));
      enqueueDescriptor(poolGet(sn + gssNode_gn), inputIndex, poolGet(e + gssEdge_dst),
          derivationUpdate(poolGet(sn + gssNode_gn), poolGet(e + gssEdge_dn), dn));
    }
  }

  /* Derivation handling *****************************************************/
  private int derivationFindNode(int dni, int leftExt, int rightExt) {
    find(sppfNodeBuckets, sppfNodeBucketCount, sppfNode_SIZE, dni, leftExt, rightExt);
    // Util.debug("derivationFindNode returns " + toStringSPPFNode(findIndex));
    return findIndex;
  }

  private int derivationUpdate(int gni, int leftChild, int rightChild) {
    int symbolNode = derivationFindNode(kindOf[gni] == CFGRules.END ? seqOf[gni] : gni,
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
    // Util.debug("dequeuDescriptor (" + toStringSlot(gn) + ", " + inputIndex + ", (" + toStringGSSNode(sn) + "), (" + toStringSPPFNode(dn) + "))");
    return true;
  }

  private void enqueueDescriptor(int gn, int i, int sn, int dn) {
    // Util.debug("enqueueDescriptor(" + toStringSlot(gn) + "," + i + ", (" + toStringGSSNode(sn) + "), (" + toStringSPPFNode(dn) + "))");
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
    ScriptInterpreter.currentStatistics.put("twes", lexicalisations.cardinality());
    ScriptInterpreter.currentStatistics.put("descriptorCount", cardinality(descriptorBuckets));
    ScriptInterpreter.currentStatistics.put("gssNodeCount", cardinality(gssNodeBuckets));
    ScriptInterpreter.currentStatistics.put("gssEdgeCount", cardinality(gssEdgeBuckets));
    ScriptInterpreter.currentStatistics.put("popCount", cardinality(popElementBuckets));

    ScriptInterpreter.currentStatistics.put("sppfSymbolPlusIntermediateNodeCount", cardinality(sppfNodeBuckets));
    ScriptInterpreter.currentStatistics.put("sppfPackNodeCount", cardinality(sppfPackNodeBuckets));
    ScriptInterpreter.currentStatistics.put("poolAllocated", getFirstUnusedElement());

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

    ScriptInterpreter.currentStatistics.put("h0", hist.get(0));
    ScriptInterpreter.currentStatistics.put("h1", hist.get(1));
    ScriptInterpreter.currentStatistics.put("h2", hist.get(2));
    ScriptInterpreter.currentStatistics.put("h3", hist.get(3));
    ScriptInterpreter.currentStatistics.put("h4", hist.get(4));
    ScriptInterpreter.currentStatistics.put("h5", hist.get(5));
    ScriptInterpreter.currentStatistics.put("h6more", hist.get(-1));
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

  String toStringSlot(int gn) {
    return lexicalisations.cfgRules.numberToRulesNodeMap.get(gn).toStringAsProduction();
  }

  String toStringGSSNode(int sn) {
    if (sn == 0) return "null GSS node";
    int gn = poolGet(sn + gssNode_gn);
    int i = poolGet(sn + gssNode_i);

    return toStringSlot(gn) + ", " + i;
  }

  String toStringSPPFNode(int dn) {
    if (dn == 0) return "null SPPF node";
    int gn = poolGet(dn + sppfNode_gn);
    int leftExtent = poolGet(dn + sppfNode_leftExt);
    int rightExtent = poolGet(dn + sppfNode_rightExt);

    return toStringSlot(gn) + ", " + leftExtent + ", " + rightExtent;
  }

  String toStringSPPFPackNode(int n) {
    if (n == 0) return "null SPPF pack node";
    int gn = poolGet(n + sppfPackNode_gn);
    int pivot = poolGet(n + sppfPackNode_pivot);

    return lexicalisations.cfgRules.numberToRulesNodeMap.get(gn).toStringAsProduction() + ", " + pivot;
  }

  @Override
  public void printCardinalities(PrintStream outputStream) {
    loadCounts();
    outputStream.println(">> " + name() + ": characters:" + fmt(lexicalisations.inputString.length()) + " TWEs:" + fmt(lexicalisations.cardinality()));

    // !! Debug !!
    // for (int bucket : sppfNodeBuckets)
    // for (int chain = bucket; chain != 0; chain = poolGet(chain)) {
    // Util.debug("SPPF node " + toStringSPPFNode(chain));
    // for (int packNode = poolGet(chain + sppfNode_packNodeList); packNode != 0; packNode = poolGet(packNode + sppfPackNode_packNodeList)) {
    // Util.debug("Pack node " + toStringSPPFPackNode(packNode) + ": " + "[" + toStringSPPFNode(poolGet(packNode + sppfPackNode_leftChild)) + "] " + "["
    // + toStringSPPFNode(poolGet(packNode + sppfPackNode_rightChild)) + "]");
    // }
    // }

  }

  private String fmt(long n) {
    return String.format("%,d", n);
  }

  public void printReport(PrintStream outputStream, int count) {
    outputStream.println(">> After " + count + " tasks: " + ScriptInterpreter.currentStatistics.currentTime() + "s elapsed");
  }

}
