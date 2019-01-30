package se.vidstedt.raytrace;

class Light {
    private final Vec3f position;
    private final float intensity;

    public Light(Vec3f position, float intensity) {
        this.position = position;
        this.intensity = intensity;
    }

    public Vec3f getPosition() {
        return position;
    }

    public float getIntensity() {
        return intensity;
    }
}
