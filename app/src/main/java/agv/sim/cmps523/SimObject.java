// Mark McKelvy
// CMPS 523
// Final Project
// File: SimObject.java
package agv.sim.cmps523;

public class SimObject {
    public double x, y;
    public double size;

    public SimObject(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    double x() {
        return x;
    }

    double y() {
        return y;
    }
}
