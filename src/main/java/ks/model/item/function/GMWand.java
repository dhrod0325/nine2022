package ks.model.item.function;

import ks.commands.gm.GmCommands;
import ks.constants.L1ActionCodes;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_AttackPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CharPosUtils;

public class GMWand extends L1ItemInstance {
    public GMWand(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            int itemId = this.getItemId();
            int objid = packet.readD();
            int spellX = packet.readH();
            int spellY = packet.readH();

            int heading = L1CharPosUtils.targetDirection(pc, spellX, spellY);

            pc.setHeading(heading);
            pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));

            if (!pc.isGm()) {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            L1Object findObject = L1World.getInstance().findObject(objid);

            if (itemId == 5000686 && findObject == null) {
                pc.sendPackets(new S_SystemMessage("채팅 타켓이 해지되었습니다."));
                return;
            }

            if (itemId == 5000683 && findObject instanceof L1PcInstance) {
                GmCommands.getInstance().handleCommands("정보 " + ((L1PcInstance) findObject).getName());
            } else if (itemId == 5000684 && findObject instanceof L1PcInstance) {
                String param = ((L1PcInstance) findObject).getName() + " 장비";
                GmCommands.getInstance().handleCommands("검사 " + param);
            } else if (itemId == 5000685 && findObject instanceof L1PcInstance) {
                String param = ((L1PcInstance) findObject).getName() + " 계정";
                GmCommands.getInstance().handleCommands("검사 " + param);
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        }
    }

    private String complementClassName(String className) {
        if (className.contains(".")) {
            return className;
        }
        if (className.contains(",")) {
            return className;
        }
        return "ks.commands.gm.command.executor." + className;
    }
}
