package ks.model.warehouse;

public class ClanWarehouseList extends WarehouseList {
    @Override
    protected ClanWarehouse createWarehouse(String name) {
        return new ClanWarehouse(name);
    }
}