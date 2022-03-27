package ks.packets.clientpackets;

import ks.constants.L1PacketBoxType;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.WarTimeScheduler;

public class C_BanClan extends ClientBasePacket {
    public C_BanClan(byte[] data, L1Client client) {
        super(data);
        String s = readS();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            int i;
            if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                for (i = 0; i < clan.getClanMemberList().size(); i++) {
                    if (pc.getName().equalsIgnoreCase(s)) {
                        return;
                    }
                }

                int castleId = clan.getCastleId();

                if (castleId != 0 && WarTimeScheduler.getInstance().isNowWar(castleId)) {
                    pc.sendPackets(new S_ServerMessage(439));
                    return;
                }

                L1PcInstance tempPc = L1World.getInstance().getPlayer(s);
                if (tempPc != null) {
                    if (tempPc.getClanId() == pc.getClanId()) {
                        tempPc.clearPlayerClanData();
                        tempPc.save();
                        clan.removeClanMember(tempPc.getName());
                        pc.sendPackets(new S_PacketBox(pc, L1PacketBoxType.PLEDGE_REFRESH_MINUS));
                        tempPc.sendPackets(new S_ServerMessage(238, pc.getClanName()));
                        pc.sendPackets(new S_ServerMessage(240, tempPc.getName()));
                    } else {
                        pc.sendPackets(new S_ServerMessage(109, s));
                    }
                } else {
                    try {
                        L1PcInstance restorePc = CharacterTable.getInstance().restoreCharacter(s);
                        if (restorePc != null && restorePc.getClanId() == pc.getClanId()) {
                            restorePc.clearPlayerClanData();
                            restorePc.save();
                            clan.removeClanMember(restorePc.getName());
                            pc.sendPackets(new S_ServerMessage(240, restorePc.getName()));
                        } else {
                            pc.sendPackets(new S_ServerMessage(109, s));
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            } else {
                pc.sendPackets(new S_ServerMessage(518));
            }
        }
    }
}
