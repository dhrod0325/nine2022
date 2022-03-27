package ks.model.warehouse;

import ks.core.datatables.item.ItemTable;
import ks.model.L1Inventory;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.item.characterTrade.CharacterTradeDao;
import ks.model.item.characterTrade.CharacterTradeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Warehouse extends L1Object {

    private final String name;
    protected List<L1ItemInstance> items = new CopyOnWriteArrayList<>();

    public Warehouse(String name) {
        super();
        this.name = name;
    }

    public abstract void loadItems();

    public abstract void deleteItem(L1ItemInstance item);

    public abstract void insertItem(L1ItemInstance item);

    public abstract void updateItem(L1ItemInstance findItem);

    protected abstract int getMax();

    public L1ItemInstance findItemId(int id) {
        for (L1ItemInstance item : items) {
            if (item.getItem().getItemId() == id) {
                return item;
            }
        }

        return null;
    }

    public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
        if (item.isStackable()) {
            L1ItemInstance findItem = findItemId(item.getItem().getItemId());
            if (findItem != null) {
                findItem.setCount(findItem.getCount() + item.getCount());
                updateItem(findItem);
                return findItem;
            }
        }

        item.setX(getX());
        item.setY(getY());
        item.setMap(getMapId());

        items.add(item);

        insertItem(item);

        return item;
    }

    public synchronized L1ItemInstance tradeItem(L1ItemInstance item, int count, L1Inventory inventory) {
        if (item == null)
            return null;

        if (item.getCount() <= 0 || count <= 0)
            return null;

        if (item.isEquipped())
            return null;

        if (!checkItem(item.getItem().getItemId(), count))
            return null;

        L1ItemInstance carryItem;

        if (item.getCount() <= count) {
            deleteItem(item);
            carryItem = item;
        } else {
            item.setCount(item.getCount() - count);
            updateItem(item);
            carryItem = ItemTable.getInstance().createItem(item.getItem().getItemId());
            carryItem.setCount(count);
            carryItem.setEnchantLevel(item.getEnchantLevel());
            carryItem.setIdentified(item.isIdentified());
            carryItem.setDurability(item.getDurability());
            carryItem.setChargeCount(item.getChargeCount());
            carryItem.setRemainingTime(item.getRemainingTime());
            carryItem.setLastUsed(item.getLastUsed());
            carryItem.setBless(item.getItem().getBless());
            carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
            carryItem.setPackage(item.isPackage());
        }

        CharacterTradeInfo info = CharacterTradeDao.getInstance().getInfo(item.getId());

        if (info != null) {
            CharacterTradeDao.getInstance().updateItemObjectId(item.getId(), carryItem.getId());
        }

        return inventory.storeTradeItem(carryItem);
    }

    public L1ItemInstance getItem(int objectId) {
        for (L1ItemInstance item : items) {
            if (item.getId() == objectId) {
                return item;
            }
        }
        return null;
    }

    public List<L1ItemInstance> getItems() {
        return items;
    }

    public void clearItems() {
        for (L1ItemInstance item : items) {
            L1World.getInstance().removeObject(item);
        }

        items.clear();
    }

    public L1ItemInstance[] findItemsId(int id) {
        ArrayList<L1ItemInstance> itemList = new ArrayList<>();
        for (L1ItemInstance item : items) {
            if (item.getItemId() == id) {
                itemList.add(item);
            }
        }
        return itemList.toArray(new L1ItemInstance[]{});
    }

    public boolean checkItem(int id, int count) {
        if (count == 0)
            return true;
        if (ItemTable.getInstance().getTemplate(id).isStackable()) {
            L1ItemInstance item = findItemId(id);
            return item != null && item.getCount() >= count;
        } else {
            Object[] itemList = findItemsId(id);
            return itemList.length >= count;
        }
    }

    public int checkAddItemToWarehouse(L1ItemInstance item, int count) {
        if (item == null)
            return -1;
        if (item.getCount() <= 0 || count <= 0)
            return -1;

        final int OK = 0, SIZE_OVER = 1;
        final int maxSize = getMax(), SIZE = getSize();

        if (SIZE > maxSize || (SIZE == maxSize && (!item.isStackable() || !checkItem(item.getItem().getItemId(), 1))))
            return SIZE_OVER;

        return OK;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return items.size();
    }
}