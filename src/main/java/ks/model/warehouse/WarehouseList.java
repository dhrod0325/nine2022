package ks.model.warehouse;

import java.util.Vector;

public abstract class WarehouseList {
    private final Vector<Warehouse> warehouseList = new Vector<>();

    protected abstract Warehouse createWarehouse(String name);

    public synchronized Warehouse findWarehouse(String name) {
        Warehouse warehouse;

        for (Warehouse wh : warehouseList) {
            if (wh.getName().equals(name)) {
                return wh;
            }
        }

        warehouse = createWarehouse(name);
        warehouseList.add(warehouse);
        return warehouse;
    }

    public synchronized void delWarehouse(String accountName) {
        Warehouse iwilldie = null;
        for (Warehouse wh : warehouseList) {
            if (wh.getName().equals(accountName)) {
                iwilldie = wh;
            }
        }

        if (iwilldie != null) {
            iwilldie.clearItems();
            warehouseList.remove(iwilldie);
        }
    }
}
