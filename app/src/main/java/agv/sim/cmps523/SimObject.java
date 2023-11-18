// Mark McKelvy
// CMPS 523
// Final Project
// File: SimObject.java
package agv.sim.cmps523;

public class SimObject {
    private double x;
    private double y;
    private double size;

    public SimObject(double x, double y, double size) {
        this.setX(x);
        this.setY(y);
        this.setSize(size);
    }

    double x() {
        return getX();
    }

    double y() {
        return getY();
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

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
