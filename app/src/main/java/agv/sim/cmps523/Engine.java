// Mark McKelvy
// CMPS 523
// Final Project
// File: Engine.java
package agv.sim.cmps523;

import java.io.PrintStream;
import java.util.Vector;

public class Engine {
    static final PrintStream cout = System.out;
    static int m_fps = Integer.parseInt((String) ControlPanel.framerate_combo.getSelectedItem()); // default, sync w/ choice 2 of cPanel
    static double m_delta_t = 0.1; //AGVsim.m_control_panel.get_current_time_delta();

    public Engine() {
    }

    public void build_architecture() {
        build_testbed_skeleton();
        AGVsim.m_testbed.addObserver(AGVsim.m_testbedview);
        reset_system();
        if (!AGVsim.m_control_panel.is_paused())  // ensure simulation is paused
            AGVsim.m_control_panel.pause_button.doClick();
        AGVsim.m_control_panel.step_button.setEnabled(true); // enable step button
        AGVsim.m_control_panel.run_button.setEnabled(true);  // enable run button
    }

    void build_testbed_skeleton() {
        AGVsim.m_agent = new Agent();
        AGVsim.m_agent.set_sensor(AGVsim.m_sensor);
        Vector<SimObject> objects = AGVsim.m_testbed.get_objects();
        AGVsim.m_testbed = new Testbed();
        AGVsim.m_testbed.set_objects(objects);
        cout.println("Engine: created new agent and testbed.");
    }

    void run_1_frame() {
        AGVsim.m_agent.act_and_observe();
        AGVsim.m_testbed.assert_model_has_changed();
    }

    void reset_system() {
        AGVsim.m_testbed.initialize_bot_pose();
        AGVsim.m_agent.initialize_subjective_bot_pose();
        AGVsim.m_agent.set_translational_velocity(AGVsim.m_control_panel.get_current_translational_velocity());
        AGVsim.m_agent.set_rotational_velocity(AGVsim.m_control_panel.get_current_rotational_velocity());
        if (AGVsim.algorithm == 2)
            AGVsim.m_agent.initialize_particles();
        AGVsim.m_testbed.assert_model_has_changed();
    }
}
