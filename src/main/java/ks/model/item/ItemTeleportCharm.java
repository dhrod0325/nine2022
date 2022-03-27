package ks.model.item;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1ItemDelay;
import ks.model.L1Teleport;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1TeleportUtils;
import ks.util.common.random.RandomUtils;

public class ItemTeleportCharm extends L1ItemInstance {
    public ItemTeleportCharm(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        L1PcInstance pc = (L1PcInstance) cha;

        if (L1ItemDelay.hasItemDelay(pc, this)) {
            return;
        }

        switch (getItemId()) {
            case 6000039:
                teleportGiran(pc);
                break;
            case 6000056:
                teleportMake(pc);
                break;
            case 6000050:
                teleportBugBareRace(pc);
                break;
        }

        L1ItemDelay.onItemUse(pc, this);
    }

    private void teleportMake(L1PcInstance pc) {
        if (pc.isEscapable()) {
            L1Teleport.teleport(pc, 32766, 32830, (short) 610, pc.getHeading(), true);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }

    private void teleportBugBareRace(L1PcInstance pc) {
        if (pc.isEscapable()) {
            int gn4 = RandomUtils.nextInt(3) + 1;

            if (gn4 == 1) {
                L1Teleport.teleport(pc, 33534, 32848, (short) 4, pc.getHeading(), true);
            } else if (gn4 == 2) {
                L1Teleport.teleport(pc, 33536, 32874, (short) 4, pc.getHeading(), true);
            } else if (gn4 == 3) {
                L1Teleport.teleport(pc, 33511, 32854, (short) 4, pc.getHeading(), true);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }

    public void teleportGiran(L1PcInstance pc) {
        if (pc.isEscapable()) {
            L1TeleportUtils.teleportToGiran(pc);
        } else {
            pc.sendPackets(new S_ServerMessage(647));
        }
    }
}
