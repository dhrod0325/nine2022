package ks.model.attack.physics.impl.damage.pc;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.SkillsTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.attack.physics.impl.L1AttackDamage;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1ArmorUtils;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.attack.utils.L1WeaponUtils;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_EffectLocation;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ks.constants.L1SkillId.*;
import static ks.constants.L1Types.TYPE1_WEAPON_TYPE_CRAW;
import static ks.constants.L1Types.TYPE1_WEAPON_TYPE_EDORYU;

public class DefaultPcDamage implements L1AttackDamage {
    private final Logger logger = LogManager.getLogger();

    private final L1PcInstance attacker;
    private final L1Character target;

    private final L1ItemInstance weapon;

    private int weaponType;

    public DefaultPcDamage(L1PcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;

        L1ItemInstance weapon = this.attacker.getWeapon();
        this.weapon = weapon;

        if (weapon != null) {
            this.weaponType = weapon.getItem().getType1();
        }
    }

    @Override
    public L1PcInstance getAttacker() {
        return attacker;
    }

    @Override
    public L1Character getTarget() {
        return target;
    }

    @Override
    public int calcDamage(L1AttackParam attackParam) {
        try {
            if (weapon == null) {
                return (attacker.getAbility().getTotalStr() / 5) + 1;
            }

            int weaponMaxDamage = L1AttackUtils.getWeaponMaxDamage(attacker, target);

            int wDamage;

            if (attackParam.isCritical()) {
                wDamage = weaponMaxDamage;
            } else {
                wDamage = RandomUtils.nextInt(weaponMaxDamage + 1);
            }

            if (attacker.getSkillEffectTimerSet().hasSkillEffect(SOUL_OF_FLAME)) {
                if (!attacker.isLongAttack()) {
                    wDamage = (int) (weaponMaxDamage * 1.5);
                }
            }

            int enchantDamage = weapon.getEnchantLevel();
            int weaponDamage = wDamage + enchantDamage;

            L1LogUtils.damageLog("무기 대미지 : {}", weaponDamage);

            if (isDarkElfWeapon() && RandomUtils.isWinning(100, weapon.getDoublePer())) {
                if (weaponType == TYPE1_WEAPON_TYPE_EDORYU) {
                    weaponDamage = weaponDamage * 2;
                } else if (weaponType == TYPE1_WEAPON_TYPE_CRAW) {
                    weaponDamage = weaponMaxDamage + 1;
                }

                L1LogUtils.damageLog("더블로 증가한 대미지 : {}", weaponDamage);
            }

            if (attacker.getSkillEffectTimerSet().hasSkillEffect(ELEMENTAL_FIRE)) {
                if (RandomUtils.isWinning(100, 45)) {
                    weaponDamage *= 1.5;
                    L1LogUtils.damageLog("엘리멘탈파이어로 증가한 대미지 : {}", weaponDamage);
                }
            }

            double dmg = weaponDamage;

            if (!attacker.isLongAttack()) {
                dmg += attacker.getTotalDmg() + attacker.getAddDmg();
            } else {
                dmg += attacker.getTotalBowDmg() + attacker.getAddDmg();
            }

            L1LogUtils.damageLog("스탯으로 증가한 대미지 : {}", dmg);

            int addDamage = 0;

            //속성대미지
            addDamage += L1WeaponUtils.getWeaponAttrDamage(weapon.getAttrEnchantLevel());
            L1LogUtils.damageLog("속성공격으로 증가한 대미지 : {}", addDamage + dmg);

            //화살,스팅 대미지
            addDamage += L1WeaponUtils.calcWeaponTypeDamage(attacker);
            L1LogUtils.damageLog("화살,스팅 증가한 대미지 : {}", addDamage + dmg);

            //등급 추가 대미지
            addDamage += L1WeaponUtils.gradeAndEnchantDamage(attacker);
            L1LogUtils.damageLog("등급 증가한 대미지 : {}", addDamage + dmg);

            //귀걸이 대미지
            addDamage += L1ArmorUtils.addDamageByEaring(attacker);
            L1LogUtils.damageLog("귀걸이 대미지 : {}", addDamage + dmg);

            addDamage -= weapon.getDurability() * 3;
            L1LogUtils.damageLog("손상감소 대미지 : {}", addDamage + dmg);

            addDamage += L1WeaponUtils.weaponSkillDamage(attacker, target);

            dmg += addDamage;

            if (attacker.getSkillEffectTimerSet().hasSkillEffect(BURNING_SPIRIT) && isDarkElfWeapon()) {
                double burningDamage = (weaponDamage + addDamage) * 1.5;

                int per = ((attacker.getLevel() - 45) / 5) + weapon.getDoublePer();
                int random = RandomUtils.nextInt(100) + 1;

                if (random < per) {
                    attacker.sendPackets(new S_EffectLocation(target.getX(), target.getY(), 7727));
                    Broadcaster.broadcastPacket(attacker, new S_EffectLocation(target.getX(), target.getY(), 7727));
                    dmg += burningDamage;

                    L1LogUtils.damageLog("버닝 대미지 : {}", dmg);
                }
            }

            if (attacker.getSkillEffectTimerSet().hasSkillEffect(DOUBLE_BRAKE) && isDarkElfWeapon()) {
                double doubleDamage = (weaponDamage * 2) + addDamage;

                int per = ((attacker.getLevel() - 45) / 5) + attacker.getWeapon().getDoublePer();

                int random = RandomUtils.nextInt(100) + 1;

                if (random <= per) {
                    dmg += doubleDamage;

                    attacker.sendPackets(new S_EffectLocation(target.getX(), target.getY(), 6532));
                    Broadcaster.broadcastPacket(attacker, new S_EffectLocation(target.getX(), target.getY(), 6532));
                    L1LogUtils.damageLog("더블브레이크 대미지 : {}", dmg);
                }
            }

            if (attacker.getInventory().getCurrentItem().isValaArmor()) {
                int per = attacker.getInventory().getCurrentItem().getArmor().getMagicPercent();

                if (RandomUtils.isWinning(100, per)) {
                    int valaDamage = (RandomUtils.nextInt(CodeConfig.DRAGON_VALA_MIN, CodeConfig.DRAGON_VALA_MAX));

                    if (attacker.isElf()) {
                        valaDamage *= CodeConfig.DRAGON_VALA_ELF;
                    }

                    dmg += valaDamage;

                    attacker.sendPackets(new S_SkillSound(attacker.getId(), 15841));
                    Broadcaster.broadcastPacket(attacker, new S_SkillSound(attacker.getId(), 15841));

                    L1LogUtils.damageLog("발라증가 대미지 : {}", dmg);
                }
            }

            if (attacker.getCurrentDoll() != null) {
                dmg += attacker.getCurrentDoll().getAttackDamage(attacker, target);
            }

            if (attacker.getSkillEffectTimerSet().hasSkillEffect(BURNING_SLASH)) {
                dmg += 7;
                attacker.sendPackets(new S_SkillSound(attacker.getId(), 6591));
                Broadcaster.broadcastPacket(attacker, new S_SkillSound(attacker.getId(), 6591));
                attacker.getSkillEffectTimerSet().killSkillEffectTimer(BURNING_SLASH);
            }

            if (attacker.getSkillEffectTimerSet().hasSkillEffect(BRAVE_MENTAL) && attacker.isCrown()) {
                L1Skills skill = SkillsTable.getInstance().getTemplate(BRAVE_MENTAL);

                if (RandomUtils.isWinning(100, skill.getProbabilityValue())) {
                    dmg *= 1.5;
                    L1LogUtils.damageLog("브레이브 멘탈 대미지 : {}", dmg);
                }
            }

            if (target.getSkillEffectTimerSet().hasSkillEffect(ARMOR_BRAKE)) {
                if (!attacker.isLongAttack()) {
                    dmg *= 1.58;
                    L1LogUtils.damageLog("아머브레이크 대미지 : {}", dmg);
                }
            }

            if (target.getSkillEffectTimerSet().hasSkillEffect(STRIKER_GALE)) {
                if (attacker.isLongAttack()) {
                    dmg *= 1.1;
                    L1LogUtils.damageLog("게일 대미지 : {}", dmg);
                }
            }

            if (attacker.isTripleAction()) {
                dmg = (int) (dmg * CodeConfig.TRIPLE_BALANCE);
                L1LogUtils.damageLog("트리플대미지 : {}", dmg);
            }

            dmg -= L1AttackUtils.reduceDamageByAc(target);

            L1LogUtils.damageLog("AC에 의한 감소 대미지 : {}", dmg);

            L1AttackUtils.poisonAttack(attacker, target);

            if (dmg <= 0) {
                attacker.setDrainHp(0);
            }

            return (int) dmg;
        } catch (Exception e) {
            logger.error("오류", e);
        }

        return 0;
    }

    private boolean isDarkElfWeapon() {
        return weaponType == TYPE1_WEAPON_TYPE_EDORYU || weaponType == TYPE1_WEAPON_TYPE_CRAW;
    }
}
