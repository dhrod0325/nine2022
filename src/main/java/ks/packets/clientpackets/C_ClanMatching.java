package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ClanMatching;
import ks.packets.serverpackets.S_SystemMessage;

public class C_ClanMatching extends ClientBasePacket {
    public C_ClanMatching(byte[] decrypt, L1Client client) {
        super(decrypt);

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }


        int type = readC();
        int objid = 0;
        int htype = 0;

        if (type == 0) {
            L1ClanMatching cml = L1ClanMatching.getInstance();

            htype = readC();
            String text = readS();

            if (!cml.isClanMatchingList(pc.getClanName())) {
                cml.writeClanMatching(pc.getClanName(), text, htype);
            } else {
                cml.updateClanMatching(pc.getClanName(), text, htype);
            }
        } else if (type == 1) {
            L1ClanMatching cml = L1ClanMatching.getInstance();
            if (cml.isClanMatchingList(pc.getClanName())) {
                cml.deleteClanMatching(pc);
            }
        } else if (type == 4) {
            L1ClanMatching cml = L1ClanMatching.getInstance();
            if (pc.getClanId() == 0) {
                if (!pc.isCrown()) {
                    cml.loadClanMatchingApcList_User(pc);
                }
            } else {
                switch (pc.getClanRank()) {
                    case 3:
                    case 10:
                    case 9: // 부군주, 혈맹군주, 수호기사
                        cml.loadClanMatchingApcList_Crown(pc);
                        break;
                }
            }

        } else if (type == 5) {
            objid = readD();
            L1Clan clan = getClan(objid);
            if (clan != null && !pc.getCMAList().contains(clan.getClanName())) {
                L1ClanMatching cml = L1ClanMatching.getInstance();
                cml.writeClanMatchingApcList_User(pc, clan);
            }
        } else if (type == 6) {
            objid = readD();
            htype = readC(); // 1: 승인, 2: 거절, 3: 삭제
            L1ClanMatching cml = L1ClanMatching.getInstance();
            if (htype == 1) {
                L1Object target = L1World.getInstance().findObject(objid);
                if (target != null & target instanceof L1PcInstance) {
                    L1PcInstance user = (L1PcInstance) target;
                    if (!pc.getCMAList().contains(user.getName())) {
                        pc.sendPackets(new S_SystemMessage("신청을 취소한 유저입니다."));
                    } else {
                        if (L1ClanJoin.getInstance().clanJoin(pc, user)) {
                            cml.deleteClanMatchingApcList(user);
                        }
                    }
                } else if (target == null) {
                    pc.sendPackets(new S_SystemMessage("비접속중인 유저 입니다."));
                }
            } else if (htype == 2) {
                L1Object target = L1World.getInstance().findObject(objid);
                if (target != null) {
                    if (target instanceof L1PcInstance) {
                        L1PcInstance user = (L1PcInstance) target;
                        user.removeCMAList(pc.getName());
                        pc.removeCMAList(user.getName());
                        cml.deleteClanMatchingApcList(user, user.getId(), pc.getClan());
                    }
                } else {
                    cml.deleteClanMatchingApcList(null, objid, pc.getClan());
                }
            } else if (htype == 3) {
                L1Clan clan = getClan(objid);
                if (clan != null && pc.getCMAList().contains(clan.getClanName())) {
                    cml.deleteClanMatchingApcList(pc, clan);
                }
            }
        }

        pc.sendPackets(new S_ClanMatching(pc, type, objid, htype));
    }

    private L1Clan getClan(int objid) {
        L1Clan clan = null;
        for (L1Clan c : L1World.getInstance().getAllClans()) {
            if (c.getClanId() == objid) {
                clan = c;
                break;
            }
        }

        return clan;
    }
}
