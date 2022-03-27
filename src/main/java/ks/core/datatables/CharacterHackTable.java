package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.model.pc.L1PcInstance;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CharacterHackTable {
    public static CharacterHackTable getInstance() {
        return LineageAppContext.getBean(CharacterHackTable.class);
    }

    public void insertHack(L1PcInstance pc) {
        int gfxId = pc.getGfxId().getTempCharGfx();
        insertHack(pc.getId(), pc.getName(), pc.getSpeedHack().getSpeedHackCount(gfxId).getCount(), gfxId, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public void insertHack(int charId, String charName, int speedHackCount, int gfxId, String regDate) {
        String sql = "INSERT INTO characters_hack (charId,charName,speedHackCount,gfxId,regDate) values (?,?,?,?,?) ON DUPLICATE KEY UPDATE speedHackCount=?,gfxId=?";
        SqlUtils.update(sql, charId, charName, speedHackCount, gfxId, regDate, speedHackCount, gfxId);
    }
}
