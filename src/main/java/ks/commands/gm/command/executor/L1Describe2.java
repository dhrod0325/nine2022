package ks.commands.gm.command.executor;

import ks.app.config.prop.CodeConfig;
import ks.model.L1CalcStat;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1Describe2 implements L1CommandExecutor {
    private final Logger logger = LogManager.getLogger(getClass());

    public static L1Describe2 getInstance() {
        return new L1Describe2();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String name = st.nextToken();

            L1PcInstance target = L1World.getInstance().getPlayer(name);

            if (target == null) {
                pc.sendPackets(new S_ServerMessage(73, name));
                return;
            }

            desc(pc, target);
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".정보 [케릭터명] 으로 입력하세요."));
            e.printStackTrace();
        }
    }


    public void desc(L1PcInstance pc, L1PcInstance target) {
        int hpr = target.getCurrentHpTic();
        int mpr = target.getCurrentMpTic();

        String hprMsg = hpr + "";

        if (!target.isHpMpRegenNotAble()) {
            hprMsg += " + 최대 " + target.getHprMaxBonus();
        }

        int statDmg = L1CalcStat.calcStatDmg(target.getAbility().getTotalStr());

        L1LogUtils.gmLog(pc, "dmgUp:{},dmgUpByArmor:{},addDmgupByArmor:{},statDmg:{},getAddDmg:{},totalDmg : {}", pc.getDmgUp(), pc.getDmgUpByArmor(), pc.getAddDmgUpByArmor(), statDmg, pc.getAddDmg(), pc.getTotalDmg());

        S_ShowCCHtml packet = new S_ShowCCHtml(pc.getId(), "cc_desc",
                target.getName(),
                target.getLevel() + "." + target.getExpPer(),
                CodeConfig.MAX_LEVEL,
                hprMsg,
                mpr,
                target.getAbility().getSp(),
                target.getResistance().getEffectedMrBySkill(),
                target.getEr(),
                target.getAC().getAc(),
                target.getTotalReduction(),
                target.getTotalDmg() + target.getAddDmg(),
                target.getTotalHitUp(),
                target.getTotalBowDmg() + target.getAddDmg(),
                target.getTotalBowHitUp(),
                target.getResistance().getStun(),
                target.getResistance().getHold(),
                target.getResistance().getEarth(),
                target.getResistance().getFire(),
                target.getResistance().getWater(),
                target.getResistance().getWind(),
                target.getAddPvpDmgUp(),
                target.getTotalMagicHitUp(),
                target.getAddStunHit(),
                target.getAbility().getElixirCount(),
                target.getKarma(),
                (target.getKarma() > 0 ? "발록진영" : "야히진영") + "[" + target.getKarmaLevel() + "단계]",
                target.getAddPvpReduction(),
                target.getCriticalPer(),
                target.getBowCriticalPer(),
                target.getMagicCriticalPer(),
                target.getResistance().getElf(),
                target.getResistance().getPetrifaction(),
                target.getResistance().getSleep(),
                target.getResistance().getFreeze(),
                target.getResistance().getBlind()

        );

        pc.sendPackets(packet);
    }
}
