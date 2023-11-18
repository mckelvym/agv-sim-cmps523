// Mark McKelvy
// CMPS 523
// Final Project
// File: Engine.java
package agv.sim.cmps523;

import static java.lang.System.out;

import java.util.Vector;

public class Engine {
    private static int fps = Integer.parseInt(String.valueOf(ControlPanel.getFramerateCombo().getSelectedItem())); // default, sync w/ choice 2 of cPanel
    private static double deltaT = 0.1; //AGVsim.control_panel.get_current_time_delta();

    public Engine() {
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        Engine.fps = fps;
    }

    public static double getDeltaT() {
        return deltaT;
    }

    public static void setDeltaT(double deltaT) {
        Engine.deltaT = deltaT;
    }

    public void buildArchitecture() {
        buildTestbedSkeleton();
        AGVsim.getTestbed().addObserver(AGVsim.getTestbedview());
        resetSystem();
        if (AGVsim.getControlPanel().isNotPaused())  // ensure simulation is paused
            AGVsim.getControlPanel().getPauseButton().doClick();
        AGVsim.getControlPanel().getStepButton().setEnabled(true); // enable step button
        AGVsim.getControlPanel().getRunButton().setEnabled(true);  // enable run button
    }

    void buildTestbedSkeleton() {
        AGVsim.setAgent(new Agent());
        AGVsim.getAgent().setSensor(AGVsim.getSensor());
        Vector<SimObject> objects = AGVsim.getTestbed().getObjects();
        AGVsim.setTestbed(new Testbed());
        AGVsim.getTestbed().set_objects(objects);
        out.println("Engine: created new agent and testbed.");
    }

    void run1Frame() {
        AGVsim.getAgent().actAndObserve();
        AGVsim.getTestbed().assertModelHasChanged();
    }

    void resetSystem() {
        AGVsim.getTestbed().initializeBotPose();
        AGVsim.getAgent().initializeSubjectiveBotPose();
        AGVsim.getAgent().setTranslationalVelocity(AGVsim.getControlPanel().getCurrentTranslationalVelocity());
        AGVsim.getAgent().setRotationalVelocity(AGVsim.getControlPanel().getCurrentRotationalVelocity());
        if (AGVsim.getAlgorithm() == 2)
            AGVsim.getAgent().initializeParticles();
        AGVsim.getTestbed().assertModelHasChanged();
    }
}
