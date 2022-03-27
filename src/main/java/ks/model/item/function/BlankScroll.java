package ks.model.item.function;

import ks.core.datatables.SkillsTable;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Character;
import ks.model.L1Inventory;
import ks.model.L1Item;
import ks.model.L1Skills;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;

public class BlankScroll extends L1ItemInstance {

    public BlankScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int blankScSkillId = packet.readC();

            int itemId = useItem.getItemId();

            if (pc.isWizard()) {
                if (itemId == 40090 && blankScSkillId <= 7 ||
                        itemId == 40091 && blankScSkillId <= 15 ||
                        itemId == 40092 && blankScSkillId <= 22 ||
                        itemId == 40093 && blankScSkillId <= 31 ||
                        itemId == 40094 && blankScSkillId <= 39) {
                    L1ItemInstance spellsc = ItemTable.getInstance().createItem(40859 + blankScSkillId);

                    if (spellsc != null) {
                        if (pc.getInventory().checkAddItem(spellsc, 1) == L1Inventory.OK) {
                            L1Skills skills = SkillsTable.getInstance().getTemplate(blankScSkillId + 1);

                            if (pc.getCurrentHp() + 1 < skills.getHpConsume() + 1) {
                                pc.sendPackets(new S_ServerMessage(279));
                                return;
                            }

                            if (pc.getCurrentMp() < skills.getMpConsume()) {
                                pc.sendPackets(new S_ServerMessage(278));
                                return;
                            }

                            if (skills.getItemConsumeId() != 0) {
                                if (!pc.getInventory().checkItem(skills.getItemConsumeId(), skills.getItemConsumeCount())) {
                                    pc.sendPackets(new S_ServerMessage(299));
                                    return;
                                }
                            }

                            pc.setCurrentHp(pc.getCurrentHp() - skills.getHpConsume());
                            pc.setCurrentMp(pc.getCurrentMp() - skills.getMpConsume());

                            int lawful = pc.getLawful() + skills.getLawful();
                            if (lawful > 32767) {
                                lawful = 32767;
                            }
                            if (lawful < -32767) {
                                lawful = -32767;
                            }

                            pc.setLawful(lawful);

                            if (skills.getItemConsumeId() != 0) {
                                pc.getInventory().consumeItem(skills.getItemConsumeId(), skills.getItemConsumeCount());
                            }

                            pc.getInventory().removeItem(useItem, 1);
                            pc.getInventory().storeItem(spellsc);
                        }
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(591));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(264));
            }
        }
    }
}
