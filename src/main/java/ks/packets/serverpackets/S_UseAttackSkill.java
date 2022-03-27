package ks.packets.serverpackets;

import ks.constants.L1ActionCodes;
import ks.constants.L1SkillId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;
import ks.util.L1MotionUtils;

import java.util.concurrent.atomic.AtomicInteger;


public class S_UseAttackSkill extends ServerBasePacket {
    private static final AtomicInteger _sequentialNumber = new AtomicInteger(0);

    public S_UseAttackSkill(L1Character cha, int targetId, int spellGfx, int x, int y, int actionId) {
        buildPacket(cha, targetId, spellGfx, x, y, actionId, 6, true);
    }

    public S_UseAttackSkill(L1Character cha, int targetId, int spellGfx, int x, int y, int actionId, boolean motion) {
        buildPacket(cha, targetId, spellGfx, x, y, actionId, 0, motion);
    }

    private void buildPacket(L1Character cha, int targetId, int spellGfx, int x, int y, int actionId, int isHit, boolean withCastMotion) {
        if (cha instanceof L1PcInstance) {
            if (cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE) && actionId == L1ActionCodes.ACTION_SkillAttack) {
                int tempchargfx = cha.getGfxId().getTempCharGfx();
                if (tempchargfx == 5727 || tempchargfx == 5730) {
                    actionId = L1ActionCodes.ACTION_SkillBuff;
                } else if (tempchargfx == 5733 || tempchargfx == 5736) {
                    actionId = L1ActionCodes.ACTION_Attack;
                }
            }
        }

        if (cha.getGfxId().getTempCharGfx() == 4013) {
            actionId = L1ActionCodes.ACTION_Attack;
        }

        int autoNum = _sequentialNumber.incrementAndGet();

        int newHeading = L1CommonUtils.calcHeading(cha.getX(), cha.getY(), x, y);
        cha.setHeading(newHeading);

        writeC(L1Opcodes.S_OPCODE_ATTACKPACKET);
        writeC(actionId);
        writeD(withCastMotion ? cha.getId() : 0);

        L1MotionUtils.motionForAttackSkillPacket(this, targetId, spellGfx, actionId);

        writeC(isHit);
        writeC(0x00);
        writeC(newHeading);
        writeD(autoNum); // 번호가 겹치지 않게 보낸다
        writeH(spellGfx);
        writeC(0); // 뱀파-0 , 에볼- 6 타켓지종:6, 범위&타켓지종:8, 범위:0
        writeH(cha.getX());
        writeH(cha.getY());
        writeH(x);
        writeH(y);
        writeD(0);
    }
}