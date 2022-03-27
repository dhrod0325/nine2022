package ks.model.pc.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.system.chat.L1ChatEventHandler;
import ks.system.chat.L1ChatEventListener;
import ks.util.L1CharPosUtils;
import ks.util.common.random.RandomUtils;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

public class L1AutoCheckScheduler implements Runnable {
    private final L1PcInstance pc;

    private int checkNum;

    private ScheduledFuture<?> future;

    private int failCount;

    private L1ChatEventHandler chatYnHandler;
    private int normalZoneTime;

    public L1AutoCheckScheduler(L1PcInstance pc) {
        this.pc = pc;
    }

    public void start() {
        if (!CodeConfig.AUTO_CHECK_USE) {
            return;
        }

        future = LineageAppContext.autoUpdateTaskScheduler().scheduleWithFixedDelay(this, Duration.ofMillis(1000));
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
            future = null;
        }

        if (chatYnHandler != null) {
            chatYnHandler.stopHandle();
            L1ChatEventListener.getInstance().remove(pc);
        }
    }

    public void addNormalZoneTime(int normalZoneTime) {
        this.normalZoneTime += normalZoneTime;
    }

    public void autoCheck() {
        checkNum = RandomUtils.nextInt(10, 99);

        chatYnHandler = new L1ChatEventHandler() {
            @Override
            public void process() {
                checkNum = 0;
                failCount = 0;
                pc.sendPackets("자동방지 인증에 성공하였습니다");
            }

            @Override
            public void fail() {
                failCount++;
                pc.sendPackets("자동방지 인증실패 실패횟수: " + failCount);
            }
        };

        String msg = "자동방지를 위해 다음 숫자를 입력해주세요 : " + checkNum;

        pc.sendPackets(new S_ChatPacket(pc, msg, L1Opcodes.S_OPCODE_NORMALCHAT, 2));

        chatYnHandler.setPc(pc);
        chatYnHandler.setChatMessage(checkNum + "");
        chatYnHandler.setWaitTimeSecond(30);
        chatYnHandler.handle();

        L1ChatEventListener.getInstance().add(pc, chatYnHandler);
    }

    @Override
    public void run() {
        if (L1CharPosUtils.isNormalZone(pc) && pc.getMapId() != 34) {
            addNormalZoneTime(1);
        }

        if (normalZoneTime == 60 * 60) {
            autoCheck();
            normalZoneTime = 0;
        }

        if (failCount >= 5) {
            L1Teleport.teleport(pc, 32736, 32799, (short) 34, 5, true);

            failCount = 0;
            normalZoneTime = 0;
        }
    }
}
