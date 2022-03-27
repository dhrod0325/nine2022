package ks.model.item.function;

import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.S_UseAttackSkill;
import ks.util.L1CharPosUtils;

public class FireWand extends L1ItemInstance {
    public FireWand(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int spellsc_objid = 0;
            int spellsc_x = 0;
            int spellsc_y = 0;
            spellsc_objid = packet.readD();
            spellsc_x = packet.readH();
            spellsc_y = packet.readH();
            pc.cancelAbsoluteBarrier();

            int itemId = this.getItemId();
            int delay_id = 0;
            if (itemId == 46113) {
                delay_id = useItem.getItem().getDelayId();
            }
            if (delay_id != 0) {
                if (pc.hasItemDelay(delay_id)) {
                    return;
                }
            }

            if (pc.isInvisible()) {
                pc.sendPackets(new S_ServerMessage(1003));
                return;
            }
            int chargeCount = useItem.getChargeCount();
            if (chargeCount <= 0) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }
            if (pc.getMap().isSafetyZone(pc.getLocation())) {
                pc.sendPackets(new S_SystemMessage("마을안에서는 화염 막대를 사용 할 수 없습니다."));
                return;
            }

            if (pc.getMapId() != L1Map.MAP_ICE_CASTLE1 && pc.getMapId() != L1Map.MAP_ICE_CASTLE2) {
                pc.sendPackets(new S_SystemMessage("얼음 여왕의 성에서만 사용 할 수 있습니다."));
                return;
            }

            L1Object target = L1World.getInstance().findObject(spellsc_objid);

            int heding = L1CharPosUtils.targetDirection(pc, spellsc_x, spellsc_y);
            pc.setHeading(heding);

            if (target != null) {
                doWandAction(pc, target);
            } else {
                pc.sendPackets(new S_UseAttackSkill(pc, 0, 762, spellsc_x, spellsc_y, 18));
                Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, 762, spellsc_x, spellsc_y, 18));
            }
            useItem.setChargeCount(useItem.getChargeCount() - 1);
            pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
            if (useItem.getChargeCount() == 0) {
                pc.getInventory().removeItem(useItem);
            }
            if (itemId == 46113) {
                L1ItemDelay.onItemUse(pc, useItem);
            }
        }
    }

    private void doWandAction(L1PcInstance user, L1Object target) {
        if (target instanceof L1Character) {
            if (!L1CharPosUtils.glanceCheck(user, target.getX(), target.getY())) {
                return;
            }
        }

        for (L1Object object : L1World.getInstance().getVisibleObjects(target, 3)) {
            if (object instanceof L1MonsterInstance) {
                L1NpcInstance npc = (L1NpcInstance) object;
                npc.receiveDamage(user, 100);
            }
        }
        if (target instanceof L1MonsterInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            npc.receiveDamage(user, 100);
        }
        user.sendPackets(new S_UseAttackSkill(user, target.getId(), 762, target.getX(), target.getY(), 18));
        Broadcaster.broadcastPacket(user, new S_UseAttackSkill(user, target.getId(), 762, target.getX(), target.getY(), 18));
    }
}

