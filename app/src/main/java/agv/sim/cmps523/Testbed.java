// Mark McKelvy
// CMPS 523
// Final Project
// File: Testbed.java
package agv.sim.cmps523;

import static java.lang.System.out;

import Jama.Matrix;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;

public class Testbed extends Observable {
    // testbed noise
    static long randSeed = 314159265;
    static Random randGen = new Random(randSeed);
    static double muNoise = 0.0; // default value
    static double sigmaVNoise = 2.0; // default value
    static double sigmaWNoise = 2.0; // default value
    static boolean configOrientation = false;
    Matrix pose;
    private double botLocationX0 = 180;
    private double botLocationY0 = 200;
    private double botOrientation0 = Math.PI / 2;
    private Vector<SimObject> objects;

    public Testbed() {
        pose = new Matrix(3, 1);
        objects = new Vector<>();
        initializeBotPose();
        initializeTestbedNoise();
    }

    double getXPosition() {
        return pose.get(0, 0);
    }

    double getYPosition() {
        return pose.get(1, 0);
    }

    double getInitialXPosition() {
        return botLocationX0;
    }

    double getInitialYPosition() {
        return botLocationY0;
    }

    double getOrientation() {
        return pose.get(2, 0);
    }

    void setOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        pose.set(2, 0, orient);
        AGVsim.sensor.set_orientation(orient);
    }

    void setInitialOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        botOrientation0 = orient;
        AGVsim.sensor.set_orientation(orient);
    }

    void setInitialPosition(double x, double y) {
        botLocationX0 = x;
        botLocationY0 = y;
        AGVsim.sensor.set_position(x, y);
    }

    void setPosition(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        AGVsim.sensor.set_position(x, y);
    }

    void initializeTestbedNoise() {
        sigmaVNoise = NoiseControlPanel.getTestbedVNoise();
        sigmaWNoise = NoiseControlPanel.getTestbedWNoise();
        out.println("Testbed: sigma_v_noise = " + sigmaVNoise + " sigma_w_noise = " + sigmaWNoise);
    }

    // used in Engine.resetSystem()
    void initializeBotPose() {
        setPosition(botLocationX0, botLocationY0);
        setOrientation(botOrientation0);

        AGVsim.logger.saveTestbedPose(pose);
    }

    // The agent invokes this method to move within the testbed.
    // Control noise is added to the command.
    public void move(Matrix velocity_control_commands) {
        double v_noise = sigmaVNoise * randGen.nextGaussian() + muNoise;
        double w_noise = sigmaWNoise * randGen.nextGaussian() + muNoise;
        double dt = Engine.deltaT;            // delta time
        double v = (velocity_control_commands.get(0, 0) + v_noise); // v from robot motion - translational velocity
        double w = (velocity_control_commands.get(1, 0) + w_noise); // omega from robot motion - rotataional velocity
        double x = getXPosition();
        double y = getYPosition();
        double theta = getOrientation();
        double dtheta = w * dt;

        setOrientation(theta + dtheta);
        if (w != 0.0)
            setPosition(
                    x - ((v / w) * Math.sin(theta)) + ((v / w) * Math.sin((theta + dtheta))),
                    y + ((v / w) * Math.cos(theta)) - ((v / w) * Math.cos((theta + dtheta)))
            );
        else
            setPosition(
                    x + (v * dt) * Math.cos(theta),
                    y + (v * dt) * Math.sin(theta)
            );
        out.println("Pose: " + getXPosition() + " " + getYPosition() + " " + Math.toDegrees(getOrientation()));

        AGVsim.logger.saveTestbedPose(pose);
    }

    public void assertModelHasChanged() {
        setChanged();
        notifyObservers();
    }

    public void addObjectWithoutRepaint(int x, int y, double size) {
        objects.add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.objectCombo.addItem(label);
    }

    public void addObject(int x, int y, double size) {
        objects.add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.objectCombo.addItem(label);
        AGVsim.testbedview.repaint();
    }

    public int numObjects() {
        return objects.size();
    }

    public SimObject objectAt(int index) {
        return objects.elementAt(index);
    }

    public void removeObject(int objectId) {
        if (objectId > numObjects() || objectId < 0)
            return;
        objects.removeElementAt(objectId - 1);
        AGVsim.testbedview.repaint();
    }

    public Vector<SimObject> getObjects() {
        return objects;
    }

    public void set_objects(Vector<SimObject> objs) {
        objects = objs;
    }
}
