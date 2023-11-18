// Mark McKelvy
// CMPS 523
// Final Project
// File: Sensor.java
package agv.sim.cmps523;

import static java.lang.System.out;

import Jama.Matrix;
import java.util.Vector;


public class Sensor {

    private final double rangeResolution;
    private final Vector<SensorReading> sensorHits;
    private final Matrix pose;
    private final int fovDegrees;
    private final double minRange;
    Matrix noise;
    double sigmaHit;
    double lambdaShort;
    private double maxRange;
    private double angularResolution;
    private SensorReading[] sensorReadings;

    Sensor() {
        pose = new Matrix(3, 1);
        fovDegrees = 180;
        minRange = 0;
        maxRange = 500;
        sensorHits = new Vector<>();
        rangeResolution = 1.0;
        angularResolution = Math.toRadians(5.0);
        init_readings();
    }

    void normalize_noise() {
        double susquares = 0.0;
        double length;
        for (int i = 0; i < 4; i++) {
            noise.set(0, i, SensorControlPanel.getSensorNoiseProbability(i));
            susquares += Utils.square(noise.get(0, i));
        }
        if (susquares <= 0.0)
            return;
        length = Math.sqrt(susquares);
        for (int i = 0; i < 4; i++) {
            noise.set(0, i, noise.get(0, i) / length);
        }
        //noise.print(20,4);
    }

    void init_readings() {
        sensorReadings = new SensorReading[(int) ((fovDegrees) / Math.toDegrees(angularResolution) + 1)];
        for (int i = 0; i < sensorReadings.length; i++)
            sensorReadings[i] = new SensorReading();
        noise = Matrix.identity(1, 4);
        for (int i = 0; i < 4; i++) {
            noise.set(0, i, SensorControlPanel.getSensorNoiseProbability(i));
        }
        sigmaHit = SensorControlPanel.getSigmaHit();
        lambdaShort = SensorControlPanel.getLambdaShort();
        normalize_noise();
    }

    double get_x_position() {
        return pose.get(0, 0);
    }

    double get_y_position() {
        return pose.get(1, 0);
    }

    double get_orientation() {
        return pose.get(2, 0);
    }

