package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1Skills;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SkillsTable {
    private final Map<Integer, L1Skills> skills = new HashMap<>();

    public static SkillsTable getInstance() {
        return LineageAppContext.getBean(SkillsTable.class);
    }

    @LogTime
    public void load() {
        skills.clear();

        List<L1Skills> list = selectList();

        for (L1Skills skill : list) {
            if (skill == null)
                continue;

            skills.put(skill.getSkillId(), skill);
        }
    }

    public Map<Integer, L1Skills> getSkills() {
        return skills;
    }

    public List<L1Skills> selectList() {
        return SqlUtils.query("SELECT * FROM skills", (rs, i) -> {
            L1Skills skill = new L1Skills();

            int skillId = rs.getInt("skill_id");

            skill.setSkillId(skillId);
            skill.setName(rs.getString("name"));
            skill.setSkillLevel(rs.getInt("skill_level"));
            skill.setSkillNumber(rs.getInt("skill_number"));
            skill.setMpConsume(rs.getInt("mpConsume"));
            skill.setHpConsume(rs.getInt("hpConsume"));
            skill.setItemConsumeId(rs.getInt("itemConsumeId"));
            skill.setItemConsumeCount(rs.getInt("itemConsumeCount"));
            skill.setReuseDelay(rs.getInt("reuseDelay"));
            skill.setBuffDuration(rs.getInt("buffDuration"));
            skill.setTarget(rs.getString("target"));
            skill.setTargetTo(rs.getInt("target_to"));
            skill.setDamageValue(rs.getInt("damage_value"));
            skill.setDamageDice(rs.getInt("damage_dice"));
            skill.setDamageDiceCount(rs.getInt("damage_dice_count"));
            skill.setProbabilityValue(rs.getInt("probability_value"));
            skill.setProbabilityDice(rs.getInt("probability_dice"));
            skill.setAttr(rs.getInt("attr"));
            skill.setType(rs.getInt("type"));
            skill.setLawful(rs.getInt("lawful"));
            skill.setRanged(rs.getInt("ranged"));
            skill.setArea(rs.getInt("area"));//범위
            skill.setThrough(rs.getBoolean("through"));
            skill.setId(rs.getInt("id"));
            skill.setNameId(rs.getString("nameid"));
            skill.setActionId(rs.getInt("action_id"));
            skill.setCastGfx(rs.getInt("castgfx"));
            skill.setCastGfx2(rs.getInt("castgfx2"));
            skill.setSysmsgIdHappen(rs.getInt("sysmsgID_happen"));
            skill.setSysmsgIdStop(rs.getInt("sysmsgID_stop"));
            skill.setSystemMsgIdFail(rs.getInt("sysmsgID_fail"));
            skill.setMaxDmg(rs.getInt("max_dmg"));

            return skill;
        });
    }

    public void spellMastery(int playerobjid, int skillid, String skillname, int active, int time) {
        if (spellCheck(playerobjid, skillid)) {
            return;
        }

        L1PcInstance pc = (L1PcInstance) L1World.getInstance().findObject(playerobjid);

        if (pc != null) {
            pc.setSkillMastery(skillid);
        }

        SqlUtils.update("INSERT INTO character_skills SET char_obj_id=?, skill_id=?, skill_name=?, is_active=?, activetimeleft=?", playerobjid, skillid, skillname, active, time);
    }

    public void spellLost(int playerObjId, int skillId) {
        L1PcInstance pc = (L1PcInstance) L1World.getInstance().findObject(playerObjId);

        if (pc != null) {
            pc.removeSkillMastery(skillId);
        }

        SqlUtils.update("DELETE FROM character_skills WHERE char_obj_id=? AND skill_id=?", playerObjId, skillId);
    }

    public boolean spellCheck(int playerobjid, int skillid) {
        return SqlUtils.selectInteger("SELECT count(*) FROM character_skills WHERE char_obj_id=? AND skill_id=?", playerobjid, skillid) > 0;
    }

    public L1Skills getTemplate(int i) {
        return skills.get(i);
    }

    public int findSkillIdByNameWithoutSpace(String name) {
        int skillId = 0;

        for (L1Skills skill : skills.values()) {
            if (skill != null && skill.getName().replace(" ", "").equals(name)) {
                skillId = skill.getSkillId();
                break;
            }
        }

        return skillId;
    }
}
