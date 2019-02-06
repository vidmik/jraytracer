package se.vidstedt.raytrace;

import java.util.Optional;

class Sphere implements Shape {
    private final Vec3f center;
    private final float radius;
    private final Material material;

    Sphere(Vec3f center, float radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    public Optional<Intersection> rayIntersect(Vec3f orig, Vec3f dir) {
        Vec3f L = center.sub(orig);
        float tca = L.dotProduct(dir);
        float d2 = L.dotProduct(L) - tca * tca;
        if (d2 > radius * radius) {
            return Optional.empty();
        }
        float thc = (float) Math.sqrt(radius * radius - d2);
        float t0 = tca - thc;
        float t1 = tca + thc;
        if (t0 < 0) {
            t0 = t1;
        }
        if (t0 >= 0) {
            Vec3f hit = orig.add(dir.mul(t0));
            return Optional.of(new Intersection(t0, hit, hit.sub(center).normalize(), material));
        } else {
            return Optional.empty();
        }
    }
}
