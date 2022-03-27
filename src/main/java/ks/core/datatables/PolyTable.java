package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.L1PolyMorph;
import ks.util.common.SqlUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PolyTable {
    private final Map<String, L1PolyMorph> polymorphs = new HashMap<>();

    private final Map<Integer, L1PolyMorph> polyIdIndex = new HashMap<>();

    private final List<Integer> polyRings = new ArrayList<>();

    public static PolyTable getInstance() {
        return LineageAppContext.getBean(PolyTable.class);
    }

    @LogTime
    public void load() {
        polymorphs.clear();
        polyIdIndex.clear();

        SqlUtils.query("SELECT * FROM polymorphs", (rs, i) -> {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int polyId = rs.getInt("polyid");
            int minLevel = rs.getInt("minlevel");
            int weaponEquipFlg = rs.getInt("weaponequip");
            int armorEquipFlg = rs.getInt("armorequip");
            boolean canUseSkill = rs.getBoolean("isSkillUse");
            int causeFlg = rs.getInt("cause");

            L1PolyMorph poly = new L1PolyMorph(id, name, polyId, minLevel, weaponEquipFlg, armorEquipFlg, canUseSkill, causeFlg);

            polymorphs.put(name, poly);
            polyIdIndex.put(polyId, poly);

            return null;
        });

        loadRings();
    }

    public void loadRings() {
        polyRings.clear();
        polyRings.addAll(SqlUtils.queryForList("select polyId from polymorphs_ring", Integer.class));
    }

    public L1PolyMorph getTemplate(String name) {
        return polymorphs.get(name);
    }

    public L1PolyMorph getTemplate(int polyId) {
        return polyIdIndex.get(polyId);
    }

    public boolean isRingRequiredPoly(L1PolyMorph poly) {
        return polyRings.contains(poly.getPolyId());
    }
}
