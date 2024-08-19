public class TimingTest {

  static void delay1() {
    for (int i = 0; i < 100; i++)
      System.err.printf("%d ", i);
    System.err.printf("\n\n");
  }

  static void delay2() {
    for (int i = 0; i < 100000; i++)
      ;
  }

  public static void main(String[] args) {
    long startTime = System.nanoTime();
    delay1();
    long stopTime = System.nanoTime();

    double interval = ((double) stopTime - (double) startTime) * 1E-9;

    System.out.println(interval);
  }

}
