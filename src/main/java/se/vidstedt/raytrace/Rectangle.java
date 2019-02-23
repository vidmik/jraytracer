package se.vidstedt.raytrace;

import java.util.Optional;

public class Rectangle implements Shape {
    private final Vec3f corner;
    private final Vec3f edge1, edge2;
    private final Material material;

    public Rectangle(Vec3f corner, Vec3f edge1, Vec3f edge2, Material material) {
        this.corner = corner;
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.material = material;
    }

    @Override
    public Optional<Intersection> rayIntersect(Vec3f orig, Vec3f dir) {
        // R0: ray orig
        // D: ray direction (unit vector)
        // P: point intersection on rectangle
        // P0: rectangle corner
        // S1: rectangle edge 1
        // S2: rectangle edge 2
        // N: rectangle normal in P0 (unit vector)
        // a: length of ray
        //
        // Point lies on ray:
        //
        // P = R0 + a * D
        //
        // Vector on plane from corner to intersection point is perpendicular to normal:
        //
        // (P0-P) dot N = 0  ===>  P0 dot N - P dot N = 0  ===>  P0 dot N = P dot N
        //
        // P0 dot N = (R0 + a * D) dot N = R0 dot N + a * D dot N  ===>  a * D dot N = P0 dot N - R0 dot N = (P0 - R0) dot N
        //
        // ===> a = ((P0 - R0) dot N) / (D dot N)
        //
        // To ensure point is within rectangle bounds:
        //
        // Q1: Projection of P0P along S1
        // Q2: Projection of P0P along S2
        //
        // Projection w onto v:
        //
        // projection(v) W = (v dot w) * v / v dot v;
        //
        // Point within rectangle bounds iff:
        //
        // 0 <= length(Q1) <= length(S1)   AND   0 <= length(Q2) <= length(S2)

        if (Math.abs(dir.norm() - 1) > .0001f) {
            throw new IllegalArgumentException();
        }

        Vec3f R0 = orig;
        Vec3f D = dir;
        Vec3f P0 = corner;
        Vec3f S1 = edge1;
        Vec3f S2 = edge2;

        // CMH: The direction of the normal matters...
        Vec3f N = S1.cross(S2).normalize();

        float a = P0.sub(R0).dotProduct(N) / D.dotProduct(N);
        if (a < 0) {
            return Optional.empty();
        }

        Vec3f P = R0.add(D.mul(a));
        Vec3f P0P = P.sub(P0);

        // Projection must be in same general direction
        if (S1.dotProduct(P0P) < 0 || S2.dotProduct(P0P) < 0) {
            return Optional.empty();
        }

        Vec3f Q1 = S1.mul(S1.dotProduct(P0P)).mul(1 / S1.dotProduct(S1));
        Vec3f Q2 = S2.mul(S2.dotProduct(P0P)).mul(1 / S2.dotProduct(S2));

        float Q1norm = Q1.norm();
        float S1norm = S1.norm();
        float Q2norm = Q2.norm();
        float S2norm = S2.norm();
        if (0 < Q1norm && Q1norm < S1norm && 0 < Q2norm && Q2norm < S2norm) {
            // Make sure normal is in reverse direction of ray
            if (D.dotProduct(N) > 0) {
                N = N.mul(-1);
            }

            return Optional.of(new Intersection(a, P, N, material));
        } else {
            return Optional.empty();
        }
    }
}
