package ks.packets.serverpackets;

import ks.core.datatables.clan.ClanTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Clan;
import ks.model.L1ClanMember;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;

import java.util.List;

import static ks.constants.L1PacketBoxType.*;

public class S_PacketBox extends ServerBasePacket {
    int count = 0;

    private int subCode;
    private String msg;

    public S_PacketBox(int subCode) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case MSG_WAR_INITIATIVE:
            case MSG_WAR_OCCUPY:
            case MSG_MARRIED:
            case MSG_FEEL_GOOD:
            case MSG_CANT_LOGOUT:
            case LOGOUT:
            case ICON_SECURITY_SERVICE:  //추가
            case ICON_PC_BANG:  //추가
                break;
            case DEL_ICON:
                writeH(0);
                break;
            case ICON_AURA:
                writeC(0x98);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                writeC(0);
                break;
            case MINIGAME_10SECOND_COUNT:
                writeC(10);
                writeC(109);
                writeC(85);
                writeC(208);
                writeC(2);
                writeC(220);
                break;
            case MINIGAME_END:
                writeC(147);
                writeC(92);
                writeC(151);
                writeC(220);
                writeC(42);
                writeC(74);
                break;
            case MINIGAME_START_COUNT:
                writeC(5);
                writeC(129);
                writeC(252);
                writeC(125);
                writeC(110);
                writeC(17);
                break;
            default:
                break;
        }
    }

    public S_PacketBox(int subCode, int value) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case 204: // 콤보시스템
            case MSG_WAR_BEGIN:
            case MSG_WAR_END:
            case MSG_WAR_GOING:
                writeC(value);
                writeH(0);
                break;
            case AMETYHST:
                writeC(0x81);
                writeH(257);
                writeH(value * 60);
                break;
            case ICON_BLUEPOTION:
            case ICON_CHATBAN:
            case ICON_I2H:
            case ICON_POLYMORPH:
            case MINIGAME_TIME:
            case BOOKMARK_SIZE_PLUS_10:
            case MSG_ELF:
            case MSG_RANK_CHANGED:
            case MSG_COLOSSEUM:
            case SPOT:
            case ER_UPUDATE:
            case MSG_SMS_SENT:
            case WEIGHT:
            case FOOD:
            case UPDATE_DG:
                writeC(value);
                break;
            case DODGE:// UI DG표시
            case MAP_TIMER:
                writeH(value); // time
                break;
            case KARMA: // 우호도표기추가
                writeD(value);
                break;
            case MSG_LEVEL_OVER:
                writeC(0); // ?
                writeC(value); // 0-49이외는 표시되지 않는다
                break;
            case COOK_WINDOW:
                writeC(0xdb); // ?
                writeC(0x31);
                writeC(0xdf);
                writeC(0x02);
                writeC(0x01);
                writeC(value); // level
                break;
            case AINHASAD:
                value /= 10000;
                writeD(value);// % 수치 1~200
            case HADIN_DISPLAY://인던이펙트
                writeC(value);
                break;
            default:
                break;
        }
    }

    public S_PacketBox(int subCode, int type, int time) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case ROUND:
            case MSG_DUEL:
                writeD(type);
                writeD(time);
                break;
            case DRAGONPERL:// 드래곤진주
            case EXP_POTION2:
            case EXP_POTION3:
                writeC(time);
                writeC(type);
                break;
            case ACCOUNT_TIME:
                writeD(time);
                writeC(type);
            case ICON_COOKING:
                if (type != 7) {
                    writeC(0x0c);
                    writeC(0x0c);
                    writeC(0x0c);
                    writeC(0x12);
                    writeC(0x0c);
                    writeC(0x09);
                    writeC(0x00);
                    writeC(0x00);
                    writeC(type);
                    writeC(0x24);
                    writeH(time);
                    writeH(0x00);
                } else {
                    writeC(0x0c);
                    writeC(0x0c);
                    writeC(0x0c);
                    writeC(0x12);
                    writeC(0x0c);
                    writeC(0x09);
                    writeC(0xc8);
                    writeC(0x00);
                    writeC(type);
                    writeC(0x26);
                    writeH(time);
                    writeC(0x3e);
                    writeC(0x87);
                }
                break;
            case ICON_AURA:
                writeC(0xdd);
                writeH(time);
                writeC(type);
                break;
            case EMERALD_EVA://드래곤에메랄드
                writeC(0x70);
                writeC(1);
                writeC(type);
                writeH(time);
                break;
            case 20:
                writeH(type);
            case UNLIMITED_ICON:
            case UNLIMITED_ICON1:
                writeC(type);
                writeD(time);
                writeD(0x00000D67);
                writeH(0x00);
                break;
            default:
                writeC(type);
                writeD(time);
                break;
        }
    }

    public S_PacketBox(int subCode, int type, boolean show) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case UNLIMITED_ICON:
                writeC(show ? 0x01 : 0x00);
                writeC(type);
                break;
            case UNLIMITED_ICON1:
                writeC(show ? 0x01 : 0x00);
                writeD(type);
                writeD(0);
                writeH(0);
                break;
            case BAPO:
                writeD(type);
                writeD(show ? 0x01 : 0x00);
            default:
                break;
        }
    }


    public S_PacketBox(int subCode, L1ItemInstance item, int type) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        if (subCode == ITEM_STATUS) {
            writeD(item.getId());
            writeH(type);
        }
    }

    public S_PacketBox(int subCode, int type, int petId, int ac) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        if (subCode == PET_ITEM) {
            writeC(type);
            writeD(petId); // pet objid
            writeH(ac);
        }
    }

    public S_PacketBox(int subCode, String name) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case ADD_EXCLUDE:
            case REM_EXCLUDE:
            case MSG_TOWN_LEADER:
            case HTML_PLEDGE_REALEASE_ANNOUNCE:
                writeS(name);
                break;
            case PLAYSOUND:
                writeH(Integer.parseInt(name));
                break;
            case GREEN_MESSAGE:
                writeC(2);
                writeS(name);
                break;
            default:
                break;
        }

        this.subCode = subCode;
        this.msg = name;
    }

    public S_PacketBox(int subCode, int rank, String name) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        if (subCode == MSG_RANK_CHANGED) {
            writeC(rank);
            writeS(name);
        }
    }

    public S_PacketBox(int subCode, Object[] names) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case ADD_EXCLUDE2:
                writeC(names.length);

                for (Object name : names) {
                    writeS(name.toString());
                }
                break;
            case HTML_PLEDGE_ONLINE_MEMBERS:
                writeH(names.length);
                for (Object name : names) {
                    L1PcInstance pc = (L1PcInstance) name;
                    writeS(pc.getName());
                    writeC(0x00);
                }
                break;
            default:
                break;
        }
    }

    public S_PacketBox(L1PcInstance pc, int subCode) {
        String clanName = pc.getClanName();
        L1Clan clan = L1World.getInstance().getClan(clanName);

        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(subCode);

        switch (subCode) {
            case CLAN_WAREHOUSE_LIST:
                SqlUtils.query("SELECT id, time FROM clan_warehouse_log WHERE clan_name=?", (rs1, i) -> {
                    if (System.currentTimeMillis() - rs1.getTimestamp(2).getTime() > 4320000) {
                        int id = rs1.getInt(1);
                        SqlUtils.update("DELETE FROM clan_warehouse_log WHERE id=?", id);
                    } else {
                        count++;
                    }

                    return null;
                }, pc.getClanName());

                writeD(count);
                count = 0;
                SqlUtils.query("SELECT name, item_name, item_count, type, time FROM clan_warehouse_log WHERE clan_name=?", (rs, i) -> {
                    writeS(rs.getString(1));
                    writeC(rs.getInt(4));// 0:맡김 1:찾음
                    writeS(rs.getString(2));
                    writeD(rs.getInt(3));
                    writeD((int) (System.currentTimeMillis() - rs.getTimestamp(5).getTime()) / 60000);// 경과시간 분

                    return null;
                }, pc.getClanName());

                break;
            case PLEDGE_ONE:
                writeD(clan.getOnlineMemberCount());
                for (L1PcInstance targetPc : clan.getOnlineClanMember()) {
                    writeS(targetPc.getName());
                    writeC(targetPc.getClanRank());
                }
                writeD((int) (System.currentTimeMillis() / 1000L));
                writeS(clan.getLeaderName());
                break;
            case PLEDGE_TWO:
                writeD(clan.getClanMemberList().size());

                List<L1ClanMember> clanMemberList = clan.getClanMemberList();

                for (L1ClanMember member : clanMemberList) {
                    writeS(member.name);
                    writeC(member.rank);
                }

                writeD(clan.getOnlineMemberCount());

                for (L1PcInstance targetPc : clan.getOnlineClanMember()) {
                    writeS(targetPc.getName());
                }
                break;
            case PLEDGE_REFRESH_PLUS:
            case PLEDGE_REFRESH_MINUS:
                writeS(pc.getName());
                writeC(pc.getClanRank());
                writeH(0);
                break;
            case KARMA:
                writeD(pc.getKarma());
                writeH(0); // 필요할까?
                break;
            case 드래곤포탈선택:
                StringBuilder sb = new StringBuilder();

                List<Integer> clanList = ClanTable.getInstance().selectClanIdListByAllience(pc.getClan().getAlliance());

                for (int clanId : clanList) {
                    L1Clan c = L1World.getInstance().getClan(clanId);

                    if (c == null)
                        continue;

                    if (pc.getClanId() == clanId) {
                        continue;
                    }

                    sb.append(c.getClanName()).append(" ");
                }

                writeS(sb.toString());

                break;
            default:
                break;
        }
    }

    public int getSubCode() {
        return subCode;
    }

    public String getMsg() {
        return msg;
    }
}
