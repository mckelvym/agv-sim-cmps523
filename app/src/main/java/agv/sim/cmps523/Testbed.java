// Mark McKelvy
// CMPS 523
// Final Project
// File: Testbed.java
package agv.sim.cmps523;

import static java.lang.Math.*;
import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import Jama.Matrix;
import agv.sim.cmps523.data.Logger;
import java.util.Random;

// actual world
public class Testbed {

    // testbed noise
    private static final long randSeed = 314159265;
    private static final Random randGen = new Random(randSeed);
    private static Testbed INSTANCE;
    private final double muNoise; // default value
    private final Values values;
    private final Matrix pose;
    private final Logger logger;
    private boolean configOrientation;
    private double botLocationX0;
    private double botLocationY0;
    private double botOrientation0;

    private Testbed(Values values, Logger logger) {
        this.values = requireNonNull(values);
        this.logger = requireNonNull(logger);
        configOrientation = false;
        this.values.setTranslationalNoise(2d);
        this.values.setRotationalNoise(toRadians(2d));
        pose = new Matrix(3, 1);
        botLocationX0 = 180;
        botLocationY0 = 200;
        botOrientation0 = Math.PI / 2;
        muNoise = 0.0;
        initializeBotPose();
        out.println("Testbed: sigma_v_noise = " + this.values.getTranslationalNoise() + " sigma_w_noise = " + this.values.getRotationalNoise());
    }

    public static Testbed newTestbed(Values values, Logger logger) {
        INSTANCE = new Testbed(values, logger);
        return getCurrent();
    }

    public static Testbed getCurrent() {
        return requireNonNull(INSTANCE);
    }

    public boolean isConfigOrientation() {
        return configOrientation;
    }

    public void setConfigOrientation(boolean configOrientation) {
        this.configOrientation = configOrientation;
    }

    public double getXPosition() {
        return pose.get(0, 0);
    }

    public double getYPosition() {
        return pose.get(1, 0);
    }

    public double getInitialXPosition() {
        return botLocationX0;
    }

    public double getInitialYPosition() {
        return botLocationY0;
    }

    public double getOrientation() {
        return pose.get(2, 0);
    }

    private void setOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        pose.set(2, 0, orient);
        Agent.getCurrent().getSensor().setOrientation(orient);
    }

    public void setInitialOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        this.botOrientation0 = orient;
        Agent.getCurrent().getSensor().setOrientation(orient);
    }

    public void setInitialPosition(double x, double y) {
        this.botLocationX0 = x;
        this.botLocationY0 = y;
        Agent.getCurrent().getSensor().setPosition(x, y);
    }

    private void setPosition(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        Agent.getCurrent().getSensor().setPosition(x, y);
    }

    // used in Engine.resetSystem()
    public void initializeBotPose() {
        setPosition(botLocationX0, botLocationY0);
        setOrientation(botOrientation0);

        logger.addTestbedPose(pose);
    }

    // The agent invokes this method to move within the testbed.
    // Control noise is added to the command.
    public void move(Matrix velocityControlCommands) {
        double v_noise = values.getTranslationalNoise() * randGen.nextGaussian() + muNoise;
        double w_noise = values.getRotationalNoise() * randGen.nextGaussian() + muNoise;
        double dt = values.getTimestampDelta();            // delta time
        double v = (velocityControlCommands.get(0, 0) + v_noise); // v from robot motion - translational velocity
        double w = (velocityControlCommands.get(1, 0) + w_noise); // omega from robot motion - rotataional velocity
        double x = getXPosition();
        double y = getYPosition();
        double theta = getOrientation();
        double dtheta = w * dt;

        setOrientation(theta + dtheta);
        if (w != 0.0)
            setPosition(
                    x - ((v / w) * sin(theta)) + ((v / w) * sin((theta + dtheta))),
                    y + ((v / w) * cos(theta)) - ((v / w) * cos((theta + dtheta)))
            );
        else
            setPosition(
                    x + (v * dt) * cos(theta),
                    y + (v * dt) * sin(theta)
            );
        out.println("Pose: " + getXPosition() + " " + getYPosition() + " " + toDegrees(getOrientation()));

        logger.addTestbedPose(pose);
    }
}
