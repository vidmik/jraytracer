package se.vidstedt.raytrace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

class RayTracer {
    private static final boolean CHECKERBOARD = true;
    private static final boolean REFLECT = true;
    private static final boolean REFRACT = true;

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

    private static Vec3f refract(Vec3f I, Vec3f N, float etaT) {
        // Snell's law
        return refract(I, N, etaT, 1.f);
    }

    private static Vec3f refract(Vec3f I, Vec3f N, float etaT, float etaI) {
        // Snell's law
        float cosI = -Math.max(-1.f, Math.min(1.f, I.dotProduct(N)));
        if (cosI < 0) {
            // if the ray comes from the inside the object, swap the air and the media
            return refract(I, N.mul(-1), etaI, etaT);
        }
        float eta = etaI / etaT;
        float k = 1 - eta * eta * (1 - cosI * cosI);
        if (k < 0) {
            return new Vec3f(1, 0, 0);
        } else {
            // k<0 = total reflection, no ray to refract. I refract it anyways, this has no physical meaning
            return I.mul(eta).add(N.mul((float) (eta * cosI - Math.sqrt(k))));
        }
    }

    private Optional<Intersection> sceneIntersect(Vec3f orig, Vec3f dir, Collection<Shape> shapes) {
        Optional<Intersection> intersection = Optional.empty();

        for (Shape shape : shapes) {
            Optional<Intersection> candidateIntersection = shape.rayIntersect(orig, dir);
            if (candidateIntersection.isPresent()) {
                if (intersection.isEmpty() || candidateIntersection.get().getDistance() < intersection.get().getDistance()) {
                    intersection = candidateIntersection;
                }
            }
        }

        if (CHECKERBOARD) {
            if (Math.abs(dir.y()) > 1e-3) {
                // the checkerboard plane has equation y = -4
                float checkerboardDist = -(orig.y() + 4) / dir.y();
                Vec3f pt = orig.add(dir.mul(checkerboardDist));
                if (checkerboardDist > 0 && Math.abs(pt.x()) < 10 && pt.z() < -10 && pt.z() > -30) {
                    if (intersection.isEmpty() || checkerboardDist < intersection.get().getDistance()) {
                        Vec3f hit = pt;
                        Vec3f N = new Vec3f(0, 1, 0);

                        int s = (int) (.5f * hit.x() + 1000) + (int) (.5 * hit.z());
                        Vec3f color;
                        if ((s & 1) != 0) {
                            color = new Vec3f(.3f, .3f, .3f);
                        } else {
                            color = new Vec3f(.1f, .2f, .3f);
                        }
                        Material material = intersection.isPresent() ? intersection.get().getMaterial() : new Material();
                        material = material.withDiffuseColor(color);
                        intersection = Optional.of(new Intersection(checkerboardDist, hit, N, material));
                    }
                }
            }
        }

        if (intersection.isPresent() && intersection.get().getDistance() < 1000) {
            return intersection;
        } else {
            return Optional.empty();
        }
    }

    private Vec3f castRay(Vec3f orig, Vec3f dir, Scene scene) {
        return castRay(orig, dir, scene, 0);
    }

    private Vec3f getEnvironment(Vec3f dir) {
        int x, y;
        switch (background.getType()) {
            case RECTANGULAR:
                x = (int) ((dir.x() / 2 + 0.5) * background.getWidth());
                y = (int) ((-dir.y() / 2 + 0.5) * background.getHeight());
                break;
            case SPHERICAL:
                x = (int) ((Math.atan2(dir.z(), dir.x()) / (2 * Math.PI) + 0.5) * background.getWidth());
                y = (int) (Math.acos(dir.y()) / Math.PI * background.getHeight());
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return background.getPixel(x, y);

    }

    private Vec3f castRay(Vec3f orig, Vec3f dir, Scene scene, int depth) {
        if (depth > 4) {
            // any color
            return new Vec3f(0.2f, 0.7f, 0.8f);
        }
        Optional<Intersection> intersection = sceneIntersect(orig, dir, scene.getShapes());
        if (intersection.isEmpty()) {
            return getEnvironment(dir);
        }

        Vec3f point = intersection.get().getPoint();
        Vec3f N = intersection.get().getN();
        Material material = intersection.get().getMaterial();

        Vec3f reflectColor = new Vec3f(0, 0, 0);
        if (REFLECT) {
            Vec3f reflectDir = reflect(dir, N).normalize();
            // offset the original point to avoid occlusion by the object itself
            Vec3f reflectOrig = reflectDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f));
            reflectColor = castRay(reflectOrig, reflectDir, scene, depth + 1);
        }

