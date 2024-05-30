package uk.ac.rhul.cs.csle.alero.hardCoded;

import javafx.scene.Node;
import javafx.scene.shape.Box;
import uk.ac.rhul.cs.csle.alero.Alero;

public class TrackGenerator {

  TrackGenerator() {
    board(1200, 600);

  }

  Node board(double x, double y) {
    double boardZ = 3;
    Box ret = new Box(x, y, boardZ);
    ret.setMaterial(Alero.plywoodMaterial);
    ret.setTranslateZ(-boardZ / 2);
    Alero.add(ret);
    return ret;
  }
}
