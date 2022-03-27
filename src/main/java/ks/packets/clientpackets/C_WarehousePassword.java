package ks.packets.clientpackets;

import ks.core.datatables.account.AccountTable;
import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_RetrieveList;
import ks.packets.serverpackets.S_ServerMessage;

public class C_WarehousePassword extends ClientBasePacket {
    public C_WarehousePassword(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        int gamepassword = client.getAccount().getGamePassword();

        int type = readC();

        if (type == 0) {
            int oldpass = readCH();

            readC();

            int newpass = readCH();

            if (gamepassword == 0 || gamepassword == oldpass) {
                AccountTable.getInstance().setGamePassword(client.getAccount(), newpass);
            } else {
                pc.sendPackets(new S_ServerMessage(835));
            }
        } else if (type == 1) {
            int chkpass = readCH();
            readC();
            int objId = readD();

            if (gamepassword == 0 || gamepassword == chkpass) {
                if (pc.getLevel() >= 5) {
                    pc.sendPackets(new S_RetrieveList(objId, pc));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(835));
            }
        }
    }
}