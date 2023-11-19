// Mark McKelvy
// CMPS 523
// Final Project
// File: Sensor.java
package agv.sim.cmps523;

import static agv.sim.cmps523.math.MathUtil.clampAngle;
import static agv.sim.cmps523.math.MathUtil.square;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import Jama.Matrix;
import agv.sim.cmps523.data.SensorReading;
import agv.sim.cmps523.data.SimObject;
import agv.sim.cmps523.event.SensorNoiseProbabilityListener;
import agv.sim.cmps523.type.SensorNoiseProbabilityType;
import java.util.Vector;


public class Sensor implements SensorNoiseProbabilityListener {
    private final double rangeResolution;
    private final Vector<SensorReading> sensorHits;
    private final Vector<SensorReading> sensorReadings;
    private final Matrix pose;
    private final int fovDegrees;
    private final double minRange;
    private final Values values;
    private Matrix noise;
    private double maxRange;
    private double angularResolution;

    public Sensor(Values values) {
        this.values = requireNonNull(values);
        pose = new Matrix(3, 1);
        sensorHits = new Vector<>();
        sensorReadings = new Vector<>();
        fovDegrees = values.getSensorFovDegrees();
        minRange = values.getSensorMinRange();
        maxRange = values.getSensorMaxRange();
        rangeResolution = values.getSensorRangeResolution();
        angularResolution = values.getSensorAngularResolution();
        initReadings();
        values.addSensorNoiseProbabilityListener(this);
    }

    private void initReadings() {
        sensorReadings.clear();
        final int numReadings = (int) ((fovDegrees) / toDegrees(angularResolution) + 1);
        for (int i = 0; i < numReadings; i++) {
            sensorReadings.add(SensorReading.newSensorReading());
        }
        noise = Matrix.identity(1, 4);
        normalizeNoise();
    }

    public void normalizeNoise() {
        double sumsquares = 0.0;
        double length;
        for (int i = 0; i < 4; i++) {
            final SensorNoiseProbabilityType noiseProbabilityType = SensorNoiseProbabilityType.at(i);
            final double sensorNoiseProbability = values.getSensorNoiseProbability(noiseProbabilityType);
            noise.set(0, i, sensorNoiseProbability);
            sumsquares += square(noise.get(0, i));
        }
        if (sumsquares <= 0.0)
            return;
        length = Math.sqrt(sumsquares);
        for (int i = 0; i < 4; i++) {
            noise.set(0, i, noise.get(0, i) / length);
        }
    }

    public double getXPosition() {
        return pose.get(0, 0);
    }

    public double getYPosition() {
        return pose.get(1, 0);
    }

    private double getOrientation() {
        return pose.get(2, 0);
    }

    public void setOrientation(double orient) {
        pose.set(2, 0, clampAngle(orient));
        initReadings();
    }

    public void setPosition(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        initReadings();
    }

    public void setAngularResolution(double ang_res_degrees) {
        if (ang_res_degrees > 0 && ang_res_degrees <= fovDegrees) {
            angularResolution = toRadians(ang_res_degrees);
            sensorHits.clear();
            initReadings();
        }
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double max_range) {
        if (max_range > minRange) {
            this.maxRange = max_range;
        }
    }

    // Page 155
    private double pHit(double measured_range, double actual_range) {
        double p = 0.0;
        if (measured_range >= 0 &&
                measured_range <= maxRange && values.getSigmaHit() > 0.0) {
            // p = Utils.gaussian(actual_range, sigma_hit);
            p = 1 / (Math.sqrt(2 * Math.PI * square(values.getSigmaHit()))) *
                    Math.exp(-0.5 * square(measured_range - actual_range) / square(values.getSigmaHit()));
        }

        return p;
    }

    // Page 156
    private double pShort(double measured_range, double actual_range) {
        double p = 0.0;
        if (measured_range >= 0 &&
                measured_range <= actual_range && values.getLambdaShort() > 0.0) {
            double eta = 1.0 / (1 - Math.exp(-values.getLambdaShort() * actual_range));
            p = eta * values.getLambdaShort() * Math.exp(-values.getLambdaShort() * measured_range);
        }

        return p;
    }

