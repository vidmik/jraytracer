package se.vidstedt.raytrace;

class Main {
    private static final String BACKGROUND_IMAGE = "/envmap.jpg";

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private ImageMap readBackground() {
        return new ImageMapReader(BACKGROUND_IMAGE).read();
    }

    Scene createScene() {
        Material ivory = new Material(1.0f, new Albedo(0.6f, 0.3f, 0.1f, 0.0f), new Vec3f(0.4f, 0.4f, 0.3f), 50.f);
        Material glass = new Material(1.5f, new Albedo(0.0f, 0.5f, 0.1f, 0.8f), new Vec3f(0.6f, 0.7f, 0.8f), 125.f);
        Material redRubber = new Material(1.0f, new Albedo(0.9f, 0.1f, 0.0f, 0.0f), new Vec3f(0.3f, 0.1f, 0.1f), 10.f);
        Material mirror = new Material(1.0f, new Albedo(0.0f, 10.0f, 0.8f, 0.0f), new Vec3f(1.0f, 1.0f, 1.0f), 1425.f);

        return new SceneBuilder()
                .add(new Sphere(new Vec3f(-3f, 0f, -16f), 2, ivory))
                .add(new Sphere(new Vec3f(-1.0f, -1.5f, -12f), 2, glass))
                .add(new Sphere(new Vec3f(1.5f, -0.5f, -18f), 3, redRubber))
                .add(new Sphere(new Vec3f(7f, 5f, -18f), 4, mirror))

                .add(new Light(new Vec3f(-20f, 20f, 20f), 1.5f))
                .add(new Light(new Vec3f(30f, 50f, -25f), 1.8f))
                .add(new Light(new Vec3f(30f, 20f, 30f), 1.7f))

                .add(new Cube(
                        new Vec3f(3f, 0f, -10f),
                        new Vec3f(3f, 0f, 0f),
                        new Vec3f(0f, -3f, 0f),
                        new Vec3f(0f, 0f, -3f),
                        redRubber))

                //.add(new Light(new Vec3f(1f, 0f, -2f), 2.0f));
                //.add(new Light(new Vec3f(1f, 0f, -14f), 2.0f));

                .build();
    }

    public RayTracer createRayTracer() {
        return new RayTracer(WIDTH, HEIGHT, readBackground());
    }

    public static void main(String[] args) {
        Main main = new Main();
        Benchmark benchmark = new Benchmark(main.createRayTracer(), main.createScene());
        benchmark.benchmark();
    }
}
