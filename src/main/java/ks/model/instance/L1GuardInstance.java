package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.constants.L1NpcConstants;
import ks.core.datatables.NPCTalkDataTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.model.txt.L1TxtAlert;
import ks.model.types.Point;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_NpcChatPacket;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;
import org.apache.commons.lang3.StringUtils;

public class L1GuardInstance extends L1NpcInstance {
    public L1GuardInstance(L1Npc template) {
        super(template);
    }

    public void setTarget(L1PcInstance targetPlayer) {
        if (targetPlayer != null) {
            hateList.add(targetPlayer, 0);
            setTarget(targetPlayer);
        }
    }

    @Override
    public boolean noTarget() {
        if (getTemplate().getNpcId() == 777838 || getTemplate().getNpcId() == 60503 || getTemplate().getNpcId() == 60504) {
            int newMoveX = 0;
            int newMoveY = 0;

            int ReturnHome = RandomUtils.nextInt(100);

            int randomX;
            int randomY;

            if (ReturnHome == 10) {
                newMoveX = getHomeX();
                newMoveY = getHomeY();
            } else {
                if (getTemplate().getNpcId() == 777838) {
                    for (int i = 0; i <= 50; i++) {
                        randomX = RandomUtils.nextInt(-1, 1);
                        randomY = RandomUtils.nextInt(-1, 1);

                        newMoveX = getX() + randomX;
                        newMoveY = getY() + randomY;

                        if (getMap().isPassable(newMoveX, newMoveY)) {
                            break;
                        }
                    }

                    if (RandomUtils.isWinning(100, 10)) {
                        String chat = L1TxtAlert.getInstance().getRandom();

                        if (!StringUtils.isEmpty(chat)) {
                            Broadcaster.wideBroadcastPacket(L1GuardInstance.this, new S_NpcChatPacket(L1GuardInstance.this, chat, 2));
                        }
                    }
                } else {
                    randomX = RandomUtils.nextInt(30) - RandomUtils.nextInt(30);
                    randomY = RandomUtils.nextInt(30) - RandomUtils.nextInt(30);

                    newMoveX = getX() + randomX;
                    newMoveY = getY() + randomY;
                }
            }

            int dir = moveDirection(newMoveX, newMoveY);

            if (dir != -1) {
                toMoveDirection(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));

                return false;
            }
        } else if (getLocation().getTileLineDistance(new Point(getHomeX(), getHomeY())) > 0) {
            int dir = moveDirection(getHomeX(), getHomeY());

            if (dir != -1) {
                toMoveDirection(dir);
                setSleepTime(calcSleepTime(getPassiSpeed(), L1NpcConstants.MOVE_SPEED));
            } else {
                teleport(getHomeX(), getHomeY(), 1);
            }
        } else {
            if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
                return true;
            }

            return isDead();
        }

        return false;
    }

    @Override
    public void onNpcAI() {
        if (getPassiSpeed() <= 0)
            return;
        if (isAiRunning()) {
            return;
        }
        setActivated(false);
        startAI();
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (pc == null)
            return;
        if (!isDead()) {
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
        String clan_name = "";
        String pri_name = "";

        if (talking != null) {
            switch (npcid) {
                case 4707000:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.KENT_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "ktguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 60560:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.OT_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "orcguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 60552:
                case 4711000:
                case 4711001:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.WW_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "wdguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 60524:
                case 60525:
                case 60529:
                case 4708001:
                case 4708000:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.GIRAN_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "grguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 70857:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.HEINE_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "heguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 60530:
                case 60531:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.DOWA_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "dcguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 60533:
                case 60534:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.ADEN_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "adguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
                case 81156:
                    for (L1Clan clan : L1World.getInstance().getAllClans()) {
                        if (clan.getCastleId() == L1CastleLocation.DIAD_CASTLE_ID) {
                            clan_name = clan.getClanName();
                            pri_name = clan.getLeaderName();
                            break;
                        }
                    }
                    htmlid = "ktguard6";
                    htmldata = new String[]{getName(), clan_name, pri_name};
                    break;
            }

            if (htmlid != null) {
                player.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
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
        if (attacker == null)
            return;
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
        } else if (getCurrentHp() <= 0 || !isDead()) {
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
            death();
        }
    }

    @Override
    public void checkTarget() {
        L1CommonUtils.npcCheckTarget(this);
    }

    public void death() {
        try {
            setDeathProcessing(true);
            setCurrentHp(0);
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_Die);
            getMap().setPassable(getLocation(), true);
            Broadcaster.broadcastPacket(L1GuardInstance.this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Die));
            startChat(L1NpcConstants.CHAT_TIMING_DEAD);
            setDeathProcessing(false);
            allTargetClear();
            startDeleteTimer();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
