package se.vidstedt.raytrace;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

class ImageMapReader {
    private final String resourcePath;

    public ImageMapReader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public ImageMap read() {
        PixelReader reader;
        int width, height;
        Image image = new Image(this.getClass().getResourceAsStream(resourcePath));
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

        return new ImageMap(ImageMap.ImageMapType.SPHERICAL, width, height, pixels);
    }
}
