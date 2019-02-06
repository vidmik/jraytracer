package se.vidstedt.raytrace;

import java.util.Optional;

interface Shape {
    Optional<Intersection> rayIntersect(Vec3f orig, Vec3f dir);
}
