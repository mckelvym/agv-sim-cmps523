// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorReading.java
package agv.sim.cmps523;

public class SensorReading {
    public double actual_angle;
    public double actual_range;
    public double believed_angle;
    public double believed_range;
    public int signature;
    public double x_actual_hit;
    public double y_actual_hit;
    public double x_believed_hit;
    public double y_believed_hit;

    SensorReading() {
        actual_angle = 0;
        actual_range = -1;
        believed_angle = 0;
        believed_range = -1;
        signature = -1;
        x_actual_hit = 0;
        y_actual_hit = 0;
        x_believed_hit = 0;
        y_believed_hit = 0;
    }

    SensorReading(
            double actual_angle, double actual_range,
            double believed_angle, double believed_range,
            int signature,
            double actual_x, double actual_y,
            double believed_x, double believed_y) {
        this.actual_angle = actual_angle;
        this.actual_range = actual_range;
        this.believed_angle = believed_angle;
        this.believed_range = believed_range;
        this.signature = signature;
        this.x_actual_hit = actual_x;
        this.y_actual_hit = actual_y;
        this.x_believed_hit = believed_x;
        this.y_believed_hit = believed_y;
    }
}
