package ks.run;

import basic.test.basic.AdenDropCheck;
import ks.base.AbstractTest;
import ks.commands.gm.command.executor.L1Describe2;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1Npc;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import ks.util.common.SqlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdenaUpdateTest extends AbstractTest {
    public void test() {
        L1PcInstance pc = new CharacterTable().loadCharacter("기사1");
        C_SelectCharacter.init(pc);

        L1ItemInstance weapon = ItemTable.getInstance().createItem(45000617);
        weapon.setEnchantLevel(8);
        pc.setWeapon(weapon);
        pc.addDmgUp(-12);
        pc.addHitUp(50);

        L1Npc tpl = NpcTable.getInstance().getTemplate(460000192);
        checkNpcDieCount(pc, new L1MonsterInstance(tpl));

        new L1Describe2().desc(pc, pc);

        List<Integer> mapIds = new ArrayList<>(Arrays.asList(
                307,
                308,
                309,
                807,
                808,
                812,
                813,
                108,
                109,
                110,

                200,
                1024,

                53,
                534
        ));

        for (int map : mapIds) {
            updateForMapId(pc, map);
        }

    }

    private final int 기준아데나 = 3121;
    private final int 기준경험치 = 4000;

    public void updateForMapId(L1PcInstance pc, int mapId) {
        String nid = "npc_templateId";

        if (mapId == 53) {
            nid = "transform_id";
        }

        List<Integer> monsterIds = SqlUtils.queryForList(
                "select mobId from droplist where mobid in (select " + nid + " from spawnlist_all where mapid=?) and itemid = 40308",
                Integer.class, mapId
        );

        //select mobId from droplist where mobid in (select npc_templateid from spawnlist_all where mapid=813) and itemid = 40308

        for (int monId : monsterIds) {
            int sum = 0;

            L1Npc tpl = NpcTable.getInstance().getTemplate(monId);

            for (int i = 0; i < 100; i++) {
                L1NpcInstance npc = new L1MonsterInstance(tpl);
                int checkCount = checkNpcDieCount(pc, npc);
                sum += checkCount;
            }

            //45
            AdenDropCheck.CheckAden s = AdenDropCheck.checkAdenCount(기준아데나, 26, sum / 100.0);

            L1MonsterInstance npc = new L1MonsterInstance(tpl);
            npc.setDrop();

            if (npc.getInventory().checkItem(40308)) {
                SqlUtils.update("update droplist set min=?,max=? where mobid=? and itemid=40308", s.min, s.max, monId);
            } else {
                SqlUtils.update("insert into droplist (mobid,mobname,moblevel,mobnote,itemid,itemname,min,max,chance,mobloc)" +
                                "values (?,?,0,'',40308,'아데나',?,?,1000000,'')",
                        monId, npc.getName(), s.min, s.max
                );
            }

            logger.info("name : {}, min : {}, max : {}", tpl.getName(), s.min, s.max);

            double per = s.per;
            int exp = (int) (기준경험치 * per);

            SqlUtils.update("update npc set exp=? where npcid=?",
                    exp, monId
            );

            System.out.println(String.format("name : %s,min : %d,max : %d check : %f", tpl.getName(), s.min, s.max, sum / 100.0));
        }
    }

    public int checkNpcDieCount(L1PcInstance pc, L1NpcInstance npc) {
        int i;

        pc.setCurrentHp(pc.getMaxHp());

        for (i = 0; i < 500; i++) {
            if (npc.isDead()) {
                break;
            }

            npc.setLocation(pc.getLocation());
            pc.getWeapon().setDurability(0);
            npc.onAction(pc);
        }

        //System.out.println(npc.getName() + " 공격횟수 : " + i);

        return i;
    }
}
