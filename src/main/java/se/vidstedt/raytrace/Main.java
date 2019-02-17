package se.vidstedt.raytrace;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

class Main {
    private static final String BACKGROUND_IMAGE = "/envmap.jpg";

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    public static void main(String[] args) {
        new Main().doIt();
    }

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

    public Frame doIt() {
        ImageMap background = getBackground();

        Material ivory = new Material(1.0f, new Vec4f(0.6f, 0.3f, 0.1f, 0.0f), new Vec3f(0.4f, 0.4f, 0.3f), 50.f);
        Material glass = new Material(1.5f, new Vec4f(0.0f, 0.5f, 0.1f, 0.8f), new Vec3f(0.6f, 0.7f, 0.8f), 125.f);
        Material redRubber = new Material(1.0f, new Vec4f(0.9f, 0.1f, 0.0f, 0.0f), new Vec3f(0.3f, 0.1f, 0.1f), 10.f);
        Material mirror = new Material(1.0f, new Vec4f(0.0f, 10.0f, 0.8f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f), 1425.f);

        SceneBuilder sceneBuilder = new SceneBuilder();

        if (true) {
            sceneBuilder.add(new Sphere(new Vec3f(-3f, 0f, -16f), 2, ivory));
            sceneBuilder.add(new Sphere(new Vec3f(-1.0f, -1.5f, -12f), 2, glass));
            sceneBuilder.add(new Sphere(new Vec3f(1.5f, -0.5f, -18f), 3, redRubber));
            sceneBuilder.add(new Sphere(new Vec3f(7f, 5f, -18f), 4, mirror));
        }

        if (true) {
            sceneBuilder.add(new Light(new Vec3f(-20f, 20f, 20f), 1.5f));
            sceneBuilder.add(new Light(new Vec3f(30f, 50f, -25f), 1.8f));
            sceneBuilder.add(new Light(new Vec3f(30f, 20f, 30f), 1.7f));
        }

        // Trying out Rectangle and Cube
        if (true) {
            if (true) {
                sceneBuilder.add(new Rectangle(new Vec3f(1.5f, -0.5f, -7f), new Vec3f(0.5f, 0, 0), new Vec3f(0, 1f, 0), redRubber));
                if (false) {
                    sceneBuilder.add(new Sphere(new Vec3f(1.5f, -0.5f, -10f), 0.1f, ivory));
                    sceneBuilder.add(new Sphere(new Vec3f(2.5f, 1.5f, -10f), 0.2f, redRubber));
                }
                if (true) {
                    sceneBuilder.add(new Light(new Vec3f(1f, 0f, -5f), 2.0f));
                }
            }

            if (false) {
                sceneBuilder.add(
                        new Cube(
                                new Vec3f(-0.5f, -0.5f, -5f),
                                new Vec3f(2f, 0, 0),
                                new Vec3f(0, 3f, 0),
                                new Vec3f(0, 0f, -1f),
                                redRubber));
                if (true) {
                    sceneBuilder.add(new Light(new Vec3f(1f, 0f, -2f), 2.0f));
                }
            }
        }

        return new RayTracer(WIDTH, HEIGHT, background).render(sceneBuilder.build());
    }
}
