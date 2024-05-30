package uk.ac.rhul.cs.csle.alero.hardCoded;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Box;
import uk.ac.rhul.cs.csle.alero.Alero;

public class PrintonOnSea {
  double runningLineGap = Alero.fi(6, 0);
  double yardLineGap = Alero.fi(10, 0);

  int facets = 180;
  double outlineWidth = 10;

  double boardX = 1200;
  double boardY = 600;
  double boardZ = 36;

  double sleeperX = Alero.i(10);
  double sleeperY = Alero.fi(9, 0);
  double sleeperZ = Alero.i(5);
  // These are the sleeper spacing for a 30 foot panel on the LBSCR
  double[] sleepersLBSCR30 = { Alero.fi(1.625, 0) + 0 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 1 * Alero.fi(2, 6.25),
      Alero.fi(1.625, 0) + 2 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 3 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 4 * Alero.fi(2, 6.25),
      Alero.fi(1.625, 0) + 5 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 6 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 7 * Alero.fi(2, 6.25),
      Alero.fi(1.625, 0) + 8 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 9 * Alero.fi(2, 6.25), Alero.fi(1.625, 0) + 10 * Alero.fi(2, 6.25),
      Alero.fi(1.625, 0) + 11 * Alero.fi(2, 6.25) };

  double mainLineY = -(boardY / 2 - 100);
  double rulingRadius = Alero.fi(10, 0); // 10 feet like Peco points
  double rulingSeparation = Alero.fi(11, 0); // 11 real feet cente separation
  double rulingTurnoutAngle = curveAngleY(rulingRadius, rulingSeparation / 2);
  double rulingTurnoutX = curveX(rulingRadius, rulingTurnoutAngle);
  double rulingTurnoutY = curveY(rulingRadius, rulingTurnoutAngle);

  // Track dimensions
  double railZ = Alero.i(6);
  double railY = Alero.i(2.5);
  double railBaseZ = Alero.i(1);

  // Rolling stock dimensions
  double coachExtension = 150;

  double wagonX = 200;
  double wagonY = 78;
  double coachX = 650;
  double coachY = 90;
  double locoX = 400;

  double curveX(double r, double theta) { // The x-displacement for a curve of radius r which subtends angle theta
    return r * Math.sin(Math.abs(theta));
  };

  double curveY(double r, double theta) { // The y-displacement for a curve of radius r which subtends angle theta
    return r - (r * Math.cos(Math.abs(theta)));
  };

  double curveAngleX(double radius, double Y) {
    return Math.acos(radius);
  }

  double curveAngleY(double radius, double Y) {
    return Math.acos((radius - Y) / radius);
  }

  Node board(double x, double y) {
    Box ret = new Box(x, y, boardZ);
    ret.setMaterial(Alero.plywoodMaterial);
    ret.setTranslateZ(-boardZ);
    Alero.add(ret);
    return ret;
  }

  Node sleeper(double originX, double originY) {
    Box ret = new Box(sleeperX, sleeperY, sleeperZ);
    ret.setTranslateX(originX - sleeperX / 2);
    ret.setTranslateY(originY);
    ret.setTranslateZ(sleeperZ / 2);
    ret.setMaterial(Alero.sleeperMaterial);
    return ret;
  }

  Node straight(double length, double originX, double originY, double orientationAngle, double... sleeperPosition) {
    Group ret = new Group();
    Box centreLine = new Box(length, 2, 20);
    centreLine.setMaterial(Alero.greenMaterial);
    Box leftBar = new Box(8, 10, 20);
    leftBar.setMaterial(Alero.redMaterial);
    leftBar.setTranslateX(-length / 2 + 4);
    Box rightBar = new Box(8, 10, 20);
    rightBar.setMaterial(Alero.blueMaterial);
    rightBar.setTranslateX(length / 2 - 4);

    Box rail1 = new Box(length, railY, railZ);
    rail1.setTranslateY(Alero.gauge / 2 + railY / 2);
    rail1.setTranslateZ(sleeperZ + railZ + railBaseZ);
    rail1.setMaterial(Alero.steelMaterial);

    Box rail2 = new Box(length, railY, railZ);
    rail2.setTranslateY(-Alero.gauge / 2 - railY / 2);
    rail2.setTranslateZ(sleeperZ + railZ + railBaseZ);
    rail2.setMaterial(Alero.steelMaterial);

    ret.getChildren().addAll(centreLine, leftBar, rightBar, rail1, rail2);

    for (double x : sleeperPosition)
      ret.getChildren().add(sleeper(-length / 2 + x, 0));

    ret.setRotate(orientationAngle);

    ret.setTranslateX(length / 2 + originX);
    ret.setTranslateY(originY);

    Alero.add(ret);

    return ret;
  }

  Node straightLBSCR(double originX, double originY, double orientationAngle) {
    return straight(Alero.fi(30, 0), originX, originY, orientationAngle, sleepersLBSCR30);
  }

  public void createWorld() {
    board(boardX, boardY);
    board(boardX, boardY).setTranslateX(boardX);
    board(boardX, boardY).setTranslateX(boardX * 2);
    for (int i = -6; i < 6; i++)
      straightLBSCR(i * Alero.fi(30, 0), 0, 0);

    for (int i = -2; i < 2; i++)
      straightLBSCR(i * Alero.fi(30, 0), -Alero.gauge - runningLineGap, 0);

  }
}
