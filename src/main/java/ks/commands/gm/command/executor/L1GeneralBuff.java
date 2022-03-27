package ks.commands.gm.command.executor;

import ks.constants.L1SkillId;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1GeneralBuff implements L1CommandExecutor {
    private static final Logger logger = LogManager.getLogger(L1GeneralBuff.class);

    private L1GeneralBuff() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1GeneralBuff();
    }

    private boolean isStatusInitZone(L1PcInstance pc) {
        return pc.getMapId() == 5166;
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            List<L1PcInstance> players = new ArrayList<>();

            players.addAll(L1World.getInstance().getRobotPlayers());
            players.addAll(L1World.getInstance().getAllPlayers());

            StringTokenizer st = new StringTokenizer(arg);

            String type;

            if (st.hasMoreTokens()) {
                type = st.nextToken();
            } else {
                throw new Exception();
            }


            if (type.equalsIgnoreCase("1")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeExpPotion(player);
                        new L1SkillUse(player, L1SkillId.STATUS_COMA_3, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("2")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }
                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeExpPotion(player);
                        new L1SkillUse(player, L1SkillId.STATUS_COMA_5, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("5")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        new L1SkillUse(player, L1SkillId.STATUS_LUCK_A, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("6")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }
                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        new L1SkillUse(player, L1SkillId.STATUS_LUCK_B, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("7")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        new L1SkillUse(player, L1SkillId.STATUS_LUCK_C, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("8")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        new L1SkillUse(player, L1SkillId.STATUS_LUCK_D, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("9")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeMaan(player);

                        new L1SkillUse(player, 7671, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("10")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeMaan(player);

                        new L1SkillUse(player, 7672, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("11")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeMaan(player);

                        new L1SkillUse(player, 7673, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("12")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }
                        if (isStatusInitZone(player)) {
                            continue;
                        }
                        removeMaan(player);

                        new L1SkillUse(player, 7674, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("13")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeMaan(player);

                        new L1SkillUse(player, 7675, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("14")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeMaan(player);

                        new L1SkillUse(player, 7676, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("15")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        removeMaan(player);

                        new L1SkillUse(player, 7677, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                return;
            }

            if (type.equalsIgnoreCase("16")) {
                for (L1PcInstance player : players) {
                    try {
                        if (player == null) {
                            continue;
                        }

                        if (isStatusInitZone(player)) {
                            continue;
                        }

                        int[] allBuffSkill = {
                                L1SkillId.PHYSICAL_ENCHANT_DEX,
                                L1SkillId.PHYSICAL_ENCHANT_STR,
                                L1SkillId.BLESS_WEAPON,
                                L1SkillId.HASTE,
                                L1SkillId.ADVANCE_SPIRIT,
                                L1SkillId.EARTH_SKIN,
                                L1SkillId.SHINING_SHILELD,
                                L1SkillId.FIRE_WEAPON
                        };

                        for (int value : allBuffSkill) {
                            new L1SkillUse(player, value, player.getId(), player.getX(), player.getY(), 0, L1SkillUse.TYPE_GM_BUFF).run();
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
            pc.sendPackets(new S_SystemMessage(".특별버프 [옵션]"));
            pc.sendPackets(new S_SystemMessage(" [1:코마(3) 2.코마(5) 3:상아탑축복 4:드래곤진주]"));
            pc.sendPackets(new S_SystemMessage(" [5:쪽지버프A 6.쪽지버프B 7:쪽지버프C 8:쪽지버프D]"));
            pc.sendPackets(new S_SystemMessage(" [9:지룡마안 10.수룡마안 11:화룡마안 12:풍룡마안]"));
            pc.sendPackets(new S_SystemMessage(" [13:탄생마안 14.형상마안 15:생명마안 16:버프]"));
            pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
        }
    }

    private void removeMaan(L1PcInstance player) {
        if (player.getSkillEffectTimerSet().hasSkillEffect(7671)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7671);
        }
        if (player.getSkillEffectTimerSet().hasSkillEffect(7672)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7672);
        }
        if (player.getSkillEffectTimerSet().hasSkillEffect(7673)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7673);
        }
        if (player.getSkillEffectTimerSet().hasSkillEffect(7674)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7674);
        }
        if (player.getSkillEffectTimerSet().hasSkillEffect(7675)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7675);
        }
        if (player.getSkillEffectTimerSet().hasSkillEffect(7676)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7676);
        }
        if (player.getSkillEffectTimerSet().hasSkillEffect(7677)) {
            player.getSkillEffectTimerSet().removeSkillEffect(7677);
        }
    }

    private void removeExpPotion(L1PcInstance player) {
        if (player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION1)) {
            player.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.EXP_POTION1);
        }

        if (player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_COMA_3)) {
            player.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_COMA_3);
        }

        if (player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_COMA_5)) {
            player.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_COMA_5);
        }
    }
}
