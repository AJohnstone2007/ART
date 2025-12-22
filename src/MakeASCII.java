
public class MakeASCII {

  public static void main(String[] args) {
    for (int i = 0; i < 128; i++) {
      Character c = (char) i;
      if (!Character.isISOControl(c)) System.out.println("| `" + (c == '\\' ? "\\\\" : c));
    }

  }
}
