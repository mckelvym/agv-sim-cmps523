// Mark McKelvy
// CMPS 523
// Final Project
// File: Particle.java
package agv.sim.cmps523;

public class Particle {
    public double x;
    public double y;
    public double angle;

    Particle(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    double x() {
        return x;
    }

    double y() {
        return y;
    }

    double theta() {
        return angle;
    }
}
