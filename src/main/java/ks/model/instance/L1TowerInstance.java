package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_NPCPack;
import ks.scheduler.WarTimeScheduler;
import ks.util.L1CommonUtils;

import java.util.Collection;

@SuppressWarnings("unused")
public class L1TowerInstance extends L1NpcInstance {
    private int castleId;
    private int crackStatus;

    public L1TowerInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        if (perceivedFrom == null)
            return;

        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
    }

    @Override
    public void onAction(L1PcInstance player) {
        if (getCurrentHp() > 0 && !isDead()) {
            L1AttackRun attack = new L1AttackRun(player, this);
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (castleId == 0) {
            if (isSubTower()) {
                castleId = L1CastleLocation.ADEN_CASTLE_ID;
            } else {
                castleId = L1CastleLocation.getCastleId(getX(), getY(), getMapId());
            }
        }

        if (castleId > 0 && WarTimeScheduler.getInstance().isNowWar(castleId)) {
            if (castleId == L1CastleLocation.ADEN_CASTLE_ID && !isSubTower()) {
                int subTowerDeadCount = 0;

                Collection<L1Object> list = L1World.getInstance().getAllObject();

                for (L1Object l1object : list) {
                    if (l1object == null)
                        continue;
                    if (l1object instanceof L1TowerInstance) {
                        L1TowerInstance lt = (L1TowerInstance) l1object;

                        if (lt.isSubTower() && lt.isDead()) {
                            subTowerDeadCount++;

                            if (subTowerDeadCount == 4) {
                                break;
                            }
                        }
                    }

                }
                if (subTowerDeadCount < 3) {
                    return;
                }
            }

            L1PcInstance pc = L1CommonUtils.getAttackerToPc(attacker);

            if (pc == null) {
                return;
            }

            boolean existDefenseClan = false;

            for (L1Clan clan : L1World.getInstance().getAllClans()) {
                int clanCastleId = clan.getCastleId();

                if (clanCastleId == castleId) {
                    existDefenseClan = true;
                    break;
                }
            }

            boolean isProclamation = false;

            for (L1War war : L1World.getInstance().getWarList()) {
                if (castleId == war.getCastleId()) {
                    isProclamation = war.checkClanInWar(pc.getClanName());
                    break;
                }
            }

            if (existDefenseClan && !isProclamation) {
                return;
            }

            if (getCurrentHp() > 0 && !isDead()) {
                int newHp = getCurrentHp() - damage;

                if (newHp <= 0 && !isDead()) {
                    setCurrentHp(0);
                    setDead(true);
                    setActionStatus(L1ActionCodes.ACTION_TowerDie);
                    crackStatus = 0;
                    death();
                }

                if (newHp > 0) {
                    setCurrentHp(newHp);
                    if ((getMaxHp() / 4) > getCurrentHp()) {
                        if (crackStatus != 3) {
                            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_TowerCrack3));
                            setActionStatus(L1ActionCodes.ACTION_TowerCrack3);
                            crackStatus = 3;
                        }
                    } else if ((getMaxHp() * 2 / 4) > getCurrentHp()) {
                        if (crackStatus != 2) {
                            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_TowerCrack2));
                            setActionStatus(L1ActionCodes.ACTION_TowerCrack2);
                            crackStatus = 2;
                        }
                    } else if ((getMaxHp() * 3 / 4) > getCurrentHp()) {
                        if (crackStatus != 1) {
                            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_TowerCrack1));
                            setActionStatus(L1ActionCodes.ACTION_TowerCrack1);
                            crackStatus = 1;
                        }
                    }
                }
            } else if (!isDead()) {
                setDead(true);
                setActionStatus(L1ActionCodes.ACTION_TowerDie);
                death();
            }
        }
    }

    private void death() {
        try {
            L1Object object = L1World.getInstance().findObject(getId());
            L1TowerInstance npc = (L1TowerInstance) object;
            setCurrentHp(0);
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_TowerDie);
            int targetobjid = npc.getId();
            npc.getMap().setPassable(npc.getLocation(), true);
            Broadcaster.broadcastPacket(npc, new S_DoActionGFX(targetobjid, L1ActionCodes.ACTION_TowerDie));

            if (!isSubTower()) {
                L1WarSpawn warspawn = new L1WarSpawn();
                warspawn.SpawnCrown(castleId);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    @Override
    public void deleteMe() {
        removed();
    }

    public boolean isSubTower() {
        return (getTemplate().getNpcId() == 81190
                || getTemplate().getNpcId() == 81191
                || getTemplate().getNpcId() == 81192
                || getTemplate().getNpcId() == 81193);
    }
}
