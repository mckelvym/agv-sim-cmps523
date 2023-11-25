package agv.sim.cmps523.type;

public enum ClickMode {
    ADD_OBJECT {
        @Override
        public String getDescription() {
            return "Add Object";
        }
    },
    BOT_ACTUAL {
        @Override
        public String getDescription() {
            return "Bot Actual";
        }
    },
    BOT_BELIEF {
        @Override
        public String getDescription() {
            return "Bot Belief";
        }
    },
    ;

    public static ClickMode at(int index) {
        switch (index) {
            case 0:
                return ADD_OBJECT;
            case 1:
                return BOT_ACTUAL;
            case 2:
                return BOT_BELIEF;
        }
        throw new IllegalArgumentException(String.valueOf(index));
    }

    public abstract String getDescription();
}
