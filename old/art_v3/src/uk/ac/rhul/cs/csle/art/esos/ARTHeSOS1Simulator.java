package uk.ac.rhul.cs.csle.art.esos;

import uk.ac.rhul.cs.csle.art.value.ARTValueTermUsingList;

public class ARTHeSOS1Simulator {
  final int termStoreExtent = 16 * 1024 * 1024;
  final int hashTableExtent = 1024;
  final int transitionExtent = 3;
  final int constructorExtent = 128;
  final int functionExtent = 64;
  final int typeExtent = 32;
  final int variableExtent = 16;
  final int arityExtent = 5;

  short termStore[] = new short[termStoreExtent];
  int hashTable[] = new int[hashTableExtent];
  int rules[][] = new int[transitionExtent][constructorExtent];

  void is(ARTValueTermUsingList lhs, int type) {
  }

  void op(ARTValueTermUsingList rhs) {
  }

  void op(ARTValueTermUsingList lhs, ARTValueTermUsingList rhs) {
  }

  ARTHeSOS1Simulator(ARTeSOSSpecification arteSOSSpecification) {
    compile(arteSOSSpecification);
  }

  private void compile(ARTeSOSSpecification arteSOSSpecification) {

  }

  void simulate(String name, int traceLevel, ARTeSOSRelation relation, ARTValueTermUsingList lhs) {

  }

  void fSOS(int theta) {
    int thetaPrime[] = new int[arityExtent];
    int variables[] = new int[variableExtent];
  }
}
