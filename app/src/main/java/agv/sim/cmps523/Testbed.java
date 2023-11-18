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
        initialize_bot_pose();
        initialize_testbed_noise();
    }

    double get_x_position() {
        return pose.get(0, 0);
    }

    double get_y_position() {
        return pose.get(1, 0);
    }

    double get_initial_x_position() {
        return botLocationX0;
    }

    double get_initial_y_position() {
        return botLocationY0;
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
        AGVsim.sensor.set_orientation(orient);
    }

    void set_initial_orientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        botOrientation0 = orient;
        AGVsim.sensor.set_orientation(orient);
    }

    void set_initial_position(double x, double y) {
        botLocationX0 = x;
        botLocationY0 = y;
        AGVsim.sensor.set_position(x, y);
    }

    void set_position(double x, double y) {
        pose.set(0, 0, x);
        pose.set(1, 0, y);
        AGVsim.sensor.set_position(x, y);
    }

    void initialize_testbed_noise() {
        sigmaVNoise = NoiseControlPanel.get_testbed_v_noise();
        sigmaWNoise = NoiseControlPanel.get_testbed_w_noise();
        out.println("Testbed: sigma_v_noise = " + sigmaVNoise + " sigma_w_noise = " + sigmaWNoise);
    }

    // used in Engine.resetSystem()
    void initialize_bot_pose() {
        set_position(botLocationX0, botLocationY0);
        set_orientation(botOrientation0);

        AGVsim.logger.save_testbed_pose(pose);
    }

    // The agent invokes this method to move within the testbed.
    // Control noise is added to the command.
    public void move(Matrix velocity_control_commands) {
        double v_noise = sigmaVNoise * randGen.nextGaussian() + muNoise;
        double w_noise = sigmaWNoise * randGen.nextGaussian() + muNoise;
        double dt = Engine.deltaT;            // delta time
        double v = (velocity_control_commands.get(0, 0) + v_noise); // v from robot motion - translational velocity
        double w = (velocity_control_commands.get(1, 0) + w_noise); // omega from robot motion - rotataional velocity
        double x = get_x_position();
        double y = get_y_position();
        double theta = get_orientation();
        double dtheta = w * dt;

        set_orientation(theta + dtheta);
        if (w != 0.0)
            set_position(
                    x - ((v / w) * Math.sin(theta)) + ((v / w) * Math.sin((theta + dtheta))),
                    y + ((v / w) * Math.cos(theta)) - ((v / w) * Math.cos((theta + dtheta)))
            );
        else
            set_position(
                    x + (v * dt) * Math.cos(theta),
                    y + (v * dt) * Math.sin(theta)
            );
        out.println("Pose: " + get_x_position() + " " + get_y_position() + " " + Math.toDegrees(get_orientation()));

        AGVsim.logger.save_testbed_pose(pose);
    }

    public void assert_model_has_changed() {
        setChanged();
        notifyObservers();
    }

    public void add_object_without_repaint(int x, int y, double size) {
        objects.add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.objectCombo.addItem(label);
    }

    public void add_object(int x, int y, double size) {
        objects.add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.objectCombo.addItem(label);
        AGVsim.testbedview.repaint();
    }

    public int nuobjects() {
        return objects.size();
    }

    public SimObject object_at(int index) {
        return objects.elementAt(index);
    }

    public void remove_object(int object_id) {
        if (object_id > nuobjects() || object_id < 0)
            return;
        objects.removeElementAt(object_id - 1);
        AGVsim.testbedview.repaint();
    }

    public Vector<SimObject> get_objects() {
        return objects;
    }

    public void set_objects(Vector<SimObject> objs) {
        objects = objs;
    }
}
