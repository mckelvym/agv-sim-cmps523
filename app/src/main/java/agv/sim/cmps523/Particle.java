// Mark McKelvy
// CMPS 523
// Final Project
// File: Particle.java
package agv.sim.cmps523;

public class Particle {
    private double x;
    private double y;
    private double angle;

    Particle(double x, double y, double angle) {
        this.setX(x);
        this.setY(y);
        this.setAngle(angle);
    }

    double x() {
        return getX();
    }

    double y() {
        return getY();
    }

    double theta() {
        return getAngle();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
