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
    private static long randSeed = 314159265;
    private static Random randGen = new Random(getRandSeed());
    private static double muNoise = 0.0; // default value
    private static double sigmaVNoise = 2.0; // default value
    private static double sigmaWNoise = 2.0; // default value
    private static boolean configOrientation = false;
    private Matrix pose;
    private double botLocationX0 = 180;
    private double botLocationY0 = 200;
    private double botOrientation0 = Math.PI / 2;
    private Vector<SimObject> objects;

    public Testbed() {
        setPose(new Matrix(3, 1));
        setObjects(new Vector<>());
        initializeBotPose();
        initializeTestbedNoise();
    }

    public static long getRandSeed() {
        return randSeed;
    }

    public static void setRandSeed(long randSeed) {
        Testbed.randSeed = randSeed;
    }

    public static Random getRandGen() {
        return randGen;
    }

    public static void setRandGen(Random randGen) {
        Testbed.randGen = randGen;
    }

    public static double getMuNoise() {
        return muNoise;
    }

    public static void setMuNoise(double muNoise) {
        Testbed.muNoise = muNoise;
    }

    public static double getSigmaVNoise() {
        return sigmaVNoise;
    }

    public static void setSigmaVNoise(double sigmaVNoise) {
        Testbed.sigmaVNoise = sigmaVNoise;
    }

    public static double getSigmaWNoise() {
        return sigmaWNoise;
    }

    public static void setSigmaWNoise(double sigmaWNoise) {
        Testbed.sigmaWNoise = sigmaWNoise;
    }

    public static boolean isConfigOrientation() {
        return configOrientation;
    }

    public static void setConfigOrientation(boolean configOrientation) {
        Testbed.configOrientation = configOrientation;
    }

    double getXPosition() {
        return getPose().get(0, 0);
    }

    double getYPosition() {
        return getPose().get(1, 0);
    }

    double getInitialXPosition() {
        return getBotLocationX0();
    }

    double getInitialYPosition() {
        return getBotLocationY0();
    }

    double getOrientation() {
        return getPose().get(2, 0);
    }

    void setOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        getPose().set(2, 0, orient);
        AGVsim.getSensor().set_orientation(orient);
    }

    void setInitialOrientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        setBotOrientation0(orient);
        AGVsim.getSensor().set_orientation(orient);
    }

    void setInitialPosition(double x, double y) {
        setBotLocationX0(x);
        setBotLocationY0(y);
        AGVsim.getSensor().set_position(x, y);
    }

    void setPosition(double x, double y) {
        getPose().set(0, 0, x);
        getPose().set(1, 0, y);
        AGVsim.getSensor().set_position(x, y);
    }

    void initializeTestbedNoise() {
        setSigmaVNoise(NoiseControlPanel.getTestbedVNoise());
        setSigmaWNoise(NoiseControlPanel.getTestbedWNoise());
        out.println("Testbed: sigma_v_noise = " + getSigmaVNoise() + " sigma_w_noise = " + getSigmaWNoise());
    }

    // used in Engine.resetSystem()
    void initializeBotPose() {
        setPosition(getBotLocationX0(), getBotLocationY0());
        setOrientation(getBotOrientation0());

        AGVsim.getLogger().saveTestbedPose(getPose());
    }

    // The agent invokes this method to move within the testbed.
    // Control noise is added to the command.
    public void move(Matrix velocity_control_commands) {
        double v_noise = getSigmaVNoise() * getRandGen().nextGaussian() + getMuNoise();
        double w_noise = getSigmaWNoise() * getRandGen().nextGaussian() + getMuNoise();
        double dt = Engine.getDeltaT();            // delta time
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

        AGVsim.getLogger().saveTestbedPose(getPose());
    }

    public void assertModelHasChanged() {
        setChanged();
        notifyObservers();
    }

    public void addObjectWithoutRepaint(int x, int y, double size) {
        getObjects().add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.getObjectCombo().addItem(label);
    }

    public void addObject(int x, int y, double size) {
        getObjects().add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.getObjectCombo().addItem(label);
        AGVsim.getTestbedview().repaint();
    }

    public int numObjects() {
        return getObjects().size();
    }

    public SimObject objectAt(int index) {
        return getObjects().elementAt(index);
    }

    public void removeObject(int objectId) {
        if (objectId > numObjects() || objectId < 0)
            return;
        getObjects().removeElementAt(objectId - 1);
        AGVsim.getTestbedview().repaint();
    }

    public Vector<SimObject> getObjects() {
        return objects;
    }

    public void setObjects(Vector<SimObject> objects) {
        this.objects = objects;
    }

    public void set_objects(Vector<SimObject> objs) {
        setObjects(objs);
    }

    public Matrix getPose() {
        return pose;
    }

    public void setPose(Matrix pose) {
        this.pose = pose;
    }

    public double getBotLocationX0() {
        return botLocationX0;
    }

    public void setBotLocationX0(double botLocationX0) {
        this.botLocationX0 = botLocationX0;
    }

    public double getBotLocationY0() {
        return botLocationY0;
    }

    public void setBotLocationY0(double botLocationY0) {
        this.botLocationY0 = botLocationY0;
    }

    public double getBotOrientation0() {
        return botOrientation0;
    }

    public void setBotOrientation0(double botOrientation0) {
        this.botOrientation0 = botOrientation0;
    }
}
