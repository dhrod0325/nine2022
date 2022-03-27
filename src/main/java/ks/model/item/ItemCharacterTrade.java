package ks.model.item;

import ks.core.datatables.account.AccountTable;
import ks.core.datatables.buff.CharBuffTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.L1Character;
import ks.model.L1Inventory;
import ks.model.instance.L1ItemInstance;
import ks.model.item.characterTrade.CharacterTradeDao;
import ks.model.item.characterTrade.CharacterTradeInfo;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.PrivateWarehouse;
import ks.model.warehouse.WarehouseManager;
import ks.packets.clientpackets.ClientBasePacket;
import ks.system.userShop.L1UserShopManager;
import ks.system.userShop.L1UserShopNpcInstance;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemCharacterTrade extends L1ItemInstance {
    private final Logger logger = LogManager.getLogger();

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            CharacterTradeInfo info = CharacterTradeDao.getInstance().getInfo(getId());

            if (info == null) {
                saveChar(pc);
            } else {
                open(pc, info);
            }
        }
    }

    public void saveChar(L1PcInstance pc) {
        L1ItemInstance useItem = pc.getInventory().getItem(getId());
        PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());

        if (pc.isGm()) {
            pc.sendPackets("운영자 캐릭터를 구슬에 저장할 수 없습니다");
            return;
        }

        if (pc.getClanId() != 0) {
            pc.sendPackets("캐릭터를 구슬에 저장하시려면 혈맹을 탈퇴하셔야 합니다");
            return;
        }

        if (!L1CharPosUtils.isSafeZone(pc)) {
            pc.sendPackets("SafeZone에서 사용해 주시기 바랍니다");
            return;
        }

        if (warehouse == null || warehouse.checkAddItemToWarehouse(useItem, 1) != L1Inventory.OK) {
            pc.sendPackets("창고에 구슬을 저장할 수 없는 상태입니다");
            return;
        }

        L1UserShopNpcInstance shop = L1UserShopManager.getInstance().find(pc);

        if (shop != null) {
            pc.sendPackets("상점을 종료하셔야 합니다");
            return;
        }

        CharBuffTable.delete(pc);
        CharBuffTable.save(pc);

        pc.getInventory().tradeItem(useItem, 1, warehouse);

        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.isEquipped()) {
                pc.getInventory().setEquipped(item, false);
            }
        }

        CharacterTradeDao.getInstance().save(pc.getId(), useItem.getId(), pc.getName());

        pc.disconnect();
    }

    public void open(L1PcInstance pc, CharacterTradeInfo info) {
        try {
            int slot = AccountTable.getInstance().countCharacters(pc.getAccount());
            if (slot >= 6) {
                pc.sendPackets("빈 캐릭터 슬롯이 없습니다. 캐릭터 슬롯을 확보하고 다시 시도해주시기 바랍니다");
                return;
            }

            L1PcInstance target = info.getTargetPc();
            target.setAccountName(pc.getAccountName());
            CharacterTable.getInstance().storeNewCharacter(target);

            ItemHpMpResetScroll.resetHpMp(target);

            CharacterTradeDao.getInstance().delete(getId(), target.getId());

            pc.getInventory().removeItem(this, 1);

            pc.disconnect();
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
