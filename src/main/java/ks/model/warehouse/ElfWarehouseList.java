package ks.model.warehouse;

public class ElfWarehouseList extends WarehouseList {
    @Override
    protected ElfWarehouse createWarehouse(String name) {
        return new ElfWarehouse(name);
    }
}
