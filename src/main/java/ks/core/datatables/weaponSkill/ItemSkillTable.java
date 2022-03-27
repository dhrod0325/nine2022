package ks.core.datatables.weaponSkill;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.core.datatables.SkillsTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_EffectLocation;
import ks.packets.serverpackets.S_UseAttackSkill;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ItemSkillTable {
    private final List<ItemSkill> list = new ArrayList<>();

    public static ItemSkillTable getInstance() {
        return LineageAppContext.getBean(ItemSkillTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<ItemSkill> selectList() {
        String sql = "SELECT * FROM item_skill";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(ItemSkill.class));
    }

    public List<ItemSkill> getList() {
        return list;
    }

    public ItemSkill find(int weaponId) {
        return list.stream()
                .filter(Objects::nonNull)
                .filter(skill -> skill.getItemId() == weaponId)
                .findFirst()
                .orElse(null);
    }

    public int findDmg(L1ItemInstance item, L1PcInstance pc, L1Character cha) {
        if (item == null)
            return 0;

        ItemSkill v = find(item.getItemId());

        if (v == null) {
            return 0;
        }

        if (v.getSkillStartEnchant() > 0) {
            if (item.getEnchantLevel() < v.getSkillStartEnchant()) {
                return 0;
            }
        }

        int prob = v.getProbability();

        if (v.getEnchantProbabilityStart() >= item.getEnchantLevel()) {
            prob += (v.getEnchantProbabilityStart() - (item.getEnchantLevel() - 1)) * v.getEnchantProbability();
        }

        if (RandomUtils.isWinning(100, prob)) {
            int dmg = RandomUtils.nextInt(v.getDmgMin(), v.getDmgMax());

            double coefficient = 1;

            if ("int".equalsIgnoreCase(v.getDmgType())) {
                int powerInt = pc.getAbility().getTotalInt();
                coefficient = (powerInt - 9) * CodeConfig.MAGIC_DMG_INT;
            } else if ("sp".equalsIgnoreCase(v.getDmgType())) {
                int powerSp = pc.getAbility().getSp() - pc.getAbility().getMagicLevel();
                coefficient = 1.0 + (powerSp + 1) * CodeConfig.MAGIC_DMG_SP;
            } else if ("intSp".equalsIgnoreCase(v.getDmgType())) {
                int powerInt = pc.getAbility().getTotalInt();
                int powerSp = pc.getAbility().getSp();

                coefficient = 1.0 + (powerSp + 1) * CodeConfig.MAGIC_DMG_SP;
                coefficient += (powerInt - 9) * CodeConfig.MAGIC_DMG_INT;
            } else if ("drainHp".equalsIgnoreCase(v.getDmgType())) {
                pc.setDrainHp(dmg);
            } else if ("drainMp".equalsIgnoreCase(v.getDmgType())) {
                pc.setDrainMp(dmg);

                if (pc.getDrainMp() > CodeConfig.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
                    pc.setDrainMp(CodeConfig.MANA_DRAIN_LIMIT_PER_SOM_ATTACK);
                }
            }

            if (coefficient < 1) {
                coefficient = 1;
            }

            dmg *= coefficient;

            switch (v.getSkillType()) {
                case ItemSkill.SKILL_TYPE_ATTACK_SKILL:
                    pc.sendPackets(new S_UseAttackSkill(pc, cha.getId(), v.getEffectId(), cha.getX(), cha.getY(), L1ActionCodes.ACTION_Attack, false));
                    Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, cha.getId(), v.getEffectId(), cha.getX(), cha.getY(), L1ActionCodes.ACTION_Attack, false));
                    break;
                case ItemSkill.SKILL_TYPE_MAGIC:
                    if (cha.getSkillEffectTimerSet().hasSkillEffect(v.getSkillId())) {
                        cha.getSkillEffectTimerSet().killSkillEffectTimer(v.getSkillId());
                    }

                    L1Skills skill = SkillsTable.getInstance().getTemplate(v.getSkillId());
                    cha.getSkillEffectTimerSet().setSkillEffect(v.getSkillId(), v.getSkillTime() * 1000);
                    L1CommonUtils.locationEffect(cha, cha.getX(), cha.getY(), skill.getCastGfx());

                    break;
                case ItemSkill.SKILL_TYPE_NORMAL:
                    if (v.getEffectId() > 0) {
                        pc.sendPackets(new S_EffectLocation(cha.getX(), cha.getY(), v.getEffectId()));
                        Broadcaster.broadcastPacket(pc, new S_EffectLocation(cha.getX(), cha.getY(), v.getEffectId()));
                    }

                    break;
            }

            return dmg;

        }

        return 0;
    }
}
