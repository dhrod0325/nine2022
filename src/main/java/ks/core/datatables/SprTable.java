package ks.core.datatables;

import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static ks.constants.L1ActionCodes.*;

public class SprTable {
    private final Logger logger = LogManager.getLogger();

    private static final Map<Integer, Spr> dataMap = new HashMap<>();
    private static final SprTable instance = new SprTable();

    public static SprTable getInstance() {
        return instance;
    }

    public void load() {
        dataMap.clear();

        SqlUtils.query("SELECT * FROM spr_action", (rs, i) -> {
            Spr spr;

            int actid = rs.getInt("act_id");
            int frameCount = rs.getInt("framecount");
            int frameRate = rs.getInt("framerate");
            int speed = calcActionSpeed(frameCount, frameRate);

            int key = rs.getInt("spr_id");

            if (!dataMap.containsKey(key)) {
                spr = new Spr();
                dataMap.put(key, spr);
            } else {
                spr = dataMap.get(key);
            }

            switch (actid) {
                case ACTION_Walk:
                case ACTION_SwordWalk:
                case ACTION_AxeWalk:
                case ACTION_BowWalk:
                case ACTION_SpearWalk:
                case ACTION_StaffWalk:
                case ACTION_DaggerWalk:
                case ACTION_TwoHandSwordWalk:
                case ACTION_EdoryuWalk:
                case ACTION_ClawWalk:
                case ACTION_ThrowingKnifeWalk:
                    spr.moveSpeed.put(actid, speed);
                    break;
                case ACTION_SkillAttack:
                    spr.dirSpellSpeed = speed;
                    break;
                case ACTION_SkillBuff:
                    spr.nodirSpellSpeed = speed;
                    break;
                case ACTION_Attack:
                case ACTION_SwordAttack:
                case ACTION_AxeAttack:
                case ACTION_BowAttack:
                case ACTION_SpearAttack:
                case ACTION_AltAttack://가속기 수정및 외부화
                case ACTION_SpellDirectionExtra://가속기 수정및 외부화
                case ACTION_StaffAttack:
                case ACTION_DaggerAttack:
                case ACTION_TwoHandSwordAttack:
                case ACTION_EdoryuAttack:
                case ACTION_ClawAttack:
                case ACTION_ThrowingKnifeAttack:
                    spr.attackSpeed.put(actid, speed);
                    break;
                case ACTION_Damage:
                case ACTION_AxeDamage:
                case ACTION_BowDamage:
                case ACTION_DaggerDamage:
                case ACTION_ClawDamage:
                case ACTION_EdoryuDamage:
                case ACTION_SwordDamage:
                    spr.damagedSpeed.put(actid, speed);
                    break;
                default:
                    break;
            }

            return null;
        });
    }

    private int calcActionSpeed(int frameCount, int frameRate) {
        return (int) (frameCount * 40 * (24D / frameRate));
    }

    public int getAttackSpeed(int sprId, int actId) {
        if (dataMap.containsKey(sprId)) {
            if (dataMap.get(sprId).attackSpeed.containsKey(actId)) {
                return dataMap.get(sprId).attackSpeed.get(actId);
            } else if (actId == ACTION_Attack) {
                return 0;
            } else {
                try {
                    return dataMap.get(sprId).attackSpeed.get(ACTION_Attack);
                } catch (Exception e) {
                    logger.error("오류", e);
                    return 0;
                }

            }//
        }
        return 0;
    }

    public int getMoveSpeed(int sprid, int actid) {
        if (dataMap.containsKey(sprid)) {
            if (dataMap.get(sprid).moveSpeed.containsKey(actid)) {
                return dataMap.get(sprid).moveSpeed.get(actid);
            } else if (actid == ACTION_Walk) {
                return 0;
            } else {
                return dataMap.get(sprid).moveSpeed.get(ACTION_Walk);
            }
        }
        return 0;
    }

    public int getDirSpellSpeed(int sprid) {
        if (dataMap.containsKey(sprid)) {
            return dataMap.get(sprid).dirSpellSpeed;
        }

        return 0;
    }

    public int getNoDelaySpellSpeed(int sprid) {
        if (dataMap.containsKey(sprid)) {
            return dataMap.get(sprid).nodirSpellSpeed;
        }

        return 0;
    }

    public int getDamagedSpeed(int sprid, int actid) {
        if (dataMap.containsKey(sprid)) {
            if (dataMap.get(sprid).damagedSpeed.containsKey(actid)) {
                return dataMap.get(sprid).damagedSpeed.get(actid);
            } else if (actid == ACTION_Damage) {
                return 0;
            } else {
                return dataMap.get(sprid).damagedSpeed.get(ACTION_Damage);
            }
        }

        return 0;
    }

    private static class Spr {
        private final Map<Integer, Integer> moveSpeed = new HashMap<>();

        private final Map<Integer, Integer> attackSpeed = new HashMap<>();

        private final Map<Integer, Integer> damagedSpeed = new HashMap<>();

        private int nodirSpellSpeed = 1200;

        private int dirSpellSpeed = 1200;

    }
}
