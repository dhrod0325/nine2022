package ks.model.item.function;

import ks.constants.L1ActionCodes;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_AttackPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CharPosUtils;
import ks.util.L1CommonUtils;

public class PolyWand extends L1ItemInstance {
    public PolyWand(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = this.getItemId();
            int objectId = packet.readD();
            int x = packet.readH();
            int y = packet.readH();

            if (pc.getMapId() == 63 || pc.getMapId() == 552 || pc.getMapId() == 555 || pc.getMapId() == 557 || pc.getMapId() == 558 || pc.getMapId() == 779) {
                pc.sendPackets(new S_ServerMessage(563));
            } else {
                int heading = L1CharPosUtils.targetDirection(pc, x, y);
                pc.setHeading(heading);

                pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
                Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
                int chargeCount = useItem.getChargeCount();

                if (chargeCount <= 0 && itemId != 40410) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }

                if (pc.getMap().isSafetyZone(pc.getLocation())) {
                    pc.sendPackets(new S_SystemMessage("마을안에서는 단풍 막대를 사용 할 수 없습니다."));
                    return;
                }

                L1Object target = L1World.getInstance().findObject(objectId);

                if (target != null) {
                    L1Character character = (L1Character) target;
                    L1CommonUtils.polyAction(pc, character);
                    pc.cancelAbsoluteBarrier();

                    if (itemId == 40008 || itemId == 140008 || itemId == 45464) {//픽시의 변신막대
                        useItem.setChargeCount(useItem.getChargeCount() - 1);
                        pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
                    } else {
                        pc.getInventory().removeItem(useItem, 1);
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }
        }
    }
}