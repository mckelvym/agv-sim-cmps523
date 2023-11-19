package agv.sim.cmps523.type;

public enum SensorNoiseType {
    RANGE,
    BEARING,
    SIGNATURE,
    ;

    public static SensorNoiseType at(int index) {
        switch (index) {
            case 0:
                return RANGE;
            case 1:
                return BEARING;
            case 2:
                return SIGNATURE;
        }
        throw new IllegalArgumentException(String.valueOf(index));
    }
}
