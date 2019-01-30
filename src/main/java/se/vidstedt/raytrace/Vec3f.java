package se.vidstedt.raytrace;

public class Vec3f {
    private float[] values;

    public Vec3f(float f1, float f2, float f3) {
        this(new float[]{f1, f2, f3});
    }

    private Vec3f(float[] values) {
        if (values.length != 3) {
            throw new IllegalArgumentException();
        }
        this.values = values;
    }

    public Vec3f() {
        this(0, 0, 0);
    }

    public float[] values() {
        return values;
    }

    public Vec3f add(Vec3f other) {
        float[] ret = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            ret[i] = values[i] + other.values[i];
        }
        return new Vec3f(ret);
    }

    public Vec3f sub(Vec3f other) {
        float[] ret = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            ret[i] = values[i] - other.values[i];
        }
        return new Vec3f(ret);
    }

    public Vec3f neg() {
        return mul(-1);
    }

    public Vec3f mul(float f) {
        float[] ret = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            ret[i] = values[i] * f;
        }
        return new Vec3f(ret);
    }

    public float dotProduct(Vec3f other) {
        float ret = 0;
        for (int i = 0; i < values.length; i++) {
            ret += values[i] * other.values[i];
        }
        return ret;
    }

    float norm() {
        return (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
    }

    Vec3f normalize() {
        return normalize(1);
    }

    private Vec3f normalize(float l) {
        // CMH: Original code mutates the instance here...
        return mul(l / norm());
    }
}
