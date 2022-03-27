package ks.core.datatables.pet;

import ks.model.L1Pet;
import ks.model.instance.L1NpcInstance;
import ks.util.common.SqlUtils;

import java.util.HashMap;
import java.util.Map;

public class PetTable {
    private static final PetTable instance = new PetTable();

    private final Map<Integer, L1Pet> pets = new HashMap<>();

    public static PetTable getInstance() {
        return instance;
    }

    public static boolean isNameExists(String name) {
        String nameLower = name.toLowerCase();

        int count = SqlUtils.selectInteger("SELECT count(*) FROM pets WHERE LOWER(name)=?", nameLower);

        if (count == 0) {
            return false;
        }

        return !PetTypeTable.getInstance().isNameDefault(nameLower);
    }

    public void load() {
        pets.clear();

        SqlUtils.query("SELECT * FROM pets", (rs, i) -> {
            L1Pet pet = new L1Pet();
            int itemobjid = rs.getInt(1);
            pet.setItemobjId(itemobjid);
            pet.setObjId(rs.getInt(2));
            pet.setNpcId(rs.getInt(3));
            pet.setName(rs.getString(4));
            pet.setLevel(rs.getInt(5));
            pet.setHp(rs.getInt(6));
            pet.setMp(rs.getInt(7));
            pet.setExp(rs.getInt(8));
            pet.setLawful(rs.getInt(9));
            pet.setFood(rs.getInt(10));
            pet.setFoodTime(rs.getInt(11));

            pets.put(itemobjid, pet);

            return null;
        });
    }

    public void storeNewPet(L1NpcInstance npc, int objid, int itemobjid) {
        L1Pet pet = new L1Pet();
        pet.setItemobjId(itemobjid);
        pet.setObjId(objid);
        pet.setNpcId(npc.getTemplate().getNpcId());
        pet.setName(npc.getTemplate().getNameId());
        pet.setLevel(npc.getTemplate().getLevel());
        pet.setHp(npc.getMaxHp());
        pet.setMp(npc.getMaxMp());
        pet.setExp(750);
        pet.setLawful(0);
        pet.setFood(0);
        pet.setFoodTime(1200000);

        pets.put(itemobjid, pet);

        SqlUtils.update("INSERT INTO pets SET item_obj_id=?,objid=?,npcid=?,name=?,lvl=?,hp=?,mp=?,exp=?,lawful=?,food=?,foodtime=?",
                pet.getItemobjId(),
                pet.getObjId(),
                pet.getNpcId(),
                pet.getName(),
                pet.getLevel(),
                pet.getHp(),
                pet.getMp(),
                pet.getExp(),
                pet.getLawful(),
                pet.getFood(),
                pet.getFoodTime()
        );
    }

    public void storePet(L1Pet pet) {
        SqlUtils.update("UPDATE pets SET objid=?,npcid=?,name=?,lvl=?,hp=?,mp=?,exp=?,lawful=?,food=? WHERE item_obj_id=?",
                pet.getObjId(),
                pet.getNpcId(),
                pet.getName(),
                pet.getLevel(),
                pet.getHp(),
                pet.getMp(),
                pet.getExp(),
                pet.getLawful(),
                pet.getFood(),
                pet.getItemobjId()
        );
    }

    public void storePetFoodTime(int id, int food, int foodtime) {
        SqlUtils.update("UPDATE pets SET food=?,foodtime=? WHERE objid=?", food, foodtime, id);
    }

    public void deletePet(int itemobjid) {
        SqlUtils.update("DELETE FROM pets WHERE item_obj_id=?", itemobjid);
        pets.remove(itemobjid);
    }

    public L1Pet getTemplate(int itemobjid) {
        return pets.get(itemobjid);
    }

}
