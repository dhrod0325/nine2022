package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.instance.L1TowerInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;

public class ResurrectionScroll extends L1ItemInstance {
    public ResurrectionScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = useItem.getItemId();

            L1Character reObject = (L1Character) L1World.getInstance().findObject(packet.readD());

            if (reObject != null) {
                if (reObject instanceof L1PcInstance) {
                    L1PcInstance target = (L1PcInstance) reObject;

                    if (pc.getId() == target.getId()) {
                        return;
                    }

                    if (L1World.getInstance().getVisiblePlayer(target, 0).size() > 0) {
                        for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(target, 0)) {
                            if (!visiblePc.isDead()) {
                                pc.sendPackets(new S_ServerMessage(592));
                                return;
                            }
                        }
                    }

                    if (target.getCurrentHp() == 0 && target.isDead()) {
                        if (pc.isUseResurrection()) {
                            target.setTempID(pc.getId());
                            if (itemId == 40089) {
                                target.sendPackets(new S_Message_YN(321, ""));
                            } else if (itemId == 140089) {
                                target.sendPackets(new S_Message_YN(322, ""));
                            }
                        } else {
                            return;
                        }
                    }
                } else if (reObject instanceof L1NpcInstance) {
                    if (!(reObject instanceof L1TowerInstance)) {
                        L1NpcInstance npc = (L1NpcInstance) reObject;

                        if (npc instanceof L1PetInstance) {
                            if (L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
                                for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
                                    if (!visiblePc.isDead()) {
                                        pc.sendPackets(new S_ServerMessage(592));
                                        return;
                                    }
                                }
                            }
                        } else {
                            if (npc.getTemplate().isCantResurrect()) {
                                pc.getInventory().removeItem(useItem, 1);
                                return;
                            }
                        }

                        if (npc.getCurrentHp() == 0 && npc.isDead()) {
                            npc.resurrect(npc.getMaxHp() / 4);
                        }
                    }
                }
            }

            pc.getInventory().removeItem(useItem, 1);
        }
    }
}
