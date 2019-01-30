package se.vidstedt.raytrace;

public class Frame {
    private final int width;
    private final int height;
    private final byte[] data;

    public Frame(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getData() {
        return data;
    }
}
