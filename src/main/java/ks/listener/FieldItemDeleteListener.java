package ks.listener;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.model.L1World;
import ks.packets.serverpackets.S_SystemMessage;
import ks.scheduler.timer.BaseTime;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class FieldItemDeleteListener extends TimeListenerAdapter {
    private static final Logger logger = LogManager.getLogger(FieldItemDeleteListener.class);

    private int remainingMinute;
    private int remainingSecondTime;

    private int temp;

    public static FieldItemDeleteListener getInstance() {
        return LineageAppContext.getBean(FieldItemDeleteListener.class);
    }

    @Override
    public void onMinuteChanged(BaseTime time) {
        int minute = time.get(Calendar.MINUTE);
        int check = CodeConfig.FIELD_ITEM_DELETE_MINUTE - minute;

        if (check < 0) {
            return;
        }

        if (check == 0) {
            LineageAppContext.commonTaskScheduler().execute(this::deleteItems);
        } else if (check <= 5) {
            notifyDeleteMessage(check);
        }
    }

    @Override
    public void onSecondChanged(BaseTime time) {
        if (temp != CodeConfig.FIELD_ITEM_DELETE_MINUTE) {
            temp = CodeConfig.FIELD_ITEM_DELETE_MINUTE;

            remainingSecondTime = 0;
            remainingMinute = 0;

            onMinuteChanged(time);

            return;
        }

        if (remainingMinute == 1) {
            remainingSecondTime = 60 - time.get(Calendar.SECOND);
        }
    }

    public void notifyDeleteMessage(int minute) {
        remainingMinute = minute;
        L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(minute + "분후 지면의 청소가 시작됩니다"));
    }

    public void deleteItems() {
        try {
            remainingMinute = 0;
            remainingSecondTime = 0;
            L1CommonUtils.deleteGroundItems();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public int getRemainingSecondTime() {
        return remainingSecondTime;
    }
}
