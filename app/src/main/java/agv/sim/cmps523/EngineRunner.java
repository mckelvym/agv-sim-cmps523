package agv.sim.cmps523;

import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.data.Logger;

class EngineRunner extends Thread { //Thread to run simulation
    private final Values values;
    private final Logger logger;
    private final Runnable run1Frame;

    public EngineRunner(Values values, Runnable run1Frame, Logger logger) {
        this.values = requireNonNull(values);
        this.logger = requireNonNull(logger);
        this.run1Frame = requireNonNull(run1Frame);
    }

    @Override
    public void run() {
        long beforeTime = System.currentTimeMillis(); // from Davision, KGPJ, p. 23
        int numRuns = 0;
        while (!values.isEnginePaused() && numRuns < 9000) {        //while program is running
            run1Frame.run();    //run program 1 step and delay
            final double engineTimestep = values.getEngineTimestep();
            logger.addTimestamp(engineTimestep);
            values.setEngineTimestep(engineTimestep + values.getTimestampDelta());
            long delay = 1000 / values.getFramesPerSecond(); //according to  framerate
            long timeDiff = System.currentTimeMillis() - beforeTime;
            long sleepTime = delay - timeDiff;
            if (sleepTime < 0) sleepTime = 5; // be nice
            try {
                Thread.sleep(sleepTime);
            } //sleep until delay is ready for next cycle
            catch (Exception e) {
                e.printStackTrace();
            }
            beforeTime = System.currentTimeMillis();
            numRuns++;
        }
    }
}
