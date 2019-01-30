package se.vidstedt.raytrace;

import java.util.ArrayList;

class RayTracer {
    private static Vec3f reflect(Vec3f I, Vec3f N) {
        // I - N*2.f*(I*N)
        return I.sub(N.mul(2.f).mul(I.dotProduct(N)));
    }

    private static Vec3f refract(Vec3f I, Vec3f N, float etaT) { // Snell's law
        return refract(I, N, etaT, 1.f);
    }

    private static Vec3f refract(Vec3f I, Vec3f N, float etaT, float etaI) { // Snell's law
        float cosI = -Math.max(-1.f, Math.min(1.f, I.dotProduct(N)));
        if (cosI < 0) {
            return refract(I, N.mul(-1), etaI, etaT); // if the ray comes from the inside the object, swap the air and the media
        }
        float eta = etaI / etaT;
        float k = 1 - eta * eta * (1 - cosI * cosI);
        if (k < 0) {
            return new Vec3f(1, 0, 0);
        } else {
            return I.mul(eta).add(N.mul((float) (eta * cosI - Math.sqrt(k)))); // k<0 = total reflection, no ray to refract. I refract it anyways, this has no physical meaning
        }
    }

    private boolean sceneIntersect(Vec3f orig, Vec3f dir, ArrayList<Sphere> spheres, Vec3f[] hit, Vec3f[] N, Material[] material) {
        float spheresDist = Float.MAX_VALUE;
        for (Sphere sphere : spheres) {
            Float[] distI = new Float[1];
            if (sphere.rayIntersect(orig, dir, distI) && distI[0] < spheresDist) {
                spheresDist = distI[0];
                hit[0] = orig.add(dir.mul(distI[0]));
                N[0] = hit[0].sub(sphere.getCenter()).normalize();
                material[0] = sphere.getMaterial();
            }
        }

        float checkerboardDist = Float.MAX_VALUE;
        if (Math.abs(dir.values()[1]) > 1e-3) {
            float d = -(orig.values()[1] + 4) / dir.values()[1]; // the checkerboard plane has equation y = -4
            Vec3f pt = orig.add(dir.mul(d));
            if (d > 0 && Math.abs(pt.values()[0]) < 10 && pt.values()[2] < -10 && pt.values()[2] > -30 && d < spheresDist) {
                checkerboardDist = d;
                hit[0] = pt;
                N[0] = new Vec3f(0, 1, 0);

                //(int(.5*hit.x+1000) + int(.5*hit.z)) & 1 ? Vec3f(.3, .3, .3) : Vec3f(.3, .2, .1);
                int s = (int) (.5f * hit[0].values()[0] + 1000) + (int) (.5 * hit[0].values()[2]);
                Vec3f color;
                if ((s & 1) != 0) {
                    color = new Vec3f(.3f, .3f, .3f);
                } else {
                    color = new Vec3f(.3f, .2f, .1f);
                }
                material[0].setDiffuseColor(color);
            }
        }
        return Math.min(spheresDist, checkerboardDist) < 1000;
    }

    private Vec3f castRay(Vec3f orig, Vec3f dir, ArrayList<Sphere> spheres, ArrayList<Light> lights) {
        return castRay(orig, dir, spheres, lights, 0);
    }

    private Vec3f castRay(Vec3f orig, Vec3f dir, ArrayList<Sphere> spheres, ArrayList<Light> lights, int depth) {
        Vec3f[] pointBox = new Vec3f[1], NBox = new Vec3f[1];
        Material[] materialBox = {new Material()};

        if (depth > 4 || !sceneIntersect(orig, dir, spheres, pointBox, NBox, materialBox)) {
            return new Vec3f(0.2f, 0.7f, 0.8f); // background color
        }

        Vec3f point = pointBox[0];
        Vec3f N = NBox[0];
        Material material = materialBox[0];

        Vec3f reflectDir = reflect(dir, N).normalize();
        Vec3f refractDir = refract(dir, N, material.getRefractiveIndex()).normalize();
        Vec3f reflectOrig = reflectDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f)); // offset the original point to avoid occlusion by the object itself
        Vec3f refractOrig = refractDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f));
        Vec3f reflectColor = castRay(reflectOrig, reflectDir, spheres, lights, depth + 1);
        Vec3f refractColor = castRay(refractOrig, refractDir, spheres, lights, depth + 1);

        float diffuseLightIntensity = 0, specularLightIntensity = 0;
        for (Light light : lights) {
            Vec3f lightDir = (light.getPosition().sub(point)).normalize();
            float lightDistance = (light.getPosition().sub(point)).norm();

            Vec3f shadowOrig = lightDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f)); // checking if the point lies in the shadow of the lights[i]
            Vec3f[] shadowPtBox = new Vec3f[1], shadowNBox = new Vec3f[1];
            Material[] tmpMaterialBox = {new Material()};
            if (sceneIntersect(shadowOrig, lightDir, spheres, shadowPtBox, shadowNBox, tmpMaterialBox)) {
                Vec3f shadowPt = shadowPtBox[0];
                if (shadowPt.sub(shadowOrig).norm() < lightDistance) {
                    continue;
                }
            }

            diffuseLightIntensity += light.getIntensity() * Math.max(0.f, lightDir.dotProduct(N));
            specularLightIntensity += Math.pow(Math.max(0.f, reflect(lightDir.neg(), N).neg().dotProduct(dir)), material.getSpecularExponent()) * light.getIntensity();
        }
        // CMH: This is almost certainly incorrect...
        return material.getDiffuseColor().mul(diffuseLightIntensity).mul(material.getAlbedo().values()[0])
                       .add(new Vec3f(1.f, 1.f, 1.f).mul(specularLightIntensity).mul(material.getAlbedo().values()[1]))
                       .add(reflectColor.mul(material.getAlbedo().values()[2]).add(refractColor.mul(material.getAlbedo().values()[3])));
    }

    Frame render(ArrayList<Sphere> spheres, ArrayList<Light> lights) {
        int width = 1024;
        int height = 768;
        float fov = (float) (Math.PI / 3.);
        Vec3f[] framebuffer = new Vec3f[width * height];

        for (int j = 0; j < height; j++) { // actual rendering loop
            if ((j % 100) == 0) {
                System.out.println("Rendering line " + j + "...");
            }
            for (int i = 0; i < width; i++) {
                float dirX = (i + 0.5f) - width / 2.f;
                float dirY = -(j + 0.5f) + height / 2.f;    // this flips the image at the same time
                float dirZ = -height / (2.f * (float) Math.tan(fov / 2.f));
                Vec3f c = castRay(new Vec3f(0, 0, 0), new Vec3f(dirX, dirY, dirZ).normalize(), spheres, lights);
                framebuffer[i + j * width] = c;
            }
        }

        byte[] data = new byte[width * height * 3];
        for (int i = 0; i < height * width; i++) {
            Vec3f c = framebuffer[i];
            float max = Math.max(c.values()[0], Math.max(c.values()[1], c.values()[2]));
            if (max > 1) {
                c = c.mul(1.f / max);
            }
            for (int j = 0; j < 3; j++) {
                byte b = (byte) (255 * Math.max(0.f, Math.min(1.f, c.values()[j])));
                data[i * 3 + j] = b;
            }
        }

        return new Frame(width, height, data);
    }
}
