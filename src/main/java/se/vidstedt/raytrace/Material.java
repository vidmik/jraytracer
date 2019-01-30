package se.vidstedt.raytrace;

public class Material {
    private final float refractiveIndex;
    private Vec3f diffuseColor;
    private final Vec4f albedo;
    private final float specularExponent;

    public Material(float refractiveIndex, Vec4f albedo, Vec3f diffuseColor, float specularExponent) {
        this.refractiveIndex = refractiveIndex;
        this.diffuseColor = diffuseColor;
        this.albedo = albedo;
        this.specularExponent = specularExponent;
    }

    public Material() {
        this(1, new Vec4f(1, 0, 0, 0), new Vec3f(), 0);
    }

    public float getRefractiveIndex() {
        return refractiveIndex;
    }

    public Vec3f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vec3f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vec4f getAlbedo() {
        return albedo;
    }

    public float getSpecularExponent() {
        return specularExponent;
    }
}
