package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_FixWeaponList;

public class C_FixWeaponList extends ClientBasePacket {
    public C_FixWeaponList(byte[] data, L1Client clientthread) {
        super(data);
        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }
        pc.sendPackets(new S_FixWeaponList(pc));
    }

}
