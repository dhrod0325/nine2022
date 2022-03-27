package ks.test;

import ks.base.AbstractTest;
import ks.constants.L1SkillId;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1Location;
import ks.model.attack.magic.L1MagicRun;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import ks.util.L1ClassUtils;

public class MagicDamageTest extends AbstractTest {
    public void pcToPc() {
        L1PcInstance attacker = new L1PcInstance();
        attacker.setName("공격자");
        attacker.setLevel(50);
        attacker.getAbility().setBaseInt(18);
        attacker.getAbility().setInt(21);
        attacker.getAbility().addSp(4);
        
        attacker.setClassId(L1ClassUtils.CLASSID_WIZARD_MALE);
        attacker.setLocation(new L1Location(33107, 32903, 4));

        L1PcInstance target = new L1PcInstance();
        target.setName("샌드백");
        target.setLevel(50);
        target.getAbility().setInt(21);

        target.setLocation(attacker.getLocation());
        target.getResistance().setBaseMr(100);

        int mr = target.getResistance().getEffectedMrBySkill();

        int count = 100;
        int sum = 0;

        for (int i = 0; i < count; i++) {
            L1MagicRun magicRun = new L1MagicRun(attacker, target);
            int dmg = magicRun.calcMagicDamage(L1SkillId.LIGHTNING);

            logger.debug("int : {},sp : {},dmg : {},mr : {}",
                    attacker.getAbility().getInt(),
                    attacker.getAbility().getSp(),
                    dmg,
                    mr);

            sum += dmg;
        }

        logger.debug("avg : {} ", sum / count);

        System.out.println();

        attacker.setLevel(57);
        attacker.getAbility().setInt(28);

        sum = 0;

        for (int i = 0; i < count; i++) {
            L1MagicRun magicRun = new L1MagicRun(attacker, target);
            int dmg = magicRun.calcMagicDamage(L1SkillId.LIGHTNING);

            logger.debug("int : {},sp : {},dmg : {},mr : {}",
                    attacker.getAbility().getInt(),
                    attacker.getAbility().getSp(),
                    dmg,
                    mr);

            sum += dmg;
        }

        logger.debug("avg : {} ", sum / count);

        System.out.println();

        attacker.setLevel(70);
        attacker.getAbility().setInt(35);

        sum = 0;

        for (int i = 0; i < count; i++) {
            L1MagicRun magicRun = new L1MagicRun(attacker, target);
            int dmg = magicRun.calcMagicDamage(L1SkillId.LIGHTNING);

            logger.debug("int : {},sp : {},dmg : {},mr : {}",
                    attacker.getAbility().getInt(),
                    attacker.getAbility().getSp(),
                    dmg,
                    mr);

            sum += dmg;
        }

        logger.debug("avg : {} ", sum / count);


//        L1PcInstance attacker = CharacterTable.getInstance().loadCharacter("미소피아");
//        C_SelectCharacter.init(attacker);
//
//        L1PcInstance target = CharacterTable.getInstance().loadCharacter("버닝");
//        C_SelectCharacter.init(target);
//
//        target.setLocation(attacker.getLocation());
//
//        attacker.getAbility().setInt(20);
//        attacker.getAbility().addSp(8);
//
//        target.getResistance().addMr(-50);
//        int mr = target.getResistance().getEffectedMrBySkill();
//
//        for (int i = 0; i < 10; i++) {
//            L1MagicRun magicRun = new L1MagicRun(attacker, target);
//            int dmg = magicRun.calcMagicDamage(74);
//
//            logger.debug("int : {},sp : {},dmg : {},mr : {}",
//                    attacker.getAbility().getInt(),
//                    attacker.getAbility().getSp(),
//                    dmg,
//                    mr);
//        }
    }

    public void npcToPc() {
        try {
            L1PcInstance target = CharacterTable.getInstance().loadCharacter("기사1");
            C_SelectCharacter.init(target);

            L1NpcInstance attacker = NpcTable.getInstance().newNpcInstance(8500019);
            target.setLocation(attacker.getLocation());

            for (int i = 0; i < 50; i++) {
                int mr = target.getResistance().getEffectedMrBySkill();

                L1MagicRun magicRun = new L1MagicRun(attacker, target);
                int dmg = magicRun.calcMagicDamage(707074);

                logger.debug("int : {},sp : {},dmg : {},mr : {}",
                        attacker.getAbility().getInt(),
                        attacker.getAbility().getSp(),
                        dmg,
                        mr);

                target.getResistance().addMr(5);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
