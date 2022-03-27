package ks.system.grangKin;

import ks.app.LineageAppContext;
import ks.core.datatables.account.Account;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.util.L1CharPosUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GrangKainScheduler {
    @Scheduled(fixedDelay = 1000)
    public void scheduled() {
        if (!LineageAppContext.isRun())
            return;

        L1World.getInstance().getAllPlayers().stream()
                .filter(Objects::nonNull)
                .filter(pc -> pc.getAccount() != null)
                .forEach(pc -> {
                    Account account = pc.getAccount();

                    GrangKain grangKain = account.getGrangKain();

                    int oldGrangKinStat = grangKain.getStat();

                    if (L1CharPosUtils.isSafeZone(pc)) {
                        grangKain.addSecond(-1);
                    } else {
                        grangKain.addSecond(1);
                    }

                    if (oldGrangKinStat != grangKain.getStat()) {
                        changedGrangKainStat(pc);
                    }

                    GrangKainTable.getInstance().updateTime(account);
                });
    }

    private void changedGrangKainStat(L1PcInstance pc) {
        pc.getAccount().getGrangKain().sendIcon(pc);
    }
}