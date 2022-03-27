package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.core.datatables.NPCTalkDataTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.model.types.Point;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.util.L1CommonUtils;

import java.util.List;

public class L1CastleGuardInstance extends L1NpcInstance {
    private boolean isAttackClan = false;

    public L1CastleGuardInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void searchTarget() {
        L1PcInstance targetPlayer = null;
        List<L1PcInstance> list = L1World.getInstance().getVisiblePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc == null || pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()) {
                continue;
            }
            if (!pc.isInvisible() || getTemplate().isAgrocoi()) {
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war == null)
                        continue;
                    boolean isInWar = war.checkClanInWar(pc.getClanName());
                    isAttackClan = war.checkAttackClan(pc.getClanName());
                    if (isInWar && isAttackClan) {
                        targetPlayer = pc;
                        break;
                    }
                }
            }
        }

        if (targetPlayer != null) {
            boolean isNowWar = L1CastleLocation.isNowWarByArea(targetPlayer);

            if (isNowWar && !isAttackClan) {
                targetPlayer = null;
            }
        }

        if (targetPlayer != null) {
            setTarget(targetPlayer);
        }
    }

    public void setTarget(L1PcInstance targetPlayer) {
        if (targetPlayer != null) {
            hateList.add(targetPlayer, 0);
            setTarget(targetPlayer);
        }
    }

    @Override
    public boolean noTarget() {
        if (getLocation().getTileLineDistance(new Point(getHomeX(), getHomeY())) > 0) {
            int dir = moveDirection(getHomeX(), getHomeY());
            if (dir != -1) {
                toMoveDirection(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            } else {
                teleport(getHomeX(), getHomeY(), 1);
            }
        } else {
            return L1World.getInstance().getRecognizePlayer(this).size() == 0;
        }
        return false;
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }

        setActivated(false);
        startAI();
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (pc != null && !isDead()) {
            if (getCurrentHp() > 0) {
                L1AttackRun attack = new L1AttackRun(pc, this);
                attack.action();
                attack.commit();
            } else {
                L1AttackRun attack = new L1AttackRun(pc, this);
                attack.action();
            }
        }
    }

    @Override
    public void onTalkAction(L1PcInstance player) {
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(getTemplate().getNpcId());
        int npcid = getTemplate().getNpcId();
        String htmlid = null;
        String[] htmldata = null;
        boolean hascastle;
        String clan_name = "";
        String pri_name = "";

        if (talking != null) {
            if (npcid == 70549 || npcid == 70985 || npcid == 70656) {
                hascastle = checkHasCastle(player, L1CastleLocation.KENT_CASTLE_ID);

                if (hascastle) {
                    htmlid = "gateokeeper";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "gatekeeperop";
                }
            } else if (npcid == 70600 || npcid == 70986) {
                hascastle = checkHasCastle(player, L1CastleLocation.OT_CASTLE_ID);
                if (hascastle) {
                    htmlid = "orckeeper";
                } else {
                    htmlid = "orckeeperop";
                }
            } else if (npcid == 70687 || npcid == 70987 || npcid == 70778) {
                hascastle = checkHasCastle(player, L1CastleLocation.WW_CASTLE_ID);

                if (hascastle) {
                    htmlid = "gateokeeper";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "gatekeeperop";
                }
            } else if (npcid == 70800 || npcid == 70988 || npcid == 70989
                    || npcid == 70990 || npcid == 70991 || npcid == 70817) {
                hascastle = checkHasCastle(player, L1CastleLocation.GIRAN_CASTLE_ID);
                if (hascastle) {
                    htmlid = "gateokeeper";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "gatekeeperop";
                }
            } else if (npcid == 70862 || npcid == 70992 || npcid == 70863) {
                hascastle = checkHasCastle(player,
                        L1CastleLocation.HEINE_CASTLE_ID);
                if (hascastle) {
                    htmlid = "gateokeeper";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "gatekeeperop";
                }
            } else if (npcid == 70993 || npcid == 70994 || npcid == 70995) {
                hascastle = checkHasCastle(player,
                        L1CastleLocation.DOWA_CASTLE_ID);
                if (hascastle) {
                    htmlid = "gateokeeper";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "gatekeeperop";
                }
            } else if (npcid == 70996) {
                hascastle = checkHasCastle(player,
                        L1CastleLocation.ADEN_CASTLE_ID);
                if (hascastle) {
                    htmlid = "gatekeeper";
                    htmldata = new String[]{player.getName()};
                } else {
                    htmlid = "gatekeeperop";
                }
            } else if (npcid == 60514) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.KENT_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "ktguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 60560) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.OT_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "orcguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 60552) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.WW_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "wdguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 60524 || npcid == 60525 || npcid == 60529) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.GIRAN_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "grguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 70857 || npcid == 4710001) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.HEINE_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "heguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 60530 || npcid == 60531) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.DOWA_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "dcguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 60533 || npcid == 60534 || npcid == 60535
                    || npcid == 60536) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.ADEN_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "adguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            } else if (npcid == 81156) {
                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getCastleId() == L1CastleLocation.DIAD_CASTLE_ID) {
                        clan_name = clan.getClanName();
                        pri_name = clan.getLeaderName();
                        break;
                    }
                }
                htmlid = "ktguard6";
                htmldata = new String[]{getName(), clan_name, pri_name};
            }

            if (htmlid != null) {
                if (htmldata != null) {
                    player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
                            htmldata));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
                }
            } else {
                if (player.getLawful() < -1000) {
                    player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
                } else {
                    player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
                }
            }
        }
    }

    public void onFinalAction() {

    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (getCurrentHp() > 0 && !isDead()) {
            if (damage >= 0) {
                if (!(attacker instanceof L1EffectInstance)) {
                    setHate(attacker, damage);
                }
            }

            if (damage > 0) {
                L1SkillUtils.removeSleep(this);
            }

            onNpcAI();

            if (attacker instanceof L1PcInstance && damage > 0) {
                L1PcInstance pc = (L1PcInstance) attacker;
                pc.setPetTarget(this);
            }

            int newHp = getCurrentHp() - damage;
            if (newHp <= 0 && !isDead()) {
                setCurrentHp(0);
                setDead(true);
                setActionStatus(L1ActionCodes.ACTION_Die);
                death();
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) {
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
            death();
        }
    }

    @Override
    public void checkTarget() {
        L1CommonUtils.npcCheckTarget(this);
    }

    private void death() {
        try {
            setDeathProcessing(true);
            setCurrentHp(0);
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
            getMap().setPassable(getLocation(), true);
            Broadcaster.broadcastPacket(L1CastleGuardInstance.this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Die));
            startChat(L1NpcConstants.CHAT_TIMING_DEAD);
            setDeathProcessing(false);
            allTargetClear();
            startDeleteTimer();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private boolean checkHasCastle(L1PcInstance pc, int castleId) {
        if (pc.isGm()) {
            pc.sendPackets("GM이라서 열어준겁니다");
            return true;
        }

        if (pc.getClanId() != 0) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
            if (clan != null) {
                return clan.getCastleId() == castleId;
            }
        }

        return false;
    }

}
