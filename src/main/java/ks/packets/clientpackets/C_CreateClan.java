package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.core.datatables.clan.ClanTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class C_CreateClan extends ClientBasePacket {
    public C_CreateClan(byte[] data, L1Client client) throws Exception {
        super(data);

        String s = readS();

        int numOfNameBytes = s.getBytes("EUC-KR").length;

        L1PcInstance pc = client.getActiveChar();

        if (pc.isCrown()) {
            if (pc.getClanId() == 0) {
                for (int o = 0; o < s.length(); o++) {
                    if (s.charAt(o) == '/' || s.charAt(o) == ' ') { // 특수문자가 아니라면..
                        return;
                    }
                }

                if (!pc.getInventory().checkItem(L1ItemId.ADENA, CodeConfig.CREATE_CLAN_PRICE)) {
                    pc.sendPackets(new S_ServerMessage(337, "$4"));
                    return;
                }

                if (s.length() > 1000) {
                    logger.warn("패킷렉공격 ID :" + pc.getName() + ", 패킷렉공격 IP :" + client.getIp());
                    client.disconnect();
                    return;
                }

                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ' || s.charAt(i) == 'ㅤ') {
                        pc.sendPackets(new S_ServerMessage(53));
                        return;
                    }
                }

                if (8 < (numOfNameBytes - s.length()) || 16 < numOfNameBytes) {
                    pc.sendPackets(new S_ServerMessage(98));
                }

                for (L1Clan clan : L1World.getInstance().getAllClans()) {
                    if (clan.getClanName().equalsIgnoreCase(s)) {
                        pc.sendPackets(new S_ServerMessage(99));
                        return;
                    }
                }

                L1Clan clan = ClanTable.getInstance().createClan(pc, s);

                pc.getInventory().consumeItem(L1ItemId.ADENA, CodeConfig.CREATE_CLAN_PRICE);

                if (clan != null) {
                    pc.sendPackets(new S_ServerMessage(84, s));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(86));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(85));
        }
    }
}

