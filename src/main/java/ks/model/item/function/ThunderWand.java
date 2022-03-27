package ks.model.item.function;

import ks.constants.L1TimerKey;
import ks.model.*;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.S_UseAttackSkill;
import ks.util.L1CharPosUtils;
import ks.util.common.IntRange;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.COUNTER_MAGIC;

public class ThunderWand extends L1ItemInstance {
    public ThunderWand(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            int objectId = packet.readD();
            int x = packet.readH();
            int y = packet.readH();

            pc.cancelAbsoluteBarrier();

            if (pc.isInvisible()) {
                pc.sendPackets(new S_ServerMessage(1003));
                return;
            }

            int chargeCount = useItem.getChargeCount();

            if (chargeCount <= 0) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (pc.getMap().isSafetyZone(pc.getLocation())) {
                pc.sendPackets(new S_SystemMessage("마을안에서는 흑단 막대를 사용 할 수 없습니다."));
                return;
            }

            logger.debug("timeOver : {}", pc.getTimer().isTimeOver(L1TimerKey.THUNDER_WAND));

            if (!pc.getTimer().isTimeOver(L1TimerKey.THUNDER_WAND)) {
                return;
            }

            L1Object target = L1World.getInstance().findObject(objectId);

            int heading = L1CharPosUtils.targetDirection(pc, x, y);
            pc.setHeading(heading);

            if (target != null) {
                doWandAction(pc, target);
            } else {
                pc.sendPackets(new S_UseAttackSkill(pc, 0, 10, x, y, 17));
                Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, 10, x, y, 17));
            }

            useItem.setChargeCount(useItem.getChargeCount() - 1);

            pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);

            if (useItem.getChargeCount() == 0) {
                pc.getInventory().removeItem(useItem);
            }

            pc.getTimer().setWaitTime(L1TimerKey.THUNDER_WAND, useItem.getItem().getDelayTime());
        }
    }

    private void doWandAction(L1PcInstance attacker, L1Object target) {
        if (!L1CharPosUtils.glanceCheck(attacker, target.getX(), target.getY())) {
            return;
        }

        int minDmg = 2;
        int maxDmg = 8;

        // 흑단 데미지 적당한 대미지 계산, 요점 수정
        int dmg = RandomUtils.nextInt(minDmg, maxDmg);

        if (target instanceof L1Character) {
            L1SkillUtils.removeSleep((L1Character) target);
        }

        if (target instanceof L1PcInstance) {
            L1PcInstance targetPc = (L1PcInstance) target;

            dmg -= targetPc.getTotalReduction();
            dmg = IntRange.ensure(dmg, minDmg, maxDmg);

            if (L1AttackUtils.isNotAttackAbleByTargetStatus(targetPc)) {
                dmg = 0;
            }

            if (L1CharPosUtils.isSafeZone(targetPc) || attacker.checkNonPvP() || L1CharPosUtils.isSafeZone(attacker)) {
                attacker.sendPackets(new S_UseAttackSkill(attacker, 0, 10, targetPc.getX(), targetPc.getY(), 17));
                Broadcaster.broadcastPacket(attacker, new S_UseAttackSkill(attacker, 0, 10, targetPc.getX(), targetPc.getY(), 17));
                return;
            }

            if (targetPc.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
                L1SkillUtils.removeCounterMagic(targetPc);
                return;
            }

            if (L1SkillUtils.hasEraseMagic(targetPc)) {
                L1SkillUtils.removeEraseMagic(targetPc);
            }

            attacker.sendPackets(new S_UseAttackSkill(attacker, targetPc.getId(), 10, targetPc.getX(), targetPc.getY(), 17));
            Broadcaster.broadcastPacket(attacker, new S_UseAttackSkill(attacker, targetPc.getId(), 10, targetPc.getX(), targetPc.getY(), 17));

            L1PinkName.onAction(targetPc, attacker);

            targetPc.receiveDamage(attacker, dmg);
        } else if (target instanceof L1MonsterInstance) {
            L1MonsterInstance mob = (L1MonsterInstance) target;
            attacker.sendPackets(new S_UseAttackSkill(attacker, mob.getId(), 10, mob.getX(), mob.getY(), 17));
            Broadcaster.broadcastPacket(attacker, new S_UseAttackSkill(attacker, mob.getId(), 10, mob.getX(), mob.getY(), 17));
            mob.receiveDamage(attacker, dmg);
        } else if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            attacker.sendPackets(new S_UseAttackSkill(attacker, npc.getId(), 10, npc.getX(), npc.getY(), 17));
            Broadcaster.broadcastPacket(attacker, new S_UseAttackSkill(attacker, npc.getId(), 10, npc.getX(), npc.getY(), 17));
        }
    }
}
