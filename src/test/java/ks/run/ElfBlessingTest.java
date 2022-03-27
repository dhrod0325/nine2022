package ks.run;

import ks.constants.L1SkillId;
import ks.core.datatables.pc.CharacterTable;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.clientpackets.C_SelectCharacter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ElfBlessingTest {
    @Resource
    private CharacterTable storage;

    private final Logger logger = LogManager.getLogger();

    public void test() {
        L1PcInstance pc = storage.loadCharacter("나당");
        C_SelectCharacter.init(pc);

        for (int i = 0; i < 40; i++) {
            logger.debug("pcInt : {}", pc.getAbility().getTotalInt());

            L1SkillUse skillUse = new L1SkillUse(pc, L1SkillId.NATURES_BLESSING, pc.getId(), pc.getX(), pc.getY(), 0);
            skillUse.run();

            pc.getAbility().addInt(1);
        }
    }
}
