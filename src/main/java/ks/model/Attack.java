package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.constants.L1TimerKey;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1ScarecrowInstance;
import ks.model.pc.L1PcInstance;
import ks.model.pc.hackCheck.speedHack.L1AcceleratorCheck;
import ks.packets.serverpackets.*;
import ks.util.L1CharPosUtils;
import ks.util.L1CommonUtils;
import ks.util.common.NumberUtils;

import static ks.model.pc.L1PcInstance.REGEN_STATE_ATTACK;

public class Attack {
    private final L1PcInstance pc;

    public Attack(L1PcInstance pc) {
        this.pc = pc;
    }

    public boolean isAttack(L1Object target) {
        if (pc.isDead() || pc.isTeleport()) {
            return false;
        }

        if (pc.isInvisible()) {
            return false;
        }

        if (pc.isInvisDelay()) {
            return false;
        }

        if (pc.isAutoKingBuff()) {
            pc.setAutoKingBuff(false);
            pc.sendPackets("자동군업이 종료되었습니다");
            return false;
        }

        if (pc.getMapId() == 350) {
            pc.sendPackets(new S_SystemMessage("시장 안에서는 공격이 불가능합니다."));
            return false;
        }

        if (pc.getInventory().isOverWeight82()) {
            pc.sendPackets(new S_ServerMessage(110));
            return false;
        }

        if (target instanceof L1Character) {
            if (target.getMapId() != pc.getMapId() || pc.getLocation().getLineDistance(target.getLocation()) > 20D) {
                return false;
            }
        }

        if (target instanceof L1PcInstance) {
            if (pc.getWeapon() != null && NumberUtils.contains(pc.getWeapon().getItemId(), 45000611, 45000612, 45000613, 45000614, 45000615)) {
                pc.sendPackets("상대방을 공격할 수 없는 무기를 착용중입니다");
                return false;
            }
        }

        if (target != null) {
            if (target instanceof L1NpcInstance) {
                int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
                if (hiddenStatus == L1NpcConstants.HIDDEN_STATUS_SINK || hiddenStatus == L1NpcConstants.HIDDEN_STATUS_FLY) {
                    return false;
                }
            }
        }

        if (!(target instanceof L1ScarecrowInstance)) {
            if (L1CommonUtils.isStandByServer(pc)) {
                return false;
            }
        }

        pc.endFishing();

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
            pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
        }

        pc.delInvis();
        pc.setRegenState(REGEN_STATE_ATTACK);