    // Page 156
    private double pMax(double measured_range) {
        double p = 0.0;
        if (measured_range == maxRange)
            p = 1.0;

        return p;
    }

    // Page 157
    private double pRand(double measured_range) {
        double p = 0.0;
        if (measured_range <= maxRange)
            p = 1.0 / maxRange;

        return p;
    }

    public void sense(Testbed testbed) {
        double actualBeam;
        double believedBeam;
        actualBeam = getOrientation() - toRadians(fovDegrees / 2.0);
        believedBeam = Agent.getCurrent().getSensorOrientation() - toRadians(fovDegrees / 2.0);
        out.println("Sensor: bot orientation vs. sensor orientation: " + toDegrees(getOrientation()) + " " + toDegrees(Agent.getCurrent().getSensorOrientation()));

        sensorHits.clear();
        for (int beanum = 0; beanum < sensorReadings.size(); beanum++) {
            processBeam(testbed, actualBeam, believedBeam, beanum);
            actualBeam += angularResolution;
            believedBeam += angularResolution;
        }
    }

    public Vector<SensorReading> getHits() {
        return sensorHits;
    }

    public Vector<SensorReading> getReadings() {
        return sensorReadings;
    }

    @Override
    public void sensorNoiseProbabilityChanged() {
        normalizeNoise();
    }

    private void processBeam(Testbed testbed, double actualBeam, double believedBeam, int beaIndex) {
        double actualAngle, believedAngle, range, believedRange;
        boolean hit;
        Matrix noiseMatrix = Matrix.identity(4, 1);
        actualAngle = actualBeam;
        believedAngle = believedBeam;
        hit = false;
        for (range = minRange; range <= maxRange; range += rangeResolution) {
            if (hit) {
                break;
            }
            double actualBeamX = getXPosition() + range * Math.cos(actualAngle);
            double actualBeamY = getYPosition() + range * Math.sin(actualAngle);
            int objectIndex = -1;
            for (SimObject simObject : values.getSimObjects()) {
                objectIndex++;
                if (1.0 >= Math.sqrt(square(actualBeamX - simObject.x()) + square(actualBeamY - simObject.y()))) {
                    hit = true;

                    noiseMatrix.set(0, 0, pHit(range, range));
                    noiseMatrix.set(1, 0, pShort(range, range));
                    noiseMatrix.set(2, 0, pMax(range));
                    noiseMatrix.set(3, 0, pRand(range));
                    double p = (noise.times(noiseMatrix)).get(0, 0);
                    out.println("p=" + p);

                    believedRange = range * (1 - p);
                    double believedBeaX = (Agent.getCurrent().getSensorXPosition() + believedRange * Math.cos(believedAngle));
                    double believedBeaY = (Agent.getCurrent().getSensorYPosition() + believedRange * Math.sin(believedAngle));
                    out.println("Sensor: actual angle vs believed angle: " + toDegrees(actualAngle) + " " + toDegrees(believedAngle));
                    final SensorReading sensorReading = new SensorReading(actualAngle - getOrientation(), range, believedAngle - Agent.getCurrent().getSensorOrientation(), believedRange, objectIndex,
                            simObject.x(), simObject.y(), believedBeaX, believedBeaY);
                    sensorHits.add(sensorReading);
                    sensorReadings.set(beaIndex, sensorReading);
                    break;
                }
            }
        }
        if (!hit) {
            double actualBeaX = getXPosition() + range * Math.cos(actualAngle);
            double actualBeaY = getYPosition() + range * Math.sin(actualAngle);
            double believedBeaX = Agent.getCurrent().getSensorXPosition() + range * Math.cos(believedAngle);
            double believedBeaY = Agent.getCurrent().getSensorYPosition() + range * Math.sin(believedAngle);
            sensorReadings.set(beaIndex, new SensorReading(actualAngle - getOrientation(), range, believedAngle - Agent.getCurrent().getSensorOrientation(), range, -1,
                    actualBeaX, actualBeaY, believedBeaX, believedBeaY));
        }
    }
}
