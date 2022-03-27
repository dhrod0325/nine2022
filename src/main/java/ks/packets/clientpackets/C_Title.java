package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.core.network.L1Client;
import ks.model.Broadcaster;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CharTitle;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;

public class C_Title extends ClientBasePacket {
    public C_Title(byte[] data, L1Client clientthread) {
        super(data);

        L1PcInstance pc = clientthread.getActiveChar();

        if (pc == null) {
            return;
        }

        String charName = readS();
        String title = readS();
        if (title.length() > 16) {
            pc.sendPackets(new S_SystemMessage("호칭으로 쓸 수 있는 글자수를 초과하였습니다."));
            return;
        }

        if (charName.isEmpty() || title.isEmpty()) {
            pc.sendPackets(new S_ServerMessage(196));
            return;
        }

        L1PcInstance target = L1World.getInstance().getPlayer(charName);
        if (target == null) {
            return;
        }

        if (pc.isGm()) {
            changeTitle(target, title);
            return;
        }

        if (isClanLeader(pc)) { // 혈맹주
            if (pc.getId() == target.getId()) {
                if (pc.getLevel() < 10) {
                    pc.sendPackets(new S_ServerMessage(197));
                    return;
                }
                changeTitle(pc, title);
            } else {
                if (pc.getClanId() != target.getClanId()) {
                    pc.sendPackets(new S_ServerMessage(199));
                    return;
                }
                if (target.getLevel() < 10) {
                    pc.sendPackets(new S_ServerMessage(202, charName));
                    return;
                }
                changeTitle(target, title);
                L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                if (clan != null) {
                    for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
                        clanPc.sendPackets(new S_ServerMessage(203, pc.getName(), charName, title));
                    }
                }
            }
        } else if (pc.getClanRank() == 3) {
            if (pc.getId() == target.getId()) { // 자신
                if (pc.getLevel() < 10) {
                    pc.sendPackets(new S_ServerMessage(197));
                    return;
                }
                changeTitle(pc, title);
            } else {
                if (pc.getClanId() != target.getClanId()) {
                    pc.sendPackets(new S_ServerMessage(199));
                    return;
                }
                if (target.getLevel() < 10) {
                    pc.sendPackets(new S_ServerMessage(202, charName));
                    return;
                }
                changeTitle(target, title);
                L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                if (clan != null) {
                    for (L1PcInstance clanPc : clan.getOnlineClanMember()) {
                        clanPc.sendPackets(new S_ServerMessage(203, pc.getName(), charName, title));
                    }
                }
            }
        } else {
            if (pc.getId() == target.getId()) {
                if (pc.getClanId() != 0 && !CodeConfig.CHANGE_TITLE_BY_ONESELF) {
                    pc.sendPackets(new S_ServerMessage(198));
                    return;
                }
                if (target.getLevel() < 55) {
                    pc.sendPackets(new S_SystemMessage("초보가 아닌데 호칭을 가지려면 , 레벨 55이상이 아니면 안됩니다."));
                    return;
                }

                changeTitle(pc, title);
            } else { // 타인
                if (pc.isCrown()) {
                    if (pc.getClanId() == target.getClanId()) {
                        pc.sendPackets(new S_ServerMessage(201, pc.getClanName()));
                    }
                }
            }
        }
    }

    private void changeTitle(L1PcInstance pc, String title) {
        int objectId = pc.getId();
        pc.setTitle(title);
        pc.sendPackets(new S_CharTitle(objectId, title));
        Broadcaster.broadcastPacket(pc, new S_CharTitle(objectId, title));
        try {
            pc.save(); // DB에 캐릭터 정보를 써 우
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private boolean isClanLeader(L1PcInstance pc) {
        boolean isClanLeader = false;
        if (pc.getClanId() != 0) { // 크란 소속
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
            if (clan != null) {
                if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                    isClanLeader = true;
                }
            }
        }
        return isClanLeader;
    }
}
//