package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.skill.utils.L1SkillUtils;
import ks.util.L1CommonUtils;
import ks.util.L1MotionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ks.constants.L1SkillId.ABSOLUTE_BARRIER;

public class S_RangeSkill extends ServerBasePacket {
    public static final int TYPE_NODIR = 0;
    public static final int TYPE_DIR = 8;
    private static final AtomicInteger sequentialNumber = new AtomicInteger(0);

    public S_RangeSkill(L1Character cha, List<L1Character> target, int spellGfx, int actionId, int type) {
        buildPacket(cha, target, spellGfx, actionId, type);
    }

    private void buildPacket(L1Character cha, List<L1Character> targetList, int spellGfx, int actionId, int type) {
        writeC(L1Opcodes.S_OPCODE_RANGESKILLS);
        writeC(actionId);
        writeD(cha.getId());
        writeH(cha.getX());
        writeH(cha.getY());

        if (type == TYPE_NODIR) {
            writeC(cha.getHeading());
        } else if (type == TYPE_DIR) {
            int newHeading = cha.getHeading();

            if (!targetList.isEmpty()) {
                newHeading = L1CommonUtils.calcHeading(cha.getX(), cha.getY(), targetList.get(0).getX(), targetList.get(0).getY());
            }

            cha.setHeading(newHeading);
            writeC(cha.getHeading());
        }

        writeD(sequentialNumber.incrementAndGet()); // 번호가 겹치지 않게 보낸다.
        writeH(spellGfx);
        writeC(type);
        writeH(0);
        writeH(targetList.size());

        for (L1Character target : targetList) {
            writeD(target.getId());

            if (target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER) || target.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)) {
                writeH(0);
                L1AttackUtils.missAttack(target);
            } else {
                L1MotionUtils.motionForRangeSkillPacket(this, target);
            }
        }
    }
}