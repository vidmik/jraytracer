package se.vidstedt.raytrace;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui extends Application {
    private static final String BACKGROUND_IMAGE = "/envmap.jpg";

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private ImageMap getBackground() {
        PixelReader reader;
        int width, height;
        Image image = new Image(this.getClass().getResourceAsStream(BACKGROUND_IMAGE));
        width = (int) image.getWidth();
        height = (int) image.getHeight();
        reader = image.getPixelReader();
        Vec3f[] pixels = new Vec3f[width * height];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = reader.getColor(x, y);
                pixels[index++] = new Vec3f((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue());
            }
        }

        return new ImageMap(width, height, pixels);
    }

    Scene createScene() {
        Material ivory = new Material(1.0f, new Vec4f(0.6f, 0.3f, 0.1f, 0.0f), new Vec3f(0.4f, 0.4f, 0.3f), 50.f);
        Material glass = new Material(1.5f, new Vec4f(0.0f, 0.5f, 0.1f, 0.8f), new Vec3f(0.6f, 0.7f, 0.8f), 125.f);
        Material redRubber = new Material(1.0f, new Vec4f(0.9f, 0.1f, 0.0f, 0.0f), new Vec3f(0.3f, 0.1f, 0.1f), 10.f);
        Material mirror = new Material(1.0f, new Vec4f(0.0f, 10.0f, 0.8f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f), 1425.f);

        return new SceneBuilder()
                .add(new Sphere(new Vec3f(-3f, 0f, -16f), 2, ivory))
                .add(new Sphere(new Vec3f(-1.0f, -1.5f, -12f), 2, glass))
                .add(new Sphere(new Vec3f(1.5f, -0.5f, -18f), 3, redRubber))
                .add(new Sphere(new Vec3f(7f, 5f, -18f), 4, mirror))

                .add(new Light(new Vec3f(-20f, 20f, 20f), 1.5f))
                .add(new Light(new Vec3f(30f, 50f, -25f), 1.8f))
                .add(new Light(new Vec3f(30f, 20f, 30f), 1.7f))

                .add(new Cube(
                        new Vec3f(3f, 0f, -10f),
                        new Vec3f(1f, 0f, 0f),
                        new Vec3f(0f, 1f, 0f),
                        new Vec3f(0f, 0f, -1f),
                        redRubber))

                //.add(new Light(new Vec3f(1f, 0f, -2f), 2.0f));
                //.add(new Light(new Vec3f(1f, 0f, -14f), 2.0f));

                .build();
    }

    private Frame renderFrame() {
        ImageMap background = getBackground();
        Scene scene = createScene();

        return new RayTracer(WIDTH, HEIGHT, background).render(scene);
    }

    private Image renderImage() {
        Frame frame = renderFrame();

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
        javafx.scene.Scene scene = new javafx.scene.Scene(box);

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
