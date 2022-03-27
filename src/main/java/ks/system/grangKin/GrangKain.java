package ks.system.grangKin;

import ks.constants.L1PacketBoxType;
import ks.core.datatables.account.Account;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.util.common.IntRange;

import java.util.concurrent.TimeUnit;

public class GrangKain {
    private int second;

    public void addSecond(int second) {
        this.second += second;
        this.second = IntRange.ensure(this.second, 0, 60 * 60 * 10);
    }

    public int getMinutes() {
        return (int) TimeUnit.SECONDS.toMinutes(second);
    }

    public int getStat() {
        int minutes = getMinutes();

        if (getMinutes() <= 0)
            return 0;

        int grangKinOneStep = 100;
        int grangKinTwoStep = 200;
        int grangKinThreeStep = 300;
        int grangKinFourStep = 400;
        int grangKinFiveStep = 500;
        int grangKinSixStep = 600;

        if (minutes >= grangKinOneStep && minutes < grangKinTwoStep)
            return 1;
        if (minutes >= grangKinTwoStep && minutes < grangKinThreeStep)
            return 2;
        if (minutes >= grangKinThreeStep && minutes < grangKinFourStep)
            return 3;
        if (minutes >= grangKinFourStep && minutes < grangKinFiveStep)
            return 4;
        if (minutes >= grangKinFiveStep && minutes < grangKinSixStep)
            return 5;
        if (minutes >= grangKinSixStep)
            return 6;

        return 0;
    }

    public void updateByTime(long checkTime) {
        long diffMill = System.currentTimeMillis() - checkTime;
        if (diffMill >= 1000) {
            addSecond(-(int) (diffMill / 1000));
        }
    }

    public void initWithAccount(Account account) {
        int grangKainTime = GrangKainTable.getInstance().selectTime(account.getName());
        updateByTime(account.getLastLogout().getTime());
        addSecond(grangKainTime);
    }

    public int getSecond() {
        return second;
    }

    public void sendIcon(L1PcInstance pc) {
        int stat = getStat();

        int start = 410;
        int icon = start + (stat - 1);

        if (stat > 0) {
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, icon, true));
        } else {
            for (int i = 0; i < 6; i++) {
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.UNLIMITED_ICON1, start + i, false));
            }
        }
    }
}