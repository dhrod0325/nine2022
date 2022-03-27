package ks.core.datatables.npc;

import ks.model.L1Npc;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class NpcRowMapper implements RowMapper<L1Npc> {
    private final Map<String, Integer> familyTypes;

    public NpcRowMapper(Map<String, Integer> familyTypes) {
        this.familyTypes = familyTypes;
    }

    @Override
    public L1Npc mapRow(ResultSet rs, int i) throws SQLException {
        L1Npc npc = new L1Npc();
        int npcId = rs.getInt("npcid");
        npc.setNpcId(npcId);
        npc.setName(rs.getString("name"));
        npc.setNameId(rs.getString("nameid"));
        npc.setImpl(rs.getString("impl"));
        npc.setGfxid(rs.getInt("gfxid"));
        npc.setLevel(rs.getInt("lvl"));
        npc.setHp(rs.getInt("hp"));
        npc.setMp(rs.getInt("mp"));
        npc.setAc(rs.getInt("ac"));
        npc.setStr(rs.getByte("str"));
        npc.setCon(rs.getByte("con"));
        npc.setDex(rs.getByte("dex"));
        npc.setWis(rs.getByte("wis"));
        npc.set_int(rs.getByte("intel"));
        npc.setMr(rs.getInt("mr"));
        npc.setExp(rs.getInt("exp"));
        npc.setLawful(rs.getInt("lawful"));
        npc.setSize(rs.getString("size"));
        npc.setWeakAttr(rs.getInt("weakAttr"));
        npc.setRanged(rs.getInt("ranged"));
        npc.setTamable(rs.getBoolean("tamable"));
        npc.setPassispeed(rs.getInt("passispeed"));
        npc.setAtkspeed(rs.getInt("atkspeed"));
        npc.setAtkMagicSpeed(rs.getInt("atk_magic_speed"));
        npc.setSubMagicSpeed(rs.getInt("sub_magic_speed"));
        npc.setUndead(rs.getInt("undead"));
        npc.setPoisonAtk(rs.getInt("poison_atk"));
        npc.setParalysisAtk(rs.getInt("paralysis_atk"));
        npc.setAgro(rs.getBoolean("agro"));
        npc.setAgrososc(rs.getBoolean("agrososc"));
        npc.setAgrocoi(rs.getBoolean("agrocoi"));

        Integer family = familyTypes.get(rs.getString("family"));

        if (family == null)
            npc.setFamily(0);
        else
            npc.setFamily(family);

        int agrofamily = rs.getInt("agrofamily");

        if (npc.getFamily() == 0 && agrofamily == 1)
            npc.setAgroFamily(0);
        else
            npc.setAgroFamily(agrofamily);

        npc.setAgroGfxId1(rs.getInt("agrogfxid1"));
        npc.setAgroGfxId2(rs.getInt("agrogfxid2"));
        npc.setPicupItem(rs.getBoolean("picupitem"));
        npc.setDigestItem(rs.getInt("digestitem"));
        npc.setBraveSpeed(rs.getBoolean("bravespeed"));
        npc.setHprInterval(rs.getInt("hprinterval"));
        npc.setHpr(rs.getInt("hpr"));
        npc.setMprInterval(rs.getInt("mprinterval"));
        npc.setMpr(rs.getInt("mpr"));
        npc.setTeleport(rs.getBoolean("teleport"));
        npc.setRandomLevel(rs.getInt("randomlevel"));
        npc.setRandomHp(rs.getInt("randomhp"));
        npc.setRandomMp(rs.getInt("randommp"));
        npc.setRandomAc(rs.getInt("randomac"));
        npc.setRandomExp(rs.getInt("randomexp"));
        npc.setRandomLawful(rs.getInt("randomlawful"));
        npc.setDamageReduction(rs.getInt("damage_reduction"));
        npc.setHard(rs.getBoolean("hard"));
        npc.setDoppel(rs.getBoolean("doppel"));
        npc.setIsTU(rs.getBoolean("IsTU"));
        npc.setIsErase(rs.getBoolean("IsErase"));
        npc.setIsDurability(rs.getInt("isDurability"));
        npc.setBowActId(rs.getInt("bowActId"));
        npc.setKarma(rs.getInt("karma"));
        npc.setTransformId(rs.getInt("transform_id"));
        npc.setTransformGfxId(rs.getInt("transform_gfxid"));
        npc.setLightSize(rs.getInt("light_size"));
        npc.setAmountFixed(rs.getBoolean("amount_fixed"));
        npc.setChangeHead(rs.getBoolean("change_head"));
        npc.setCantResurrect(rs.getBoolean("cant_resurrect"));
        npc.setDamage(rs.getInt("damage"));

        return npc;
    }
}
