package ks.core.datatables.test;

import ks.model.instance.L1MonsterInstance;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.util.Date;

public class TestNpcDieTimeLogTable {
    private static final TestNpcDieTimeLogTable instance = new TestNpcDieTimeLogTable();

    public static TestNpcDieTimeLogTable getInstance() {
        return instance;
    }

    public void save(L1PcInstance pc, L1MonsterInstance mon) {
        TestNpcDieTimeLog v = new TestNpcDieTimeLog();
        v.setTargetNpcId(mon.getNpcId());
        v.setTargetNpcName(mon.getName());
        v.setMinDmg(mon.getDamageCheck().getMinDamage());
        v.setMaxDmg(mon.getDamageCheck().getMaxDamage());
        v.setAvgDmg(mon.getDamageCheck().getAvgDmage());
        v.setDieSecond(mon.getDamageCheck().getTotalAttackSecond());
        v.setAttackerId(pc.getId());
        v.setAttackerName(pc.getName());
        v.setAttackerWeapon(pc.getWeaponInfo().getWeaponId());
        v.setAttackerHitUp(pc.getTotalHitUp());
        v.setAttackerDmgUp(pc.getTotalDmg());
        v.setAttackerBowHitUp(pc.getTotalBowHitUp());
        v.setAttackerBowDmgUp(pc.getTotalBowDmg());
        v.setAttackCount(mon.getDamageCheck().getAttackCount());
        v.setRegDate(new Date());

        String sql = "insert into test_npc_log ("
                + "targetNpcId,\n" +
                "                targetNpcName,\n" +
                "                minDmg,\n" +
                "                maxDmg,\n" +
                "                avgDmg,\n" +
                "                dieSecond,\n" +
                "                attackerId,\n" +
                "                attackerName,\n" +
                "                attackerWeapon,\n" +
                "                attackerHitUp,\n" +
                "                attackerDmgUp,\n" +
                "                attackerBowHitUp,\n" +
                "                attackerBowDmgUp,\n" +
                "                attackCount,\n" +
                "                regDate) values"

                + "(:targetNpcId,\n" +
                "                :targetNpcName,\n" +
                "                :minDmg,\n" +
                "                :maxDmg,\n" +
                "                :avgDmg,\n" +
                "                :dieSecond,\n" +
                "                :attackerId,\n" +
                "                :attackerName,\n" +
                "                :attackerWeapon,\n" +
                "                :attackerHitUp,\n" +
                "                :attackerDmgUp,\n" +
                "                :attackerBowHitUp,\n" +
                "                :attackerBowDmgUp,\n" +
                "                :attackCount,\n" +
                "                :regDate) ";


        SqlUtils.update(sql, new BeanPropertySqlParameterSource(v));
    }

}
