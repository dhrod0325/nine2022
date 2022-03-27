package ks.scheduler;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.model.L1Clan;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PremiumScheduler {
    private final Logger logger = LogManager.getLogger();

    public static double CLAN_BONUS = CodeConfig.FEATHER_CLAN_BONUS;
    public static double CASTLE_BONUS = CodeConfig.FEATHER_CASTLE_BONUS;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        if (CodeConfig.FEATHER_NUMBER <= 0) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            try {
                giveFeather(pc);
            } catch (Exception e) {
                logger.error("오류", e);
            }
        });
    }

    private void giveFeather(L1PcInstance pc) {
        if (pc == null) {
            return;
        }

        int count = pc.getLevel() / 5 * 3;

        pc.getInventory().storeItem(41159, count);
        pc.sendPackets(new S_SystemMessage("픽시의 깃털 (" + count + ")을 얻었습니다."));

        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            if (clan.getClanId() != 0) {
                pc.getInventory().storeItem(41159, (int) (count * CLAN_BONUS));
                pc.sendPackets(new S_SystemMessage("\\fY혈맹 보너스 픽시의 깃털(" + (int) (count * CLAN_BONUS) + ")개 추가 지급."));
            }

            if (clan.getCastleId() != 0) {
                pc.getInventory().storeItem(41159, (int) (count * CASTLE_BONUS));
                pc.sendPackets(new S_SystemMessage("\\fU성혈 보너스 픽시의 깃털(" + (int) (count * CASTLE_BONUS) + ")개 추가 지급."));
            }
        }
    }
}
