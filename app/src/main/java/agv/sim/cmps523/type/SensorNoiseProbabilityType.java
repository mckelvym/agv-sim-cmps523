package agv.sim.cmps523.type;

public enum SensorNoiseProbabilityType {
    Z_HIT,
    Z_SHORT,
    Z_MAX,
    Z_RAND,
    ;

    public static SensorNoiseProbabilityType at(int index) {
        switch (index) {
            case 0:
                return Z_HIT;
            case 1:
                return Z_SHORT;
            case 2:
                return Z_MAX;
            case 3:
                return Z_RAND;
        }
        throw new IllegalArgumentException(String.valueOf(index));
    }
}
