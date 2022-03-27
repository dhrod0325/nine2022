package ks.core.network;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.auth.AuthorizationUtils;
import ks.core.datatables.account.Account;
import ks.core.network.opcode.L1Opcodes;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Disconnect;
import ks.packets.serverpackets.ServerBasePacket;
import ks.util.L1CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.StringTokenizer;

public class L1Client {
    private static final Logger logger = LogManager.getLogger(L1Client.class);
    private final L1HcPacket moveHandler = new L1HcPacket(this, 3);
    private final L1HcPacket otherHandler = new L1HcPacket(this, 2);
    private final L1Session session;
    private L1Packet packet = new L1Packet();
    private boolean clientVersionCheck = false;
    private boolean clientLoginCheck = false;
    private int checkCount = 1;
    private boolean disconnect = false;
    private boolean close = false;
    private Account account;

    private L1PcInstance pc;

    public L1Client(L1Session session) {
        this.session = session;

        LineageAppContext.generalThreadPool().execute(moveHandler);
        LineageAppContext.generalThreadPool().execute(otherHandler);
    }

    public void kick() {
        sendPacket(new S_Disconnect());
    }

    public void disconnectNow() {
        kick();
        disconnect(0);
    }

    public void sendPacket(ServerBasePacket bp, boolean isClear) {
        if (session == null) {
            return;
        }

        session.write(bp);

        if (isClear) {
            bp.close();
        }
    }

    public void sendPacket(ServerBasePacket bp) {
        sendPacket(bp, true);
    }

    public void disconnect() {
        disconnect(CodeConfig.FORCE_DISCONNECT_QUIT);
    }

    public void disconnect(int time) {
        if (disconnect) {
            return;
        }

        disconnect = true;

        if (!StringUtils.isEmpty(getAccountName())) {
            logger.debug("[연결종료] 계정명 : {} ", getAccountName());
        }

        close(time);
    }

    private void close(int time) {
        if (close) {
            return;
        }

        close = true;

        try {
            if (pc != null) {
                LineageAppContext.commonTaskScheduler().schedule(() -> {
                    pc.quitGame();
                    pc.logout();

                    setActiveChar(null);

                    AuthorizationUtils.getInstance().logout(this);
                }, Instant.now().plusMillis(1000L * time));
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        try {
            session.close();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public void receivePacket(byte[] data) {
        try {
            if (data == null) {
                return;
            }

            int opcode = data[0] & 0xFF;

            if (L1CommonUtils.isStandByServer()) {
                L1PcInstance pc = getActiveChar();

                if (pc != null) {
                    if (L1CommonUtils.isStandByServer(pc)) {
                        if (opcode == L1Opcodes.C_OPCODE_TRADE
                                || opcode == L1Opcodes.C_OPCODE_DROPITEM
                                || opcode == L1Opcodes.C_OPCODE_PICKUPITEM
                                || opcode == L1Opcodes.C_OPCODE_GIVEITEM
                                || opcode == L1Opcodes.C_OPCODE_USESKILL) {
                            L1CommonUtils.sendStandByMsg(pc);

                            return;
                        }
                    }
                }
            }

            packetReceived();

            if (opcode == L1Opcodes.C_OPCODE_MOVECHAR
                    || opcode == L1Opcodes.C_OPCODE_ATTACK
                    || opcode == L1Opcodes.C_OPCODE_USESKILL
                    || opcode == L1Opcodes.C_OPCODE_ARROWATTACK) {
                moveHandler.receive(data);
            } else {
                otherHandler.receive(data);
            }

        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    public L1PcInstance getActiveChar() {
        return pc;
    }

    public void setActiveChar(L1PcInstance pc) {
        this.pc = pc;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAccountName() {
        if (account == null) {
            return null;
        }

        return account.getName();
    }

    public String getHostname() {
        if (session == null)
            return "-1";

        try {
            StringTokenizer st = new StringTokenizer(session.getRemoteAddress().substring(1), ":");

            return st.nextToken();
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return "-1";
    }

    public String getIp() {
        try {
            if (session == null || session.getRemoteAddress() == null) {
                return "";
            }

            StringTokenizer st = new StringTokenizer(session.getRemoteAddress().substring(1), ":");
            return st.nextToken();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isClosed() {
        return session.isClosed();
    }

    public void queueNotify() {
        moveHandler.queueNotify();
        otherHandler.queueNotify();
    }

    public L1Packet getPacket() {
        return packet;
    }

    public void setPacket(L1Packet packet) {
        this.packet = packet;
    }

    public byte[] encrypt(byte[] bytes) {
        return packet.encrypt(bytes);
    }

    public byte[] decrypt(byte[] bytes) {
        return packet.decrypt(bytes);
    }

    public boolean isPacketAttack() {
        return session.isPacketAttack();
    }

    public boolean isClientVersionCheck() {
        return clientVersionCheck;
    }

    public void setClientVersionCheck(boolean clientVersionCheck) {
        this.clientVersionCheck = clientVersionCheck;
    }

    public boolean isClientLoginCheck() {
        return clientLoginCheck;
    }

    public void setClientLoginCheck(boolean clientLoginCheck) {
        this.clientLoginCheck = clientLoginCheck;
    }

    public void packetReceived() {
        ++checkCount;
    }

    public int getCheckCount() {
        return checkCount;
    }

    public void setCheckCount(int checkCount) {
        this.checkCount = checkCount;
    }

    @Override
    public String toString() {
        String result = "LineageClient : ";

        result += "ip : " + getIp() + " ";
        result += "accountName : " + getAccountName() + "";

        if (getActiveChar() != null) {
            result += "name : " + getActiveChar().getName() + "";
        }

        return result;
    }
}
