package ks.model.attack.physics.impl.action;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackAction;
import ks.model.attack.physics.impl.action.utils.L1ActionUtils;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_AttackMissPacket;
import ks.packets.serverpackets.S_AttackPacket;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_UseArrowSkill;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1AttackActionPc implements L1AttackAction {
    private final Logger logger = LogManager.getLogger();
    private final L1PcInstance attacker;
    private final L1Character target;

    public L1AttackActionPc(L1PcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void action(L1AttackParam attackParam) {
        try {
            boolean isHit = attackParam.isHitUp();

            attacker.setHeading(L1CharPosUtils.targetDirection(attacker, target.getX(), target.getY()));

            L1MagicUtils.stopAbsoluteBarrier(attacker);

            boolean critical = attackParam.isCritical();

            int targetId = target.getId();
            int targetX = target.getX();
            int targetY = target.getY();

            L1ItemInstance weapon = attacker.getWeapon();

            int weaponType = 0;
            int weaponId = 0;

            if (weapon != null) {
                weaponType = weapon.getItem().getType1();
                weaponId = weapon.getItem().getItemId();
            }

            if (weaponType == 20) {
                L1ItemInstance arrow = attacker.getInventory().getArrow();

                if (arrow != null) {
                    attacker.getInventory().removeItem(arrow, 1);

                    if (attacker.getGfxId().getTempCharGfx() == 7968) {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 7972, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 7972, targetX, targetY, isHit));
                    } else if (attacker.getGfxId().getTempCharGfx() == 8842) {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 8904, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 8904, targetX, targetY, isHit));
                    } else if (attacker.getGfxId().getTempCharGfx() == 8845) {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 8916, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 8916, targetX, targetY, isHit));
                    } else {
                        int gfx = 66;

                        if (critical) {
                            gfx = 13392;
                        }

                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, gfx, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, gfx, targetX, targetY, isHit));
                    }
                } else if (weaponId == 190) {
                    int gfx = 2349;

                    if (critical) {
                        gfx = 13392;
                    }

                    attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, gfx, targetX, targetY, isHit));
                    Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, gfx, targetX, targetY, isHit));
                }
            } else if (weaponType == 62) {
                L1ItemInstance sting = attacker.getInventory().getSting();

                if (sting != null) {
                    attacker.getInventory().removeItem(sting, 1);

                    if (attacker.getGfxId().getTempCharGfx() == 7968) {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 7972, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 7972, targetX, targetY, isHit));
                    } else if (attacker.getGfxId().getTempCharGfx() == 8842) {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 8904, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 8904, targetX, targetY, isHit));
                    } else if (attacker.getGfxId().getTempCharGfx() == 8845) {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 8916, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 8916, targetX, targetY, isHit));
                    } else {
                        attacker.sendPackets(new S_UseArrowSkill(attacker, targetId, 2989, targetX, targetY, isHit));
                        Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, targetId, 2989, targetX, targetY, isHit));
                    }
                }
            } else {
                if (isHit) {
                    attacker.sendPackets(new S_AttackPacket(attacker, targetId, L1ActionCodes.ACTION_Attack, 0));
                    Broadcaster.broadcastPacket(attacker, new S_AttackPacket(attacker, targetId, L1ActionCodes.ACTION_Attack, 0));

                    if (critical) {
                        L1ActionUtils.criticalAction(attacker, target);
                    }

                } else {
                    if (targetId > 0) {
                        attacker.sendPackets(new S_AttackMissPacket(attacker, targetId));
                        Broadcaster.broadcastPacket(attacker, new S_AttackMissPacket(attacker, targetId));

                        if (!L1AttackUtils.isNotAttackAbleByPos(attacker, target)) {
                            L1AttackUtils.missAttack(target);
                        }
                    } else {
                        attacker.sendPackets(new S_AttackPacket(attacker, 0, L1ActionCodes.ACTION_Attack));
                        Broadcaster.broadcastPacket(attacker, new S_AttackPacket(attacker, 0, L1ActionCodes.ACTION_Attack));
                    }
                }
            }

            if (isHit) {
                Broadcaster.broadcastPacketExceptTargetSight(target, new S_DoActionGFX(targetId, L1ActionCodes.ACTION_Damage), attacker);
            } else {
                L1AttackUtils.missAttack(target);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}