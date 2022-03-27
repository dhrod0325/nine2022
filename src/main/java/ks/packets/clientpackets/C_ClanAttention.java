package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ClanAttention;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;

import java.io.File;

public class C_ClanAttention extends ClientBasePacket {
    public C_ClanAttention(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        int type = readC();
        //0 혈맹추가, 1 목록삭제, 2 혈맹목록

        switch (type) {
            case 0: {
                String pcClanName = pc.getClanName();
                String targetClanName = readS();
                L1Clan clan = L1World.getInstance().getClan(pcClanName);
                if (clan == null) { // 자크란이 발견되지 않는다
                    pc.sendPackets(new S_SystemMessage("\\aG먼저 혈맹을 창설하시길 바랍니다."));
                    return;
                }

                if (pcClanName.equalsIgnoreCase(targetClanName)) { // 자크란을 지정
                    pc.sendPackets(new S_SystemMessage("\\aG자신의 클랜에 주시를 할 수 없습니다."));
                    return;
                }

                for (int i = 0; i < clan.getGazeList().size(); i++) {
                    if (clan.getGazeList().get(i).equalsIgnoreCase(targetClanName)) {
                        pc.sendPackets(new S_SystemMessage("\\aG이미 상대혈맹과 주시를 하고 있습니다."));
                        return;
                    }
                }

                if (clan.getGazeList().size() >= 5) {
                    pc.sendPackets(new S_SystemMessage("\\aG문장주시는 최대 5개 혈맹에만 가능합니다."));
                    return;
                }

                L1Clan targetClan = L1World.getInstance().getClan(targetClanName);

                if (targetClan == null) { // 상대 크란이 발견되지 않았다
                    pc.sendPackets(new S_SystemMessage("\\aG상대혈맹이 존재하지 않습니다."));
                    return;
                }

                File file = new File("data/emblem/" + clan.getEmblemId());

                if (!file.exists()) {
                    pc.sendPackets(new S_SystemMessage("혈마크 없이는 문장주시를 요청 할 수 없습니다."));
                    return;
                }

                file = new File("data/emblem/" + targetClan.getEmblemId());
                if (!file.exists()) {
                    pc.sendPackets(new S_SystemMessage("상대혈맹에 혈마크가 없습니다."));
                    return;
                }

                L1PcInstance target = L1World.getInstance().getPlayer(targetClan.getLeaderName());

                if (target != null) {
                    pc.sendPackets(new S_SystemMessage("혈맹 주시: 요청중입니다. 기다려주세요."));
                    target.setTempID(pc.getId());
                    target.sendPackets(new S_Message_YN(3348, pc.getClanName()));// %0 혈맹의 문장 주시를 승낙 하시겠습니까?
                } else {
                    pc.sendPackets(new S_ServerMessage(3349));// 문장 주시 불가, 없는 혈맹 혹은 연합혈맹 이거나 군주가 오프라인 상태
                }
            }


            break;
            case 1: {
                String targetClanName2 = readS();
                if (!pc.isCrown()) {
                    pc.sendPackets(new S_SystemMessage("\\aG군주만이 문장주시를 해제할 수 있습니다."));
                    return;
                }

                L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
                if (clan == null) { // 자크란이 발견되지 않는다
                    return;
                }

                L1Clan targetClan = L1World.getInstance().getClan(targetClanName2);

                if (targetClan == null) { // 상대 크란이 발견되지 않았다
                    pc.sendPackets(new S_SystemMessage("\\aG상대혈맹이 존재하지 않습니다."));
                    return;
                }

                //주시 리스트에서 삭제
                clan.removeGazelist(targetClan.getClanName());
                targetClan.removeGazelist(clan.getClanName());

                //문장주시 리스트 업데이트
                for (L1PcInstance member : clan.getOnlineClanMember()) {
                    member.sendPackets(new S_ClanAttention(clan.getGazeSize(), clan.getGazeList()));
                }

                for (L1PcInstance member : targetClan.getOnlineClanMember()) {
                    member.sendPackets(new S_ClanAttention(targetClan.getGazeSize(), targetClan.getGazeList()));
                }
            }

            break;
            case 2: // 문장주시 혈맹 목록

                break;
        }
    }
}
