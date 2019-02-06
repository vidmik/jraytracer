package se.vidstedt.raytrace;

class Intersection {
    private final float distance;
    private final Vec3f point;
    private final Vec3f N;
    private final Material material;

    public Intersection(float distance, Vec3f point, Vec3f n, Material material) {
        this.distance = distance;
        this.point = point;
        this.N = n;
        this.material = material;
    }

    public float getDistance() {
        return distance;
    }

    public Vec3f getPoint() {
        return point;
    }

    public Vec3f getN() {
        return N;
    }

    public Material getMaterial() {
        return material;
    }
}
