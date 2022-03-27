package ks.model.instance;

import ks.app.LineageAppContext;
import ks.constants.L1ActionCodes;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.npc.NpcTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class L1EventTowerInstance extends L1NpcInstance {
    public static boolean isBoss = true;
    private final int[] crownloc = {-2, -1, 0, 1, 2};
    private int crackStatus;

    public L1EventTowerInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
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
        L1PcInstance pc = L1CommonUtils.getAttackerToPc(attacker);
        if (pc == null) {
            return;
        }

        if (getCurrentHp() > 0 && !isDead()) {
            damage = damage / RandomUtils.nextInt(15, 20);

            attacker.setEventDamage(attacker.getEventDamage() + damage);

            int newHp = getCurrentHp() - damage;

            if (newHp <= 0 && !isDead()) {
                setCurrentHp(0);
                setDead(true);
                setActionStatus(L1ActionCodes.ACTION_TowerDie);
                crackStatus = 0;
                LineageAppContext.commonTaskScheduler().execute(new Death());
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
            LineageAppContext.commonTaskScheduler().execute(new Death());
        }
    }

    @Override
    public void deleteMe() {
        destroyed = true;
        if (getInventory() != null) {
            getInventory().clearItems();
        }
        allTargetClear();
        master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            pc.getNearObjects().removeKnownObject(this);
            pc.sendPackets(new S_RemoveObject(this));
        }
        getNearObjects().removeAllKnownObjects();
    }

    public boolean isSubTower() {
        return (getTemplate().getNpcId() == 81190 || getTemplate().getNpcId() == 81191 || getTemplate().getNpcId() == 81192 || getTemplate().getNpcId() == 81193);
    }

    class Death implements Runnable {
        L1Object object = L1World.getInstance().findObject(getId());
        L1EventTowerInstance npc = (L1EventTowerInstance) object;

        @Override
        public void run() {
            setCurrentHp(0);
            setDead(true);
            setActionStatus(L1ActionCodes.ACTION_TowerDie);
            int targetobjid = npc.getId();

            npc.getMap().setPassable(npc.getLocation(), true);

            Broadcaster.broadcastPacket(npc, new S_DoActionGFX(targetobjid, L1ActionCodes.ACTION_TowerDie));

            if (!isSubTower()) {
                L1WarSpawn warspawn = new L1WarSpawn();
                L1Npc l1npc = NpcTable.getInstance().getTemplate(6100002);
                //l1npc.set_Spot(getNpcTemplate().get_npcId() - 49100);
                int[] loc = new int[3];
                loc[0] = npc.getX() + crownloc[RandomUtils.nextInt(crownloc.length)];
                loc[1] = npc.getY() + crownloc[RandomUtils.nextInt(crownloc.length)];
                loc[2] = npc.getMapId();
                warspawn.SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
            }

            if (getTemplate().getNpcId() == 6100001) {
                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY이벤트 왕관이 모습을 드러냅니다."));
                L1World.getInstance().broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "\\fC이벤트 왕관이 모습을 드러냅니다."));
            }
        }
    }
}
