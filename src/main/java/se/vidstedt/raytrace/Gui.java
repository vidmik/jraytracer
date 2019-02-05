package se.vidstedt.raytrace;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui extends Application {
    private Image renderImage() {
        Frame frame = new Main().doIt();

        WritableImage image = new WritableImage(frame.getWidth(), frame.getHeight());
        PixelWriter writer = image.getPixelWriter();
        int index = 0;
        byte[] data = frame.getData();
        for (int height = 0; height < frame.getHeight(); height++) {
            for (int width = 0; width < frame.getWidth(); width++) {
                Color color = Color.rgb(
                        Byte.toUnsignedInt(data[index++]),
                        Byte.toUnsignedInt(data[index++]),
                        Byte.toUnsignedInt(data[index++]));
                writer.setColor(width, height, color);
            }
        }
        return image;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox box = new VBox();
        Scene scene = new Scene(box);

        Image image = renderImage();
        ImageView iv = new ImageView(image);
        box.getChildren().add(iv);

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                case Q:
                    Platform.exit();
                    break;
            }
        });

        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
