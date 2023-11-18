// Mark McKelvy
// CMPS 523
// Final Project
// File: Engine.java
package agv.sim.cmps523;

import static java.lang.System.out;

import java.util.Vector;

public class Engine {
    static int fps = Integer.parseInt(String.valueOf(ControlPanel.FRAMERATE_COMBO.getSelectedItem())); // default, sync w/ choice 2 of cPanel
    static double deltaT = 0.1; //AGVsim.control_panel.get_current_time_delta();

    public Engine() {
    }

    public void buildArchitecture() {
        buildTestbedSkeleton();
        AGVsim.testbed.addObserver(AGVsim.testbedview);
        resetSystem();
        if (AGVsim.controlPanel.isNotPaused())  // ensure simulation is paused
            AGVsim.controlPanel.pauseButton.doClick();
        AGVsim.controlPanel.stepButton.setEnabled(true); // enable step button
        AGVsim.controlPanel.runButton.setEnabled(true);  // enable run button
    }

    void buildTestbedSkeleton() {
        AGVsim.agent = new Agent();
        AGVsim.agent.setSensor(AGVsim.sensor);
        Vector<SimObject> objects = AGVsim.testbed.getObjects();
        AGVsim.testbed = new Testbed();
        AGVsim.testbed.set_objects(objects);
        out.println("Engine: created new agent and testbed.");
    }

    void run1Frame() {
        AGVsim.agent.actAndObserve();
        AGVsim.testbed.assertModelHasChanged();
    }

    void resetSystem() {
        AGVsim.testbed.initializeBotPose();
        AGVsim.agent.initializeSubjectiveBotPose();
        AGVsim.agent.setTranslationalVelocity(AGVsim.controlPanel.getCurrentTranslationalVelocity());
        AGVsim.agent.setRotationalVelocity(AGVsim.controlPanel.getCurrentRotationalVelocity());
        if (AGVsim.algorithm == 2)
            AGVsim.agent.initializeParticles();
        AGVsim.testbed.assertModelHasChanged();
    }
}
