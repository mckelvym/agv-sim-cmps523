package agv.sim.cmps523.type;

public enum AlgorithmType {
    NONE {
        @Override
        public int getAgentNoiseCount() {
            return 0;
        }

        @Override
        public String getDescription() {
            return "None";
        }
    },
    EXTENDED_KALMAN_FILTER {
        @Override
        public int getAgentNoiseCount() {
            return 4;
        }

        @Override
        public String getDescription() {
            return "Extended Kalman Filter (EKF)";
        }
    },
    MONTE_CARLO_LOCALIZATION {
        @Override
        public int getAgentNoiseCount() {
            return 6;
        }

        @Override
        public String getDescription() {
            return "Monte Carlo Localization (MCL)";
        }
    },
    ;

    public static AlgorithmType at(int index) {
        switch (index) {
            case 0:
                return NONE;
            case 1:
                return EXTENDED_KALMAN_FILTER;
            case 2:
                return MONTE_CARLO_LOCALIZATION;
        }
        throw new IllegalArgumentException(String.valueOf(index));
    }

    public abstract int getAgentNoiseCount();

    public abstract String getDescription();
}
