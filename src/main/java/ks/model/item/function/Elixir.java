package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_OwnCharStatus2;
import ks.packets.serverpackets.S_ServerMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Elixir extends L1ItemInstance {
    private static final Logger logger = LogManager.getLogger(Elixir.class);

    public Elixir(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        try {
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

                int itemId = useItem.getItemId();
                int minLevel = useItem.getItem().getMinLevel();
                int maxLevel = useItem.getItem().getMaxLevel();

                if (minLevel != 0 && minLevel > pc.getLevel() && !pc.isGm()) {
                    pc.sendPackets(new S_ServerMessage(318, String.valueOf(minLevel)));
                    return;
                } else if (maxLevel != 0 && maxLevel < pc.getLevel() && !pc.isGm()) {
                    pc.sendPackets(new S_ServerMessage(673, String.valueOf(maxLevel)));
                    return;
                }

                switch (itemId) {
                    case 40033:
                        if (pc.getAbility().getStr() < 35 && pc.getAbility().getElixirCount() < 5) {
                            pc.getAbility().addStr((byte) 1);
                            pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(481));
                        }

                        break;
                    case 40034:
                        if (pc.getAbility().getCon() < 35 && pc.getAbility().getElixirCount() < 5) {
                            pc.getAbility().addCon((byte) 1);
                            pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(481));
                        }
                        break;
                    case 40035:
                        if (pc.getAbility().getDex() < 35 && pc.getAbility().getElixirCount() < 5) {
                            pc.getAbility().addDex((byte) 1);
                            pc.getPcExpManager().resetAc();
                            pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(481));
                        }
                        break;
                    case 40036:
                        if (pc.getAbility().getInt() < 35 && pc.getAbility().getElixirCount() < 5) {
                            pc.getAbility().addInt((byte) 1);
                            pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(481));
                        }
                        break;
                    case 40037:
                        if (pc.getAbility().getWis() < 35 && pc.getAbility().getElixirCount() < 5) {
                            pc.getAbility().addWis((byte) 1);
                            pc.getPcExpManager().resetMr();
                            pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(481));
                        }
                        break;
                    case 40038:
                        if (pc.getAbility().getCha() < 35 && pc.getAbility().getElixirCount() < 5) {
                            pc.getAbility().addCha((byte) 1);
                            pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
                            pc.getInventory().removeItem(useItem, 1);
                            pc.sendPackets(new S_OwnCharStatus2(pc));
                            pc.save();
                        } else {
                            pc.sendPackets(new S_ServerMessage(481));
                        }
                        break;
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
