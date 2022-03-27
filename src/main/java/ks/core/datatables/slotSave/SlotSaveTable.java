package ks.core.datatables.slotSave;

import ks.app.LineageAppContext;
import ks.model.Broadcaster;
import ks.model.L1ItemDelay;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.*;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SlotSaveTable {
    private final Logger logger = LogManager.getLogger();

    private final Map<Integer, List<SlotSave>> slotSaveMap = new HashMap<>();

    public static SlotSaveTable getInstance() {
        return LineageAppContext.getBean(SlotSaveTable.class);
    }

    public void load() {
        slotSaveMap.clear();

        List<Integer> objIdList = selectSlotIdList();

        for (Integer o : objIdList) {
            slotSaveMap.put(o, loadSlot(o));
        }
    }

    public List<Integer> selectSlotIdList() {
        String sql = "SELECT charObjId FROM character_slot_save group by charObjId";

        return SqlUtils.queryForList(sql, Integer.class);
    }

    public void deleteSlot(int charObjId, int saveSlot) {
        String sql = "DELETE FROM character_slot_save WHERE charObjId=? and saveSlot =?";
        SqlUtils.update(sql, charObjId, saveSlot);
    }

    public void saveSlot(int charObjId, int saveSlot, List<L1ItemInstance> items) {
        for (L1ItemInstance is : items) {
            saveSlot(charObjId, saveSlot, is);
        }
    }

    public void saveSlot(int charObjId, int saveSlot, L1ItemInstance item) {
        String sql = "INSERT INTO character_slot_save (charObjId,itemObjId,saveSlot) VALUES (?,?,?)";
        SqlUtils.update(sql, charObjId, item.getId(), saveSlot);
    }

    public List<SlotSave> loadSlot(int charObjId) {
        String sql = "SELECT * FROM character_slot_save where charObjId=?";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(SlotSave.class), charObjId);
    }

    public void saveCache(int charObjId) {
        slotSaveMap.put(charObjId, loadSlot(charObjId));
    }

    public List<SlotSave> find(int charObjId, int saveSlot) {
        List<SlotSave> result = new ArrayList<>();
        List<SlotSave> r = slotSaveMap.get(charObjId);

        if (r != null && !r.isEmpty()) {
            for (SlotSave ss : r) {
                if (ss.getSaveSlot() == saveSlot) {
                    result.add(ss);
                }
            }
        }

        return result;
    }

    public void changeEqumentItem(int charObjId) {
        changeEqumentItem(charObjId, 0);
    }

    public void changeEqumentItem(int charObjId, int saveSlot) {
        L1PcInstance pc = L1World.getInstance().getPlayer(charObjId);

        if (pc == null)
            return;

//        String timerKey = "changeEqumentItem";
//
//        if (!pc.getTimer().isTimeOver(timerKey)) {
//            return;
//        }

        L1ItemInstance useItem = pc.getInventory().findItemId(60001163);

        if (useItem == null) {
            return;
        }

        if (L1ItemDelay.hasItemDelay(pc, useItem)) {
            return;
        }

        int gfxId = useItem.getGfxId();

        if (saveSlot == 0) {
            if (gfxId == 7728) {
                saveSlot = 2;
            } else {
                saveSlot = 1;
            }
        }

        List<SlotSave> slots = find(charObjId, saveSlot);

        if (slots.isEmpty()) {
            pc.sendPackets(saveSlot + "번 슬롯에 저장된 장비가 없습니다");
            return;
        }

        if (saveSlot == 1) {
            useItem.setGfxId(7728);
        } else {
            useItem.setGfxId(7730);
        }

        int prevHp = pc.getCurrentHp();
        int prevMp = pc.getCurrentMp();

        List<L1ItemInstance> items = pc.getInventory().getItems();

        for (L1ItemInstance item : items) {
            pc.getInventory().setEquipped(item, false);
        }

        for (L1ItemInstance item : items) {
            if (!L1CommonUtils.itemUseAbleCheck(pc, item)) {
                continue;
            }

            for (SlotSave ss : slots) {
                if (item.getId() == ss.getItemObjId()) {
                    pc.getInventory().setEquipped(item, true);
                    break;
                }
            }
        }

        try {
            int polyId = pc.getGfxId().getTempCharGfx();

            pc.getInventory().takeoffEquip(polyId);

            L1ItemInstance weapon = pc.getWeapon();

            if (weapon != null) {
                pc.sendPackets(new S_CharVisualUpdate(pc));
                Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }

        pc.sendPackets(saveSlot + "번 장비로 교체되었습니다");
        pc.setLastSaveSlot(saveSlot);

        pc.sendPackets(new S_DeleteInventoryItem(useItem));
        pc.sendPackets(new S_AddItem(useItem));

        pc.sendPackets(new S_SkillSound(pc.getId(), 14994));
        pc.sendPackets(new S_SkillSound(pc.getId(), 14996));

        pc.setCurrentHp(prevHp);
        pc.setCurrentMp(prevMp);
        pc.sendPackets(new S_OwnCharStatus(pc));

        L1ItemDelay.onItemUse(pc, useItem);
    }
}