        return true;
    }

    public void toAttack(int x, int y, int targetId) {
        L1Object target = L1World.getInstance().findObject(targetId);

        if (!isAttack(target)) {
            return;
        }

        if (!pc.getTimer().isTimeOver(L1TimerKey.THUNDER_WAND)) {
            return;
        }

        if (pc.getAutoAttack().isAuto()) {
            if (pc.getAutoAttack().getTargetId() == targetId) {
                return;
            }

            pc.getAutoAttack().setTargetId(targetId);

            return;
        }

        if (CodeConfig.SPEED_CHECK_ATTACK_INTERVAL) {
            int result = pc.getAcceleratorChecker().checkInterval(L1AcceleratorCheck.ACT_TYPE.ATTACK);
            if (result == L1AcceleratorCheck.R_DISCONNECTED) {
                return;
            }
        }

        if (target instanceof L1Character) {
            L1Character targetCharacter = (L1Character) target;

            if (!targetCharacter.isDead()) {
                target.onAction(pc);
            }

            return;
        }

        int weaponId = 0;
        int weaponType = 0;

        L1ItemInstance weapon = pc.getWeapon();
        L1ItemInstance arrow = null;
        L1ItemInstance sting = null;

        if (weapon != null) {
            weaponId = weapon.getItem().getItemId();
            weaponType = weapon.getItem().getType1();

            if (weaponType == 20) {
                arrow = pc.getInventory().getArrow();
            }

            if (weaponType == 62) {
                sting = pc.getInventory().getSting();
            }
        }

        pc.setHeading(L1CharPosUtils.targetDirection(pc, x, y));

        CalcOrBitResult calcOrBitResult = calcOrbit(pc.getX(), pc.getY(), pc.getHeading(), x, y);

        int targetX = calcOrBitResult.targetX;
        int targetY = calcOrBitResult.targetY;

        if (weaponType == 20 && (weaponId == 190 || (weaponId >= 11011 && weaponId <= 11013) || arrow != null)) {
            if (arrow != null) {
                if (pc.getGfxId().getTempCharGfx() == 7968) {
                    pc.sendPackets(new S_UseArrowSkill(pc, 0, 7972, targetX, targetY, true));
                    Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 7972, targetX, targetY, true));
                } else if (pc.getGfxId().getTempCharGfx() == 8842) {
                    pc.sendPackets(new S_UseArrowSkill(pc, 0, 8904, targetX, targetY, true));
                    Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 8904, targetX, targetY, true));
                } else if (pc.getGfxId().getTempCharGfx() == 8845) {
                    pc.sendPackets(new S_UseArrowSkill(pc, 0, 8916, targetX, targetY, true));
                    Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 8916, targetX, targetY, true));
                } else {
                    pc.sendPackets(new S_UseArrowSkill(pc, 0, 66, targetX, targetY, true));
                    Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 66, targetX, targetY, true));
                }

                pc.getInventory().removeItem(arrow, 1);
            } else if (weaponId == 190) {
                pc.sendPackets(new S_UseArrowSkill(pc, 0, 2349, targetX, targetY, true));
                Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 2349, targetX, targetY, true));
            }
        } else if (weaponType == 62 && sting != null) {
            if (pc.getGfxId().getTempCharGfx() == 7968) {
                pc.sendPackets(new S_UseArrowSkill(pc, 0, 7972, targetX, targetY, true));
                Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 7972, targetX, targetY, true));
            } else if (pc.getGfxId().getTempCharGfx() == 8842) {
                pc.sendPackets(new S_UseArrowSkill(pc, 0, 8904, targetX, targetY, true));
                Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 8904, targetX, targetY, true));
            } else if (pc.getGfxId().getTempCharGfx() == 8845) {
                pc.sendPackets(new S_UseArrowSkill(pc, 0, 8916, targetX, targetY, true));
                Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 8916, targetX, targetY, true));
            } else {
                pc.sendPackets(new S_UseArrowSkill(pc, 0, 2989, targetX, targetY, true));
                Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0, 2989, targetX, targetY, true));
            }

            pc.getInventory().removeItem(sting, 1);
        } else {
            pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Attack));
            Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Attack));
        }

        boolean check1 = pc.getWeapon() != null && pc.getWeapon().getItem() != null;

        if ((check1 && pc.getWeapon().getItem().getType() == 17) || (check1 && pc.getWeapon().getItem().getType() == 19)) {
            if (pc.getWeapon().getItemId() == 410003) {
                pc.sendPackets(new S_SkillSound(pc.getId(), 6983));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 6983));
            } else {
                pc.sendPackets(new S_SkillSound(pc.getId(), 7049));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7049));
            }
        }
    }

    private static CalcOrBitResult calcOrbit(int cX, int cY, int head, int targetX, int targetY) {
        byte[] HEADING_X = CodeConfig.HEADING_TABLE_X;
        byte[] HEADING_Y = CodeConfig.HEADING_TABLE_Y;

        float disX = Math.abs(cX - targetX);
        float disY = Math.abs(cY - targetY);
        float dis = Math.max(disX, disY);
        float avgX;
        float avgY;

        if (dis == 0) {
            avgX = HEADING_X[head];
            avgY = HEADING_Y[head];
        } else {
            avgX = disX / dis;
            avgY = disY / dis;
        }

        int addX = (int) Math.floor((avgX * 15) + 0.59f);
        int addY = (int) Math.floor((avgY * 15) + 0.59f);

        if (cX > targetX) {
            addX *= -1;
        }
        if (cY > targetY) {
            addY *= -1;
        }

        return new CalcOrBitResult(targetX + addX, targetY + addY);
    }

    private static class CalcOrBitResult {
        public final int targetX;
        public final int targetY;

        public CalcOrBitResult(int targetX, int targetY) {
            this.targetX = targetX;
            this.targetY = targetY;
        }
    }
}
