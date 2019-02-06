package se.vidstedt.raytrace;

import java.util.ArrayList;
import java.util.Optional;

class RayTracer {
    private final int width;
    private final int height;
    private final ImageMap background;

    public RayTracer(int width, int height, ImageMap background) {
        this.width = width;
        this.height = height;
        this.background = background;
    }

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

    private Optional<Intersection> sceneIntersect(Vec3f orig, Vec3f dir, ArrayList<Shape> shapes) {
        Optional<Intersection> intersection = Optional.empty();

        float spheresDist = Float.MAX_VALUE;
        for (Shape shape : shapes) {
            Optional<Intersection> sphereIntersection = shape.rayIntersect(orig, dir);
            if (sphereIntersection.isPresent() && sphereIntersection.get().getDistance() < spheresDist) {
                intersection = sphereIntersection;
                spheresDist = intersection.get().getDistance();
            }
        }

        if (Math.abs(dir.values()[1]) > 1e-3) {
            float checkerboardDist = -(orig.values()[1] + 4) / dir.values()[1]; // the checkerboard plane has equation y = -4
            Vec3f pt = orig.add(dir.mul(checkerboardDist));
            if (checkerboardDist > 0 && Math.abs(pt.values()[0]) < 10 && pt.values()[2] < -10 && pt.values()[2] > -30 && checkerboardDist < spheresDist) {
                Vec3f hit = pt;
                Vec3f N = new Vec3f(0, 1, 0);

                int s = (int) (.5f * hit.values()[0] + 1000) + (int) (.5 * hit.values()[2]);
                Vec3f color;
                if ((s & 1) != 0) {
                    color = new Vec3f(.3f, .3f, .3f);
                } else {
                    color = new Vec3f(.3f, .2f, .1f);
                }
                Material material = intersection.isPresent() ? intersection.get().getMaterial() : new Material();
                material.setDiffuseColor(color);
                intersection = Optional.of(new Intersection(checkerboardDist, hit, N, material));
            }
        }

        if (intersection.isPresent() && intersection.get().getDistance() < 1000) {
            return intersection;
        } else {
            return Optional.empty();
        }
    }

    private Vec3f castRay(Vec3f orig, Vec3f dir, ArrayList<Shape> shapes, ArrayList<Light> lights) {
        return castRay(orig, dir, shapes, lights, 0);
    }

    private Vec3f castRay(Vec3f orig, Vec3f dir, ArrayList<Shape> shapes, ArrayList<Light> lights, int depth) {
        if (depth > 4) {
            return new Vec3f(0.2f, 0.7f, 0.8f); // any color
        }
        Optional<Intersection> intersection = sceneIntersect(orig, dir, shapes);
        if (intersection.isEmpty()) {
            int x = (int)((dir.values()[0]/2 + 0.5) * background.getWidth());
            int y = (int)((-dir.values()[1]/2 + 0.5) * background.getHeight());
            return background.getPixel(x, y);
        }

        Vec3f point = intersection.get().getPoint();
        Vec3f N = intersection.get().getN();
        Material material = intersection.get().getMaterial();

        Vec3f reflectDir = reflect(dir, N).normalize();
        Vec3f refractDir = refract(dir, N, material.getRefractiveIndex()).normalize();
        Vec3f reflectOrig = reflectDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f)); // offset the original point to avoid occlusion by the object itself
        Vec3f refractOrig = refractDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f));
        Vec3f reflectColor = castRay(reflectOrig, reflectDir, shapes, lights, depth + 1);
        Vec3f refractColor = castRay(refractOrig, refractDir, shapes, lights, depth + 1);

        float diffuseLightIntensity = 0, specularLightIntensity = 0;
        for (Light light : lights) {
            Vec3f lightDir = (light.getPosition().sub(point)).normalize();
            float lightDistance = (light.getPosition().sub(point)).norm();

            Vec3f shadowOrig = lightDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f)); // checking if the point lies in the shadow of the lights[i]
            Optional<Intersection> shadowIntersection = sceneIntersect(shadowOrig, lightDir, shapes);
            if (shadowIntersection.isPresent()) {
                Vec3f shadowPt = shadowIntersection.get().getPoint();
                if (shadowPt.sub(shadowOrig).norm() < lightDistance) {
                    continue;
                }
            }

            diffuseLightIntensity += light.getIntensity() * Math.max(0.f, lightDir.dotProduct(N));
            specularLightIntensity += Math.pow(Math.max(0.f, reflect(lightDir.neg(), N).neg().dotProduct(dir)), material.getSpecularExponent()) * light.getIntensity();
        }

        return material.getDiffuseColor().mul(diffuseLightIntensity).mul(material.getAlbedo().values()[0])
                       .add(new Vec3f(1.f, 1.f, 1.f).mul(specularLightIntensity).mul(material.getAlbedo().values()[1]))
                       .add(reflectColor.mul(material.getAlbedo().values()[2]).add(refractColor.mul(material.getAlbedo().values()[3])));
    }

    Frame render(ArrayList<Shape> shapes, ArrayList<Light> lights) {
        return render(shapes, lights, false);
    }

    Frame render(ArrayList<Shape> shapes, ArrayList<Light> lights, boolean progress) {
        float fov = (float) (Math.PI / 3.);
        Vec3f[] framebuffer = new Vec3f[width * height];

        for (int j = 0; j < height; j++) { // actual rendering loop
            if (progress && (j % 100) == 0) {
                System.out.println("Rendering line " + j + "...");
            }
            for (int i = 0; i < width; i++) {
                float dirX = (i + 0.5f) - width / 2.f;
                float dirY = -(j + 0.5f) + height / 2.f;    // this flips the image at the same time
                float dirZ = -height / (2.f * (float) Math.tan(fov / 2.f));
                framebuffer[i + j * width] = castRay(new Vec3f(0, 0, 0), new Vec3f(dirX, dirY, dirZ).normalize(), shapes, lights);
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
