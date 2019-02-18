package se.vidstedt.raytrace;

public class Vec4f {
    private float[] values;

    public Vec4f(float f1, float f2, float f3, float f4) {
        this(new float[]{f1, f2, f3, f4});
    }

    private Vec4f(float[] values) {
        if (values.length != 4) {
            throw new IllegalArgumentException();
        }
        this.values = values;
    }

    public float getValue(int index) {
        return values[index];
    }
}
