package se.vidstedt.raytrace;

import java.util.Optional;

public class Cube implements Shape {
    private final Rectangle[] sides;

    private Cube(Vec3f corner, Vec3f[] edges, Material material) {
        if (edges.length != 3) {
            throw new IllegalArgumentException();
        }

        Vec3f oppositeCorner = corner.add(edges[0]).add(edges[1]).add(edges[2]);

        sides = new Rectangle[]{
                new Rectangle(corner, edges[0], edges[1], material),
                new Rectangle(corner, edges[0], edges[2], material),
                new Rectangle(corner, edges[1], edges[2], material),
                new Rectangle(oppositeCorner, edges[0].mul(-1), edges[1].mul(-1), material),
                new Rectangle(oppositeCorner, edges[0].mul(-1), edges[2].mul(-1), material),
                new Rectangle(oppositeCorner, edges[1].mul(-1), edges[2].mul(-1), material)
        };
    }

    Cube(Vec3f corner, Vec3f edge0, Vec3f edge1, Vec3f edge2, Material material) {
        this(corner, new Vec3f[]{edge0, edge1, edge2}, material);
    }

    @Override
    public Optional<Intersection> rayIntersect(Vec3f orig, Vec3f dir) {
        Optional<Intersection> intersection = Optional.empty();
        for (Rectangle r : sides) {
            Optional<Intersection> candidateIntersection = r.rayIntersect(orig, dir);
            if (candidateIntersection.isPresent()) {
                if (intersection.isEmpty() || candidateIntersection.get().getDistance() < intersection.get().getDistance()) {
                    intersection = candidateIntersection;
                }
            }
        }
        return intersection;
    }
}
