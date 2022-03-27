package ks.core.datatables.mobskill;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1MobSkill;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MobSkillTable {
    public static MobSkillTable getInstance() {
        return LineageAppContext.getBean(MobSkillTable.class);
    }

    private final Map<Integer, L1MobSkill> mobSkills = new HashMap<>();

    @LogTime
    public void load() {
        loadMobSkillData();
    }

    private void loadMobSkillData() {
        mobSkills.clear();

        SqlUtils.query("SELECT mobid,count(*) as cnt FROM mobskill group by mobid", (rs, i) -> {
            int mobId = rs.getInt("mobid");
            int count = rs.getInt("cnt");

            L1MobSkill mobSkill = new L1MobSkill(count);
            mobSkill.setMobId(mobId);

            SqlUtils.query("SELECT * FROM mobskill where mobid = ? order by mobid,actNo", (rs2, i1) -> {
                int actNo = rs2.getInt("actNo");

                mobSkill.setMobName(rs2.getString("mobname"));
                mobSkill.setType(actNo, rs2.getInt("type"));
                mobSkill.setTriggerRandom(actNo, rs2.getInt("TriRnd"));
                mobSkill.setTriggerHp(actNo, rs2.getInt("TriHp"));
                mobSkill.setTriggerCompanionHp(actNo, rs2.getInt("TriCompanionHp"));
                mobSkill.setTriggerRange(actNo, rs2.getInt("TriRange"));
                mobSkill.setTriggerCount(actNo, rs2.getInt("TriCount"));
                mobSkill.setChangeTarget(actNo, rs2.getInt("ChangeTarget"));
                mobSkill.setRange(actNo, rs2.getInt("Range"));
                mobSkill.setAreaWidth(actNo, rs2.getInt("AreaWidth"));
                mobSkill.setAreaHeight(actNo, rs2.getInt("AreaHeight"));
                mobSkill.setLeverage(actNo, rs2.getInt("Leverage"));
                mobSkill.setSkillId(actNo, rs2.getInt("SkillId"));
                mobSkill.setGfxid(actNo, rs2.getInt("Gfxid"));
                mobSkill.setActid(actNo, rs2.getInt("Actid"));
                mobSkill.setSummon(actNo, rs2.getInt("SummonId"));
                mobSkill.setSummonMin(actNo, rs2.getInt("SummonMin"));
                mobSkill.setSummonMax(actNo, rs2.getInt("SummonMax"));
                mobSkill.setPolyId(actNo, rs2.getInt("PolyId"));

                return null;
            }, mobId);

            mobSkills.put(mobId, mobSkill);

            return null;
        });
    }

    public L1MobSkill getTemplate(int id) {
        return mobSkills.get(id);
    }
}
