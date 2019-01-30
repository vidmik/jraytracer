package se.vidstedt.raytrace;

import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");
        new Main().doIt();
    }

    public byte[] doIt() {
        Material ivory = new Material(1.0f, new Vec4f(0.6f, 0.3f, 0.1f, 0.0f), new Vec3f(0.4f, 0.4f, 0.3f), 50.f);
        Material glass = new Material(1.5f, new Vec4f(0.0f, 0.5f, 0.1f, 0.8f), new Vec3f(0.6f, 0.7f, 0.8f), 125.f);
        Material redRubber = new Material(1.0f, new Vec4f(0.9f, 0.1f, 0.0f, 0.0f), new Vec3f(0.3f, 0.1f, 0.1f), 10.f);
        Material mirror = new Material(1.0f, new Vec4f(0.0f, 10.0f, 0.8f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f), 1425.f);

        ArrayList<Sphere> spheres = new ArrayList<>();
        spheres.add(new Sphere(new Vec3f(-3f, 0f, -16f), 2, ivory));
        spheres.add(new Sphere(new Vec3f(-1.0f, -1.5f, -12f), 2, glass));
        spheres.add(new Sphere(new Vec3f(1.5f, -0.5f, -18f), 3, redRubber));
        spheres.add(new Sphere(new Vec3f(7f, 5f, -18f), 4, mirror));

        ArrayList<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vec3f(-20f, 20f, 20f), 1.5f));
        lights.add(new Light(new Vec3f(30f, 50f, -25f), 1.8f));
        lights.add(new Light(new Vec3f(30f, 20f, 30f), 1.7f));

        return new RayTracer().render(spheres, lights);
    }
}
