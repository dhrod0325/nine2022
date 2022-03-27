package ks.model.skill;

import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.cooking.L1CookingUtils;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1Skill;
import ks.model.skill.magic.L1SkillFactory;
import ks.packets.serverpackets.*;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;

public class L1SkillStop {

    private static final Logger logger = LogManager.getLogger();

    public static void stopSkill(L1Character cha, int skillId) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        if (skill != null) {
            L1LogUtils.skillLog("스킬 정지 : {}", skill.getName());
        }

        L1Skill runSkill = L1SkillFactory.create(skillId);

        if (runSkill != null) {
            logger.debug("runSkillStop : {} ", skill);

            runSkill.stopSkill(cha);

            return;
        } else {
            logger.debug("normalStop : {} ", skill);
        }

        switch (skillId) {
            case ERASE_MAGIC:
            case MOB_RANGE_ERASE_MAGIC:
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.ICON_AURA));
                }
                break;
            case STATUS_FREEZE:
                L1MagicUtils.stopBind(cha);
                break;
            case STATUS_SHOCK_STUN:
                L1MagicUtils.stopStun(cha);
                break;
            case STATUS_HASTE: {
                L1MagicUtils.stopHaste(cha);
                break;
            }
            case STATUS_DRAGON_PERL: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;

                    pc.sendPackets(new S_DragonPerl(pc.getId(), 0));
                    Broadcaster.broadcastPacket(pc, new S_DragonPerl(pc.getId(), 0));
                    pc.sendPackets(new S_PacketBox(L1PacketBoxType.DRAGONPERL, 8, 0));

                    pc.setDragonPerlSpeed(0);
                }

                break;
            }

            case STATUS_BRAVE:
            case STATUS_ELFBRAVE: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                    Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
                }

                cha.getMoveState().setBraveSpeed(0);
                break;
            }
            case STATUS_UNDERWATER_BREATH: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
                }
                break;
            }
            case STATUS_WISDOM_POTION: {
                if (cha instanceof L1PcInstance) {
                    cha.getAbility().addSp(-2);
                }
                break;
            }

            case STATUS_POISON: {
                cha.curePoison();
                break;
            }
            case STATUS_PET_FOOD: {
                if (cha instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) cha;
                    int foodValue = pet.getFood() + 1;
                    switch (foodValue) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            pet.setFood(foodValue);
                            break;
                        case 5:
                            pet.setFood(5);
                            pet.setCurrentPetStatus(3);
                            break;
                        case 6:
                            pet.setFood(5);
                            break;
                        default:
                            break;
                    }
                    pet.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_PET_FOOD, 1200 * 1000);
                }
                break;
            }

            case STATUS_PINK_NAME: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_PinkName(pc.getId(), 0));
                    Broadcaster.broadcastPacket(pc, new S_PinkName(pc.getId(), 0));
                    pc.setPinkName(false);
                }
                break;
            }

            case STATUS_CHAT_PROHIBITED: {
                if (cha instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_ServerMessage(288));
                }
                break;
            }

            case STATUS_CUBE_IGNITION_TEAM:
                cha.getResistance().addFire(-30);
                break;
            case STATUS_CUBE_QUAKE_TEAM:
                cha.getResistance().addEarth(-30);
                break;
            case STATUS_CUBE_SHOCK_TEAM:
                cha.getResistance().addWind(-30);
                break;
            case STATUS_CUBE_BALANCE_TEAM:
                break;
        }

        if (L1CookingUtils.isCookingSkill(skillId)) {
            L1CookingUtils.stopCookingBuff(skillId, cha);
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            sendStopMessage(pc, skillId);
            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }

    private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
        L1Skills skills = SkillsTable.getInstance().getTemplate(skillid);

        if (skills == null || charaPc == null) {
            return;
        }

        int msgID = skills.getSysmsgIdStop();

        if (msgID > 0) {
            charaPc.sendPackets(new S_ServerMessage(msgID));
        }
    }
}
