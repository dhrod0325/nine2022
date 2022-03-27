package ks.packets.clientpackets;

import ks.constants.L1ItemId;
import ks.core.datatables.NpcActionTable;
import ks.core.network.L1Client;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.action.xml.L1NpcAction;
import ks.model.action.xml.L1NpcHtml;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.system.auction.Auction;
import ks.system.auction.AuctionTable;
import ks.util.L1ClanUtils;

import java.util.List;
import java.util.StringTokenizer;

public class C_Amount extends ClientBasePacket {
    public C_Amount(byte[] decrypt, L1Client client) {
        super(decrypt);

        int objectId = readD();
        int amount = readD();

        int c = readC();
        String s = readS();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(objectId);

        if (npc == null) {
            return;
        }

        StringTokenizer st = new StringTokenizer(s);
        String actionName = st.nextToken();

        if (actionName.equalsIgnoreCase("agapply")) {
            try {
                int houseId = Integer.parseInt(st.nextToken());
                buyAgit(pc, houseId);
            } catch (Exception e) {
                logger.error("오류", e);
                pc.sendPackets("오류가 발생했습니다 운영자에게 문의하세요");
            }
        } else {
            L1NpcAction npcAction = NpcActionTable.getInstance().get(s, pc, npc);

            if (npcAction != null) {
                L1NpcHtml result = npcAction.executeWithAmount(s, pc, npc, amount);

                if (result != null) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
                }
            }
        }
    }

    public void buyAgit(L1PcInstance pc, int houseId) {
        if (!pc.isCrown()) {
            pc.sendPackets("당신은 군주가 아닙니다");
            return;
        }

        if (pc.getClanId() == 0) {
            pc.sendPackets("혈맹을 소유하고 있지 않습니다");
            return;
        }

        if (pc.getClan().getLeaderId() != pc.getId()) {
            pc.sendPackets("혈맹의 군주가 아닙니다");
            return;
        }

        if (pc.getClan().getCastleId() != 0) {
            pc.sendPackets("성을 이미 소유중입니다");
            return;
        }

        AuctionTable boardTable = AuctionTable.getInstance();
        Auction auction = boardTable.selectByHouseId(houseId);

        if (auction == null) {
            return;
        }

        int nowPrice = auction.getPrice();

        if ((long) nowPrice <= 0) {
            return;
        }

        if (!pc.getInventory().checkItem(L1ItemId.ADENA, nowPrice)) {
            pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분치 않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
            return;
        }

        if (pc.getInventory().consumeItem(L1ItemId.ADENA, nowPrice)) {
            String pcName = pc.getName();

            List<Auction> allList = boardTable.selectListAll();

            for (Auction board : allList) {
                if (pcName.equalsIgnoreCase(board.getBidder())) {
                    board.setBidder_id(0);
                    board.setBidder("");
                    AuctionTable.getInstance().updateAuctionBoard(board);
                    break;
                }
            }

            pc.sendPackets("아지트를 구매 하였습니다");

            L1Clan clan = pc.getClan();
            clan.setHouseId(houseId);
            L1ClanUtils.updateClan(clan);

            for (L1PcInstance member : clan.getOnlineClanMember()) {
                member.sendPackets("[아지트 지급] : " + auction.getHouse_name());
            }
        }
    }
}
