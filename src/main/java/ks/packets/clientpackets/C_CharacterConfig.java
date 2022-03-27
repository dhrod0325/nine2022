package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.CharacterConfigTable;
import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_CharacterConfig extends ClientBasePacket {
    public C_CharacterConfig(byte[] bytes, L1Client client) {
        super(bytes);

        if (client == null) {
            return;
        }

        if (CodeConfig.CHARACTER_CONFIG_IN_SERVER_SIDE) {
            L1PcInstance pc = client.getActiveChar();

            if (pc == null) return;

            int length = readD() - 3;
            byte[] data = readByte();

            int count = CharacterConfigTable.getInstance().countCharacterConfig(pc.getId());

            if (count == 0) {
                CharacterConfigTable.getInstance().storeCharacterConfig(pc.getId(), length, data);
            } else {
                CharacterConfigTable.getInstance().updateCharacterConfig(pc.getId(), length, data);
            }
        }
    }
}