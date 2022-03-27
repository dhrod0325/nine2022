package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.scheduler.timer.gametime.GameTimeScheduler;


public class S_OwnCharAttrDef extends ServerBasePacket {
    public S_OwnCharAttrDef(L1Character pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1Character cha) {
        if (cha == null) {
            return;
        }
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            int time = GameTimeScheduler.getInstance().getTime().getSeconds();
            time = time - (time % 300);

            writeC(L1Opcodes.S_OPCODE_OWNCHARSTATUS);
            writeD(pc.getId());

            if (pc.getLevel() < 1) {
                writeC(1);
            } else writeC(Math.min(pc.getLevel(), 127));

            writeD(pc.getExp());
            writeC(pc.getAbility().getTotalStr());
            writeC(pc.getAbility().getTotalInt());
            writeC(pc.getAbility().getTotalWis());
            writeC(pc.getAbility().getTotalDex());
            writeC(pc.getAbility().getTotalCon());
            writeC(pc.getAbility().getTotalCha());
            writeH(pc.getCurrentHp());
            writeH(pc.getMaxHp());
            writeH(pc.getCurrentMp());
            writeH(pc.getMaxMp());
            writeC(pc.getAC().getAc());
            writeD(time);
            writeC(pc.getFood());
            writeC(pc.getInventory().getWeight240());
            writeH(pc.getLawful());

            writeH(pc.getResistance().getFire());
            writeH(pc.getResistance().getWater());
            writeH(pc.getResistance().getWind());
            writeH(pc.getResistance().getEarth());

            writeD(pc.getMonsterKill());
        }
    }
}
