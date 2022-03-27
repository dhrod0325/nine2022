package ks.model.warehouse;

public class WarehouseManager {
    private static final WarehouseManager instance = new WarehouseManager();

    private final PrivateWarehouseList plist = new PrivateWarehouseList();
    private final ElfWarehouseList elist = new ElfWarehouseList();
    private final ClanWarehouseList clist = new ClanWarehouseList();
    private final ExtraWarehouseList extralist = new ExtraWarehouseList();

    public static WarehouseManager getInstance() {
        return instance;
    }

    public PrivateWarehouse getPrivateWarehouse(String name) {
        return (PrivateWarehouse) plist.findWarehouse(name);
    }

    public ElfWarehouse getElfWarehouse(String name) {
        return (ElfWarehouse) elist.findWarehouse(name);
    }

    public ExtraWarehouse getExtraWarehouse(String name) {
        return (ExtraWarehouse) extralist.findWarehouse(name);
    }

    public ClanWarehouse getClanWarehouse(String name) {
        return (ClanWarehouse) clist.findWarehouse(name);
    }

    public void delPrivateWarehouse(String name) {
        plist.delWarehouse(name);
    }

    public void delElfWarehouse(String name) {
        elist.delWarehouse(name);
    }

    public void delExtraWarehouse(String name) {
        extralist.delWarehouse(name);
    }

    public void delClanWarehouse(String name) {
        clist.delWarehouse(name);
    }

    public void reloadAll(String name) {
        PrivateWarehouse warehouse = getPrivateWarehouse(name);
        warehouse.loadItems();

        ElfWarehouse elfwarehouse = getElfWarehouse(name);
        elfwarehouse.loadItems();

        ExtraWarehouse extraWarehouse = getExtraWarehouse(name);
        extraWarehouse.loadItems();
    }
}
