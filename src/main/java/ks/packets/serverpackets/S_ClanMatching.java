package ks.packets.serverpackets;

import ks.core.datatables.pc.CharacterTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Clan;
import ks.model.L1ClanMatching;
import ks.model.L1War;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;

import java.util.ArrayList;
import java.util.Collection;

public class S_ClanMatching extends ServerBasePacket {
    public S_ClanMatching(L1PcInstance pc, int type, int objid, int htype) {
        L1ClanMatching cml = L1ClanMatching.getInstance();

        writeC(L1Opcodes.S_OPCODE_PLEDGE_RECOMMENDATION);
        writeC(type);

        if (type == 2) { // 추천혈맹
            ArrayList<L1ClanMatching.ClanMatchingList> list = new ArrayList<>();

            for (int i = 0; i < cml.getMatchingList().size(); i++) {
                String result = cml.getMatchingList().get(i).clanName;
                if (!pc.getCMAList().contains(result)) {
                    list.add(cml.getMatchingList().get(i));
                }
            }

            int size = list.size();

            writeC(0x00);
            writeC(size); // 갯수.

            for (L1ClanMatching.ClanMatchingList clanMatchingList : list) {
                String clanname = clanMatchingList.clanName;
                String text = clanMatchingList.text;
                int type2 = clanMatchingList.type;

                L1Clan clan = L1World.getInstance().getClan(clanname);
                writeD(clan.getClanId()); // 혈마크
                writeS(clan.getClanName()); // 혈맹 이름.
                writeS(clan.getLeaderName()); // 군주이름
                writeD(clan.getOnlineMaxUser()); // 혈맹 규모 : 주간 최대 접속자 수
                writeC(type2); // 0: 사냥, 1: 전투, 2: 친목

                if (clan.getHouseId() != 0) writeC(0x01); // 아지트 0: X , 1: O
                else writeC(0x00);

                boolean inWar = false;
                Collection<L1War> warList = L1World.getInstance().getWarList(); // 전쟁 리스트를 취득
                for (L1War war : warList) {
                    if (war.checkClanInWar(clanname)) { // 자크란이 이미 전쟁중
                        inWar = true;
                        break;
                    }
                }

                if (inWar) writeC(0x01); // 전쟁 상태	0: X , 1: O
                else writeC(0x00);
                writeC(0x00); // 고정값.
                writeS(text);// 소개멘트.
                writeD(clan.getClanId()); // 혈맹 objid
            }
            list.clear();
        } else if (type == 3) { // 신청목록
            int size = pc.getCMAList().size();
            writeC(0x00);
            writeC(size); // 갯수.

            for (int i = 0; i < size; i++) {
                String clanname = pc.getCMAList().get(i);
                String text = cml.getClanMatchingList(clanname).text;
                int type2 = cml.getClanMatchingList(clanname).type;
                L1Clan clan = L1World.getInstance().getClan(clanname);
                writeD(clan.getClanId()); // 삭제 누를때 뜨는 obj값
                writeC(0x00);
                writeD(clan.getClanId()); // 혈마크.
                writeS(clan.getClanName()); // 혈맹 이름.
                writeS(clan.getLeaderName()); // 군주이름
                writeD(clan.getOnlineMaxUser()); // 혈맹 규모 : 주간 최대 접속자 수
                writeC(type2); // 0: 사냥, 1: 전투, 2: 친목

                if (clan.getHouseId() != 0) writeC(0x01); // 아지트 0: X , 1: O
                else writeC(0x00);

                boolean inWar = false;
                Collection<L1War> warList = L1World.getInstance().getWarList(); // 전쟁 리스트를 취득
                for (L1War war : warList) {
                    if (war.checkClanInWar(clanname)) { // 자크란이 이미 전쟁중
                        inWar = true;
                        break;
                    }
                }

                if (inWar) writeC(0x01); // 전쟁 상태	0: X , 1: O
                else writeC(0x00);
                writeC(0x00); // 고정값.
                writeS(text);// 소개멘트.
                writeD(clan.getClanId()); // 혈맹 objid
            }
        } else if (type == 4) { // 요청목록

            if (!cml.isClanMatchingList(pc.getClanName())) {
                writeC(0x82); // 요청 목록이 없을땐 이것만 날린다.
            } else {
                int size = pc.getCMAList().size();

                writeC(0x00);
                writeC(0x02);
                writeC(0x00);// 고정
                writeC(size); // size

                for (int i = 0; i < size; i++) {
                    String username = pc.getCMAList().get(i);
                    L1PcInstance user = L1World.getInstance().getPlayer(username);

                    if (user == null) {
                        try {
                            user = CharacterTable.getInstance().restoreCharacter(username);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (user == null) {
                        return;
                    }

                    writeD(user.getId()); // 신청자의 objectid
                    writeC(0x00);
                    writeC(user.getOnlineStatus()); // 0x01:접속,  0x00:비접속
                    writeS(username); // 신청자의 이름.
                    writeC(user.getType()); // 캐릭터 클래스
                    writeH(user.getLawful()); // 라우풀
                    writeC(user.getLevel()); // 레벨
                    writeC(0x01); // 이름앞에 나오는 풀잎의 변경
                }
            }
        } else if (type == 5 || type == 6) {
            writeC(0x00);
            writeD(objid);
            writeC(htype);
        }
    }
}
