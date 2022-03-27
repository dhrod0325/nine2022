package ks.commands.gm.command.executor;

import ks.core.datatables.account.AccountTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.Warehouse;
import ks.model.warehouse.WarehouseManager;
import ks.packets.serverpackets.S_SystemMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1CheckCharacter implements L1CommandExecutor {
    private static final Logger logger = LogManager.getLogger(L1CheckCharacter.class);

    public static L1CommandExecutor getInstance() {
        return new L1CheckCharacter();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String charname = st.nextToken();
            String type = st.nextToken();

            if (type.equalsIgnoreCase("인벤")) {
                try {
                    Integer charId = CharacterTable.getInstance().selectCharIdByName(charname);

                    pc.sendPackets("\\fW** 검사: " + type + " 캐릭: " + charname + " **");

                    L1PcInstance target = L1World.getInstance().getPlayer(charname);

                    if (target == null) {
                        target = CharacterTable.getInstance().restoreCharacter(charId);
                        CharacterTable.getInstance().restoreInventory(target);
                    }

                    int searchCount = 0;

                    for (L1ItemInstance item : target.getInventory().getItems()) {
                        pc.sendPackets("\\fU" + ++searchCount + ". " + item.getViewName2());
                    }
                } catch (Exception e) {
                    pc.sendPackets(new S_SystemMessage("\\fW** [" + charname + "] 캐릭 검색 오류 **"));
                }
            } else if (type.equalsIgnoreCase("창고")) {
                try {
                    String accountName = AccountTable.getInstance().selectAccountNameByCharName(charname);

                    pc.sendPackets(new S_SystemMessage("\\fW** 검사: " + type + " 캐릭: " + charname + "(" + accountName + ") **"));

                    Warehouse w = WarehouseManager.getInstance().getPrivateWarehouse(accountName);

                    int searchCount = 0;

                    for (L1ItemInstance item : w.getItems()) {
                        pc.sendPackets("\\fU" + ++searchCount + ". " + item.getViewName2());
                    }

                } catch (Exception e) {
                    pc.sendPackets(new S_SystemMessage("\\fW** [" + charname + "] 캐릭 검색 오류 **"));
                }
            } else if (type.equalsIgnoreCase("장비")) {
                try {
                    Integer charId = CharacterTable.getInstance().selectCharIdByName(charname);

                    pc.sendPackets("\\fW** 검사: " + type + " 캐릭: " + charname + " **");

                    L1PcInstance target = L1World.getInstance().getPlayer(charname);

                    if (target == null) {
                        target = CharacterTable.getInstance().restoreCharacter(charId);
                        CharacterTable.getInstance().restoreInventory(target);
                    }

                    int searchCount = 0;

                    for (L1ItemInstance item : target.getInventory().getItems()) {
                        if (item.getItem().isWeapon() || item.getItem().isArmorAndRing()) {
                            StringBuilder msg = new StringBuilder();
                            msg.append("\\fU");
                            msg.append(++searchCount);
                            msg.append(".");

                            if (!item.isIdentified()) {
                                msg.append("+ ").append(item.getEnchantLevel());
                            }

                            msg.append(" ").append(item.getViewName2());

                            pc.sendPackets(msg.toString());
                        }
                    }
                } catch (Exception e) {
                    pc.sendPackets(new S_SystemMessage("\\fW** [" + charname + "] 캐릭 검색 오류 **"));
                }
            } else if (type.equalsIgnoreCase("계정")) {
                try {
                    CharacterTable.getInstance().characterAccountCheck(pc, charname);
                } catch (Exception e) {
                    logger.error("오류", e);
                    pc.sendPackets(new S_SystemMessage("\\fW** [" + charname + "] 계정 검색 오류 **"));
                }
            } else if (type.equalsIgnoreCase("스텟")) {
                pc.sendPackets("STR : " + pc.getAbility().getStr());
                pc.sendPackets("DEX : " + pc.getAbility().getDex());
                pc.sendPackets("INT : " + pc.getAbility().getInt());
                pc.sendPackets("INT2 : " + pc.getAbility().getTotalInt());
                pc.sendPackets("S P : " + pc.getAbility().getSp());
                pc.sendPackets("CON : " + pc.getAbility().getCon());


            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".검사 [캐릭명] [인벤,창고,요정창고,장비,계정,스텟]"));
        }
    }
}
