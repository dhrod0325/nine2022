package ks.core.datatables;

import ks.model.L1ArmorSet;
import ks.model.L1ArmorSetImpl;
import ks.model.L1ArmorSets;
import ks.model.effect.*;
import ks.util.common.SqlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ArmorSetTable {
    private static final ArmorSetTable instance = new ArmorSetTable();
    private final List<L1ArmorSets> armorSetList = new ArrayList<>();
    private final List<L1ArmorSet> allSet = new ArrayList<>();

    public static ArmorSetTable getInstance() {
        return instance;
    }

    private static int[] getArray(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        int size = st.countTokens();
        String temp;
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            temp = st.nextToken();
            array[i] = Integer.parseInt(temp);
        }
        return array;
    }

    public void load() {
        armorSetList.clear();
        armorSetList.addAll(selectList());

        initArmorSets();
    }

    public void initArmorSets() {
        allSet.clear();

        for (L1ArmorSets armorSets : armorSetList) {
            try {
                L1ArmorSetImpl impl = new L1ArmorSetImpl(getArray(armorSets.getSets()));

                if (armorSets.getPolyId() != -1) {
                    impl.addEffect(new PolymorphEffect(armorSets.getPolyId()));
                }

                if (armorSets.getId() == 128) {
                    impl.addEffect(new EvaiconEffect());
                }

                impl.addEffect(new DamageEffect(armorSets.getSp(), armorSets.getShortHitup(), armorSets.getShortDmgup(), armorSets.getLongHitup(), armorSets.getLongDmgup()));
                impl.addEffect(new AcHpMpBonusEffect(armorSets.getAc(), armorSets.getHp(), armorSets.getMp(), armorSets.getHpr(), armorSets.getMpr(), armorSets.getMr()));
                impl.addEffect(new StatBonusEffect(armorSets.getStr(), armorSets.getDex(), armorSets.getCon(), armorSets.getWis(), armorSets.getCha(), armorSets.getIntl()));

                allSet.add(impl);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<L1ArmorSets> selectList() {
        return SqlUtils.query("SELECT * FROM armor_set", (rs, i) -> {
            L1ArmorSets as = new L1ArmorSets();
            as.setId(rs.getInt("id"));
            as.setSets(rs.getString("sets"));
            as.setPolyId(rs.getInt("polyid"));
            as.setAc(rs.getInt("ac"));
            as.setHp(rs.getInt("hp"));
            as.setMp(rs.getInt("mp"));
            as.setHpr(rs.getInt("hpr"));
            as.setMpr(rs.getInt("mpr"));
            as.setMr(rs.getInt("mr"));
            as.setStr(rs.getInt("str"));
            as.setDex(rs.getInt("dex"));
            as.setCon(rs.getInt("con"));
            as.setWis(rs.getInt("wis"));
            as.setCha(rs.getInt("cha"));
            as.setIntl(rs.getInt("intl"));
            as.setSp(rs.getInt("sp"));
            as.setShortHitup(rs.getInt("shorthitup"));
            as.setShortDmgup(rs.getInt("shortdmgup"));
            as.setLongHitup(rs.getInt("longhitup"));
            as.setLongDmgup(rs.getInt("longdmgup"));

            return as;
        });
    }

    public L1ArmorSets findOne(int setId) {
        for (L1ArmorSets armorSets : armorSetList) {
            List<Integer> t = armorSets.getSetList();

            if (t.size() == 1) {
                if (t.get(0).equals(setId)) {
                    return armorSets;
                }
            }
        }

        return null;
    }

    public List<L1ArmorSet> getAllSet() {
        return allSet;
    }
}
