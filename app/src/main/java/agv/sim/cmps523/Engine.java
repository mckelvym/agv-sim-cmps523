// Mark McKelvy
// CMPS 523
// Final Project
// File: Engine.java
package agv.sim.cmps523;

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.data.Logger;
import agv.sim.cmps523.event.EngineEventListener;

public class Engine implements EngineEventListener {
    private final Values values;
    private final Logger logger;

    public Engine(Values values, Logger logger) {
        this.values = requireNonNull(values);
        this.logger = requireNonNull(logger);
        values.addEngineEventListener(this);
    }

    @Override
    public void buildRequested() {
        final Sensor sensor = requireNonNull(Agent.getCurrent().getSensor());
        Agent.newAgent(values, logger).setSensor(sensor);
        Testbed.newTestbed(values, logger);
        out.println("Engine: created new agent and testbed.");
        resetRequested();
    }

    @Override
    public void pauseStatusChanged() {
        if (values.isEnginePaused()) {
            return;
        }
        EngineRunner thread = new EngineRunner(values, this::runStepRequested, logger);    // Create a new thread
        thread.setPriority(Thread.MAX_PRIORITY);    // with max priority
        thread.start();
    }

    @Override
    public void resetRequested() {
        logger.init();
        Testbed.getCurrent().initializeBotPose();
        Agent.getCurrent().initializeSubjectiveBotPose();
        Agent.getCurrent().initializeParticles();
        values.notifyTestbedChanged();
    }

    @Override
    public void runStepRequested() {
        Agent.getCurrent().actAndObserve();
        values.notifyTestbedChanged();
    }
}
