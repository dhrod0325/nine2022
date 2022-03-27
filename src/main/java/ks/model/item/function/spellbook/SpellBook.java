package ks.model.item.function.spellbook;

import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1ItemDelay;
import ks.model.L1Skills;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.BraveAvatarScheduler;

public class SpellBook extends L1ItemInstance {
    public SpellBook(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = useItem.getItemId();

            if (useItem.getItem().getType2() == 0) {
                int delayId = useItem.getItem().getDelayId();

                if (delayId != 0) {
                    if (pc.hasItemDelay(delayId)) {
                        return;
                    }
                }
            }

            if (itemId > 40169 && itemId < 40226 || itemId >= 45000 && itemId <= 45022) { // 마법서
                useSkillBook(pc, useItem, itemId);
            } else if ((itemId > 40225 && itemId < 40232) || itemId == 6000103) {
                if (pc.isCrown() || pc.isGm()) {
                    if (itemId == 40226 && pc.getLevel() >= 15) {
                        useCrownBook(pc, useItem);
                    } else if (itemId == 40228 && pc.getLevel() >= 30) {
                        useCrownBook(pc, useItem);
                    } else if (itemId == 40227 && pc.getLevel() >= 40) {
                        useCrownBook(pc, useItem);
                    } else if (itemId == 40231 && pc.getLevel() >= 45) {
                        useCrownBook(pc, useItem);
                    } else if (itemId == 40230 && pc.getLevel() >= 50) {
                        useCrownBook(pc, useItem);
                    } else if (itemId == 40229 && pc.getLevel() >= 55) {
                        useCrownBook(pc, useItem);
                    } else if (itemId == 6000103 && pc.getLevel() >= 60) {
                        useCrownBook(pc, useItem);
                        BraveAvatarScheduler.getInstance().addCrown(pc);
                    } else {
                        pc.sendPackets(new S_ServerMessage(312));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (itemId >= 40232 && itemId <= 40264 || itemId >= 41149 && itemId <= 41153) {
                useElfSpellBook(pc, useItem, itemId);
            } else if (itemId > 40264 && itemId < 40280 || itemId == 5559) {
                if (pc.isDarkElf() || pc.isGm()) {
                    if (itemId >= 40265 && itemId <= 40269 && pc.getLevel() >= 15) {
                        useDarkElfBook(pc, useItem);
                    } else if (itemId >= 40270 && itemId <= 40274 && pc.getLevel() >= 30) {
                        useDarkElfBook(pc, useItem);
                    } else if (itemId >= 40275 && pc.getLevel() >= 45) {
                        useDarkElfBook(pc, useItem);
                    } else if (itemId == 5559 && pc.getLevel() >= 60) {
                        useDarkElfBook(pc, useItem);
                    } else {
                        pc.sendPackets(new S_ServerMessage(312));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (itemId >= 40164 && itemId <= 40166 || itemId >= 41147 && itemId <= 41148) {
                if (pc.isKnight() || pc.isGm()) {
                    if (itemId <= 40165 && pc.getLevel() >= 50) {
                        useKnightBook(pc, useItem);
                    } else if (itemId >= 41147 && pc.getLevel() >= 50) {
                        useKnightBook(pc, useItem);
                    } else if (itemId == 40166 && pc.getLevel() >= 60) { // 바운스아탁크
                        useKnightBook(pc, useItem);
                    } else {
                        pc.sendPackets(new S_ServerMessage(312));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }

            L1ItemDelay.onItemUse(pc, useItem);
        }
    }

    private void useSkillBook(L1PcInstance pc, L1ItemInstance item, int itemId) {
        boolean isLawful = true;

        int pcX = pc.getX();
        int pcY = pc.getY();
        int mapId = pc.getMapId();
        int level = pc.getLevel();

        if (pcX > 32880 && pcX < 32892 && pcY > 32646 && pcY < 32658 && mapId == 4 || pcX > 32662 && pcX < 32674 && pcY > 32297 && pcY < 32309 && mapId == 4) {
            isLawful = false;
        }

        if (pc.isGm()) {
            useSkillBook(pc, item, isLawful);
        } else {
            if (pc.isKnight()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 50) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45000 && itemId <= 45007) {
                    pc.sendPackets(new S_ServerMessage(312));
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (pc.isCrown() || pc.isDarkElf()) {
                if (itemId == 40178 && pc.isCrown()) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45000 && itemId <= 45007 && level >= 10) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 && level >= 20) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 || itemId >= 45000 && itemId <= 45007) {
                    pc.sendPackets(new S_ServerMessage(312));
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (pc.isElf()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 8) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 && level >= 16) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45016 && itemId <= 45022 && level >= 24) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40170 && itemId <= 40177 && level >= 32) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40179 && itemId <= 40185 && level >= 40) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40186 && itemId <= 40193 && level >= 48) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45000 && itemId <= 45022 || itemId >= 40170 && itemId <= 40193) {
                    pc.sendPackets(new S_ServerMessage(312));
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (pc.isWizard()) {
                if (itemId >= 45000 && itemId <= 45007 && level >= 4) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45008 && itemId <= 45015 && level >= 8) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 45016 && itemId <= 45022 && level >= 12) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40170 && itemId <= 40177 && level >= 16) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40179 && itemId <= 40185 && level >= 20) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40186 && itemId <= 40193 && level >= 24) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40194 && itemId <= 40201 && level >= 28) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40202 && itemId <= 40209 && level >= 32) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40210 && itemId <= 40217 && level >= 36) {
                    useSkillBook(pc, item, isLawful);
                } else if (itemId >= 40218 && itemId <= 40225 && level >= 40) {
                    useSkillBook(pc, item, isLawful);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }
        }
    }

    private void useElfSpellBook(L1PcInstance pc, L1ItemInstance item, int itemId) {
        int level = pc.getLevel();

        if ((pc.isElf() || pc.isGm())) {
            if (itemId >= 40232 && itemId <= 40234 && level >= 10) {
                useElfBook(pc, item);
            } else if (itemId >= 40235 && itemId <= 40236 && level >= 20) {
                useElfBook(pc, item);
            } else if (itemId >= 40237 && itemId <= 40240 && level >= 30) {
                useElfBook(pc, item);
            } else if (itemId >= 40241 && itemId <= 40243 && level >= 40) {
                useElfBook(pc, item);
            } else if (itemId >= 40244 && itemId <= 40246 && level >= 50) {
                useElfBook(pc, item);
            } else if (itemId >= 40247 && itemId <= 40248 && level >= 30) {
                useElfBook(pc, item);
            } else if (itemId >= 40249 && itemId <= 40250 && level >= 40) {
                useElfBook(pc, item);
            } else if (itemId >= 40251 && itemId <= 40252 && level >= 50) {
                useElfBook(pc, item);
            } else if (itemId == 40253 && level >= 30) {
                useElfBook(pc, item);
            } else if (itemId == 40254 && level >= 40) {
                useElfBook(pc, item);
            } else if (itemId == 40255 && level >= 50) {
                useElfBook(pc, item);
            } else if (itemId == 40256 && level >= 30) {
                useElfBook(pc, item);
            } else if (itemId == 40257 && level >= 40) {
                useElfBook(pc, item);
            } else if (itemId >= 40258 && itemId <= 40259 && level >= 50) {
                useElfBook(pc, item);
            } else if (itemId >= 40260 && itemId <= 40261 && level >= 30) {
                useElfBook(pc, item);
            } else if (itemId == 40262 && level >= 40) {
                useElfBook(pc, item);
            } else if (itemId >= 40263 && itemId <= 40264 && level >= 50) {
                useElfBook(pc, item);
            } else if (itemId >= 41149 && itemId <= 41150 && level >= 50) {
                useElfBook(pc, item);
            } else if (itemId == 41151 && level >= 40) {
                useElfBook(pc, item);
            } else if (itemId >= 41152 && itemId <= 41153 && level >= 50) {
                useElfBook(pc, item);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }

    private void useSkillBook(L1PcInstance pc, L1ItemInstance item, boolean isLawful) {
        SpellBookUse spellBookUse = new SpellBookUse(isLawful ? 224 : 231);

        for (int skillId = 1; skillId <= 80; skillId++) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            String bookName = "마법서 (" + skill.getName() + ")";
            spellBookUse.init(skillId, bookName, item);
        }

        spellBookUse.useItem(pc, item);
    }

    private void useDarkElfBook(L1PcInstance pc, L1ItemInstance item) {
        SpellBookUse spellBookUse = new SpellBookUse(231);

        for (int skillId = 97; skillId <= 112; skillId++) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            String bookName = "흑정령의 수정 (" + skill.getName() + ")";
            spellBookUse.init(skillId, bookName, item);
        }

        spellBookUse.useItem(pc, item);
    }

    private void useElfBook(L1PcInstance pc, L1ItemInstance item) {
        SpellBookUse spellBook = new SpellBookUse(224);

        for (int skillId = 129; skillId <= 176; skillId++) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            String bookName = "정령의 수정 (" + skill.getName() + ")";
            String itemName = item.getName();

            if (itemName.equalsIgnoreCase(bookName)) {
                if (!pc.isGm() && skill.getAttr() != 0 && pc.getElfAttr() != skill.getAttr()) {
                    if (pc.getElfAttr() == 0 || pc.getElfAttr() == 1 || pc.getElfAttr() == 2 || pc.getElfAttr() == 4 || pc.getElfAttr() == 8) {
                        pc.sendPackets(new S_ServerMessage(79));
                        return;
                    }
                }
            }

            spellBook.init(skillId, bookName, item);
        }

        spellBook.useItem(pc, item);
    }

    private void useKnightBook(L1PcInstance pc, L1ItemInstance item) {
        SpellBookUse spellBook = new SpellBookUse(224);

        for (int skillId = 87; skillId <= 91; skillId++) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            String bookName = "기술서 (" + skill.getName() + ")";
            spellBook.init(skillId, bookName, item);
        }

        spellBook.useItem(pc, item);
    }

    private void useCrownBook(L1PcInstance pc, L1ItemInstance item) {
        SpellBookUse spellBook = new SpellBookUse(224);

        for (int skillId = 113; skillId < 121; skillId++) {
            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);
            String bookName = "마법서 (" + skill.getName() + ")";
            spellBook.init(skillId, bookName, item);
        }

        spellBook.useItem(pc, item);
    }
}
