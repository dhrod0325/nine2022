package ks.model.warehouse;

public class ExtraWarehouseList extends WarehouseList {
    @Override
    protected ExtraWarehouse createWarehouse(String name) {
        return new ExtraWarehouse(name);
    }
}
