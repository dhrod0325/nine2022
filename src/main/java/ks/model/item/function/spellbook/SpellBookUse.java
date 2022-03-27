package ks.model.item.function.spellbook;

import ks.core.datatables.SkillsTable;
import ks.model.Broadcaster;
import ks.model.L1Skills;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_AddSkill;
import ks.packets.serverpackets.S_SkillSound;

public class SpellBookUse {
    public int gfxId;

    public SpellBookUse(int gfxId) {
        this.gfxId = gfxId;
    }

    public int skillId = 0;
    public String skillName;

    public int level1 = 0;
    public int level2 = 0;
    public int level3 = 0;
    public int level4 = 0;
    public int level5 = 0;
    public int level6 = 0;
    public int level7 = 0;
    public int level8 = 0;
    public int level9 = 0;
    public int level10 = 0;
    public int knight1 = 0;
    public int knight2 = 0;
    public int darkElf1 = 0;
    public int darkElf2 = 0;
    public int royal1 = 0;
    public int royal2 = 0;
    public int elf1 = 0;
    public int elf2 = 0;
    public int elf3 = 0;
    public int elf4 = 0;
    public int elf5 = 0;
    public int elf6 = 0;
    public int dk1 = 0;
    public int dk2 = 0;
    public int dk3 = 0;
    public int bw1 = 0;
    public int bw2 = 0;
    public int bw3 = 0;

    public void init(int skillId, String skillItemName, L1ItemInstance item) {
        L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

        String itemName = item.getName();

        if (skillItemName.equalsIgnoreCase(itemName)) {
            int skillLevel = skill.getSkillLevel();
            int sid = skill.getId();

            this.skillName = skill.getName();
            this.skillId = skill.getSkillId();

            switch (skillLevel) {
                case 1:
                    level1 = sid;
                    break;
                case 2:
                    level2 = sid;
                    break;
                case 3:
                    level3 = sid;
                    break;
                case 4:
                    level4 = sid;
                    break;
                case 5:
                    level5 = sid;
                    break;
                case 6:
                    level6 = sid;
                    break;
                case 7:
                    level7 = sid;
                    break;
                case 8:
                    level8 = sid;
                    break;
                case 9:
                    level9 = sid;
                    break;
                case 10:
                    level10 = sid;
                    break;
                case 11:
                    knight1 = sid;
                    break;
                case 12:
                    knight2 = sid;
                    break;
                case 13:
                    darkElf1 = sid;
                    break;
                case 14:
                    darkElf2 = sid;
                    break;
                case 15:
                    royal1 = sid;
                    break;
                case 16:
                    royal2 = sid;
                    break;
                case 17:
                    elf1 = sid;
                    break;
                case 18:
                    elf2 = sid;
                    break;
                case 19:
                    elf3 = sid;
                    break;
                case 20:
                    elf4 = sid;
                    break;
                case 21:
                    elf5 = sid;
                    break;
                case 22:
                    elf6 = sid;
                    break;
                case 23:
                    dk1 = sid;
                    break;
                case 24:
                    dk2 = sid;
                    break;
                case 25:
                    dk3 = sid;
                    break;
                case 26:
                    bw1 = sid;
                    break;
                case 27:
                    bw2 = sid;
                    break;
                case 28:
                    bw3 = sid;
                    break;
            }
        }
    }

    public void useItem(L1PcInstance pc, L1ItemInstance item) {
        pc.sendPackets(new S_AddSkill(
                level1, level2, level3, level4, level5, level6, level7, level8, level9, level10,
                knight1, knight2,
                darkElf1, darkElf2,
                royal1, royal2,
                elf1, elf2, elf3, elf4, elf5, elf6,
                dk1, dk2, dk3,
                bw1, bw2, bw3
        ));

        pc.sendPackets(new S_SkillSound(pc.getId(), gfxId));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxId));

        SkillsTable.getInstance().spellMastery(pc.getId(), skillId, skillName, 0, 0);

        pc.getInventory().removeItem(item, 1);
    }
}
