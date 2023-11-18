// Mark McKelvy
// CMPS 523
// Final Project
// File: Testbed.java
package agv.sim.cmps523;

import Jama.Matrix;
import java.io.PrintStream;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;

public class Testbed extends Observable {
    static final PrintStream cout = System.out; // console out
    // testbed noise
    static long m_rand_seed = 314159265;
    static Random m_rand_gen = new Random(m_rand_seed);
    static double m_mu_noise = 0.0; // default value
    static double m_sigma_v_noise = 2.0; // default value
    static double m_sigma_w_noise = 2.0; // default value
    static boolean m_config_orientation = false;
    Matrix m_pose;
    private double m_bot_location_x0 = 180;
    private double m_bot_location_y0 = 200;
    private double m_bot_orientation0 = Math.PI / 2;
    private Vector<SimObject> m_objects;

    public Testbed() {
        m_pose = new Matrix(3, 1);
        m_objects = new Vector<>();
        initialize_bot_pose();
        initialize_testbed_noise();
    }

    double get_x_position() {
        return m_pose.get(0, 0);
    }

    double get_y_position() {
        return m_pose.get(1, 0);
    }

    double get_initial_x_position() {
        return m_bot_location_x0;
    }

    double get_initial_y_position() {
        return m_bot_location_y0;
    }

    double get_orientation() {
        return m_pose.get(2, 0);
    }

    void set_orientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        m_pose.set(2, 0, orient);
        AGVsim.m_sensor.set_orientation(orient);
    }

    double get_initial_orientation() {
        return m_bot_orientation0;
    }

    void set_initial_orientation(double orient) {
        if (orient > Math.PI * 2)
            orient -= Math.PI * 2;
        else if (orient < -Math.PI * 2)
            orient += Math.PI * 2;
        m_bot_orientation0 = orient;
        AGVsim.m_sensor.set_orientation(orient);
    }

    void set_initial_position(double x, double y) {
        m_bot_location_x0 = x;
        m_bot_location_y0 = y;
        AGVsim.m_sensor.set_position(x, y);
    }

    void set_position(double x, double y) {
        m_pose.set(0, 0, x);
        m_pose.set(1, 0, y);
        AGVsim.m_sensor.set_position(x, y);
    }

    void initialize_testbed_noise() {
        m_sigma_v_noise = NoiseControlPanel.get_testbed_v_noise();
        m_sigma_w_noise = NoiseControlPanel.get_testbed_w_noise();
        cout.println("Testbed: sigma_v_noise = " + m_sigma_v_noise + " sigma_w_noise = " + m_sigma_w_noise);
    }

    // used in Engine.resetSystem()
    void initialize_bot_pose() {
        set_position(m_bot_location_x0, m_bot_location_y0);
        set_orientation(m_bot_orientation0);

        AGVsim.m_logger.save_testbed_pose(m_pose);
    }

    // The agent invokes this method to move within the testbed.
    // Control noise is added to the command.
    public void move(Matrix velocity_control_commands) {
        double v_noise = m_sigma_v_noise * m_rand_gen.nextGaussian() + m_mu_noise;
        double w_noise = m_sigma_w_noise * m_rand_gen.nextGaussian() + m_mu_noise;
        double dt = Engine.m_delta_t;            // delta time
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
        cout.println("Pose: " + get_x_position() + " " + get_y_position() + " " + Math.toDegrees(get_orientation()));

        AGVsim.m_logger.save_testbed_pose(m_pose);
    }

    public void assert_model_has_changed() {
        setChanged();
        notifyObservers();
    }

    public void add_object_without_repaint(int x, int y, double size) {
        m_objects.add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.object_combo.addItem(label);
    }

    public void add_object(int x, int y, double size) {
        m_objects.add(new SimObject(x, y, size));
        String label = "Object at (" + x + ", " + y + ")";
        ObjectControlPanel.object_combo.addItem(label);
        AGVsim.m_testbedview.repaint();
    }

    public int num_objects() {
        return m_objects.size();
    }

    public SimObject object_at(int index) {
        return m_objects.elementAt(index);
    }

    public void remove_object(int object_id) {
        if (object_id > num_objects() || object_id < 0)
            return;
        m_objects.removeElementAt(object_id - 1);
        AGVsim.m_testbedview.repaint();
    }

    public Vector<SimObject> get_objects() {
        return m_objects;
    }

    public void set_objects(Vector<SimObject> objs) {
        m_objects = objs;
    }
}
