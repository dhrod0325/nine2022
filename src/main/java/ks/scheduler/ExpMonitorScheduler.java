package ks.scheduler;

import ks.app.LineageAppContext;
import ks.constants.L1PacketBoxType;
import ks.model.Broadcaster;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Lawful;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SPMR;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpMonitorScheduler {
    private static final Logger logger = LogManager.getLogger(ExpMonitorScheduler.class);

    @Scheduled(fixedDelay = 200)
    public void run() {
        if (!LineageAppContext.isRun()) {
            return;
        }

        L1World.getInstance().getAllPlayers().forEach(pc -> {
            try {
                if (pc.getOldLawful() != pc.getLawful()) {
                    pc.setOldLawful(pc.getLawful());

                    pc.sendPackets(new S_Lawful(pc.getId(), pc.getOldLawful()));
                    Broadcaster.broadcastPacket(pc, new S_Lawful(pc.getId(), pc.getOldLawful()));

                    lawfulBonus(pc);
                }

                if (pc.getOldExp() != pc.getExp()) {
                    pc.setOldExp(pc.getExp());
                    pc.onChangeExp();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        });
    }

    private void lawfulBonus(L1PcInstance pc) {
        int sp = 0;
        int attack = 0;
        int bapho;

        if (pc.getLawful() >= -9999 && pc.getLawful() <= 9999) {
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapho = 7;
            pc.setNBapoLevel(bapho);
        } else if (pc.getLawful() <= -10000 && pc.getLawful() >= -19999) {
            sp = 1;
            attack = 1;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapho = 3;
            pc.setNBapoLevel(bapho);
        } else if (pc.getLawful() <= -20000 && pc.getLawful() >= -29999) {
            sp = 2;
            attack = 3;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapho = 4;
            pc.setNBapoLevel(bapho);
        } else if (pc.getLawful() <= -30000 && pc.getLawful() >= -32768) {
            sp = 3;
            attack = 5;
            pc.setOBapoLevel(pc.getNBapoLevel());
            bapho = 5;
            pc.setNBapoLevel(bapho);
        }

        if (pc.getOBapoLevel() != pc.getNBapoLevel()) {
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.BAPO, pc.getOBapoLevel(), false));
            pc.sendPackets(new S_PacketBox(L1PacketBoxType.BAPO, pc.getNBapoLevel(), true));
            pc.setOBapoLevel(pc.getNBapoLevel());
        }

        if (attack != 0) {
            if (pc.lawfulAttack != 0) {
                pc.setBapodmg(pc.lawfulAttack * -1);
            }
            pc.lawfulAttack = attack;
            pc.setBapodmg(attack);
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else if (pc.lawfulSP != 0) {
            pc.setBapodmg(pc.lawfulAttack * -1);
            pc.lawfulAttack = 0;
            pc.sendPackets(new S_OwnCharStatus(pc));
        }

        if (sp != 0) {
            if (pc.lawfulSP != 0) {
                pc.getAbility().addSp(pc.lawfulSP * -1);
            }
            pc.lawfulSP = sp;
            pc.getAbility().addSp(sp);
            pc.sendPackets(new S_SPMR(pc));
        } else if (pc.lawfulSP != 0) {
            pc.getAbility().addSp(pc.lawfulSP * -1);
            pc.lawfulSP = 0;
            pc.sendPackets(new S_SPMR(pc));
        }
    }
}