    void set_orientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        pose.set(2, 0, orient);
        init_readings();
    }

    void set_position(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        init_readings();
    }

    void set_angular_resolution(double ang_res_degrees) {
        if (ang_res_degrees > 0 && ang_res_degrees <= fovDegrees) {
            angularResolution = Math.toRadians(ang_res_degrees);
            sensorHits.clear();
            init_readings();
        }
    }

    double get_max_range() {
        return maxRange;
    }

    void set_max_range(double max_range) {
        if (max_range > minRange) {
            this.maxRange = max_range;
        }
    }

    // Page 155
    double p_hit(double measured_range, double actual_range) {
        double p = 0.0;
        if (measured_range >= 0 &&
                measured_range <= maxRange &&
                sigmaHit > 0.0) {
            // p = Utils.gaussian(actual_range, sigma_hit);
            p = 1 / (Math.sqrt(2 * Math.PI * Utils.square(sigmaHit))) *
                    Math.exp(-0.5 * Utils.square(measured_range - actual_range) / Utils.square(sigmaHit));
        }

        return p;
    }

    // Page 156
    double p_short(double measured_range, double actual_range) {
        double p = 0.0;
        if (measured_range >= 0 &&
                measured_range <= actual_range &&
                lambdaShort > 0.0) {
            double eta = 1.0 / (1 - Math.exp(-lambdaShort * actual_range));
            p = eta * lambdaShort * Math.exp(-lambdaShort * measured_range);
        }

        return p;
    }

    // Page 156
    double p_max(double measured_range) {
        double p = 0.0;
        if (measured_range == maxRange)
            p = 1.0;

        return p;
    }

    // Page 157
    double p_rand(double measured_range) {
        double p = 0.0;
        if (measured_range <= maxRange)
            p = 1.0 / maxRange;

        return p;
    }

    void sense(Testbed testbed) {
        double actual_beam;
        double believed_beam;
        actual_beam = get_orientation() - Math.toRadians(fovDegrees / 2.0);
        believed_beam = AGVsim.getAgent().getSensorOrientation() - Math.toRadians(fovDegrees / 2.0);
        out.println("Sensor: bot orientation vs. sensor orientation: " + Math.toDegrees(get_orientation()) + " " + Math.toDegrees(AGVsim.getAgent().getSensorOrientation()));

        sensorHits.clear();
        for (int beanum = 0; beanum < sensorReadings.length; beanum++) {
            new ProcessBeam(testbed, actual_beam, believed_beam, beanum);
            actual_beam += angularResolution;
            believed_beam += angularResolution;
        }
    }

    Vector<SensorReading> get_hits() {
        return sensorHits;
    }

    SensorReading[] get_readings() {
        return sensorReadings;
    }

    private class ProcessBeam {
        double actual_angle, believed_angle, range, believed_range;
        double actual_beax = 0.0, actual_beay = 0.0;
        double believed_beax = 0.0, believed_beay = 0.0;
        double obj_x, obj_y;
        boolean hit;
        Matrix noise_matrix;

        ProcessBeam(Testbed testbed, double actual_beam, double believed_beam, int beaindex) {
            noise_matrix = Matrix.identity(4, 1);
            actual_angle = actual_beam;
            believed_angle = believed_beam;
            hit = false;
            for (range = minRange; range <= maxRange; range += rangeResolution) {
                if (hit)
                    break;
                actual_beax = get_x_position() + range * Math.cos(actual_angle);
                actual_beay = get_y_position() + range * Math.sin(actual_angle);
                for (int o = 0; o < testbed.numObjects(); o++) {
                    obj_x = testbed.objectAt(o).getX();
                    obj_y = testbed.objectAt(o).getY();
                    if (1.0 >= Math.sqrt(Utils.square(actual_beax - obj_x) + Utils.square(actual_beay - obj_y))) {
                        hit = true;

                        noise_matrix.set(0, 0, p_hit(range, range));
                        noise_matrix.set(1, 0, p_short(range, range));
                        noise_matrix.set(2, 0, p_max(range));
                        noise_matrix.set(3, 0, p_rand(range));
                        double p = (noise.times(noise_matrix)).get(0, 0);
                        out.println("p=" + p);

                        believed_range = range * (1 - p);
                        believed_beax = (AGVsim.getAgent().getSensorXPosition() + believed_range * Math.cos(believed_angle));
                        believed_beay = (AGVsim.getAgent().getSensorYPosition() + believed_range * Math.sin(believed_angle));
                        out.println("Sensor: actual angle vs believed angle: " + Math.toDegrees(actual_angle) + " " + Math.toDegrees(believed_angle));
                        sensorHits.add(new SensorReading(actual_angle - get_orientation(), range, believed_angle - AGVsim.getAgent().getSensorOrientation(), believed_range, o,
                                testbed.objectAt(o).getX(), testbed.objectAt(o).getY(), believed_beax, believed_beay));
                        sensorReadings[beaindex] = new SensorReading(actual_angle - get_orientation(), range, believed_angle - AGVsim.getAgent().getSensorOrientation(), believed_range, o,
                                testbed.objectAt(o).getX(), testbed.objectAt(o).getY(), believed_beax, believed_beay);
                        break;
                    }
                }
            }
            if (!hit) {
                actual_beax = get_x_position() + range * Math.cos(actual_angle);
                actual_beay = get_y_position() + range * Math.sin(actual_angle);
                believed_beax = AGVsim.getAgent().getSensorXPosition() + range * Math.cos(believed_angle);
                believed_beay = AGVsim.getAgent().getSensorYPosition() + range * Math.sin(believed_angle);
                sensorReadings[beaindex] = new SensorReading(actual_angle - get_orientation(), range, believed_angle - AGVsim.getAgent().getSensorOrientation(), range, -1,
                        actual_beax, actual_beay, believed_beax, believed_beay);
            }
        }
    }
}
