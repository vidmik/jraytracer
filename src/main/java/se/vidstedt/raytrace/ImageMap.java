package se.vidstedt.raytrace;

class ImageMap {
    enum ImageMapType { RECTANGULAR, SPHERICAL };
    private final ImageMapType type;
    private final int width;
    private final int height;
    private final Vec3f[] pixels;

    public ImageMap(ImageMapType type, int width, int height, Vec3f[] pixels) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public ImageMapType getType() {
        return type;
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
