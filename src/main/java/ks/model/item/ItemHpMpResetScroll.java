package ks.model.item;

import ks.constants.L1SkillId;
import ks.model.L1CalcStat;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.*;
import ks.util.L1CharPosUtils;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.ABSOLUTE_BARRIER;
import static ks.constants.L1SkillId.ADVANCE_SPIRIT;

public class ItemHpMpResetScroll extends L1ItemInstance {
    private static final Logger logger = LogManager.getLogger(ItemHpMpResetScroll.class);

    public static void resetHpMp(L1PcInstance pc) {
        if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(ABSOLUTE_BARRIER);
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(ADVANCE_SPIRIT)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(ADVANCE_SPIRIT);
        }

        int currentHp = pc.getCurrentHp();
        int currentMp = pc.getCurrentMp();

        L1SkillUse skillUse = new L1SkillUse(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), 0);
        skillUse.run();

        L1CommonUtils.takeoffItems(pc);

        pc.sendPackets(new S_SPMR(pc));
        pc.sendPackets(new S_OwnCharAttrDef(pc));
        pc.sendPackets(new S_OwnCharStatus2(pc));

        int lvl = pc.getLevel();

        int hp = L1CalcStat.calcInitHp(pc);
        int mp = L1CalcStat.calcInitMp(pc);

        for (int i = 1; i < lvl; i++) {
            short randomHp = L1CalcStat.calcStatHp(pc.getType(), hp, pc.getAbility().getCon());
            short randomMp = L1CalcStat.calcStatMp(pc.getType(), mp, pc.getAbility().getWis());

            hp += randomHp;
            mp += randomMp;
        }

        pc.addBaseMaxHp((short) -pc.getBaseMaxHp());
        pc.addBaseMaxMp((short) -pc.getBaseMaxMp());
        pc.addBaseMaxHp((short) hp);
        pc.addBaseMaxMp((short) mp);

        if (currentHp > hp + 1) {
            currentHp = hp + 1;
        }

        if (currentMp > mp + 1) {
            currentMp = mp + 1;
        }

        pc.setCurrentHp(currentHp);
        pc.setCurrentMp(currentMp);

        pc.checkStatus();
        pc.sendPackets(new S_OwnCharStatus(pc));

        pc.save();
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (!L1CharPosUtils.isSafeZone(pc)) {
                pc.sendPackets(getLogName() + "은 세이프존에서만 사용 가능합니다");
                return;
            }

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            try {
                resetHpMp(pc);

                pc.sendPackets(new S_SystemMessage("HP MP 재조정이 완료되었습니다."));
            } catch (Exception e) {
                logger.error(e);
                pc.sendPackets(new S_SystemMessage("오류가 발생했습니다. 운영자에게 문의하세요"));
            }

            pc.getInventory().removeItem(useItem, 1);
        }
    }
}
