package ks.packets.clientpackets;

import ks.constants.L1ItemId;
import ks.core.datatables.BoardTable;
import ks.core.datatables.LetterTable;
import ks.core.network.L1Client;
import ks.model.L1Clan;
import ks.model.L1ClanMember;
import ks.model.L1World;
import ks.model.board.S_Board;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_LetterList;
import ks.packets.serverpackets.S_ReadLetter;
import ks.packets.serverpackets.S_RenewLetter;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1LetterUtils;
import ks.util.common.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class C_MailBox extends ClientBasePacket {
    public static final int TYPE_PRIVATE_MAIL = 0;  // 개인 편지
    public static final int TYPE_BLOOD_PLEDGE_MAIL = 1;  // 혈맹 편지
    public static final int TYPE_KEPT_MAIL = 2;  // 보관 편지
    public static final int SIZE_PRIVATE_MAILBOX = 20;    // 개인 편지함 크기
    public static final int SIZE_BLOOD_PLEDGE_MAILBOX = 50;    // 혈맹 편지함 크기
    public static final int SIZE_KEPT_MAIL_MAILBOX = 10;    // 편지보관함 크기
    private static final int READ_PRIVATE_MAIL = 16; // 개인 편지읽기
    private static final int READ_BLOOD_PLEDGE_MAIL = 17; // 혈맹 편지읽기
    private static final int READ_KEPT_MAIL_ = 18; // 보관함 편지읽기
    private static final int WRITE_PRIVATE_MAIL = 32; // 개인 편지쓰기
    private static final int WRITE_BLOOD_PLEDGE_MAIL = 33; // 혈맹 편지쓰기
    private static final int DEL_PRIVATE_MAIL = 48; // 개인 편지삭제
    private static final int DEL_PRIVATE_MAIL_FROM_LIST = 96; // 보관함 편지삭제
    private static final int DEL_BLOOD_PLEDGE_MAIL = 49; // 혈맹 편지삭제
    private static final int DEL_KEPT_MAIL = 50; // 보관함 편지삭제
    private static final int TO_KEEP_MAIL = 64; // 편지 보관하기
    private static final int PRICE_PRIVATE_MAIL = 50;    // 개인 편지 가격
    private static final int PRICE_BLOOD_PLEDGE_MAIL = 1000; // 혈맹 편지 가격

    public C_MailBox(byte[] data, L1Client client) {
        super(data);
        int type = readC();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        switch (type) {
            case TYPE_PRIVATE_MAIL:
                L1LetterUtils.letterList(pc, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX);
                break;
            case TYPE_BLOOD_PLEDGE_MAIL:
                L1LetterUtils.letterList(pc, TYPE_BLOOD_PLEDGE_MAIL, SIZE_BLOOD_PLEDGE_MAILBOX);
                break;
            case TYPE_KEPT_MAIL:
                L1LetterUtils.letterList(pc, TYPE_KEPT_MAIL, SIZE_KEPT_MAIL_MAILBOX);
                break;
            case READ_PRIVATE_MAIL:
                readLetter(pc, READ_PRIVATE_MAIL);
                break;
            case READ_BLOOD_PLEDGE_MAIL:
                readLetter(pc, READ_BLOOD_PLEDGE_MAIL);
                break;
            case READ_KEPT_MAIL_:
                readLetter(pc, READ_KEPT_MAIL_);
                break;
            case WRITE_PRIVATE_MAIL:
                writePrivateMail(pc);
                break;
            case WRITE_BLOOD_PLEDGE_MAIL:
                writeBloodPledgeMail(pc);
                break;
            case DEL_PRIVATE_MAIL:
                deleteLetter(pc, DEL_PRIVATE_MAIL);
                break;
            case DEL_BLOOD_PLEDGE_MAIL:
                deleteLetter(pc, DEL_BLOOD_PLEDGE_MAIL);
                break;
            case DEL_KEPT_MAIL:
                deleteLetter(pc, DEL_KEPT_MAIL);
                break;
            case TO_KEEP_MAIL:
                saveLetter(pc);
                break;
            case DEL_PRIVATE_MAIL_FROM_LIST:
                int size = readD();
                for (int i = 0; i < size; i++) {
                    int id = readD();
                    LetterTable.getInstance().deleteLetter(id);
                }

                pc.sendPackets(new S_LetterList(pc, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX));

                break;
            default:
        }
    }

    private boolean hasPayMailCost(final L1PcInstance receiver, final int PRICE) {
        int AdenaCnt = receiver.getInventory().countItems(L1ItemId.ADENA);

        if (AdenaCnt < PRICE) {
            receiver.sendPackets(new S_ServerMessage(189, ""));
            return false;
        }

        receiver.getInventory().consumeItem(L1ItemId.ADENA, PRICE);
        return true;
    }

    private void writePrivateMail(L1PcInstance sender) {
        if (!hasPayMailCost(sender, PRICE_PRIVATE_MAIL))
            return;

        int paper = readH();
        int time;

        String dTime = null;
        Calendar Cal = Calendar.getInstance();
        SimpleDateFormat Time = new SimpleDateFormat("yyyyMMdd");
        Cal.setTimeInMillis(System.currentTimeMillis());

        try {
            time = Integer.parseInt(Time.format(Cal.getTime()));
            dTime = Integer.toString(time);
        } catch (Exception ignored) {
        }

        String receiverName = readS();
        String subject = readSS();
        String content = readSS();

        boolean isGm = "메티스".equalsIgnoreCase(receiverName) || "미소피아".equalsIgnoreCase(receiverName);

        if (isGm) {
            L1PcInstance gm = new L1PcInstance();
            gm.setName(receiverName);

            String sender1 = sender + " -> " + receiverName;

            BoardTable.getInstance().writeTopic(gm, DateUtils.currentTime(), sender1, content, 460000118);
        }

        L1PcInstance target = L1World.getInstance().getPlayer(receiverName);

        if (L1LetterUtils.mailCountCheck(receiverName, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX)) {
            if (isGm) {
                if (target != null) {
                    try {
                        L1NpcInstance o = L1World.getInstance().getNpc(460000118, 2007122025);
                        target.sendPackets(new S_Board(o, null, 0));
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }

                sender.sendPackets(new S_ServerMessage(1239)); //수신자에게 편지를 보냈습니다.
                sender.sendPackets(new S_LetterList(sender, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX));
            }

            return;
        }

        LetterTable.getInstance().writeLetter(paper, dTime, sender.getName(), receiverName, TYPE_PRIVATE_MAIL, subject, content);
        L1LetterUtils.sendMessageToReceiver(target, sender, TYPE_PRIVATE_MAIL, SIZE_PRIVATE_MAILBOX);
    }

    private void writeBloodPledgeMail(L1PcInstance sender) {
        if (!hasPayMailCost(sender, PRICE_BLOOD_PLEDGE_MAIL))
            return;

        int paper = readH(); // 편지지
        int time;

        String dTime = null;
        Calendar Cal = Calendar.getInstance();
        SimpleDateFormat Time = new SimpleDateFormat("yyyyMMdd");
        Cal.setTimeInMillis(System.currentTimeMillis());

        try {
            time = Integer.parseInt(Time.format(Cal.getTime()));
            dTime = Integer.toString(time);
        } catch (Exception ignored) {
        }

        String receiverName = readS();
        String subject = readSS();
        String content = readSS();

        L1Clan targetClan = null;

        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (clan.getClanName().equalsIgnoreCase(receiverName)) {
                targetClan = clan;
                break;
            }
        }

        if (targetClan != null) {
            List<L1ClanMember> clanMemberList = targetClan.getClanMemberList();

            for (L1ClanMember l1ClanMember : clanMemberList) {
                String name = l1ClanMember.name;
                L1PcInstance target = L1World.getInstance().getPlayer(name);

                if (L1LetterUtils.mailCountCheck(name, TYPE_BLOOD_PLEDGE_MAIL, SIZE_BLOOD_PLEDGE_MAILBOX)) {
                    continue;
                }

                LetterTable.getInstance().writeLetter(paper, dTime, sender.getName(), name, TYPE_BLOOD_PLEDGE_MAIL, subject, content);
                L1LetterUtils.sendMessageToReceiver(target, sender, TYPE_BLOOD_PLEDGE_MAIL, SIZE_BLOOD_PLEDGE_MAILBOX);
            }
        }
    }

    private void deleteLetter(L1PcInstance pc, int type) {
        int id = readD();

        LetterTable.getInstance().deleteLetter(id);
        pc.sendPackets(new S_RenewLetter(type, id));
    }

    private void readLetter(L1PcInstance pc, int type) {
        int id = readD();

        LetterTable.getInstance().checkLetter(id);
        pc.sendPackets(new S_ReadLetter(type, id));
    }

    private void saveLetter(L1PcInstance pc) {
        int id = readD();

        LetterTable.getInstance().saveLetter(id, TYPE_KEPT_MAIL);
        pc.sendPackets(new S_RenewLetter(TO_KEEP_MAIL, id));
    }
}
