package se.vidstedt.raytrace;

class Benchmark {
    private static volatile Frame frameSink;

    private final RayTracer rayTracer;
    private final Scene scene;

    public Benchmark(RayTracer rayTracer, Scene scene) {
        this.rayTracer = rayTracer;
        this.scene = scene;
    }

    void benchmark() {
        System.out.println("Starting benchmark...");
        for (int i = 0; i < 128; i++) {
            long start = System.currentTimeMillis();
            frameSink = rayTracer.render(scene);
            long end = System.currentTimeMillis();
            System.out.println("Rendered frame in " + (end - start) + "ms");
        }
    }
}