        Vec3f refractColor = new Vec3f(0, 0, 0);
        if (REFRACT) {
            Vec3f refractDir = refract(dir, N, material.getRefractiveIndex()).normalize();
            // offset the original point to avoid occlusion by the object itself
            Vec3f refractOrig = refractDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f));
            refractColor = castRay(refractOrig, refractDir, scene, depth + 1);
        }

        float diffuseLightIntensity = 0, specularLightIntensity = 0;
        for (Light light : scene.getLights()) {
            Vec3f lightDir = (light.getPosition().sub(point)).normalize();
            float lightDistance = (light.getPosition().sub(point)).norm();

            // checking if the point lies in the shadow of the light
            Vec3f shadowOrig = lightDir.dotProduct(N) < 0 ? point.sub(N.mul(1e-3f)) : point.add(N.mul(1e-3f));
            Optional<Intersection> shadowIntersection = sceneIntersect(shadowOrig, lightDir, scene.getShapes());
            if (shadowIntersection.isPresent()) {
                Vec3f shadowPt = shadowIntersection.get().getPoint();
                float shadowDist = shadowPt.sub(shadowOrig).norm();
                if (shadowDist < lightDistance) {
                    continue;
                }
            }

            diffuseLightIntensity += light.getIntensity() * Math.max(0.f, lightDir.dotProduct(N));
            specularLightIntensity += Math.pow(Math.max(0.f, reflect(lightDir.neg(), N).neg().dotProduct(dir)), material.getSpecularExponent()) * light.getIntensity();
        }

        return material.getDiffuseColor().mul(diffuseLightIntensity).mul(material.getAlbedo().getValue(0))
                       .add(new Vec3f(1.f, 1.f, 1.f).mul(specularLightIntensity).mul(material.getAlbedo().getValue(1)))
                       .add(reflectColor.mul(material.getAlbedo().getValue(2)).add(refractColor.mul(material.getAlbedo().getValue(3))));
    }

    private Stream<Runnable> rays(Vec3f[] frameBuffer, Scene scene) {
        ArrayList<Runnable> rays = new ArrayList<>();

        float fov = (float) (Math.PI / 3.);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                float dirX = (i + 0.5f) - width / 2.f;
                float dirY = -(j + 0.5f) + height / 2.f; // this flips the image at the same time
                float dirZ = -height / (2.f * (float) Math.tan(fov / 2.f));

                final int frameBufferIndex = i + j * width;
                rays.add(() -> frameBuffer[frameBufferIndex] = castRay(new Vec3f(0, 0, 0), new Vec3f(dirX, dirY, dirZ).normalize(), scene));
            }
        }

        return rays.stream();
    }

    Frame render(Scene scene) {
        Vec3f[] frameBuffer = new Vec3f[width * height];

        Stream<Runnable> rays = rays(frameBuffer, scene);
        rays.parallel().forEach(Runnable::run);

        byte[] data = new byte[width * height * 3];
        for (int i = 0; i < height * width; i++) {
            Vec3f c = frameBuffer[i];
            float max = Math.max(c.x(), Math.max(c.y(), c.z()));
            if (max > 1) {
                c = c.mul(1.f / max);
            }
            for (int j = 0; j < 3; j++) {
                byte b = (byte) (255 * Math.max(0.f, Math.min(1.f, c.getValue(j))));
                data[i * 3 + j] = b;
            }
        }

        return new Frame(width, height, data);
    }
}
