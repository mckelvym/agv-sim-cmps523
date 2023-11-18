// Mark McKelvy
// CMPS 523
// Final Project
// File: Engine.java
package agv.sim.cmps523;

import static java.lang.System.out;

import java.util.Vector;

public class Engine {
    static int fps = Integer.parseInt(String.valueOf(ControlPanel.framerate_combo.getSelectedItem())); // default, sync w/ choice 2 of cPanel
    static double delta_t = 0.1; //AGVsim.control_panel.get_current_time_delta();

    public Engine() {
    }

    public void build_architecture() {
        build_testbed_skeleton();
        AGVsim.testbed.addObserver(AGVsim.testbedview);
        reset_system();
        if (AGVsim.control_panel.isNotPaused())  // ensure simulation is paused
            AGVsim.control_panel.pause_button.doClick();
        AGVsim.control_panel.step_button.setEnabled(true); // enable step button
        AGVsim.control_panel.run_button.setEnabled(true);  // enable run button
    }

    void build_testbed_skeleton() {
        AGVsim.agent = new Agent();
        AGVsim.agent.set_sensor(AGVsim.sensor);
        Vector<SimObject> objects = AGVsim.testbed.get_objects();
        AGVsim.testbed = new Testbed();
        AGVsim.testbed.set_objects(objects);
        out.println("Engine: created new agent and testbed.");
    }

    void run_1_frame() {
        AGVsim.agent.act_and_observe();
        AGVsim.testbed.assert_model_has_changed();
    }

    void reset_system() {
        AGVsim.testbed.initialize_bot_pose();
        AGVsim.agent.initialize_subjective_bot_pose();
        AGVsim.agent.set_translational_velocity(AGVsim.control_panel.get_current_translational_velocity());
        AGVsim.agent.set_rotational_velocity(AGVsim.control_panel.get_current_rotational_velocity());
        if (AGVsim.algorithm == 2)
            AGVsim.agent.initialize_particles();
        AGVsim.testbed.assert_model_has_changed();
    }
}
