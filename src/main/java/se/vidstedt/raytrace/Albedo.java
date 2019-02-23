package se.vidstedt.raytrace;

public class Albedo {
    private float diffuse, specular, reflect, refract;

    public Albedo(float diffuse, float specular, float reflect, float refract) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.reflect = reflect;
        this.refract = refract;
    }

    public float getDiffuse() {
        return diffuse;
    }

    public float getSpecular() {
        return specular;
    }

    public float getReflect() {
        return reflect;
    }

    public float getRefract() {
        return refract;
    }
}
