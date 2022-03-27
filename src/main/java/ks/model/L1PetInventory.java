package ks.model;

import ks.core.datatables.pet.PetInventoryTable;
import ks.core.datatables.pet.model.PetInventoryItem;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1PetInstance;

import java.util.List;

public class L1PetInventory extends L1Inventory {
    private final L1PetInstance owner;

    public L1PetInventory(L1PetInstance owner) {
        this(owner, null);
    }

    public L1PetInventory(L1PetInstance owner, L1Inventory inventory) {
        this.owner = owner;

        if (inventory != null) {
            for (L1ItemInstance item : inventory.getItems()) {
                storeItem(item);
            }
        }
    }

    @Override
    public void loadItems() {
        List<PetInventoryItem> petInventoryItems = PetInventoryTable.getInstance().selectListByObjId(owner.getItemObjId());

        for (PetInventoryItem petInventoryItem : petInventoryItems) {
            L1ItemInstance item = petInventoryItem.toItem();
            storeItem(item);
        }
    }

    @Override
    public void insertItem(L1ItemInstance item) {
        super.insertItem(item);
        PetInventoryTable.getInstance().insert(PetInventoryItem.fromItem(owner.getItemObjId(), item));
    }

    @Override
    public void updateItem(L1ItemInstance item) {
        super.updateItem(item);
        PetInventoryTable.getInstance().update(PetInventoryItem.fromItem(owner.getItemObjId(), item));
    }

    @Override
    public void deleteItem(L1ItemInstance item) {
        super.deleteItem(item);
        PetInventoryTable.getInstance().delete(PetInventoryItem.fromItem(owner.getItemObjId(), item));
    }
}
