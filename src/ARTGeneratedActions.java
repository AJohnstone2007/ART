import uk.ac.rhul.cs.csle.art.interpret.AbstractActions;
import uk.ac.rhul.cs.csle.art.util.Util;
import java.util.HashMap;
public class ARTGeneratedActions extends AbstractActions {
  HashMap<String, Integer> symbols = new HashMap<>(); 
  public String name() { return "2025-01-12 06:09:32"; }
  public static class A_INTEGER { int v; }
  public static void action83(A_INTEGER INTEGER){ INTEGER.v = Integer.parseInt(lexeme()); }
  public static class A_STRING_DQ { String v; }
  public static void action87(A_STRING_DQ STRING_DQ){ STRING_DQ.v = lexeme(); }
  public static class A_e0 { int v; }
  public static void action91(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v; }
  public static void action96(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v >  e12.v ? 1 : 0; }
  public static void action101(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v <  e12.v ? 1 : 0; }
  public static void action106(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v >= e12.v ? 1 : 0; }
  public static void action111(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v <= e12.v ? 1 : 0; }
  public static void action116(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v == e12.v ? 1 : 0; }
  public static void action121(A_e0 e0, A_e1 e11, A_e1 e12){ e0.v = e11.v != e12.v ? 1 : 0; }
  public static class A_e1 { int v; }
  public static void action125(A_e1 e1, A_e1 e11, A_e2 e21){ e1.v = e21.v; }
  public static void action130(A_e1 e1, A_e1 e11, A_e2 e21){ e1.v = e11.v + e21.v; }
  public static void action135(A_e1 e1, A_e1 e11, A_e2 e21){ e1.v = e11.v - e21.v; }
  public static class A_e2 { int v; }
  public static void action139(A_e2 e2, A_e2 e21, A_e3 e31){ e2.v= e31.v; }
  public static void action144(A_e2 e2, A_e2 e21, A_e3 e31){ e2.v = e21.v * e31.v; }
  public static void action149(A_e2 e2, A_e2 e21, A_e3 e31){ e2.v = e21.v / e31.v; }
  public static void action154(A_e2 e2, A_e2 e21, A_e3 e31){ e2.v = e21.v % e31.v; }
  public static class A_e3 { int v; }
  public static void action158(A_e3 e3, A_e3 e31, A_e4 e41){e3.v = e41.v; }
  public static void action162(A_e3 e3, A_e3 e31, A_e4 e41){e3.v = e31.v; }
  public static void action166(A_e3 e3, A_e3 e31, A_e4 e41){e3.v = -e31.v; }
  public static class A_e4 { int v; }
  public static void action170(A_e4 e4, A_e5 e51, A_e4 e41){ e4.v = e51.v; }
  public static void action175(A_e4 e4, A_e5 e51, A_e4 e41){ e4.v = (int) Math.pow(e51.v, e41.v); }
  public static class A_e5 { int v; }
  public static void action179(A_e5 e5, A_e1 e11, A_INTEGER INTEGER1){e5.v = INTEGER1.v; }
  public static void action183(A_e5 e5, A_e1 e11, A_INTEGER INTEGER1){ e5.v = e11.v; }
  public static class A_printElements { }
  public static void action188(A_printElements printElements, A_printElements printElements1, A_STRING_DQ STRING_DQ1, A_e0 e01){ System.out.print(STRING_DQ1.v); }
  public static void action191(A_printElements printElements, A_printElements printElements1, A_STRING_DQ STRING_DQ1, A_e0 e01){ System.out.print(STRING_DQ1.v); }
  public static void action196(A_printElements printElements, A_printElements printElements1, A_STRING_DQ STRING_DQ1, A_e0 e01){ System.out.print(e01.v); }
  public static void action199(A_printElements printElements, A_printElements printElements1, A_STRING_DQ STRING_DQ1, A_e0 e01){ System.out.print(e01.v); }
  public static class A_statement { }
  public void action(int nodeNumber, Object[] aBlocks) {
    switch(nodeNumber) {
      case 83: action83((A_INTEGER) aBlocks[0]); break;
      case 87: action87((A_STRING_DQ) aBlocks[0]); break;
      case 91: action91((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 96: action96((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 101: action101((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 106: action106((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 111: action111((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 116: action116((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 121: action121((A_e0) aBlocks[0], (A_e1) aBlocks[1], (A_e1) aBlocks[2]); break;
      case 125: action125((A_e1) aBlocks[0], (A_e1) aBlocks[1], (A_e2) aBlocks[2]); break;
      case 130: action130((A_e1) aBlocks[0], (A_e1) aBlocks[1], (A_e2) aBlocks[2]); break;
      case 135: action135((A_e1) aBlocks[0], (A_e1) aBlocks[1], (A_e2) aBlocks[2]); break;
      case 139: action139((A_e2) aBlocks[0], (A_e2) aBlocks[1], (A_e3) aBlocks[2]); break;
      case 144: action144((A_e2) aBlocks[0], (A_e2) aBlocks[1], (A_e3) aBlocks[2]); break;
      case 149: action149((A_e2) aBlocks[0], (A_e2) aBlocks[1], (A_e3) aBlocks[2]); break;
      case 154: action154((A_e2) aBlocks[0], (A_e2) aBlocks[1], (A_e3) aBlocks[2]); break;
      case 158: action158((A_e3) aBlocks[0], (A_e3) aBlocks[1], (A_e4) aBlocks[2]); break;
      case 162: action162((A_e3) aBlocks[0], (A_e3) aBlocks[1], (A_e4) aBlocks[2]); break;
      case 166: action166((A_e3) aBlocks[0], (A_e3) aBlocks[1], (A_e4) aBlocks[2]); break;
      case 170: action170((A_e4) aBlocks[0], (A_e5) aBlocks[1], (A_e4) aBlocks[2]); break;
      case 175: action175((A_e4) aBlocks[0], (A_e5) aBlocks[1], (A_e4) aBlocks[2]); break;
      case 179: action179((A_e5) aBlocks[0], (A_e1) aBlocks[1], (A_INTEGER) aBlocks[2]); break;
      case 183: action183((A_e5) aBlocks[0], (A_e1) aBlocks[1], (A_INTEGER) aBlocks[2]); break;
      case 188: action188((A_printElements) aBlocks[0], (A_printElements) aBlocks[1], (A_STRING_DQ) aBlocks[2], (A_e0) aBlocks[3]); break;
      case 191: action191((A_printElements) aBlocks[0], (A_printElements) aBlocks[1], (A_STRING_DQ) aBlocks[2], (A_e0) aBlocks[3]); break;
      case 196: action196((A_printElements) aBlocks[0], (A_printElements) aBlocks[1], (A_STRING_DQ) aBlocks[2], (A_e0) aBlocks[3]); break;
      case 199: action199((A_printElements) aBlocks[0], (A_printElements) aBlocks[1], (A_STRING_DQ) aBlocks[2], (A_e0) aBlocks[3]); break;
      default: break;
    }
  }
  public Object[] createAtributeBlocks(Object parent, int ei) {
    switch (ei) {
      case 21: return new Object[] { parent };
      case 22: return new Object[] { parent };
      case 23: return new Object[] { parent, new A_e1(), new A_e1() };
      case 24: return new Object[] { parent, new A_e1(), new A_e2() };
      case 25: return new Object[] { parent, new A_e2(), new A_e3() };
      case 26: return new Object[] { parent, new A_e3(), new A_e4() };
      case 27: return new Object[] { parent, new A_e5(), new A_e4() };
      case 28: return new Object[] { parent, new A_e1(), new A_INTEGER() };
      case 29: return new Object[] { parent, new A_printElements(), new A_STRING_DQ(), new A_e0() };
      case 30: return new Object[] { parent, new A_printElements() };
    }
    Util.fatal("internal error - attempt to create attribute block for unknown nonterminal element " + ei);
    return null;
  }
}
