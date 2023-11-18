// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorReading.java
package agv.sim.cmps523;

public class SensorReading {
    public double actualAngle;
    public double actualRange;
    public double believedAngle;
    public double believedRange;
    public int signature;
    public double xActualHit;
    public double yActualHit;
    public double xBelievedHit;
    public double yBelievedHit;

    SensorReading() {
        actualAngle = 0;
        actualRange = -1;
        believedAngle = 0;
        believedRange = -1;
        signature = -1;
        xActualHit = 0;
        yActualHit = 0;
        xBelievedHit = 0;
        yBelievedHit = 0;
    }

    SensorReading(
            double actual_angle, double actual_range,
            double believedAngle, double believedRange,
            int signature,
            double actual_x, double actual_y,
            double believed_x, double believed_y) {
        this.actualAngle = actual_angle;
        this.actualRange = actual_range;
        this.believedAngle = believedAngle;
        this.believedRange = believedRange;
        this.signature = signature;
        this.xActualHit = actual_x;
        this.yActualHit = actual_y;
        this.xBelievedHit = believed_x;
        this.yBelievedHit = believed_y;
    }
}
