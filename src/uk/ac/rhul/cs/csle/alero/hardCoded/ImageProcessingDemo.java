package uk.ac.rhul.cs.csle.alero.hardCoded;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import uk.ac.rhul.cs.csle.alero.Alero;

public class ImageProcessingDemo {
  ImageProcessingDemo() {
    Image inputImage = new Image("File:GERTank.png");
    System.out.println("Image Height: " + inputImage.getHeight());
    System.out.println("Image Width: " + inputImage.getWidth());

    WritableImage outputImage = new WritableImage((int) inputImage.getWidth(), (int) inputImage.getHeight());

    PixelReader pixelReader = inputImage.getPixelReader();
    PixelWriter pixelWriter = outputImage.getPixelWriter();

    for (int readY = 1; readY < inputImage.getHeight() - 1; readY++) {
      for (int readX = 1; readX < inputImage.getWidth() - 1; readX++) {
        /*
         * p4 p3 p2 p5 p0 p1 p6 p7 p8
         */
        double p0 = pixelReader.getColor(readX, readY).grayscale().getRed();
        double p1 = pixelReader.getColor(readX + 1, readY).grayscale().getRed();
        double p2 = pixelReader.getColor(readX + 1, readY + 1).grayscale().getRed();
        double p3 = pixelReader.getColor(readX, readY + 1).grayscale().getRed();
        double p4 = pixelReader.getColor(readX - 1, readY + 1).grayscale().getRed();
        double p5 = pixelReader.getColor(readX - 1, readY).grayscale().getRed();
        double p6 = pixelReader.getColor(readX - 1, readY - 1).grayscale().getRed();
        double p7 = pixelReader.getColor(readX, readY - 1).grayscale().getRed();
        double p8 = pixelReader.getColor(readX + 1, readY - 1).grayscale().getRed();

        double sobeldx = (p2 + 2 * p1 + p8) - (p4 + 2 * p5 + p6);
        double sobeldy = (p4 + 2 * p3 + p2) - (p6 + 2 * p7 + p8);
        double sobelFilter = Math.sqrt(sobeldx * sobeldx + sobeldy * sobeldy);

        double edge = threshold(0.5, sobelFilter);

        double meanFilter = (p0 + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8) / 9;

        double q0 = range(0, 1, edge);

        pixelWriter.setColor(readX, readY, new Color(q0, q0, q0, 1));
      }
    }

    ImageView inputImageView = new ImageView(inputImage);
    ImageView outputImageView = new ImageView(outputImage);

    HBox hFrame = new HBox(5.0); // Gap of give units between elements in the row
    hFrame.getChildren().addAll(inputImageView, outputImageView);
    Alero.add(hFrame);
  }

  private double range(double offset, double scale, double value) {
    double ret = offset + (scale * value);
    if (ret > 1.0) ret = 1.0;
    if (ret < 0) ret = 0;
    return ret;
  }

  private double threshold(double threshold, double value) {
    return value >= threshold ? 1.0 : 0.0;
  }
}
