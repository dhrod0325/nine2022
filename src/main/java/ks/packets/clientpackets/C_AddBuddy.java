package ks.packets.clientpackets;

import ks.core.datatables.BuddyTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.L1Client;
import ks.model.L1Buddy;
import ks.model.L1CharName;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.system.robot.is.L1RobotInstance;

public class C_AddBuddy extends ClientBasePacket {
    public C_AddBuddy(byte[] decrypt, L1Client client) {
        super(decrypt);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null || pc instanceof L1RobotInstance) {
            return;
        }

        BuddyTable buddyTable = BuddyTable.getInstance();
        L1Buddy buddyList = buddyTable.getBuddyTable(pc.getId());
        String charName = readS();

        if ("메티스".equalsIgnoreCase(charName) || "미소피아".equalsIgnoreCase(charName)) {
            if (!pc.isGm()) {
                pc.sendPackets("운영자는 친구로 등록할수 없습니다");
                return;
            }
        }

        if (charName.equalsIgnoreCase(pc.getName())) {
            pc.sendPackets("자기자신을 친구로 등록할수 없습니다");
            return;
        } else if (buddyList.contains(charName)) {
            pc.sendPackets(charName + "은(는) 이미 등록되어 있습니다");
            return;
        }

        L1CharName cn = CharacterTable.getInstance().selectCharNameByName(charName);

        if (cn != null) {
            buddyList.add(cn.getId(), cn.getName());
            buddyTable.addBuddy(pc.getId(), cn.getId(), cn.getName());
        } else {
            pc.sendPackets(new S_ServerMessage(109, charName));
        }
    }
}
