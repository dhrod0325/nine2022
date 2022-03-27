package ks.packets.clientpackets;

import ks.constants.L1PacketBoxType;
import ks.core.datatables.clan.ClanTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.bookMark.L1BookMark;
import ks.model.bookMark.L1BookMarkTable;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SendLocation;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class C_Report extends ClientBasePacket {
    public static final int MAP_SEND = 11;
    public static final int DRAGON_PORTAL = 6;
    public static final int MONSTER_KILL = 44;
    public static final int WEB = 19;
    public static final int UNKNOWN_13 = 13;
    public static final int TELEPORT = 48;
    public static final int EMBLEM_STATUS = 46;
    public static final int BOOKMARK_SAVE = 34;
    public static final int BOOKMARK_COLOR = 39;
    private static final Logger logger = LogManager.getLogger(C_Report.class.getName());

    public C_Report(byte[] data, L1Client client) {
        super(data);

        if (client == null) {
            return;
        }

        int type = readC();

        logger.debug("type:{}", type);

        if (type == UNKNOWN_13) {
            return;
        }

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        switch (type) {
            case EMBLEM_STATUS:
                if (pc.getClanRank() != 4 && pc.getClanRank() != 10) {
                    return;
                }

                int emblemStatus = readC(); // 0: 關閉 1:開啟

                L1Clan clan = pc.getClan();
                clan.setEmblemStatus(emblemStatus);
                ClanTable.getInstance().updateClan(clan);

                for (L1PcInstance member : clan.getOnlineClanMember()) {
                    member.sendPackets(new S_PacketBox(L1PacketBoxType.PLEDGE_EMBLEM_STATUS, emblemStatus));
                }

                break;
            case BOOKMARK_COLOR:
                int sizeColor = readD();

                for (int i = 0; i < sizeColor; i++) {
                    int id = 0;
                    int numId = readD();

                    for (L1BookMark book : pc.getBookMark().getBookMarkList()) {
                        if (book.getNumId() == numId) {
                            id = book.getId();
                        }
                    }

                    String name = readS();

                    SqlUtils.update("UPDATE character_teleport SET name=? WHERE id=?", name, id);
                }

                break;
            case BOOKMARK_SAVE:
                readC();

                List<L1BookMark> list = pc.getBookMark().getBookMarkList();

                for (L1BookMark bookMark : list) {
                    int num = readC();

                    bookMark.setSpeedId(-1);
                    bookMark.setTempId(num);
                }

                pc.getBookMark().getSpeedBookmarkList().clear();

                for (int i = 0; i < 5; i++) {
                    int num = readC();

                    if (num >= list.size()) {
                        continue;
                    }

                    try {
                        L1BookMark speed = pc.getBookMark().findByTempId(num);
                        speed.setSpeedId(i);

                        pc.getBookMark().addSpeedBookMark(speed);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }

                L1BookMarkTable.update(pc);

                break;
            case MONSTER_KILL:
                pc.setMonsterKill(0);
                break;
            case MAP_SEND:
                String name = readS();
                int mapId = readH();
                int x = readH();
                int y = readH();
                int msgId = readC();

                if (name.isEmpty()) {
                    return;
                }
                L1PcInstance target = L1World.getInstance().getPlayer(name);
                if (target != null) {
                    String sender = pc.getName();
                    target.sendPackets(new S_SendLocation(sender, mapId, x, y, msgId));
                    pc.sendPackets(new S_ServerMessage(1783, name));
                }
                break;
            case DRAGON_PORTAL:
//                int objectId = readD();
//                int gate = readD();
//                int dragonGate[] = {81273, 81274, 81275, 81276};
//
//                if (gate >= 0 && gate <= 3) {
//                    Calendar nowTime = Calendar.getInstance();
//                    if (nowTime.get(Calendar.HOUR_OF_DAY) >= 8 && nowTime.get(Calendar.HOUR_OF_DAY) < 12) {
//                        pc.sendPackets(new S_ServerMessage(1643));
//                    } else {
//                        boolean limit = true;
//                        switch (gate) {
//                            case 0:
//                                for (int i = 0; i < 6; i++) {
//                                    if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
//                                        limit = false;
//                                    }
//                                }
//                                break;
//                            case 1:
//                                for (int i = 6; i < 12; i++) {
//                                    if (!L1DragonSlayer.getInstance().getPortalNumber()[i]) {
//                                        limit = false;
//                                    }
//                                }
//                                break;
//                        }
//                        if (!limit) { // 未達上限可開設龍門
//                            if (!pc.getInventory().consumeItem(47010, 1)) {
//                                pc.sendPackets(new S_ServerMessage(1567)); // 需要龍之鑰匙。
//                                return;
//                            }
//                            L1SpawnUtils.spawn(pc, dragonGate[gate], 0, 120 * 60 * 1000); // 開啟 2 小時
//                        }
//                    }
//                }

                break;
            case WEB:
            case TELEPORT:
                break;
        }
    }
}

