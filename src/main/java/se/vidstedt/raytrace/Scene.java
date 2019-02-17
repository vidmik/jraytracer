package se.vidstedt.raytrace;

import java.util.Arrays;
import java.util.Collection;

class Scene {
    private final Shape[] shapes;
    private final Light[] lights;

    public Scene(Shape[] shapes, Light[] lights) {
        this.shapes = shapes;
        this.lights = lights;
    }

    public Collection<Shape> getShapes() {
        return Arrays.asList(shapes);
    }

    public Collection<Light> getLights() {
        return Arrays.asList(lights);
    }
}
