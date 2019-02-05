package se.vidstedt.raytrace;

public class ImageMap {
    private final int width;
    private final int height;
    private final Vec3f[] pixels;

    public ImageMap(int width, int height, Vec3f[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vec3f getPixel(int x, int y) {
        return pixels[y * width + x];
    }
}
