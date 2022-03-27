package ks.system.robot;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum L1RobotType {
    NORMAL(0),
    HUNT(1),
    JOMBI(2),
    AUTO_CREATE(3),
    STAND_BY(4),
    TELEPORT(5);

    private final static Logger logger = LogManager.getLogger(L1RobotType.class);

    private final int num;

    L1RobotType(int num) {
        this.num = num;
    }

    public static L1RobotType by(Integer num) {
        if (num == null) {
            logger.warn("num이 NULL이라 NORMAL 리턴함");
            return NORMAL;
        }

        for (L1RobotType type : values()) {
            if (type.getNum() == num)
                return type;
        }

        return null;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
