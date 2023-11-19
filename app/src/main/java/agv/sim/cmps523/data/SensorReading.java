// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorReading.java
package agv.sim.cmps523.data;

public record SensorReading(double actualAngle, double actualRange, double believedAngle, double believedRange,
                            int signature, double xActualHit, double yActualHit, double xBelievedHit,
                            double yBelievedHit) {
    public static SensorReading newSensorReading() {
        return new SensorReading(0, -1, 0, 0, -1, 0, 0, 0, 0);
    }
}
