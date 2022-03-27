package ks.packets.clientpackets;

import ks.constants.L1PacketBoxType;
import ks.core.datatables.exclude.CharacterExclude;
import ks.core.datatables.exclude.CharacterExcludeTable;
import ks.core.network.L1Client;
import ks.model.L1ExcludingList;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;

public class C_Exclude extends ClientBasePacket {
    public C_Exclude(byte[] data, L1Client client) {
        super(data);

        String name = readS();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1ExcludingList exList = pc.getExcludingList();

        if (name.isEmpty()) {
            for (String n : exList.getNameList()) {
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.REM_EXCLUDE, n));
            }

            for (String n : exList.getNameList()) {
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.ADD_EXCLUDE, n));
            }
            return;
        }

        try {
            if (exList.contains(name)) {
                String temp = exList.remove(name);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.REM_EXCLUDE, temp));
                CharacterExcludeTable.getInstance().delete(new CharacterExclude(pc.getId(), name));
            } else {
                if (exList.isFull()) {
                    pc.sendPackets(new S_ServerMessage(472));
                    return;
                }

                exList.add(name);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.ADD_EXCLUDE, name));
                CharacterExcludeTable.getInstance().insert(new CharacterExclude(pc.getId(), name));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
