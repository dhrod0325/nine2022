package ks.model.pc;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.exp.ExpTable;
import ks.model.L1CalcStat;
import ks.model.L1Teleport;
import ks.packets.serverpackets.S_OwnCharStatus;
import ks.packets.serverpackets.S_SystemMessage;

public class L1PcExpManager {
    private final L1PcInstance pc;
    private int prevAc = 0;
    private int prevDmgUp = 0;
    private int prevBowDmgup = 0;
    private int prevHitup = 0;
    private int prevBowHitup = 0;

    public L1PcExpManager(L1PcInstance pc) {
        this.pc = pc;
    }

    public void resetDmgUp() {
        int dmgUp = L1CalcStat.calcLevelDmgUp(pc.getType(), pc.getLevel());
        dmgUp += L1CalcStat.calcBaseStatDmgUp(pc);
        dmgUp += L1CalcStat.calcStatDmg(pc.getAbility().getTotalStr());

        int bowDmgUp = L1CalcStat.calcLevelBowDmgUp(pc.getType(), pc.getLevel());
        bowDmgUp += L1CalcStat.calcBaseStatBowDmgUp(pc);
        bowDmgUp += L1CalcStat.calcStatBowDmgUp(pc.getAbility().getTotalDex());

        pc.addDmgUp(dmgUp - prevDmgUp);
        pc.addBowDmgUp(bowDmgUp - prevBowDmgup);

        prevDmgUp = dmgUp;
        prevBowDmgup = bowDmgUp;
    }

    public void resetHitUp() {
        int hitUp = L1CalcStat.calcLevelHitUp(pc.getType(), pc.getLevel());
        hitUp += L1CalcStat.calcBaseStatHitUp(pc);
        hitUp += L1CalcStat.calcStatHitUp(pc.getAbility().getTotalStr());

        int bowHitUp = L1CalcStat.calcLevelBowHitUp(pc.getType(), pc.getLevel());
        bowHitUp += L1CalcStat.calcBaseStatBowHitUp(pc);
        bowHitUp += L1CalcStat.calcStatBowHitUp(pc.getAbility().getTotalDex());

        pc.addHitUp(hitUp - prevHitup);
        pc.addBowHitup(bowHitUp - prevBowHitup);

        prevHitup = hitUp;
        prevBowHitup = bowHitUp;
    }

    public void resetAc() {
        int ac = L1CalcStat.calcAc(pc.getAbility().getDex());
        ac -= L1CalcStat.calcBaseStatAc(pc);

        pc.getAC().addAc(ac - prevAc);

        prevAc = ac;
    }

    public void resetMr() {
        int newMr = L1CalcStat.calcInitMr(pc);
        int statMr = L1CalcStat.calcStatMr(pc.getAbility().getTotalWis());

        newMr += statMr;
        newMr += pc.getLevel() / 2;

        pc.getResistance().setBaseMr(newMr);
    }

    public void resetLevel() {
        pc.setLevel(ExpTable.getInstance().getLevelByExp(pc.getExp()));
    }

    public void refresh() {
        pc.checkChangeExp();
        baseRefresh();
    }

    public void baseRefresh() {
        resetLevel();
        statRefresh();
    }

    public void statRefresh() {
        resetHitUp();
        resetDmgUp();
        resetMr();
        resetAc();
    }

    public void refreshAtCreateCharacter() {
        pc.onChangeExp();
        baseRefresh();
    }

    void levelUp(int gap) {
        resetLevel();

        if (pc.getLevel() > 50) {
            pc.sendPackets(new S_SystemMessage("       * 현재  스텟을 확인 후 스텟을 선택해주세요 *    "));
            pc.sendPackets(new S_SystemMessage(" STR: " + pc.getAbility().getStr() + " DEX:" + pc.getAbility().getDex() + " CON:" + pc.getAbility().getCon() + " INT:" + pc.getAbility().getInt() + " WIS:" + pc.getAbility().getWis() + " CHA:" + pc.getAbility().getCha()));
        }

        for (int i = 0; i < gap; i++) {
            short randomHp = L1CalcStat.calcStatHp(pc.getType(), pc.getBaseMaxHp(), pc.getAbility().getCon());
            short randomMp = L1CalcStat.calcStatMp(pc.getType(), pc.getBaseMaxMp(), pc.getAbility().getWis());

            pc.addBaseMaxHp(randomHp);
            pc.addBaseMaxMp(randomMp);
        }

        resetHitUp();
        resetDmgUp();
        resetAc();
        resetMr();

        if (pc.getLevel() > pc.getHighLevel() && pc.getReturnStat() == 0) {
            pc.setHighLevel(pc.getLevel());
        }

        pc.save();

        pc.getQuest().checkQuest();

        pc.bonusStatCheck();

        if (pc.getLevel() < 45 || pc.getLevel() >= CodeConfig.ABANDONED_LAND_LEV) { // 버땅지정레벨
            if (pc.getMapId() == 777) {
                L1Teleport.teleport(pc, 34043, 32184, (short) 4, 5, true);
            } else if (pc.getMapId() == 778 || pc.getMapId() == 779) {
                L1Teleport.teleport(pc, 32608, 33178, (short) 4, 5, true);
            }
        }

        pc.checkStatus();
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    void levelDown(int gap) {
        resetLevel();

        for (int i = 0; i > gap; i--) {
            short randomHp = L1CalcStat.calcStatHp(pc.getType(), 0, pc.getAbility().getCon());
            short randomMp = L1CalcStat.calcStatMp(pc.getType(), 0, pc.getAbility().getWis());

            pc.addBaseMaxHp((short) -randomHp);
            pc.addBaseMaxMp((short) -randomMp);
        }

        resetHitUp();
        resetDmgUp();
        resetAc();
        resetMr();

        if (CodeConfig.LEVEL_DOWN_RANGE != 0) {
            if (pc.getHighLevel() - pc.getLevel() >= CodeConfig.LEVEL_DOWN_RANGE) {
                if (pc.isGm()) {
                    pc.sendPackets(new S_SystemMessage("\\fY운영자님은 현재 레벨다운 범위를 초과하셨습니다."));
                } else {
                    pc.setChaTra(1);
                    pc.sendPackets(new S_SystemMessage("렙다(피녹)의 범위인 " + CodeConfig.LEVEL_DOWN_RANGE + "을 넘었습니다."));
                    pc.disconnect();
                }
            }
        }

        pc.save();
        pc.sendPackets(new S_OwnCharStatus(pc));
    }
}
