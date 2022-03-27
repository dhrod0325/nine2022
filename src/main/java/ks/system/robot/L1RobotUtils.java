package ks.system.robot;

import ks.constants.L1SkillId;
import ks.model.L1Character;
import ks.model.skill.utils.L1SkillUtils;
import ks.util.L1CharPosUtils;

public class L1RobotUtils {
    public static boolean isAttack(L1Character attacker, L1Character target, int loc) {
        if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND) || target.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)) {
            return false;
        }

        if (target.getMap().isSafetyZone(target.getLocation()))
            return false;

        if (target.isDead())
            return false;

        if (target.isInvisible())
            return false;

        if (!isDistance(attacker.getX(), attacker.getY(), attacker.getMapId(), target.getX(), target.getY(), target.getMapId(), loc))
            return false;

        return L1CharPosUtils.glanceCheck(attacker, target.getX(), target.getY());
    }

    public static boolean isDistance(int x, int y, int m, int tx, int ty, int tm, int loc) {
        int distance = getDistance(x, y, tx, ty);
        if (loc < distance)
            return false;

        return m == tm;
    }

    public static int getDistance(int x, int y, int tx, int ty) {
        long dx = tx - x;
        long dy = ty - y;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}
