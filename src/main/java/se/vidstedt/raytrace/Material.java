package se.vidstedt.raytrace;

public class Material {
    private final float refractiveIndex;
    private final Vec3f diffuseColor;
    private final Albedo albedo;
    private final float specularExponent;

    public Material(float refractiveIndex, Albedo albedo, Vec3f diffuseColor, float specularExponent) {
        this.refractiveIndex = refractiveIndex;
        this.diffuseColor = diffuseColor;
        this.albedo = albedo;
        this.specularExponent = specularExponent;
    }

    public Material() {
        this(1, new Albedo(1, 0, 0, 0), new Vec3f(), 0);
    }

    public float getRefractiveIndex() {
        return refractiveIndex;
    }

    public Vec3f getDiffuseColor() {
        return diffuseColor;
    }

    public Material withDiffuseColor(Vec3f diffuseColor) {
        return new Material(refractiveIndex, albedo, diffuseColor, specularExponent);
    }

    public Albedo getAlbedo() {
        return albedo;
    }

    public float getSpecularExponent() {
        return specularExponent;
    }
}
