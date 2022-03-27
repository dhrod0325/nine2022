package basic.test.simulator;

import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1Skills;
import ks.model.attack.magic.L1MagicRun;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_SelectCharacter;
import basic.test.BaseTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestPc extends BaseTest {
    public static void main(String[] args) throws Exception {
        L1PcInstance attacker = new CharacterTable().loadCharacter("법쟈");
        C_SelectCharacter.init(attacker);

        L1PcInstance target = new CharacterTable().loadCharacter("테스트기사");
        C_SelectCharacter.init(target);

        L1MagicRun magic = new L1MagicRun(attacker, target);

        Map<Integer, L1Skills> skills = SkillsTable.getInstance().getSkills();
        List<Result> results = new ArrayList<>();

        target.getResistance().addMr(100);
        for (int i = 0; i < 20; i++) {
            int prob = avgProb(magic, L1SkillId.CANCELLATION);

            results.add(new Result("캔슬", target.getResistance().getEffectedMrBySkill(), prob));

            target.getResistance().addMr(-10);
        }

        for (Result result : results) {
            System.out.printf("skill : %s, mr:%d, per : %d%n", result.name, result.mr, result.value);
        }

        System.exit(0);
    }

    private static class Result {
        public String name;
        public int mr;
        public int value;

        public Result(String name, int mr, int value) {
            this.name = name;
            this.mr = mr;
            this.value = value;
        }
    }

    public static int avgProb(L1MagicRun magic, int skillId) {
        int count = 100;

        int avg = 0;

        for (int cnt = 0; cnt < count; cnt++) {
            int prob = magic.getMagicAttack().getProbability().calcProbability(skillId);
            avg += prob;
        }

        return avg / count;
    }

    public static int avgDmg(L1MagicRun magic, int skillId) {
        int count = 100;

        int avg = 0;

        for (int cnt = 0; cnt < count; cnt++) {
            int damage = magic.calcMagicDamage(skillId);
            avg += damage;
        }

        return avg / count;
    }
}
