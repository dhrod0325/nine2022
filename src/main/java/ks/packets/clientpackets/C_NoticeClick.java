package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1PacketBoxType;
import ks.core.datatables.account.AccountTable;
import ks.core.datatables.notice.NoticeTable;
import ks.core.datatables.pc.CharacterTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.packets.serverpackets.S_CharAmount;
import ks.packets.serverpackets.S_CharPacks;
import ks.packets.serverpackets.S_Notice;
import ks.packets.serverpackets.S_PacketBox;
import ks.util.common.DateUtils;
import ks.util.common.SqlUtils;

import java.sql.Timestamp;
import java.util.Calendar;

public class C_NoticeClick {
    private static final int LIMIT_MIN = 1;
    private static final int LIMIT_MAX = 32767;

    public C_NoticeClick(L1Client client) {
        if (client == null || client.getAccount() == null)
            return;

        if (NoticeTable.getInstance().hasReadRequireNotice(client.getAccountName())) {
            client.sendPacket(new S_Notice(client));
            return;
        }

        //삭제 설정이 되어 있는 캐릭이 있다면 삭제
        if (CodeConfig.DELETE_CHARACTER_AFTER_7DAYS) {
            deleteCharacterCheck(client);
        }

        int amountOfChars = AccountTable.getInstance().countCharacters(client.getAccount());
        int slot = client.getAccount().getCharSlot();

        client.sendPacket(new S_CharAmount(amountOfChars, slot));

        if (amountOfChars > 0) {
            sendCharPacks(client);
        }

        accountTimeCheck(client);
    }

    private void accountTimeCheck(L1Client client) {
        client.sendPacket(new S_PacketBox(L1PacketBoxType.ACCOUNT_TIME, 0, 60 * 60 * 24 * 365));
    }

    private void deleteCharacterCheck(L1Client client) {
        SqlUtils.query("SELECT * FROM characters WHERE account_name=? ORDER BY objid", (rs, i) -> {
            String name = rs.getString("char_name");
            String clanName = rs.getString("Clanname");
            Timestamp deleteTime = rs.getTimestamp("DeleteTime");

            if (deleteTime != null) {
                Calendar cal = Calendar.getInstance();
                long checkDeleteTime = ((cal.getTimeInMillis() - deleteTime.getTime()) / 1000) / 3600;

                if (checkDeleteTime >= 0) {
                    L1Clan clan = L1World.getInstance().getClan(clanName);
                    if (clan != null) {
                        clan.removeClanMember(name);
                    }

                    CharacterTable.getInstance().deleteCharacter(client.getAccountName(), name);
                }
            }

            return null;
        }, client.getAccountName());
    }

    private void sendCharPacks(L1Client client) {
        SqlUtils.query("SELECT * FROM characters WHERE account_name=? ORDER BY objid", (rs, i) -> {
            String name = rs.getString("char_name");
            String clanName = rs.getString("Clanname");
            int type = rs.getInt("Type");
            byte sex = rs.getByte("Sex");
            int lawful = rs.getInt("Lawful");

            int currenthp = rs.getInt("CurHp");
            currenthp = checkRange(currenthp);

            int currentmp = rs.getInt("CurMp");
            currentmp = checkRange(currentmp);

            int lvl;

            if (CodeConfig.CHARACTER_CONFIG_IN_SERVER_SIDE) {
                lvl = rs.getInt("level");
                if (lvl < 1) {
                    lvl = 1;
                } else if (lvl > 127) {
                    lvl = 127;
                }
            } else {
                lvl = 1;
            }

            int ac;
            if (rs.getInt("Ac") < -128) {
                ac = (byte) -128;
            } else {
                ac = rs.getByte("Ac");
            }
            int str = rs.getByte("Str");
            int dex = rs.getByte("Dex");
            int con = rs.getByte("Con");
            int wis = rs.getByte("Wis");
            int cha = rs.getByte("Cha");
            int intel = rs.getByte("Intel");
            int accessLevel = rs.getShort("AccessLevel");
            int birthday = rs.getInt("BirthDay");

            if (birthday == 0) {
                birthday = DateUtils.getTodayDate();
            }

            client.sendPacket(new S_CharPacks(name, clanName, type, sex, lawful, currenthp, currentmp, ac, lvl, str, dex, con, wis, cha, intel, accessLevel, birthday));

            return null;
        }, client.getAccountName());
    }

    private int checkRange(int i) {
        if (i < LIMIT_MIN)
            return LIMIT_MIN;
        else if (i > LIMIT_MAX)
            return LIMIT_MAX;

        return i;
    }
}
