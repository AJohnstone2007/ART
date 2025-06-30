
public class ASCIICharaters {

  public static void main(String[] args) {
    int count = 1;
    for (char c = '!'; c <= '~'; c++) {
      if ((count++) % 16 == 0) System.out.println();
      System.out.print("| '" + c + "' ");
    }
  }

}
