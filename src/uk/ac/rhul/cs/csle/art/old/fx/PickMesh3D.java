package uk.ac.rhul.cs.csle.art.old.fx;
/*
 * Copyright (c) 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * A Simple Picking Example
 */
public class PickMesh3D extends Application {

  MeshView meshView;
  final static float minX = -10;
  final static float minY = -10;
  final static float maxX = 10;
  final static float maxY = 10;

  static TriangleMesh buildTriangleMesh(int subDivX, int subDivY, float scale) {

    final int pointSize = 3;
    final int texCoordSize = 2;
    // 3 point indices and 3 texCoord indices per triangle
    final int faceSize = 6;
    int numDivX = subDivX + 1;
    int numVerts = (subDivY + 1) * numDivX;
    float points[] = new float[numVerts * pointSize];
    float texCoords[] = new float[numVerts * texCoordSize];
    int faceCount = subDivX * subDivY * 2;
    int faces[] = new int[faceCount * faceSize];

    // Create points and texCoords
    for (int y = 0; y <= subDivY; y++) {
      float dy = (float) y / subDivY;
      double fy = (1 - dy) * minY + dy * maxY;

      for (int x = 0; x <= subDivX; x++) {
        float dx = (float) x / subDivX;
        double fx = (1 - dx) * minX + dx * maxX;

        int index = y * numDivX * pointSize + (x * pointSize);
        points[index] = (float) fx * scale;
        points[index + 1] = (float) fy * scale;
        points[index + 2] = 0.0f;

        index = y * numDivX * texCoordSize + (x * texCoordSize);
        texCoords[index] = dx;
        texCoords[index + 1] = dy;
      }
    }

    // Create faces
    for (int y = 0; y < subDivY; y++) {
      for (int x = 0; x < subDivX; x++) {
        int p00 = y * numDivX + x;
        int p01 = p00 + 1;
        int p10 = p00 + numDivX;
        int p11 = p10 + 1;
        int tc00 = y * numDivX + x;
        int tc01 = tc00 + 1;
        int tc10 = tc00 + numDivX;
        int tc11 = tc10 + 1;

        int index = (y * subDivX * faceSize + (x * faceSize)) * 2;
        faces[index + 0] = p00;
        faces[index + 1] = tc00;
        faces[index + 2] = p10;
        faces[index + 3] = tc10;
        faces[index + 4] = p11;
        faces[index + 5] = tc11;

        index += faceSize;
        faces[index + 0] = p11;
        faces[index + 1] = tc11;
        faces[index + 2] = p01;
        faces[index + 3] = tc01;
        faces[index + 4] = p00;
        faces[index + 5] = tc00;
      }
    }

    TriangleMesh triangleMesh = new TriangleMesh();
    triangleMesh.getPoints().setAll(points);
    triangleMesh.getTexCoords().setAll(texCoords);
    triangleMesh.getFaces().setAll(faces);

    return triangleMesh;
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
      throw new RuntimeException("*** ERROR: common conditional SCENE3D is not supported");
    }

    stage.setTitle("JavaFX 3D Simple Picking demo");

    final PerspectiveCamera camera = new PerspectiveCamera();

    Node pickResultPanel = createOverlay();

