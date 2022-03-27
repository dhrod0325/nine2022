package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.constants.L1SkillId;
import ks.core.datatables.MapsTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.pet.PetTable;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1SummonInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_PetPack;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;
import ks.util.common.IntRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class L1CalcExp {
    private static final Logger logger = LogManager.getLogger(L1CalcExp.class.getName());

    public static void calcExp(L1PcInstance player, int targetid, List<L1Character> hateCharacterList, List<Integer> hateList, int exp) {
        double partyLevel = 0;
        double dist = 0;

        int memberExp;
        int memberLawful;

        L1Object object = L1World.getInstance().findObject(targetid);

        if (!(object instanceof L1NpcInstance)) {
            return;
        }

        L1NpcInstance npc = (L1NpcInstance) object;

        // 헤이트의 합계를 취득
        L1Character hateCharacter;

        int hate;
        int acquireExp = 0;
        int acquireLawful = 0;
        int partyExp = 0;
        int partyLawful = 0;
        int totalHateExp = 0;
        int totalHateLawful = 0;
        int partyHateExp = 0;
        int partyHateLawful = 0;
        int ownHateExp;

        if (hateCharacterList.size() != hateList.size()) {
            return;
        }

        for (int i = hateList.size() - 1; i >= 0; i--) {
            hateCharacter = hateCharacterList.get(i);
            hate = hateList.get(i);

            if (hateCharacter != null && !hateCharacter.isDead()) {
                totalHateExp += hate;

                if (hateCharacter instanceof L1PcInstance) {
                    totalHateLawful += hate;
                }
            } else {
                hateCharacterList.remove(i);
                hateList.remove(i);
            }
        }

        if (totalHateExp == 0) {
            return;
        }

        if (npc instanceof L1PetInstance || npc instanceof L1SummonInstance) {
            return;
        }

        int lawful = npc.getLawful();

        if (player.getLevel() < CodeConfig.MAX_LEVEL) {
            if (player.getAinHasad() <= 0) {
                lawful *= -1;
            }
        }

        if (player.isInParty()) {
            for (int i = hateList.size() - 1; i >= 0; i--) {
                hateCharacter = hateCharacterList.get(i);
                hate = hateList.get(i);

                if (hateCharacter instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) hateCharacter;

                    if (pc.equals(player)) {
                        partyHateExp += hate;
                        partyHateLawful += hate;
                    } else if (player.getParty().isMember(pc)) {
                        partyHateExp += hate;
                        partyHateLawful += hate;
                    } else {
                        if (totalHateExp > 0) {
                            acquireExp = (exp * hate / totalHateExp);
                        }

                        if (totalHateLawful > 0) {
                            acquireLawful = (lawful * hate / totalHateLawful);
                        }

                        addExp(pc, acquireExp, acquireLawful);
                    }
                } else if (hateCharacter instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) hateCharacter;
                    L1PcInstance master = (L1PcInstance) pet.getMaster();

                    if (master.equals(player)) {
                        partyHateExp += hate;
                    } else if (player.getParty().isMember(master)) {
                        partyHateExp += hate;
                    } else {
                        if (totalHateExp > 0) {
                            acquireExp = (exp * hate / totalHateExp);
                        }

                        addExpPet(pet, acquireExp);
                    }
                } else if (hateCharacter instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) hateCharacter;
                    L1PcInstance master = (L1PcInstance) summon.getMaster();

                    if (master.equals(player)) {
                        partyHateExp += hate;
                    } else if (player.getParty().isMember(master)) {
                        partyHateExp += hate;
                    }
                }
            }

            if (totalHateExp > 0) {
                partyExp = (exp * partyHateExp / totalHateExp);
            }

            if (totalHateLawful > 0) {
                partyLawful = (lawful * partyHateLawful / totalHateLawful);
            }

            // 프리보나스
            double priBonus = 0;

            L1PcInstance leader = player.getParty().getLeader();

            if (leader.isCrown() && (player.getNearObjects().knownsObject(leader) || player.equals(leader))) {
                priBonus = 0.059;
            }

            // PT경험치의 계산
            List<L1PcInstance> partyMembers = player.getParty().getMembers();

            double ptBonus = 0;

            for (L1PcInstance each : partyMembers) {
                if (player.getNearObjects().knownsObject(each) || player.equals(each)) {
                    partyLevel += each.getLevel() * each.getLevel();
                }

                if (player.getNearObjects().knownsObject(each)) {
                    ptBonus += 0.04;
                }
            }

            partyExp = (int) (partyExp * (1 + ptBonus + priBonus));

            // 자캐릭터와 그 애완동물·사몬의 헤이트의 합계를 산출
            if (partyLevel > 0) {
                dist = ((player.getLevel() * player.getLevel()) / partyLevel);
            }

            memberExp = (int) (partyExp * dist);
            memberLawful = (int) (partyLawful * dist);

            ownHateExp = 0;

            for (int i = hateList.size() - 1; i >= 0; i--) {
                hateCharacter = hateCharacterList.get(i);
                hate = hateList.get(i);

                if (hateCharacter instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) hateCharacter;
                    if (pc == player) {
                        ownHateExp += hate;
                    }
                } else if (hateCharacter instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) hateCharacter;
                    L1PcInstance master = (L1PcInstance) pet.getMaster();
                    if (master == player) {
                        ownHateExp += hate;
                    }
                } else if (hateCharacter instanceof L1SummonInstance) {
                    L1SummonInstance summon = (L1SummonInstance) hateCharacter;
                    L1PcInstance master = (L1PcInstance) summon.getMaster();
                    if (master == player) {
                        ownHateExp += hate;
                    }
                }
            }

            // 자캐릭터와 그 애완동물·사몬에 분배
            if (ownHateExp != 0) { // 공격에 참가하고 있었다
                for (int i = hateList.size() - 1; i >= 0; i--) {
                    hateCharacter = hateCharacterList.get(i);
                    hate = hateList.get(i);
                    if (hateCharacter instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) hateCharacter;
                        if (pc == player) {
                            if (ownHateExp > 0) {
                                acquireExp = (memberExp * hate / ownHateExp);
                            }
                            addExp(pc, acquireExp, memberLawful);
                        }
                    } else if (hateCharacter instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) hateCharacter;
                        L1PcInstance master = (L1PcInstance) pet.getMaster();

                        if (master == player) {
                            if (ownHateExp > 0) {
                                acquireExp = (memberExp * hate / ownHateExp);
                            }
                            addExpPet(pet, acquireExp);
                        }
                    }
                }
            } else {
                addExp(player, memberExp, memberLawful);
            }

            // 파티 멤버와 그 애완동물·사몬의 헤이트의 합계를 산출
            for (L1PcInstance ptMember : partyMembers) {
                if (player.getNearObjects().knownsObject(ptMember)) {
                    if (partyLevel > 0) {
                        dist = ((ptMember.getLevel() * ptMember.getLevel()) / partyLevel);
                    }

                    memberExp = (int) (partyExp * dist);
                    memberLawful = (int) (partyLawful * dist);

                    ownHateExp = 0;
                    for (int i = hateList.size() - 1; i >= 0; i--) {
                        hateCharacter = hateCharacterList.get(i);
                        hate = hateList.get(i);
                        if (hateCharacter instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) hateCharacter;
                            if (pc == ptMember) {
                                ownHateExp += hate;
                            }
                        } else if (hateCharacter instanceof L1PetInstance) {
                            L1PetInstance pet = (L1PetInstance) hateCharacter;
                            L1PcInstance master = (L1PcInstance) pet
                                    .getMaster();
                            if (master == ptMember) {
                                ownHateExp += hate;
                            }
                        } else if (hateCharacter instanceof L1SummonInstance) {
                            L1SummonInstance summon = (L1SummonInstance) hateCharacter;
                            L1PcInstance master = (L1PcInstance) summon
                                    .getMaster();
                            if (master == ptMember) {
                                ownHateExp += hate;
                            }
                        }
                    }
                    // 파티 멤버와 그 애완동물·사몬에 분배
                    if (ownHateExp != 0) { // 공격에 참가하고 있었다
                        for (int i = hateList.size() - 1; i >= 0; i--) {
                            hateCharacter = hateCharacterList
                                    .get(i);
                            hate = hateList.get(i);
                            if (hateCharacter instanceof L1PcInstance) {
                                L1PcInstance pc = (L1PcInstance) hateCharacter;
                                if (pc == ptMember) {
                                    if (ownHateExp > 0) {
                                        acquireExp = (memberExp * hate / ownHateExp);
                                    }
                                    addExp(pc, acquireExp, memberLawful);
                                }
                            } else if (hateCharacter instanceof L1PetInstance) {
                                L1PetInstance pet = (L1PetInstance) hateCharacter;
                                L1PcInstance master = (L1PcInstance) pet
                                        .getMaster();
                                if (master == ptMember) {
                                    if (ownHateExp > 0) {
                                        acquireExp = (memberExp * hate / ownHateExp);
                                    }
                                    addExpPet(pet, acquireExp);
                                }
                            }
                        }
                    } else {
                        addExp(ptMember, memberExp, memberLawful);
                    }
                }
            }
        } else {
            for (int i = hateList.size() - 1; i >= 0; i--) {
                hateCharacter = hateCharacterList.get(i);
                hate = hateList.get(i);
                acquireExp = (exp * hate / totalHateExp);
                if (hateCharacter instanceof L1PcInstance) {
                    if (totalHateLawful > 0) {
                        acquireLawful = (lawful * hate / totalHateLawful);
                    }
                }

                if (hateCharacter instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) hateCharacter;
                    addExp(pc, acquireExp, acquireLawful);
                } else if (hateCharacter instanceof L1PetInstance) {
                    L1PetInstance pet = (L1PetInstance) hateCharacter;
                    addExpPet(pet, acquireExp);
                }
            }
        }
    }

    //경험치 조절
    public static void addExp(L1PcInstance pc, int exp, int lawful) {
        if (pc.isDead())
            return;

        if (L1CommonUtils.isStandByServer(pc)) {
            return;
        }

        int pcLevel = pc.getLevel();
        int addLawful = lawful * CodeConfig.RATE_LAWFUL * -1;
        pc.addLawful(addLawful);

        double expPenalty = ExpTable.getInstance().getPenaltyRate(pcLevel);

        double foodBonus = 1;
        double expposion = 1;
        double levelBonus = 1;
        double dollBonus = 1;// 추가눈사람
        double gereng = 1;//게렝
        double ainhasadBonus = 1;
        double emeraldBonus = 0;// 드래곤에메랄드
        double amethystBonus = 0;
        double lvlbuff = 1;
        double clanBuff = 1;
        double comaBuff = 1;
        double expBonus = 1 + (pc.getExpBonus() / 100d);
        double mapExpRate = MapsTable.getInstance().getExpRate(pc.getMapId());

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_1) || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_2) || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_3)) {
            foodBonus = 1.02;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_4)) {
            foodBonus = 1.04;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_7_N) || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_7_S)) {
            foodBonus = 1.01;
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_15_N) || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_15_S)) {
            foodBonus = 1.02;
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_23_N) || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_23_S)) {
            foodBonus = 1.1;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION1) || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_COMA_5)) {
            expposion = 1.2;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION2)) {
            gereng = 1.4;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION3)) {
            gereng = 1.3;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGON_EMERALD_YES) && pc.getAinHasad() > 10000) {
            emeraldBonus = CodeConfig.EXP_BONUS_EME;

            pc.calAinHasad(-exp);
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, pc.getAinHasad()));
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_AMETYHST_YES)) {
            amethystBonus = 0.53;
        }

        if (pc.getAinHasad() > 0) {
            pc.calAinHasad(-exp * (int) (CodeConfig.RATE_XP / 100D));
            ainhasadBonus = CodeConfig.EXP_BONUS_AIN;
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.AINHASAD, pc.getAinHasad()));
        }

        /*
         if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CLAN_BUFF1)) {
         clanBuff = 1.02;
         } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CLAN_BUFF2)) {
         clanBuff = 1.03;
         } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CLAN_BUFF3)) {
         clanBuff = 1.04;
         } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CLAN_BUFF4)) {
         clanBuff = 1.05;
         } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CLAN_BUFF5)) {
         clanBuff = 1.06;
         }
         */

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_COMA_5)) {
            comaBuff = 1.2;
        }

        int newChar = 1;

        int settingEXP = CodeConfig.RATE_XP;

        double addExp = (int) (
                expBonus
                        * exp
                        * settingEXP
                        * foodBonus
                        * expposion
                        * levelBonus
                        * (ainhasadBonus + emeraldBonus + amethystBonus)
                        * expPenalty
                        * newChar
                        * dollBonus
                        * gereng
                        * lvlbuff
                        * clanBuff
                        * comaBuff
                        * mapExpRate);

        if (pc.getLevel() >= 50) {
            if ((addExp + pc.getExp()) > ExpTable.getInstance().getExpByLevel((pc.getLevel() + 1))) {
                addExp = ExpTable.getInstance().getExpByLevel((pc.getLevel() + 1)) - pc.getExp();
            }
        }

        if (addExp < 0) {
            return;
        }

        if (pc.getClan() != null) {
            pc.getClan().addExp(addExp / 1000);
        }

        if (pcLevel >= CodeConfig.MAX_LEVEL) {
            if (pc.getExpPer() >= 10) {
                addExp *= CodeConfig.MAX_LEVEL_PANALTY;
            }
        }

        pc.addExp(addExp);
        pc.sendUhodo();
    }

    private static void addExpPet(L1PetInstance pet, int exp) {
        if (L1CommonUtils.isStandByServer()) {
            return;
        }

        L1PcInstance pc = (L1PcInstance) pet.getMaster();

        int petItemObjId = pet.getItemObjId();
        int levelBefore = pet.getLevel();
        int totalExp = exp * CodeConfig.PET_EXP_RATE + pet.getExp();

        if (totalExp >= ExpTable.getInstance().getExpByLevel(51)) {
            totalExp = ExpTable.getInstance().getExpByLevel(51) - 1;
        }

        pet.setExp(totalExp);
        pet.setLevel(ExpTable.getInstance().getLevelByExp(totalExp));

        int expPercentage = ExpTable.getInstance().getExpPercentage(pet.getLevel(), totalExp);
        int gap = pet.getLevel() - levelBefore;

        for (int i = 1; i <= gap; i++) {
            IntRange hpUpRange = pet.getPetType().getHpUpRange();
            IntRange mpUpRange = pet.getPetType().getMpUpRange();

            pet.addMaxHp(hpUpRange.randomValue());
            pet.addMaxMp(mpUpRange.randomValue());
        }

        pet.setExpPercent(expPercentage);
        pc.sendPackets(new S_PetPack(pet, pc));

        if (gap != 0) {
            L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);

            if (petTemplate == null) {
                logger.warn("L1Pet == null");
                return;
            }

            petTemplate.setExp(pet.getExp());
            petTemplate.setLevel(pet.getLevel());
            petTemplate.setHp(pet.getMaxHp());
            petTemplate.setMp(pet.getMaxMp());
            PetTable.getInstance().storePet(petTemplate);
            pc.sendPackets(new S_ServerMessage(320, pet.getName()));
        }
    }

    public static void calcExp(L1PcInstance pc, L1NpcInstance monster) {
        int exp = monster.getExp();
        L1HateList hate = monster.getHateList();
        calcExp(pc, monster.getId(), hate.toTargetList(), hate.toHateList(), exp);
    }
}
