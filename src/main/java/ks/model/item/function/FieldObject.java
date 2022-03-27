package ks.model.item.function;

import ks.constants.L1ActionCodes;
import ks.core.datatables.NpcSpawnTable;
import ks.model.*;
import ks.model.instance.L1DollInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_AttackPacket;

public class FieldObject extends L1ItemInstance {
    public FieldObject(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = this.getItemId();

            if (itemId == 46160 || itemId == 6000067) {
                useFieldObjectRemovalWand(pc, packet.readD(), useItem);
            }
        }
    }

    private void useFieldObjectRemovalWand(L1PcInstance pc, int targetId, L1ItemInstance item) {
        pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
        Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));

        L1Object target = L1World.getInstance().findObject(targetId);

        if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;

            if (item.getItemId() == 6000067) {
                L1Spawn spawn = npc.getSpawn();

                if (spawn != null) {
                    NpcSpawnTable.getInstance().deleteNpc(npc.getNpcId(), spawn.getLocX(), spawn.getLocY(), spawn.getMapId());
                } else {
                    NpcSpawnTable.getInstance().deleteNpc(npc.getNpcId(), npc.getHomeX(), npc.getHomeY(), npc.getMapId());
                }
            }

            if (item.getItemId() == 46160) {
                if (target instanceof L1DollInstance) {
                    L1DollInstance doll = (L1DollInstance) target;

                    if (doll.getMaster() != null) {
                        doll.deleteDoll();
                    } else {
                        L1World.getInstance().removeObject(doll);
                        L1World.getInstance().removeVisibleObject(doll);
                    }
                }
            }

            NpcSpawnTable.getInstance().removeSpawn(npc);
            npc.setRespawn(false);
            npc.deleteMe();
        }
    }
}
