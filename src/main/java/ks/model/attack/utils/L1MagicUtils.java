package ks.model.attack.utils;

import ks.constants.L1Attrs;
import ks.constants.L1NpcConstants;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.*;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.*;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;

import static ks.constants.L1SkillId.*;

public class L1MagicUtils {
    private final static Logger logger = LogManager.getLogger();

    public static int calcPcFireWallDamage(L1Character target) {
        double attrDefence = calcAttrResistance(target, L1Attrs.ATTR_FIRE);
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
        int dmg = (int) ((1.0 - attrDefence) * l1skills.getDamageValue());

        if (target.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER, EARTH_BIND)
                || target.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)) {
            dmg = 0;
        }

        if (dmg < 0) {
            dmg = 0;
        }

        return dmg;
    }

    public static int calcNpcFireWallDamage(L1Character target) {
        double attrDefence = calcAttrResistance(target, L1Attrs.ATTR_FIRE);
        L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);

        int dmg = (int) ((1.0 - attrDefence) * l1skills.getDamageValue());

        if (L1AttackUtils.isNotAttackAbleByTargetStatus(target)) {
            dmg = 0;
        }

        if (dmg < 0) {
            dmg = 0;
        }

        return dmg;
    }

    public static double calcAttrResistance(L1Character target, int attr) {
        int resist = 0;

        double resistFloor;

        if (target instanceof L1PcInstance) {
            switch (attr) {
                case L1Attrs.ATTR_EARTH:
                    resist = target.getResistance().getEarth();
                    break;
                case L1Attrs.ATTR_FIRE:
                    resist = target.getResistance().getFire();
                    break;
                case L1Attrs.ATTR_WATER:
                    resist = target.getResistance().getWater();
                    break;
                case L1Attrs.ATTR_WIND:
                    resist = target.getResistance().getWind();
                    break;
            }
        }

        if (resist < 0) {
            resistFloor = (int) (-0.45 * Math.abs(resist));
        } else if (resist < 101) {
            resistFloor = (int) (0.45 * Math.abs(resist));
        } else {
            resistFloor = (int) (45 + 0.09 * Math.abs(resist));
        }

        return resistFloor / 100;
    }

    public static double reduceDamageByMr(double mr) {
        double per = 40 + (mr / 100 * 8);

        double pp;

        if (mr <= 100) {
            pp = 2;
        } else if (mr <= 130) {
            pp = 4;
        } else if (mr <= 160) {
            pp = 6;
        } else if (mr <= 200) {
            pp = 8;
        } else {
            pp = 10;
        }

        double result = per + ((mr - 100) / pp);

        return result / 100;
    }

    public static int calcHealHp(L1Character skillUseCharacter, L1Character cha, int skillId) {
        if (skillUseCharacter == null) {
            return 0;
        }

        //힐올의 경우 시전자에게 힐이 들어가지 않음
        if (cha.equals(skillUseCharacter) && skillId == HEAL_ALL) {
            return 0;
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(WATER_LIFE)) {
            cha.getSkillEffectTimerSet().killSkillEffectTimer(WATER_LIFE);
            cha.sendPackets(new S_PacketBox(L1PacketBoxType.DEL_ICON));
        }

        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        int dice = skill.getDamageDice();
        int diceCount = skill.getDamageDiceCount();
        int value = skill.getDamageValue();
        int magicDamage = 0;

        double powerHeal;

        for (int i = 0; i < diceCount; i++) {
            magicDamage += (RandomUtils.nextInt(dice) + 1);
        }

        magicDamage += value;

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            int powerSp = pc.getAbility().getSp();
            int powerInt = pc.getAbility().getTotalInt();
            int magicBonus = pc.getAbility().getMagicBonus();

            powerHeal = magicBonus + 1 + (powerInt * 0.1);

            switch (pc.getType()) {
                case 0: //군주
                case 4://다크엘프
                    powerHeal *= 0.8;
                    break;
                case 1: //기사
                    powerHeal *= 0.7;
                    break;
                case 2: //요정
                    powerHeal *= 0.7;
                    break;
                case 3://마법사
                    powerHeal *= 1.2;
                    break;
            }

            magicDamage *= (1 + powerHeal);

            if (skillId != NATURES_BLESSING) {
                if (pc.getLawful() > 0) {
                    magicDamage *= 1 + (pc.getLawful() / 32768.0);
                } else {
                    magicDamage /= 1.5;
                }
            }
        } else if (skillUseCharacter instanceof L1NpcInstance) {
            L1NpcInstance _npc = (L1NpcInstance) skillUseCharacter;
            int charaIntelligence = _npc.getAbility().getTotalInt();
            powerHeal = charaIntelligence / 2.0;
            magicDamage *= (1 + powerHeal);
        }

        magicDamage /= 4;
        magicDamage = (int) (magicDamage / 1.3);

        if (cha.getSkillEffectTimerSet().hasSkillEffect(WATER_LIFE)) {
            magicDamage *= 2;
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) {
            magicDamage /= 2;
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(10517)) {
            magicDamage /= 2;

            if (cha.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) {
                magicDamage /= 4;
            }
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(10518)) {// 데스힐
            magicDamage -= magicDamage;
        }

        if (skillId == NATURES_BLESSING) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (!pc.getTimer().isTimeOver("STATUS_NATURES_BLESSING")) {
                magicDamage = 0;
                logger.debug("블레싱 동작중 회복량 감소 : {}", magicDamage);
            } else {
                pc.getTimer().setWaitTime("STATUS_NATURES_BLESSING", skill.getReuseDelay());
            }
        }


        logger.debug("healHp : {}", magicDamage);

        return magicDamage;
    }

    public static int getTamingCharisma(L1PcInstance pc) {
        int petCost = 0;

        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (L1NpcInstance pet : petList) {
            petCost += pet.getPetCost();
        }

        int charisma = pc.getAbility().getTotalCha();

        if (pc.isElf()) {
            charisma += 12;
        } else if (pc.isWizard()) {
            charisma += 6;
        }

        charisma -= petCost;

        return charisma;
    }

    public static void detectionNpc(L1Character target) {
        if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            int hiddenStatus = npc.getHiddenStatus();

            if (hiddenStatus == L1NpcConstants.HIDDEN_STATUS_SINK) {
                if (npc.getNpcId() != 45682) {
                    npc.appearOnGround(target);
                }
            }
        }
    }

    public static void unFreeze(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Poison(pc.getId(), 0));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
            Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 0));
        } else if (targetCharacter instanceof L1MonsterInstance
                || targetCharacter instanceof L1SummonInstance
                || targetCharacter instanceof L1PetInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            npc.setParalyzed(false);
            npc.setParalysisTime(0);

            Broadcaster.broadcastPacket(npc, new S_Poison(npc.getId(), 0));
        }

        List<L1Object> visibleObjects = L1World.getInstance().getVisibleObjects(targetCharacter);

        for (L1Object o : visibleObjects) {
            if (o instanceof L1NpcInstance) {
                L1NpcInstance n = (L1NpcInstance) o;
                if (n.getLocation().equals(targetCharacter.getLocation())) {
                    if (n.getNpcId() == 81168) {
                        n.deleteMe();
                        break;
                    }
                }
            }
        }
    }

    public static void stopBind(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
        } else if (targetCharacter instanceof L1MonsterInstance
                || targetCharacter instanceof L1SummonInstance
                || targetCharacter instanceof L1PetInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            npc.setParalyzed(false);
        }
    }

    public static void stopStun(L1Character targetCharacter) {
        if (targetCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) targetCharacter;
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
        } else if (targetCharacter instanceof L1MonsterInstance
                || targetCharacter instanceof L1SummonInstance
                || targetCharacter instanceof L1PetInstance) {
            L1NpcInstance npc = (L1NpcInstance) targetCharacter;
            npc.setParalyzed(false);
        }
    }

    public static void stopHaste(L1Character target) {
        if (target instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) target;
            pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
            Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
        }

        target.getMoveState().setMoveSpeed(0);
    }

    public static void stopInvisible(L1Character pc) {
        if (pc.getSkillEffectTimerSet().hasSkillEffect(INVISIBILITY)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(INVISIBILITY);
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(BLIND_HIDING)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(BLIND_HIDING);
        }
    }

    public static void startInvisible(L1Character cha) {
        startInvisible(cha, 0);
    }

    public static void startInvisible(L1Character cha, int duration) {
        stopInvisible(cha);

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            for (L1DollInstance doll : pc.getDollList().values()) {
                doll.deleteDoll();
                pc.sendPackets(new S_OwnCharStatus(pc));
            }

            pc.sendPackets(new S_Invis(pc.getId(), 1));
            Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 1));
            pc.sendPackets(new S_Sound(147));
            pc.getSkillEffectTimerSet().setSkillEffect(INVISIBILITY, duration);
        }
    }

    public static void startAbsoluteBarrier(L1Character cha, int millSecond) {
        if (!cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
            cha.getSkillEffectTimerSet().setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, millSecond);
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.stopHpRegenerationByDoll();
                pc.stopMpRegenerationByDoll();
            }
        }
    }

    public static void stopAbsoluteBarrier(L1PcInstance pc) {
        if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.ABSOLUTE_BARRIER);
            pc.startHpRegenerationByDoll();
            pc.startMpRegenerationByDoll();
        }
    }

    public static boolean isTargetMeAndPartyAndClan(L1Character skillUseCharacter, L1Character targetCharacter) {
        L1LogUtils.gmLog(skillUseCharacter, "자신/혈맹/파티에게 무조건 성공하는 스킬 호출");

        if (skillUseCharacter.equals(targetCharacter)) {
            return true;
        }

        if (skillUseCharacter instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) skillUseCharacter;

            if (targetCharacter instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) targetCharacter;

                if (pc.isInParty()) {
                    if (pc.getParty().isMember(targetPc)) {
                        return true;
                    }
                }

                if (pc.getClanId() != 0) {
                    return pc.getClanId() == targetPc.getClanId();
                }
            }
        }


        return false;
    }
}
