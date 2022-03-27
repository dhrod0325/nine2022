package ks.model.instance;


import ks.app.config.prop.CodeConfig;
import ks.constants.L1DataMapKey;
import ks.core.datatables.SkillsTable;
import ks.model.*;
import ks.model.pc.L1DamageCheck;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChangeHeading;

@SuppressWarnings("unused")
public class L1ScarecrowInstance extends L1NpcInstance {
    public L1ScarecrowInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        super.onAction(pc);

        if (pc.getLevel() < CodeConfig.CRACKER_MAX_LEVEL) {
            setHate(pc, 1);
            L1CalcExp.calcExp(pc, this);
        }
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        super.receiveDamage(attacker, damage);

        int heading = getHeading();

        if (heading < 7)
            heading++;
        else
            heading = 0;

        setHeading(heading);
        Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));
    }

    public void attackDamageCheck(L1PcInstance attacker, int dmg) {
        if (!"false".equalsIgnoreCase(attacker.getDataMap().get(L1DataMapKey.DMG_CHECK))) {
            if (dmg > 0) {
                if (attacker.isInParty()) {
                    attacker.sendPackets("파티중에는 대미지 확인이 불가능 합니다");
                } else {
                    attacker.getDamageCheck().damageCheck(attacker, dmg);
                    receiveDamage(attacker, dmg);
                }
            }
        }
    }

    public void magicDamageCheck(L1PcInstance attacker, int skillId, int damage) {
        if (!"false".equalsIgnoreCase(attacker.getDataMap().get(L1DataMapKey.DMG_CHECK))) {
            if (damage > 0) {
                if (attacker.isInParty()) {
                    attacker.sendPackets("파티중에는 대미지 확인이 불가능 합니다");
                } else {
                    L1DamageCheck dmgCheck = attacker.getDamageCheck();

                    if (dmgCheck.getLastSkillId() != skillId) {
                        dmgCheck = new L1DamageCheck();
                        attacker.setDamageCheck(dmgCheck);
                    }

                    L1Skills skills = SkillsTable.getInstance().getTemplate(skillId);
                    dmgCheck.damageCheck(attacker, damage, skills.getName());
                    dmgCheck.setLastSkillId(skillId);
                }
            }
        }
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
    }

    public void onFinalAction() {
    }

    public void doFinalAction() {
    }
}