    Group root = new Group();
    root.getChildren().addAll(pickResultPanel, createSubScene(camera));
    Scene scene = new Scene(root, 800, 800);
    scene.setFill(Color.color(0.2, 0.2, 0.2));
    scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent e) {
        switch (e.getCharacter()) {
        case "L":
          System.err.print("L ");
          boolean wireframe = meshView.getDrawMode() == DrawMode.LINE;
          meshView.setDrawMode(wireframe ? DrawMode.FILL : DrawMode.LINE);
          break;
        }
      }
    });
    stage.setScene(scene);
    stage.show();

    stage.requestFocus();
  }

  private Node createSimpleMesh() {

    TriangleMesh triangleMesh = buildTriangleMesh(2, 2, 30);
    meshView = new MeshView(triangleMesh);
    activateShape(meshView, "Simple Mesh");

    meshView.setDrawMode(DrawMode.FILL);
    meshView.setCullFace(CullFace.NONE);

    PhongMaterial material = new PhongMaterial();
    material.setDiffuseColor(Color.GOLD);
    material.setSpecularColor(Color.rgb(30, 30, 30));
    meshView.setMaterial(material);

    Node group = new Group(meshView);
    group.setTranslateX(550);
    group.setTranslateY(550);

    return group;
  }

  private void activateShape(final Shape3D shape, final String name) {

    shape.setId(name);

    EventHandler<MouseEvent> moveHandler = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        PickResult res = event.getPickResult();
        setState(res);
        event.consume();
      }
    };

    shape.setOnMouseMoved(moveHandler);
    shape.setOnMouseDragOver(moveHandler);

    shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        PickResult res = event.getPickResult();
        if (res == null) {
          System.err.println("Mouse entered has not pickResult");
        }
        setState(res);
      }
    });

    shape.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        PickResult res = event.getPickResult();
        if (res == null) {
          System.err.println("Mouse exited has not pickResult");
        }
        setState(res);
        event.consume();
      }
    });
  }

  Text caption, data;

  private Node createOverlay() {
    HBox hBox = new HBox(10);

    caption = new Text("Node:\n\nPoint:\nTexture Coord:\nFace:\nDistance:");
    caption.setFont(Font.font("Times New Roman", 18));
    caption.setTextOrigin(VPos.TOP);
    caption.setTextAlignment(TextAlignment.RIGHT);

    data = new Text("-- None --\n\n\n\n");
    data.setFont(Font.font("Times New Roman", 18));
    data.setTextOrigin(VPos.TOP);
    data.setTextAlignment(TextAlignment.LEFT);

    Rectangle rect = new Rectangle(300, 150, Color.color(0.2, 0.5, 0.3, 0.8));
    hBox.getChildren().addAll(caption, data);
    return new Group(rect, hBox);
  }

  private SubScene createSubScene(Camera camera) {
    final Node simpleMesh = createSimpleMesh();

    final Group parent = new Group(simpleMesh);
    parent.setTranslateZ(600);
    parent.setTranslateX(-150);
    parent.setTranslateY(-200);
    parent.setScaleX(0.8);
    parent.setScaleY(0.8);
    parent.setScaleZ(0.8);

    final PointLight pointLight = new PointLight(Color.ANTIQUEWHITE);
    pointLight.setTranslateX(100);
    pointLight.setTranslateY(100);
    pointLight.setTranslateZ(-300);

    final Group root = new Group(parent, pointLight, new Group(camera));
    root.setDepthTest(DepthTest.ENABLE);

    final SubScene subScene = new SubScene(root, 800, 800, true, SceneAntialiasing.BALANCED);
    subScene.setCamera(camera);
    subScene.setFill(Color.TRANSPARENT);

    subScene.setId("SubScene");

    return subScene;
  }

  final void setState(PickResult result) {
    if (result.getIntersectedNode() == null) {
      data.setText("Scene\n\n" + point3DToString(result.getIntersectedPoint()) + "\n" + point2DToString(result.getIntersectedTexCoord()) + "\n"
          + result.getIntersectedFace() + "\n" + String.format("%.1f", result.getIntersectedDistance()));
    } else {
      data.setText(result.getIntersectedNode().getId() + "\n" + getCullFace(result.getIntersectedNode()) + "\n" + point3DToString(result.getIntersectedPoint())
          + "\n" + point2DToString(result.getIntersectedTexCoord()) + "\n" + result.getIntersectedFace() + "\n"
          + String.format("%.1f", result.getIntersectedDistance()));
    }
  }

  private static String point3DToString(Point3D pt) {
    if (pt == null) {
      return "null";
    }
    return String.format("%.1f; %.1f; %.1f", pt.getX(), pt.getY(), pt.getZ());
  }

  private static String point2DToString(Point2D pt) {
    if (pt == null) {
      return "null";
    }
    return String.format("%.2f; %.2f", pt.getX(), pt.getY());
  }

  private static String getCullFace(Node n) {
    if (n instanceof Shape3D) {
      return "(CullFace." + ((Shape3D) n).getCullFace() + ")";
    }
    return "";
  }
}
