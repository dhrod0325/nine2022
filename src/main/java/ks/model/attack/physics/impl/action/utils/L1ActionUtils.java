package ks.model.attack.physics.impl.action.utils;

import ks.constants.L1ActionCodes;
import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.WeaponDamageTable;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Skills;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.S_AttackCritical;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.L1CharPosUtils;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

public class L1ActionUtils {
    public static boolean actionCounterSuccess(L1Character attacker, L1Character target, int skillId, int gfx) {
        boolean isShortDistance = L1CommonUtils.isShortDistance(attacker, target);

        boolean success = false;

        if (target.getSkillEffectTimerSet().hasSkillEffect(skillId)) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            success = RandomUtils.isWinning(100, skill.getProbabilityValue());

            if (success && isShortDistance) {
                attacker.sendPackets(new S_SkillSound(target.getId(), gfx));
                Broadcaster.broadcastPacket(attacker, new S_SkillSound(target.getId(), gfx));

                attacker.setHeading(L1CharPosUtils.targetDirection(attacker, target.getX(), target.getY()));
                Broadcaster.broadcastPacket(attacker, new S_DoActionGFX(attacker.getId(), L1ActionCodes.ACTION_Damage));
                attacker.sendPackets(new S_DoActionGFX(attacker.getId(), L1ActionCodes.ACTION_Damage));
            }
        }

        return success;
    }

    public static L1ActionCounterResult actionCounter(L1Character attacker, L1Character target) {
        L1ActionCounterResult result = new L1ActionCounterResult();

        if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
            result.setSuccess(actionCounterSuccess(attacker, target, L1SkillId.COUNTER_BARRIER, 10710));

            L1ItemInstance weapon = target.getWeapon();

            if (weapon != null && weapon.getItem().getType() == 3) {
                int v = WeaponDamageTable.getInstance().getNormalValue(attacker, weapon);
                int dmg = (weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + v + weapon.getItem().getDmgModifier()) * 2;
                result.setCounterDmg(dmg);
            }
        } else if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MORTAL_BODY)) {
            result.setSuccess(actionCounterSuccess(attacker, target, L1SkillId.MORTAL_BODY, 6513));
            result.setCounterDmg(30);
        }

        return result;
    }

    public static void criticalAction(L1Character character, L1Character target) {
        int targetId = target.getId();
        int targetX = target.getX();
        int targetY = target.getY();

        L1ItemInstance weapon = character.getWeapon();

        if (weapon == null) {
            return;
        }

        int type = weapon.getItem().getType();

        int gfx = 13414;

        switch (type) {
            //한손검
            case 1:
                gfx = 13411;
                break;
            //단검
            case 2:
                gfx = 13412;
                break;
            case 3:
                //양손검
                gfx = 13410;
                break;
            case 4:
                break;
            //도끼
            case 6:
                gfx = 13414;
                break;
            case 7:
                //지팡이
                gfx = 13413;
                break;
            //크로우
            case 11:
                gfx = 13416;
                break;
            case 12:
                //이도류
                gfx = 13417;
                break;
            //양손도끼
            case 15:
                gfx = 13415;
                break;
        }

        character.sendPackets(new S_AttackCritical(character, targetId, gfx, targetX, targetY));
        Broadcaster.broadcastPacket(character, new S_AttackCritical(character, targetId, gfx, targetX, targetY));
    }
}
