package se.vidstedt.raytrace;

import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RectangleTest {
    private static final float EPSILON = 0.00001f;

    @Test
    public void testIntersectPositive() {
        Rectangle r = new Rectangle(new Vec3f(0, 0, -1), new Vec3f(2, 0, 0), new Vec3f(0, 2, 0), new Material());

        {
            Optional<Intersection> intersection = r.rayIntersect(new Vec3f(1, 1, 0), new Vec3f(0, 0, -1).normalize());
            assertTrue(intersection.isPresent());

            assertEquals(1, intersection.get().getPoint().values()[0], EPSILON);
            assertEquals(1, intersection.get().getPoint().values()[1], EPSILON);
            assertEquals(-1, intersection.get().getPoint().values()[2], EPSILON);

            assertEquals(0, intersection.get().getN().values()[0], EPSILON);
            assertEquals(0, intersection.get().getN().values()[1], EPSILON);
            assertEquals(1, intersection.get().getN().values()[2], EPSILON);
        }

        {
            Optional<Intersection> intersection = r.rayIntersect(new Vec3f(1, 1, -2), new Vec3f(0, 0, 1).normalize());
            assertTrue(intersection.isPresent());

            assertEquals(1, intersection.get().getPoint().values()[0], EPSILON);
            assertEquals(1, intersection.get().getPoint().values()[1], EPSILON);
            assertEquals(-1, intersection.get().getPoint().values()[2], EPSILON);

            assertEquals(0, intersection.get().getN().values()[0], EPSILON);
            assertEquals(0, intersection.get().getN().values()[1], EPSILON);
            assertEquals(-1, intersection.get().getN().values()[2], EPSILON);
        }

        {
            Optional<Intersection> intersection = r.rayIntersect(new Vec3f(0, 0, 0), new Vec3f(1, 1, -1).normalize());
            assertTrue(intersection.isPresent());
            assertEquals(1, intersection.get().getPoint().values()[0], EPSILON);
            assertEquals(1, intersection.get().getPoint().values()[1], EPSILON);
            assertEquals(-1, intersection.get().getPoint().values()[2], EPSILON);
        }

        {
            Optional<Intersection> intersection = r.rayIntersect(new Vec3f(0, 0, -2), new Vec3f(1, 1, 1).normalize());
            assertTrue(intersection.isPresent());
            assertEquals(1, intersection.get().getPoint().values()[0], EPSILON);
            assertEquals(1, intersection.get().getPoint().values()[1], EPSILON);
            assertEquals(-1, intersection.get().getPoint().values()[2], EPSILON);
        }
    }

    @Test
    public void testIntersectNegative() {
        Rectangle r = new Rectangle(new Vec3f(0, 0, -1), new Vec3f(2, 0, 0), new Vec3f(0, 2, 0), new Material());

        {
            Optional<Intersection> intersection = r.rayIntersect(new Vec3f(1, 1, -1.1f), new Vec3f(0, 0, -1).normalize());
            assertTrue(intersection.isEmpty());
        }

        {
            Optional<Intersection> intersection = r.rayIntersect(new Vec3f(1, 1, -0.9f), new Vec3f(0, 0, 1).normalize());
            assertTrue(intersection.isEmpty());
        }
    }
}
