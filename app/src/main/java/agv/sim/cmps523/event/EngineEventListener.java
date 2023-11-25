package agv.sim.cmps523.event;

public interface EngineEventListener {
    void buildRequested();

    void pauseStatusChanged();

    void resetRequested();

    void runStepRequested();
}
