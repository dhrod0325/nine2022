package ks.util;

import ks.core.datatables.LetterTable;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_LetterList;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class L1LetterUtils {
    public static int TYPE_ADEN_BOARD = 87;

    private final static Logger logger = LogManager.getLogger();

    public static void letterList(L1PcInstance pc, int type, int count) {
        pc.sendPackets(new S_LetterList(pc, type, count));
    }

    public static boolean mailCountCheck(String to, int type, int max) {
        if ("메티스".equalsIgnoreCase(to) || "미소피아".equalsIgnoreCase(to)) {
            return false;
        }

        int cntMailInMailBox = LetterTable.getInstance().getLetterCount(to, type);

        if (cntMailInMailBox >= max) {
            LetterTable.getInstance().deleteOldMail(to, type);

            L1PcInstance toUser = L1World.getInstance().getPlayer(to);

            if (toUser != null) {
                toUser.sendPackets("편지함이 가득 차서 오래된 편지가 삭제되었습니다");
            }

            return false;
        }

        return false;
    }

    public static void sendMessageToReceiver(L1PcInstance receiver, L1PcInstance sender, final int type, final int MAILBOX_SIZE) {
        if (receiver != null && receiver.getOnlineStatus() != 0) {
            letterList(receiver, type, MAILBOX_SIZE);
            receiver.sendPackets(new S_SkillSound(receiver.getId(), 1091));
            receiver.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.

            sender.sendPackets(new S_ServerMessage(1239)); //수신자에게 편지를 보냈습니다.
            sender.sendPackets(new S_LetterList(sender, type, MAILBOX_SIZE));
        }
    }

    public static int checkMailCount(String name, boolean read, int type) {
        int check = read ? 1 : 0;

        return SqlUtils.selectInteger("SELECT count(*) as cnt FROM letter where receiver = ? AND template_id = ? AND isCheck = ?", name, type, check);
    }

    public static void sendLetter(L1PcInstance sender, String receiverName, String subject, String content, int type, int max) {
        Calendar Cal = Calendar.getInstance();
        SimpleDateFormat Time = new SimpleDateFormat("yyyyMMdd");
        Cal.setTimeInMillis(System.currentTimeMillis());

        try {
            int time = Integer.parseInt(Time.format(Cal.getTime()));
            String dTime = Integer.toString(time);

            if (mailCountCheck(receiverName, type, max)) {
                return;
            }

            if (sender == null) {
                return;
            }

            L1PcInstance target = L1World.getInstance().getPlayer(receiverName);
            LetterTable.getInstance().writeLetter(TYPE_ADEN_BOARD, dTime, sender.getName(), receiverName, type, subject, content);

            sendMessageToReceiver(target, sender, type, max);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}