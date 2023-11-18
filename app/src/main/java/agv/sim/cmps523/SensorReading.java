// Mark McKelvy
// CMPS 523
// Final Project
// File: SensorReading.java
package agv.sim.cmps523;

public class SensorReading {
    private double actualAngle;
    private double actualRange;
    private double believedAngle;
    private double believedRange;
    private int signature;
    private double xActualHit;
    private double yActualHit;
    private double xBelievedHit;
    private double yBelievedHit;

    SensorReading() {
        setActualAngle(0);
        setActualRange(-1);
        setBelievedAngle(0);
        setBelievedRange(-1);
        setSignature(-1);
        setxActualHit(0);
        setyActualHit(0);
        setxBelievedHit(0);
        setyBelievedHit(0);
    }

    SensorReading(
            double actual_angle, double actual_range,
            double believedAngle, double believedRange,
            int signature,
            double actual_x, double actual_y,
            double believed_x, double believed_y) {
        this.setActualAngle(actual_angle);
        this.setActualRange(actual_range);
        this.setBelievedAngle(believedAngle);
        this.setBelievedRange(believedRange);
        this.setSignature(signature);
        this.setxActualHit(actual_x);
        this.setyActualHit(actual_y);
        this.setxBelievedHit(believed_x);
        this.setyBelievedHit(believed_y);
    }

    public double getActualAngle() {
        return actualAngle;
    }

    public void setActualAngle(double actualAngle) {
        this.actualAngle = actualAngle;
    }

    public double getActualRange() {
        return actualRange;
    }

    public void setActualRange(double actualRange) {
        this.actualRange = actualRange;
    }

    public double getBelievedAngle() {
        return believedAngle;
    }

    public void setBelievedAngle(double believedAngle) {
        this.believedAngle = believedAngle;
    }

    public double getBelievedRange() {
        return believedRange;
    }

    public void setBelievedRange(double believedRange) {
        this.believedRange = believedRange;
    }

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    public double getxActualHit() {
        return xActualHit;
    }

    public void setxActualHit(double xActualHit) {
        this.xActualHit = xActualHit;
    }

    public double getyActualHit() {
        return yActualHit;
    }

    public void setyActualHit(double yActualHit) {
        this.yActualHit = yActualHit;
    }

    public double getxBelievedHit() {
        return xBelievedHit;
    }

    public void setxBelievedHit(double xBelievedHit) {
        this.xBelievedHit = xBelievedHit;
    }

    public double getyBelievedHit() {
        return yBelievedHit;
    }

    public void setyBelievedHit(double yBelievedHit) {
        this.yBelievedHit = yBelievedHit;
    }
}
