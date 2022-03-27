package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.BoardTable;
import ks.core.datatables.HouseTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.*;
import ks.model.board.S_Board;
import ks.model.board.S_BoardRead;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.adenBoard.packet.S_AdenBoard;
import ks.system.adenBoard.packet.S_AdenBoardRead;
import ks.util.L1CommonUtils;

import java.util.List;

public class L1BoardInstance extends L1NpcInstance {
    private static final int MAX_TODAY_BOARD_WRITE_SIZE = 10;

    public L1BoardInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (getNpcId() == CodeConfig.ADEN_BOARD_NPC_ID) {
            String k = "adenBoardNpcId";

            if (!pc.getTimer().isTimeOver(k)) {
                pc.sendPackets(pc.getTimer().remainingSecond(k) + "초 후에 확인하세요");
                return;
            }

            pc.getTimer().setWaitTime(k, 2 * 1000);

            pc.sendPackets(new S_AdenBoard(getId()));
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, CodeConfig.getAdenaClickMent()));
            return;
        }

        if (getNpcId() == 4500200 || getNpcId() == 4500201) {
            String htmlid = null;
            String[] htmldata = null;
            List<L1Object> list = L1World.getInstance().getVisibleObjects(this, 5);

            for (L1Object object : list) {
                if (object == null)
                    continue;
                if (object instanceof L1HousekeeperInstance) {
                    L1HousekeeperInstance keeper = (L1HousekeeperInstance) object;

                    int npcid = keeper.getTemplate().getNpcId();

                    L1House targetHouse = null;

                    for (L1House house : HouseTable.getInstance().getHouseTableList()) {
                        if (house == null)
                            continue;

                        if (npcid == house.getKeeperId()) {
                            targetHouse = house;
                            break;
                        }
                    }

                    if (targetHouse != null) {
                        boolean isOccupy = false;
                        String clanName = null;
                        String leaderName = null;

                        for (L1Clan targetClan : L1World.getInstance().getAllClans()) {
                            if (targetClan != null && targetHouse.getHouseId() == targetClan.getHouseId()) {
                                isOccupy = true;
                                clanName = targetClan.getClanName();
                                leaderName = targetClan.getLeaderName();
                                break;
                            }
                        }

                        if (isOccupy) {
                            htmlid = "agname";
                            htmldata = new String[]{clanName, leaderName, targetHouse.getHouseName()};
                        } else {
                            htmlid = "agnoname";
                            htmldata = new String[]{targetHouse.getHouseName()};
                        }
                    }
                }

                if (htmlid != null) {
                    pc.sendPackets(new S_NPCTalkReturn(getId(), htmlid, htmldata));
                    break;
                }
            }
        } else {
            if (getLevel() == 10) {
                if (!pc.isGm()) {
                    pc.sendPackets("해당 게시판은 운영자만 이용 가능합니다");
                    return;
                }
            }

            if (getNpcId() == 460000181) {
                pc.sendGreenMessage("중계거래 게시판 등록 전 서버규정 필독");
            }

            pc.sendPackets(new S_Board(this, pc, 0));
        }
    }

    public void onPagingClick(L1PcInstance pc, int number) {
        if (getLevel() == 10) {
            if (!pc.isGm()) {
                pc.sendPackets("해당 게시판은 운영자만 이용 가능합니다");
                return;
            }
        }

        if (getNpcId() == CodeConfig.ADEN_BOARD_NPC_ID) {
            pc.sendPackets(new S_AdenBoard(getId(), number));
            return;
        }

        pc.sendPackets(new S_Board(this, pc, number));
    }

    public void onViewClick(L1PcInstance pc, int number) {
        if (getNpcId() == CodeConfig.ADEN_BOARD_NPC_ID) {
            pc.sendPackets(new S_AdenBoardRead(number));
            return;
        }

        if (getLevel() == 10) {
            if (!pc.isGm()) {
                pc.sendPackets("해당 게시판은 운영자만 이용 가능합니다");
                return;
            }
        }

        if (getNpcId() == 42000201) {
            if (!pc.isGm()) {
                pc.sendPackets(new S_SystemMessage("운영자만 가능합니다."));
                return;
            }
        }

        pc.sendPackets(new S_BoardRead(this, number));
    }

    public void onDeleteClick(L1PcInstance pc, int topicNumber) {
        if (getLevel() == 10) {
            if (!pc.isGm()) {
                pc.sendPackets("해당 게시판은 운영자만 이용 가능합니다");
                return;
            }
        }

        if (pc.isGm()) {
            BoardTable.getInstance().deleteTopic(getNpcId(), topicNumber);
        } else {
            pc.sendPackets(new S_SystemMessage("게시판 글은 삭제 불가능 합니다."));
        }
    }

    public void onWrite(L1PcInstance pc, String title, String content, String date) {
        if (title.length() > 10) {
            pc.sendPackets(new S_SystemMessage("게시판 제목 글자수가 초과하였습니다."));
            return;
        }

        if (getNpcId() == 460000058) {
            pc.sendPackets(new S_SystemMessage(".판매신청 명령어를 이용하셔야 합니다"));
            return;
        }

        if (getLevel() == 10) {
            if (!pc.isGm()) {
                pc.sendPackets("해당 게시판은 운영자만 이용 가능합니다");
                return;
            }
        }

        if (getNpcId() == 4500300 || getNpcId() == 42000162 || getNpcId() == 42000163 || getNpcId() == 4200099 || getNpcId() == 42000161) {
            if (!pc.isGm()) {
                pc.sendPackets(new S_SystemMessage("해당 게시판은 운영자만 사용가능합니다."));
                return;
            }
        }

        if (pc.getLevel() < 52 && getNpcId() != 4212014) {
            String chatText = "게시판 글쓰기는 레벨 52이상부터 가능합니다";

            if (!pc.getExcludingList().contains(pc.getName())) {
                pc.sendPackets(new S_ChatPacket(pc, chatText, L1Opcodes.S_OPCODE_NORMALCHAT, 2));
            }

            return;
        }

        int todayWriteSize = BoardTable.getInstance().selectTodayWriteSize(pc, date, getNpcId());

        if (todayWriteSize >= MAX_TODAY_BOARD_WRITE_SIZE) {
            pc.sendPackets(String.format("하루동안 등록 가능한 %d번을 초과하여 게시글을 등록할 수 없습니다.", MAX_TODAY_BOARD_WRITE_SIZE));
            return;
        }

        BoardTable.getInstance().writeTopic(pc, date, title, content, getNpcId());

        if (getNpcId() == 460000181) {
            L1CommonUtils.sendMessageToAllGm("운영자 중계거래 게시판에 새로운 글이 등록되었습니다");
        }

        pc.sendPackets("게시글이 등록되었습니다");
    }
}
