package se.vidstedt.raytrace;

class Sphere {
    private final Vec3f center;
    private final float radius;
    private final Material material;

    Sphere(Vec3f center, float radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    public Vec3f getCenter() {
        return center;
    }

    public Material getMaterial() {
        return material;
    }

    boolean rayIntersect(Vec3f orig, Vec3f dir, Float[] t0) {
        Vec3f L = center.sub(orig);
        float tca = L.dotProduct(dir);
        float d2 = L.dotProduct(L) - tca * tca;
        if (d2 > radius * radius) {
            return false;
        }
        float thc = (float) Math.sqrt(radius * radius - d2);
        t0[0] = tca - thc;
        float t1 = tca + thc;
        if (t0[0] < 0) {
            t0[0] = t1;
        }
        return t0[0] >= 0;
    }
}
