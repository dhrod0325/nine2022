package ks.model.item.function;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_Fishing;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.FishingTimeScheduler;
import ks.util.L1CommonUtils;

public class Fishing extends L1ItemInstance {
    public Fishing(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (L1CommonUtils.isStandByServer(pc)) {
                return;
            }

            int itemId = this.getItemId();

            int x = packet.readH();
            int y = packet.readH();

            startFishing(pc, itemId, x, y);
        }
    }

    private void startFishing(L1PcInstance pc, int itemId, int fishX, int fishY) {
        if (pc.getMapId() != L1Map.MAP_FISHING
                || fishX <= 32704
                || fishX >= 32831
                || fishY <= 32768
                || fishY >= 32895) {
            pc.sendPackets(new S_ServerMessage(1138));
            return;
        }

        int rodLength = 0;

        if (itemId == 430506) {
            rodLength = 3;
        } else if (itemId == 430520) {
            rodLength = 5;
        }

        if (pc.getMap().isFishingZone(fishX, fishY)) {
            if (pc.getMap().isFishingZone(fishX + 1, fishY) && pc.getMap().isFishingZone(fishX - 1, fishY) && pc.getMap().isFishingZone(fishX, fishY + 1) && pc.getMap().isFishingZone(fishX, fishY - 1)) {
                if (fishX > pc.getX() + rodLength || fishX < pc.getX() - rodLength) {
                    pc.sendPackets(new S_ServerMessage(1138));
                } else if (fishY > pc.getY() + rodLength || fishY < pc.getY() - rodLength) {
                    pc.sendPackets(new S_ServerMessage(1138));
                } else if (pc.getInventory().consumeItem(41295, 1)) {
                    pc.sendPackets(new S_Fishing(pc.getId(), L1ActionCodes.ACTION_Fishing, fishX, fishY));
                    Broadcaster.broadcastPacket(pc, new S_Fishing(pc.getId(), L1ActionCodes.ACTION_Fishing, fishX, fishY));
                    pc.setFishing(true);

                    pc.setFishingX(fishX);
                    pc.setFishingY(fishY);

                    FishingTimeScheduler.getInstance().addMember(pc);
                } else {
                    pc.sendPackets(new S_ServerMessage(1137));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(1138));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(1138));
        }
    }

}
