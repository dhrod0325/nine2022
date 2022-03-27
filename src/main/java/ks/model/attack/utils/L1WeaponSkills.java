package ks.model.attack.utils;

import ks.app.LineageAppContext;
import ks.constants.L1ActionCodes;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.*;
import ks.model.attack.magic.L1MagicRun;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_EffectLocation;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1WeaponSkills {
    private static final Logger logger = LogManager.getLogger(L1WeaponSkills.class);

    public static double alice(L1PcInstance pc, L1Character cha) {//앨리스8단계
        double dmg = 0;
        int chance = RandomUtils.nextInt(100) + 1;

        if (25 >= chance) {
            L1MagicRun magic = new L1MagicRun(pc, cha);
            dmg = magic.calcMagicDamage(L1SkillId.WEAPON_ALICE);//미티어 스트라이크

            if (dmg <= 0) {
                dmg = 0;
            }

            pc.sendPackets(new S_SkillSound(cha.getId(), 762));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 762));

            L1PcInstance targetPc = null;
            L1NpcInstance targetNpc;
            L1PcInstance _targetPc = null;

            if (cha instanceof L1PcInstance) {
                _targetPc = (L1PcInstance) cha;
            }

            for (L1Object object : L1World.getInstance().getVisibleObjects(cha, 2)) {
                if (object == null) {
                    continue;
                }

                if (!(object instanceof L1Character)) {
                    continue;
                }

                if (object.getId() == pc.getId() || object.getId() == cha.getId()) {
                    continue;
                }

                if (object instanceof L1PcInstance) {
                    targetPc = (L1PcInstance) object;
                    if (L1CharPosUtils.isSafeZone(targetPc)) {
                        continue;
                    }
                }

                if (cha instanceof L1MonsterInstance) {
                    if (!(object instanceof L1MonsterInstance)) {
                        continue;
                    }
                }

                if (cha instanceof L1PcInstance || cha instanceof L1SummonInstance || cha instanceof L1PetInstance) {
                    if (!(object instanceof L1PcInstance || object instanceof L1SummonInstance || object instanceof L1PetInstance || object instanceof L1MonsterInstance)) {
                        continue;
                    }

                    if (cha instanceof L1PcInstance) {
                        if (_targetPc.getClanId() > 0) {
                            if (pc.getClanId() != _targetPc.getClanId()) {
                                if (targetPc != null) {
                                    if (pc.getClanId() == targetPc.getClanId()) {
                                        continue;
                                    }
                                }

                            }
                        } else {
                            if (targetPc != null) {
                                if (pc.getClanId() == targetPc.getClanId()) {
                                    continue;
                                }
                            }
                        }
                    }
                }

                if (dmg <= 0) {
                    continue;
                }

                if (object instanceof L1PcInstance) {
                    targetPc = (L1PcInstance) object;
                    targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(), L1ActionCodes.ACTION_Damage));
                    Broadcaster.broadcastPacket(targetPc, new S_DoActionGFX(targetPc.getId(), L1ActionCodes.ACTION_Damage));
                    targetPc.receiveDamage(pc, (int) dmg);
                } else if (object instanceof L1SummonInstance
                        || object instanceof L1PetInstance
                        || object instanceof L1MonsterInstance) {
                    targetNpc = (L1NpcInstance) object;
                    Broadcaster.broadcastPacket(targetNpc, new S_DoActionGFX(targetNpc.getId(), L1ActionCodes.ACTION_Damage));
                    targetNpc.receiveDamage(pc, (int) dmg);
                }
            }
        }
        return dmg;
    }

    public static int disease(L1PcInstance pc, L1Character cha, int enchant) {
        int chance = RandomUtils.nextInt(100) + 1;
        int skillTime = 60;

        if (enchant >= chance) {
            new L1SkillUse(pc, DISEASE, cha.getId(), cha.getX(), cha.getY(), skillTime).run();
        }

        return 0;
    }

    public static double crystalStaff(L1PcInstance pc, L1Character cha, L1ItemInstance item) {
        double dmg = 0;
        int chance = RandomUtils.nextInt(100) + 1;
        int enchant = item.getEnchantLevel();

        if ((enchant * 2) + 20 >= chance) {
            dmg = calcMagicDamage(L1SkillId.WEAPON_CRYSTAL_STAFF, item);

            L1Skills skill = SkillsTable.getInstance().getTemplate(WEAPON_CRYSTAL_STAFF);
            pc.sendPackets(new S_EffectLocation(cha.getX(), cha.getY(), skill.getCastGfx()));
            Broadcaster.broadcastPacket(pc, new S_EffectLocation(cha.getX(), cha.getY(), skill.getCastGfx()));
        }

        if (dmg <= 0) {
            dmg = 0;
        }

        return dmg;
    }


    public static double 악운의단검(L1PcInstance pc, L1PcInstance targetPc, L1ItemInstance weapon) {
        double dmg = 0;
        int chance = RandomUtils.nextInt(100) + 1;

        if (3 >= chance) {
            dmg = targetPc.getCurrentHp() / 2d;

            if (targetPc.getCurrentHp() - dmg < 0) {
                dmg = 0;
            }

            String msg = weapon.getLogName();
            pc.sendPackets(new S_ServerMessage(158, msg));
            pc.getInventory().removeItem(weapon, 1);
        }

        return dmg;
    }

    public static void 테베무기(L1PcInstance pc, L1Character target, L1ItemInstance weapon, int effect) {//테베체이서
        if (target == null) {
            return;
        }

        if (target.getSkillEffectTimerSet().hasSkillEffect(TEBE_EFFECT)) {
            return;
        }

        int enchant = weapon.getEnchantLevel();

        int dmg = 0;

        int chance = RandomUtils.nextInt(100) + 1;

        if (chance <= enchant * 2) {
            switch (weapon.getItemId()) {
                case 415010:
                    dmg = calcMagicDamage(L1SkillId.WEAPON_TEBE_1, weapon);
                    break;
                case 415011:
                    dmg = calcMagicDamage(L1SkillId.WEAPON_TEBE_2, weapon);
                    break;
                case 415012:
                    dmg = calcMagicDamage(L1SkillId.WEAPON_TEBE_3, weapon);
                    break;
                case 415013:
                    dmg = calcMagicDamage(L1SkillId.WEAPON_TEBE_4, weapon);
                    break;
            }

            if (dmg < 0)
                dmg = 0;

            pc.sendPackets(new S_SkillSound(target.getId(), effect));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(), effect));

            int finalDmg = dmg;

            target.getSkillEffectTimerSet().setSkillEffect(L1SkillId.TEBE_EFFECT, 1000 * 3);

            logger.debug("테베 스킬 대미지 : {}", finalDmg);

            LineageAppContext.commonTaskScheduler().execute(() -> {
                for (int i = 0; i < 3; i++) {
                    try {
                        if (target.isDead()) {
                            break;
                        }

                        if (target instanceof L1MonsterInstance) {
                            ((L1MonsterInstance) target).receiveDamage(pc, finalDmg);
                        } else if (target instanceof L1PcInstance) {
                            ((L1PcInstance) target).receiveDamage(pc, finalDmg);
                        }

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("오류", e);
                    }
                }
            });
        }
    }

    public static int calcMagicDamage(int skillId, L1ItemInstance item) {
        int dmg = calcMagicDiceDamage(skillId);

        int safeEnchant = item.getItem().getSafeEnchant();

        if (safeEnchant == 0) {
            dmg += dmg * item.getEnchantLevel();
        } else if (safeEnchant == 6) {
            if (item.getEnchantLevel() >= 7) {
                dmg += dmg * (item.getEnchantLevel() - 7);
            }
        }

        return dmg;
    }

    public static int calcMagicDiceDamage(int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int damageDice = skill.getDamageDice();
        int diceCount = skill.getDamageDiceCount();
        int magicDamage = skill.getDamageValue();

        for (int i = 0; i < diceCount; i++) {
            magicDamage += RandomUtils.nextInt(damageDice) + 1;
        }

        return magicDamage;
    }
}
