/*
 * Example image processing plugin
 *
 * Ideas for image processing operations:
 *
 * see https://en.wikipedia.org/wiki/Median_filter and https://en.wikipedia.org/wiki/Sobel_operator and follow links
*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uk.ac.rhul.cs.csle.art.term.AbstractValuePlugin;
import uk.ac.rhul.cs.csle.art.util.Util;

public class ARTValuePluginImageProcessing extends AbstractValuePlugin {
  int windowX = 1200, windowY = 450;
  HBox root = new HBox();

  Image inputImage;
  WritableImage outputImage = new WritableImage(windowX / 2, windowY);

  @Override
  public String description() {
    return "Adrian's example image processing plugin";
  }

  @Override
  public Object plugin(Object... args) {
    switch ((String) args[0]) {
    case "init":
      System.out.println("Initialising window");
      Stage stage = new Stage();
      stage.setScene(new Scene(root, windowX, windowY));
      stage.setTitle("Image processing display from an ART language");
      stage.show();
      break;

    case "load":
      InputStream inputStream = null;
      try {
        inputStream = new FileInputStream((String) args[1]);
      } catch (FileNotFoundException e) {
        Util.error("Unable to open image file " + args[1]);
      }

      inputImage = new Image(inputStream, windowX / 2, windowY, true, true);
      root.getChildren().addAll(new ImageView(inputImage), new ImageView(outputImage));
      break;

    case "invert":
      PixelReader pixelReader = inputImage.getPixelReader();
      PixelWriter pixelWriter = outputImage.getPixelWriter();

      for (int readY = 0; readY < inputImage.getHeight(); readY++)
        for (int readX = 0; readX < inputImage.getWidth(); readX++) {
          double pixel = pixelReader.getColor(readX, readY).grayscale().getRed();
          double invertedPixel = 1 - pixel;
          pixelWriter.setColor(readX, readY, new Color(invertedPixel, invertedPixel, invertedPixel, 1));
        }
      break;

    default:
      Util.fatal("Unknown plugin operation: " + args[0]);
    }

    return __done;
  }
}