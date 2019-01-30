package se.vidstedt.raytrace;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui extends Application {
    @Override
    public void start(Stage primaryStage) {
        byte[] data = new Main().doIt();

        VBox box = new VBox();
        Scene scene = new Scene(box);

        WritableImage image = new WritableImage(1024, 768);
        PixelWriter writer = image.getPixelWriter();
        for (int height = 0; height < 768; height++) {
            for (int width = 0; width < 1024; width++) {
                int startIndex = (height * 1024 + width) * 3;
                Color color = Color.rgb(
                        Byte.toUnsignedInt(data[startIndex]),
                        Byte.toUnsignedInt(data[startIndex + 1]),
                        Byte.toUnsignedInt(data[startIndex + 2]));
                writer.setColor(width, height, color);
            }
        }
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
