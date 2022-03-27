package ks.test;

import ks.base.AbstractTest;
import ks.constants.L1SkillId;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Location;
import ks.model.L1World;
import ks.model.attack.physics.L1AttackRun;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.util.L1ClassUtils;

public class HitTest extends AbstractTest {
    L1PcInstance attacker;
    L1PcInstance target;

    public void test() {
        //testStaff();

        //testKnight();

        testBow1();
    }

    public void testKnight() {
        pcInit();

        attacker.getAbility().setBaseStr(18);
        attacker.getAbility().setStr(35);
        attacker.getAbility().setInt(13);
        attacker.setClassId(L1ClassUtils.CLASSID_KNIGHT_FEMALE);
        attacker.getPcExpManager().statRefresh();

        //test(attacker, target, 62, 124);//장궁,헤비
        test(attacker, target, 415010, 415011);//장궁,헤비
    }

    public void testStaff() {
        pcInit();

        attacker.getAbility().setBaseInt(18);
        attacker.getAbility().setInt(13);
        attacker.setClassId(L1ClassUtils.CLASSID_WIZARD_FEMALE);
        attacker.getPcExpManager().statRefresh();

        //test(attacker, target, 121, 124);//얼지,바지
        test(attacker, target, 415013, 0);//테지
    }

    public void testBow1() {
        for (int i = 0; i < 10; i++) {
            pcInit();

            attacker.getAbility().setBaseDex(18);
            attacker.getAbility().setDex(35);
            attacker.setClassId(L1ClassUtils.CLASSID_ELF_MALE);
            attacker.getPcExpManager().statRefresh();

            System.out.println(attacker.getAbility().getTotalDex());

            new L1SkillUse(attacker, L1SkillId.STORM_SHOT, 1800, L1SkillUse.TYPE_LOGIN).run();
            new L1SkillUse(attacker, L1SkillId.PHYSICAL_ENCHANT_DEX, 1800, L1SkillUse.TYPE_LOGIN).run();

            //http://lineage.gameabout.com/bbs/view.ga?id=380&row_no=14&page=7
            test(attacker, target, 181, 188);//장궁,헤비

            //
            //test(attacker, target, 205, 190);//달장,사이하
//            test(attacker, target, 205, 189);//달장,흑왕궁

            System.out.println();
        }
    }

    public void pcInit() {
        attacker = new L1PcInstance();
        attacker.setName("공격자");

        attacker.getInventory().storeItem(40746, 2000);
        attacker.setLocation(new L1Location(33107, 32903, 4));
        attacker.setMaxHp(50000);
        attacker.setCurrentMp(50000);
        attacker.setLevel(70);

        target = new L1PcInstance();
        target.setName("샌드백");
        target.setLevel(70);
        target.getAC().addAc(-81);
        target.setMaxHp(50000);
        target.setCurrentMp(50000);

        target.setLocation(attacker.getLocation());

        L1World.getInstance().addVisibleObject(attacker);
        L1World.getInstance().addVisibleObject(target);
    }

    public void test(L1PcInstance attacker, L1PcInstance target, int itemId1, int itemId2) {
        L1ItemInstance item1 = ItemTable.getInstance().createItem(itemId1);
        item1.setEnchantLevel(9);

        int hitFail = 0;
        int hitTotalDmg = 0;
        int totalDmg = 0;
        int count = 100;

        attacker.getInventory().setEquipped(item1, true);

        for (int i = 0; i < count; i++) {
            L1AttackRun run = new L1AttackRun();
            run.getAttackParam().setHitCheck(false);
            run.build(attacker, target);

            run.commit();

            if (run.getAttackParam().isHitUp()) {
                hitTotalDmg += run.getAttackParam().getDamage();
            } else {
                hitFail++;
            }

            totalDmg += run.getAttackParam().getDamage();
        }

        logger.debug("{} : 빗나감 : {} 유효평균 : {} 전체평균 : {} ", item1.getName(), hitFail, hitTotalDmg / (100 - hitFail), totalDmg / 100);

        if (itemId2 == 0) {
            return;
        }

        hitFail = 0;
        hitTotalDmg = 0;
        totalDmg = 0;


        L1ItemInstance item2 = ItemTable.getInstance().createItem(itemId2);
        item2.setEnchantLevel(9);

        attacker.getInventory().setEquipped(item2, true);

        for (int i = 0; i < count; i++) {
            L1AttackRun run = new L1AttackRun();
            run.getAttackParam().setHitCheck(false);
            run.build(attacker, target);

            run.commit();

            if (run.getAttackParam().isHitUp()) {
                hitTotalDmg += run.getAttackParam().getDamage();
            } else {
                hitFail++;
            }

            totalDmg += run.getAttackParam().getDamage();
        }

        logger.debug("{} : 빗나감 : {} 유효평균 : {} 전체평균 : {} ", item2.getName(), hitFail, hitTotalDmg / (100 - hitFail), totalDmg / 100);
    }
}
