package ks.model.warehouse;

public class PrivateWarehouseList extends WarehouseList {
    @Override
    protected PrivateWarehouse createWarehouse(String name) {
        return new PrivateWarehouse(name);
    }
}
