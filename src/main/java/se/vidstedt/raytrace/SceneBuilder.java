package se.vidstedt.raytrace;

import java.util.ArrayList;

class SceneBuilder {
    private final ArrayList<Shape> shapes = new ArrayList<>();
    private final ArrayList<Light> lights = new ArrayList<>();

    public SceneBuilder() {
    }

    SceneBuilder add(Light light) {
        lights.add(light);
        return this;
    }

    SceneBuilder add(Shape shape) {
        shapes.add(shape);
        return this;
    }

    Scene build() {
        return new Scene(shapes.toArray(new Shape[0]), lights.toArray(new Light[0]));
    }
}
