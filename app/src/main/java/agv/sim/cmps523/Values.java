package agv.sim.cmps523;

import static java.lang.Math.toRadians;
import static java.util.Objects.requireNonNull;

import agv.sim.cmps523.data.SimObject;
import agv.sim.cmps523.event.*;
import agv.sim.cmps523.type.AlgorithmType;
import agv.sim.cmps523.type.ClickMode;
import agv.sim.cmps523.type.SensorNoiseProbabilityType;
import agv.sim.cmps523.type.SensorNoiseType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Values {
    private final Map<SensorNoiseProbabilityType, Double> sensorNoiseProbability;
    private final Map<SensorNoiseType, Double> sensorNoise;
    private final int sensorFovDegrees;
    private final double sensorRangeResolution;
    private final double sensorMinRange;
    private final double sensorMaxRange;
    private final double sensorAngularResolution;
    private final Map<Integer, Double> alphaNoise;
    private final Vector<SimObject> simObjects;
    private final Set<SimObjectsListener> simObjectsListeners;
    private final Set<SensorNoiseProbabilityListener> sensorNoiseProbabilityListeners;
    private final Set<SensorSettingsListener> sensorSettingsListeners;
    private final Set<AgentSettingsListener> agentSettingsListeners;
    private final Set<EngineEventListener> engineEventListeners;
    private final Set<TestbedObserver> testbedObservers;
    private double engineTimestep;
    private double agentTranslationalVelocity;
    private double agentRotationalVelocity;
    private int numberOfParticles;
    private double sigmaHit;
    private double lambdaShort;
    private double translationalNoise;
    private double rotationalNoise;
    private AlgorithmType algorithmType;
    private boolean simObjectsDrawId;
    private double simObjectSizeChoice;
    private boolean sensorBeamsDraw;
    private boolean sensorReturnBeamsDraw;
    private boolean enginePaused;
    private int framesPerSecond;
    private double timestampDelta;
    private boolean enableCorrection;
    private ClickMode clickMode;

    public Values() {
        numberOfParticles = 100;
        sensorNoiseProbability = Maps.newHashMap();
        Stream.of(SensorNoiseProbabilityType.values()).forEach(
                type -> sensorNoiseProbability.put(type, 0.0));
        sensorNoise = Maps.newHashMap();
        Stream.of(SensorNoiseType.values()).forEach(
                type -> sensorNoise.put(type, 1.0));
        alphaNoise = Maps.newHashMap();
        IntStream.rangeClosed(1, 6).forEach(
                index -> alphaNoise.put(index, 1.0 / 100.0));
        sigmaHit = 0d;
        lambdaShort = 0d;
        translationalNoise = 2d;
        rotationalNoise = toRadians(2d);
        algorithmType = AlgorithmType.NONE;
        simObjects = new Vector<>();
        simObjectsDrawId = true;
        simObjectSizeChoice = 1.0;
        simObjectsListeners = Sets.newLinkedHashSet();
        sensorFovDegrees = 180;
        sensorMinRange = 0;
        sensorMaxRange = 500;
        sensorAngularResolution = toRadians(5.0);
        sensorRangeResolution = 1.0;
        sensorNoiseProbabilityListeners = Sets.newLinkedHashSet();
        sensorSettingsListeners = Sets.newLinkedHashSet();
        sensorBeamsDraw = true;
        sensorReturnBeamsDraw = true;
        agentSettingsListeners = Sets.newLinkedHashSet();
        agentTranslationalVelocity = 10d;
        agentRotationalVelocity = toRadians(-5d);
        enginePaused = true;
        framesPerSecond = 30;
        timestampDelta = 0.1;
        enableCorrection = true;
        clickMode = ClickMode.ADD_OBJECT;
        engineEventListeners = Sets.newLinkedHashSet();
        engineTimestep = 0;
        testbedObservers = Sets.newLinkedHashSet();

        addSimObject(new SimObject(600, 700, 1));
        addSimObject(new SimObject(700, 200, 2));
        addSimObject(new SimObject(100, 180, 1));
    }

    public int getNumberOfParticles() {
        return numberOfParticles;
    }

    public void setNumberOfParticles(int numberOfParticles) {
        System.out.println("numberOfParticles=" + numberOfParticles);
        this.numberOfParticles = numberOfParticles;
    }

    public double getSensorNoiseProbability(SensorNoiseProbabilityType type) {
        return sensorNoiseProbability.get(requireNonNull(type));
    }

    public void setSensorNoiseProbability(SensorNoiseProbabilityType type, double value) {
        System.out.println("sensorNoiseProbability[" + type + "]=" + value);
        sensorNoiseProbability.put(requireNonNull(type), value);
        notifySensorNoiseProbabilityListeners();
    }

    public void addSensorNoiseProbabilityListener(SensorNoiseProbabilityListener listener) {
        System.out.println("addSensorNoiseProbabilityListener=" + listener);
        sensorNoiseProbabilityListeners.add(listener);
    }

    public boolean removeSensorNoiseProbabilityListener(SensorNoiseProbabilityListener listener) {
        System.out.println("removeSensorNoiseProbabilityListener=" + listener);
        return sensorNoiseProbabilityListeners.remove(listener);
    }

    private void notifySensorNoiseProbabilityListeners() {
        sensorNoiseProbabilityListeners.forEach(SensorNoiseProbabilityListener::sensorNoiseProbabilityChanged);
    }

    public double getSensorNoise(SensorNoiseType type) {
        return sensorNoise.get(requireNonNull(type));
    }

    public void setSensorNoise(SensorNoiseType type, double value) {
        System.out.println("sensorNoise[" + type + "]=" + value);
        sensorNoise.put(requireNonNull(type), value);
    }

    public double getAlphaNoise(int index) {
        return alphaNoise.get(index);
    }

    public void setAlphaNoise(int index, double value) {
        System.out.println("alphaNoise[" + index + "]=" + value);
        alphaNoise.put(index, value);
    }

    public double getSigmaHit() {
        return sigmaHit;
    }

    public void setSigmaHit(double sigmaHit) {
        System.out.println("sigmaHit=" + sigmaHit);
        this.sigmaHit = sigmaHit;
    }

    public double getLambdaShort() {
        return lambdaShort;
    }

    public void setLambdaShort(double lambdaShort) {
        System.out.println("lambdaShort=" + lambdaShort);
        this.lambdaShort = lambdaShort;
    }

    public double getTranslationalNoise() {
        return translationalNoise;
    }

    public void setTranslationalNoise(double translationalNoise) {
        System.out.println("translationalNoise=" + translationalNoise);
        this.translationalNoise = translationalNoise;
    }

    public double getRotationalNoise() {
        return rotationalNoise;
    }

    public void setRotationalNoise(double rotationalNoise) {
        System.out.println("rotationalNoise=" + rotationalNoise);
        this.rotationalNoise = rotationalNoise;
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        System.out.println("algorithmType=" + algorithmType);
        this.algorithmType = requireNonNull(algorithmType);
    }

    public Collection<SimObject> getSimObjects() {
        return Collections.unmodifiableCollection(simObjects);
    }

    public void addSimObject(SimObject simObject) {
        simObjects.add(requireNonNull(simObject));
        System.out.println("addSimObject=" + simObject);
        notifySimObjectsListeners();
    }

    public void removeSimObject(int index) {
        final SimObject simObject = simObjects.remove(index);
        System.out.println("removeSimObject=" + simObject);
        notifySimObjectsListeners();
    }

    public void addSimObjectsListener(SimObjectsListener listener) {
        System.out.println("addSimObjectsListener=" + listener);
        simObjectsListeners.add(listener);
    }

    public boolean removeSimObjectsListener(SimObjectsListener listener) {
        System.out.println("removeSimObjectsListener=" + listener);
        return simObjectsListeners.remove(listener);
    }

    private void notifySimObjectsListeners() {
        simObjectsListeners.forEach(SimObjectsListener::simObjectsChanged);
    }

    public boolean isSimObjectsDrawId() {
        return simObjectsDrawId;
    }

    public void setSimObjectsDrawId(boolean simObjectsDrawId) {
        System.out.println("simObjectsDrawId=" + simObjectsDrawId);
        this.simObjectsDrawId = simObjectsDrawId;
        notifySimObjectsListeners();
    }

    public double getSimObjectSizeChoice() {
        return simObjectSizeChoice;
    }

    public void setSimObjectSizeChoice(double simObjectSizeChoice) {
        System.out.println("simObjectSizeChoice=" + simObjectSizeChoice);
        this.simObjectSizeChoice = simObjectSizeChoice;
    }

    public int getSensorFovDegrees() {
        return sensorFovDegrees;
    }

    public double getSensorMinRange() {
        return sensorMinRange;
    }

    public double getSensorMaxRange() {
        return sensorMaxRange;
    }

    public double getSensorAngularResolution() {
        return sensorAngularResolution;
    }

    public double getSensorRangeResolution() {
        return sensorRangeResolution;
    }

    public void addSensorSettingsListener(SensorSettingsListener listener) {
        System.out.println("addSensorSettingsListener=" + listener);
        sensorSettingsListeners.add(listener);
    }

    public boolean removeSensorSettingsListener(SensorSettingsListener listener) {
        System.out.println("removeSensorSettingsListener=" + listener);
        return sensorSettingsListeners.remove(listener);
    }

    private void notifySensorSettingsListeners() {
        sensorSettingsListeners.forEach(SensorSettingsListener::sensorSettingsChanged);
    }

    public boolean isSensorBeamsDraw() {
        return sensorBeamsDraw;
    }

    public void setSensorBeamsDraw(boolean sensorBeamsDraw) {
        System.out.println("sensorBeamsDraw=" + sensorBeamsDraw);
        this.sensorBeamsDraw = sensorBeamsDraw;
        notifySensorSettingsListeners();
    }

    public boolean isSensorReturnBeamsDraw() {
        return sensorReturnBeamsDraw;
    }

    public void setSensorReturnBeamsDraw(boolean sensorReturnBeamsDraw) {
        System.out.println("sensorReturnBeamsDraw=" + sensorReturnBeamsDraw);
        this.sensorReturnBeamsDraw = sensorReturnBeamsDraw;
        notifySensorSettingsListeners();
    }

    public void addAgentSettingsListener(AgentSettingsListener listener) {
        System.out.println("addAgentSettingsListener=" + listener);
        agentSettingsListeners.add(listener);
    }

    public boolean removeAgentSettingsListener(AgentSettingsListener listener) {
        System.out.println("removeAgentSettingsListener=" + listener);
        return agentSettingsListeners.remove(listener);
    }

    private void notifyAgentSettingsListeners() {
        agentSettingsListeners.forEach(AgentSettingsListener::agentSettingsChanged);
    }

    public double getAgentRotationalVelocity() {
        return agentRotationalVelocity;
    }

    public void setAgentRotationalVelocity(double agentRotationalVelocity) {
        System.out.println("agentRotationalVelocity=" + agentRotationalVelocity);
        this.agentRotationalVelocity = agentRotationalVelocity;
        notifyAgentSettingsListeners();
    }

    public double getAgentTranslationalVelocity() {
        return agentTranslationalVelocity;
    }

    public void setAgentTranslationalVelocity(double agentTranslationalVelocity) {
        System.out.println("agentTranslationalVelocity=" + agentTranslationalVelocity);
        this.agentTranslationalVelocity = agentTranslationalVelocity;
        notifyAgentSettingsListeners();
    }

    public boolean isEnginePaused() {
        return enginePaused;
    }

    public void setEnginePaused(boolean enginePaused) {
        System.out.println("enginePaused=" + enginePaused);
        boolean oldValue = this.enginePaused;
        this.enginePaused = enginePaused;
        if (enginePaused != oldValue) {
            notifyEnginePauseStatusChanged();
        }
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    public void setFramesPerSecond(int framesPerSecond) {
        System.out.println("framesPerSecond=" + framesPerSecond);
        this.framesPerSecond = framesPerSecond;
    }

    public double getTimestampDelta() {
        return timestampDelta;
    }

    public void setTimestampDelta(double timestampDelta) {
        System.out.println("timestampDelta=" + timestampDelta);
        this.timestampDelta = timestampDelta;
    }

    public boolean isEnableCorrection() {
        return enableCorrection;
    }

    public void setEnableCorrection(boolean enableCorrection) {
        System.out.println("enableCorrection=" + enableCorrection);
        this.enableCorrection = enableCorrection;
    }

    public ClickMode getClickMode() {
        return clickMode;
    }

    public void setClickMode(ClickMode clickMode) {
        System.out.println("clickMode=" + clickMode);
        this.clickMode = clickMode;
    }

    public void addEngineEventListener(EngineEventListener listener) {
        System.out.println("addEngineEventListener=" + listener);
        engineEventListeners.add(listener);
    }

    public boolean removeEngineEventListener(EngineEventListener listener) {
        System.out.println("removeEngineEventListener=" + listener);
        return engineEventListeners.remove(listener);
    }

    private void notifyEnginePauseStatusChanged() {
        engineEventListeners.forEach(EngineEventListener::pauseStatusChanged);
    }

    public void notifyEngineBuildRequested() {
        engineEventListeners.forEach(EngineEventListener::buildRequested);
    }

    public void notifyEngineResetRequested() {
        engineEventListeners.forEach(EngineEventListener::resetRequested);
    }

    public void notifyEngineRunStepRequested() {
        engineEventListeners.forEach(EngineEventListener::runStepRequested);
    }

    public double getEngineTimestep() {
        return engineTimestep;
    }

    public void setEngineTimestep(double engineTimestep) {
        System.out.println("engineTimestep=" + engineTimestep);
        this.engineTimestep = engineTimestep;
    }

    public void addTestbedObserver(TestbedObserver observer) {
        testbedObservers.add(requireNonNull(observer));
    }

    public void notifyTestbedChanged() {
        testbedObservers.forEach(TestbedObserver::changed);
    }
}
