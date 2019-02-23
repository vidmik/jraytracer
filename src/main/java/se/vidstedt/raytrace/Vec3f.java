package se.vidstedt.raytrace;

public class Vec3f {
    private float x, y, z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f() {
        this(0, 0, 0);
    }

    public float x() { return x; }
    public float y() { return y; }
    public float z() { return z; }


    public Vec3f add(Vec3f other) {
        return new Vec3f(x + other.x, y + other.y, z + other.z);
    }

    public Vec3f sub(Vec3f other) {
        return new Vec3f(x - other.x, y - other.y, z - other.z);
    }

    public Vec3f neg() {
        return mul(-1);
    }

    public Vec3f mul(float f) {
        return new Vec3f(x * f, y * f, z * f);
    }

    public float dotProduct(Vec3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3f cross(Vec3f other) {
        return new Vec3f(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    float norm() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    Vec3f normalize() {
        return normalize(1);
    }

    private Vec3f normalize(float l) {
        return mul(l / norm());
    }
}
