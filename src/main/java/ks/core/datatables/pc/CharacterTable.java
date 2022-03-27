package ks.core.datatables.pc;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.model.L1CastleLocation;
import ks.model.L1CharName;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.model.warehouse.ElfWarehouse;
import ks.model.warehouse.ExtraWarehouse;
import ks.model.warehouse.PrivateWarehouse;
import ks.model.warehouse.WarehouseManager;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.common.DateUtils;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CharacterTable {
    public static final Map<String, L1CharName> charNameList = new ConcurrentHashMap<>();
    private static final Logger logger = LogManager.getLogger(CharacterTable.class.getName());

    public static CharacterTable getInstance() {
        return LineageAppContext.getBean(CharacterTable.class);
    }

    @LogTime
    public void load() {
        loadAllCharName();
        clearOnlineStatus();
        clearTownFix();
    }

    public List<L1PcInstance> selectList() {
        return SqlUtils.query("SELECT * FROM characters", (rs, i) -> setUpCharacter(rs));
    }

    public L1PcInstance loadCharacter(int charId) {
        return loadCharacter(charId, "characters");
    }

    public L1PcInstance loadCharacter(String charName) {
        return loadCharacter(charName, "characters");
    }

    public L1PcInstance loadCharacterTrade(String charName) {
        return loadCharacter(charName, "characters_trade");
    }

    public L1PcInstance loadCharacter(int charId, String tableName) {
        return SqlUtils.select("SELECT * FROM " + tableName + " WHERE objid=?", (rs, i) -> setUpCharacter(rs), charId);
    }

    public L1PcInstance loadCharacter(String charName, String tableName) {
        L1PcInstance pc = SqlUtils.select("SELECT * FROM " + tableName + " WHERE char_name=?", (rs, i) -> setUpCharacter(rs), charName);

        if (pc == null) {
            return null;
        }

        L1Map map = L1WorldMap.getInstance().getMap(pc.getMapId());

        if (!map.isInMap(pc.getX(), pc.getY())) {
            pc.setX(33087);
            pc.setY(33396);
            pc.setMap((short) 4);
        }

        return pc;
    }

    public L1PcInstance setUpCharacter(ResultSet rs) throws SQLException {
        L1PcInstance pc = new L1PcInstance();
        pc.setAccountName(rs.getString("account_name"));
        pc.setId(rs.getInt("objid"));
        pc.setName(rs.getString("char_name"));
        pc.setHighLevel(rs.getInt("HighLevel"));
        pc.setExp(rs.getInt("Exp"));
        pc.addBaseMaxHp(rs.getShort("MaxHp"));
        short currentHp = rs.getShort("CurHp");
        if (currentHp < 1) {
            currentHp = 1;
        }

        pc.setCurrentHp(currentHp);
        pc.setDead(false);
        pc.setActionStatus(0);
        pc.addBaseMaxMp(rs.getShort("MaxMp"));

        pc.setCurrentMp(rs.getShort("CurMp"));

        pc.getAbility().setBaseStr(rs.getByte("BaseStr"));
        pc.getAbility().setStr(rs.getByte("Str"));
        pc.getAbility().setBaseCon(rs.getByte("BaseCon"));
        pc.getAbility().setCon(rs.getByte("Con"));
        pc.getAbility().setBaseDex(rs.getByte("BaseDex"));
        pc.getAbility().setDex(rs.getByte("Dex"));
        pc.getAbility().setBaseCha(rs.getByte("BaseCha"));
        pc.getAbility().setCha(rs.getByte("Cha"));
        pc.getAbility().setBaseInt(rs.getByte("BaseIntel"));
        pc.getAbility().setInt(rs.getByte("Intel"));
        pc.getAbility().setBaseWis(rs.getByte("BaseWis"));
        pc.getAbility().setWis(rs.getByte("Wis"));

        int status = rs.getInt("Status");
        pc.setCurrentWeapon(status);

        int classId = rs.getInt("Class");
        pc.setClassId(classId);

        pc.getGfxId().setTempCharGfx(classId);
        pc.getGfxId().setGfxId(classId);

        pc.setSex(rs.getInt("Sex"));
        pc.setType(rs.getInt("Type"));
        int head = rs.getInt("Heading");
        if (head > 7) {
            head = 0;
        }
        pc.setHeading(head);
        pc.setX(rs.getInt("locX"));
        pc.setY(rs.getInt("locY"));
        pc.setMap(rs.getShort("MapID"));
        pc.setFood(rs.getInt("Food"));
        pc.setLawful(rs.getInt("Lawful"));
        pc.setTitle(rs.getString("Title"));
        pc.setClanId(rs.getInt("ClanID"));
        pc.setClanName(rs.getString("Clanname"));
        pc.setClanRank(rs.getInt("ClanRank"));
        pc.getAbility().setBonusAbility(rs.getInt("BonusStatus"));
        pc.getAbility().setElixirCount(rs.getInt("ElixirStatus"));
        pc.setElfAttr(rs.getInt("ElfAttr"));
        pc.setPkCount(rs.getInt("PKcount"));
        pc.setExpRes(rs.getInt("ExpRes"));
        pc.setPartnerId(rs.getInt("PartnerID"));
        pc.setAccessLevel(rs.getShort("AccessLevel"));
        if (pc.getAccessLevel() == CodeConfig.GM_CODE) {
            pc.setGm(true);
        } else if (pc.getAccessLevel() == 100) {
            pc.setGm(false);
        } else {
            pc.setGm(false);
        }
        pc.setOnlineStatus(rs.getInt("OnlineStatus"));
        pc.setHomeTownId(rs.getInt("HomeTownID"));
        pc.setContribution(rs.getInt("Contribution"));
        pc.setHellTime(rs.getInt("HellTime"));
        pc.setBanned(rs.getBoolean("Banned"));
        pc.setKarma(rs.getInt("Karma"));
        pc.setLastPk(rs.getTimestamp("LastPk"));
        pc.setDeleteTime(rs.getTimestamp("DeleteTime"));
        pc.setReturnStat(rs.getInt("ReturnStat"));
        pc.setAinHasad(rs.getInt("Ainhasad_Exp"));
        pc.setLogOutTime(rs.getTimestamp("Logout_time"));
        pc.setSealingPW(rs.getString("sealingPW"));
        pc.setKillCount(rs.getInt("PC_Kill")); // 추가
        pc.setDeathCount(rs.getInt("PC_Death"));
        pc.setBirthDay(rs.getInt("birth"));
        pc.setMarkCount(rs.getInt("Mark_count"));

        pc.setAge(rs.getInt("Age"));
        pc.setAinState(rs.getInt("AinState")); // 쿨타임 상태
        pc.set_SpecialSize(rs.getInt("SpecialSize"));//특수창고
        pc.setHuntPrice(rs.getInt("HuntPrice"));
        pc.setReasonToHunt(rs.getString("HuntText"));
        pc.setHuntCount(rs.getInt("HuntCount"));
        pc.setChaTra(rs.getInt("ChaTra"));
        pc.setMarkCount(rs.getInt("Mark_count"));
        pc.setDream_Timer(rs.getInt("Dream_Timer"));
        pc.setIvoryTimer(rs.getInt("IvoryTower_Timer"));
        pc.setElfGrave(rs.getInt("ElfGrave_Timer"));

        pc.getPcExpManager().refresh();
        pc.getMoveState().setMoveSpeed(0);
        pc.getMoveState().setBraveSpeed(0);
        pc.setGmInvis(false);
        pc.setBattleKillCount(rs.getInt("battleKillCount"));
        pc.setBattleDeathCount(rs.getInt("battleDeathCount"));
        pc.setLastSaveSlot(rs.getInt("lastSaveSlot"));

        return pc;
    }


    public void createCharacter(L1PcInstance pc) {
        String sql = "INSERT INTO characters SET account_name=?,objid=?,char_name=?,level=?,HighLevel=?,Exp=?,MaxHp=?,CurHp=?,MaxMp=?,CurMp=?,Ac=?,Str=?,BaseStr=?,Con=?,BaseCon=?,Dex=?,BaseDex=?,Cha=?,BaseCha=?,Intel=?,BaseIntel=?,Wis=?,BaseWis=?,Status=?,Class=?,Sex=?,Type=?,Heading=?,LocX=?,LocY=?,MapID=?,Food=?,Lawful=?,Title=?,ClanID=?,Clanname=?,ClanRank=?,BonusStatus=?,ElixirStatus=?,ElfAttr=?,PKcount=?,ExpRes=?,PartnerID=?,AccessLevel=?,onlineStatus=?,HomeTownID=?,Contribution=?,Pay=?,HellTime=?,Banned=?,Karma=?,LastPk=?,DeleteTime=?,ReturnStat=?,GdungeonTime=?,LdungeonTime=?,TkddkdungeonTime=?,DdungeonTime=?,optTime=?,Ainhasad_Exp=?,Logout_time=?,sealingPW=?, Age=?,sub=?,AinState=?,SurvivalGauge=?,SpecialSize=?,HuntPrice=?, HuntText=?, HuntCount=?,ChaTra=?,Mark_count=?,Tam_Point=?,Dream_Timer=?,IvoryTower_Timer=?,ElfGrave_Timer=?,Abysspoint=?,AddDamage=?,AddDamageRate=?,AddReduction=?,AddReductionRate=?,battleKillCount=?,battleDeathCount=?,birth=?,lastSaveSlot=1";
        int hp = pc.getCurrentHp();
        if (hp < 1) {
            hp = 1;
        }

        SqlUtils.update(sql,
                pc.getAccountName(),
                pc.getId(),
                pc.getName(),
                pc.getLevel(),
                pc.getHighLevel(),
                pc.getExp(),
                pc.getBaseMaxHp(),
                hp,
                pc.getBaseMaxMp(),
                pc.getCurrentMp(),
                pc.getAC().getAc(),
                pc.getAbility().getStr(),
                pc.getAbility().getBaseStr(),
                pc.getAbility().getCon(),
                pc.getAbility().getBaseCon(),
                pc.getAbility().getDex(),
                pc.getAbility().getBaseDex(),
                pc.getAbility().getCha(),
                pc.getAbility().getBaseCha(),
                pc.getAbility().getInt(),
                pc.getAbility().getBaseInt(),
                pc.getAbility().getWis(),
                pc.getAbility().getBaseWis(),
                pc.getCurrentWeapon(),
                pc.getClassId(),
                pc.getSex(),
                pc.getType(),
                pc.getHeading(),
                pc.getX(),
                pc.getY(),
                pc.getMapId(),
                pc.getFood(),
                pc.getLawful(),
                pc.getTitle(),
                pc.getClanId(),
                pc.getClanName(),
                pc.getClanRank(),
                pc.getAbility().getBonusAbility(),
                pc.getAbility().getElixirCount(),
                pc.getElfAttr(),
                pc.getPkCount(),
                pc.getExpRes(),
                pc.getPartnerId(),
                pc.getAccessLevel(),
                pc.getOnlineStatus(),
                pc.getHomeTownId(),
                pc.getContribution(),
                0,
                pc.getHellTime(),
                pc.isBanned(),
                pc.getKarma(),
                pc.getLastPk(),
                pc.getDeleteTime(),
                pc.getReturnStat(),
                0,
                0,
                0,
                0,
                0,
                pc.getAinHasad(),
                pc.getLogOutTime(),
                pc.getSealingPW(),
                pc.getAge(),
                0,
                pc.getAinState(),
                0,
                pc.getSpecialSize(),
                pc.getHuntPrice(),
                pc.getReasonToHunt(),
                pc.getHuntCount(),
                pc.getChaTra(),
                pc.getMarkCount(),
                0,
                pc.getDreamTimer(),
                pc.getIvoryTimer(),
                pc.getElfGrave(),
                0,
                0,
                0,
                0,
                0,
                pc.getBattleKillCount(),
                pc.getBattleDeathCount(),
                pc.getBirthDay()
        );
    }

    public void deleteCharacter(String accountName, String charName) {
        int cnt = SqlUtils.selectInteger("SELECT count(*) FROM characters WHERE account_name=? AND char_name=?", accountName, charName);

        if (cnt == -1) {
            logger.warn("invalid delete char request: account=" + accountName + " char=" + charName);
            throw new RuntimeException("could not delete character");
        }

        SqlUtils.update("DELETE FROM character_buddys WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_buff WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_config WHERE object_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_items WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_quests WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_skills WHERE char_obj_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_teleport WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM character_soldier WHERE char_id IN (SELECT objid FROM characters WHERE char_name = ?)", charName);
        SqlUtils.update("DELETE FROM characters WHERE char_name=?", charName);

        charNameList.remove(charName);
        CharacterTable.charNameList.remove(charName);
    }

    public void storeCharacter(L1PcInstance pc) {
        String sql = "UPDATE characters SET level=?," +
                "HighLevel=?," +
                "Exp=?," +
                "MaxHp=?," +
                "CurHp=?," +
                "MaxMp=?," +
                "CurMp=?," +
                "Ac=?," +
                "Str=?," +
                "BaseStr=?," +
                "Con=?," +
                "BaseCon=?," +
                "Dex=?," +
                "BaseDex=?," +
                "Cha=?," +
                "BaseCha=?," +
                "Intel=?," +
                "BaseIntel=?," +
                "Wis=?," +
                "BaseWis=?," +
                "Status=?," +
                "Class=?," +
                "Sex=?," +
                "Type=?," +
                "Heading=?," +
                "LocX=?," +
                "LocY=?," +
                "MapID=?," +
                "Food=?," +
                "Lawful=?," +
                "Title=?," +
                "ClanID=?," +
                "Clanname=?," +
                "ClanRank=?," +
                "BonusStatus=?," +
                "ElixirStatus=?," +
                "ElfAttr=?," +
                "PKcount=?," +
                "ExpRes=?," +
                "PartnerID=?," +
                "AccessLevel=?," +
                "onlineStatus=?," +
                "HomeTownID=?," +
                "Contribution=?," +
                "HellTime=?," +
                "Banned=?," +
                "Karma=?," +
                "LastPk=?," +
                "DeleteTime=?," +
                "ReturnStat=?," +
                "GdungeonTime=?," +
                "LdungeonTime=?," +
                "TkddkdungeonTime=?," +
                "DdungeonTime=?," +
                "optTime=?," +
                "Ainhasad_Exp=?,Logout_time=?,sealingPW=?,PC_Kill=?,PC_Death=?,Mark_count=?,Age=?,sub=?,AinState=?,SurvivalGauge=?,SpecialSize=?,HuntPrice=?, HuntText=?, HuntCount=?,ChaTra=?,Mark_count=?,Tam_Point=?,Dream_Timer=?,IvoryTower_Timer=?,ElfGrave_Timer=?,Abysspoint=?, AddDamage=?,AddDamageRate=?,AddReduction=?,AddReductionRate=?,battleKillCount=?,battleDeathCount=?,birth=?,lastSaveSlot=? WHERE objid=?";

        int hp = pc.getCurrentHp();
        if (hp < 1) {
            hp = 1;
        }

        if (pc.getBirthDay() == 0) {
            pc.setBirthDay(DateUtils.getTodayDate());
        }

        SqlUtils.update(sql,
                pc.getLevel(),
                pc.getHighLevel(),
                pc.getExp(),
                pc.getBaseMaxHp(),
                hp,
                pc.getBaseMaxMp(),
                pc.getCurrentMp(),
                pc.getAC().getAc(),
                pc.getAbility().getStr(),
                pc.getAbility().getBaseStr(),
                pc.getAbility().getCon(),
                pc.getAbility().getBaseCon(),
                pc.getAbility().getDex(),
                pc.getAbility().getBaseDex(),
                pc.getAbility().getCha(),
                pc.getAbility().getBaseCha(),
                pc.getAbility().getInt(),
                pc.getAbility().getBaseInt(),
                pc.getAbility().getWis(),
                pc.getAbility().getBaseWis(),
                pc.getCurrentWeapon(),
                pc.getClassId(),
                pc.getSex(),
                pc.getType(),
                pc.getHeading(),
                pc.getX(),
                pc.getY(),
                pc.getMapId(),
                pc.getFood(),
                pc.getLawful(),
                pc.getTitle(),
                pc.getClanId(),
                pc.getClanName(),
                pc.getClanRank(),
                pc.getAbility().getBonusAbility(),
                pc.getAbility().getElixirCount(),
                pc.getElfAttr(),
                pc.getPkCount(),
                pc.getExpRes(),
                pc.getPartnerId(),
                pc.getAccessLevel(),
                pc.getOnlineStatus(),
                pc.getHomeTownId(),
                pc.getContribution(),
                pc.getHellTime(),
                pc.isBanned(),
                pc.getKarma(),
                pc.getLastPk(),
                pc.getDeleteTime(),
                pc.getReturnStat(),
                0,
                0,
                0,
                0,
                0,
                pc.getAinHasad(),
                pc.getLogOutTime(),
                pc.getSealingPW(),
                pc.getKillCount(),
                pc.getDeathCount(),
                pc.getMarkCount(),
                pc.getAge(),
                0,
                pc.getAinState(),
                0,
                pc.getSpecialSize(),
                pc.getHuntPrice(),
                pc.getReasonToHunt(),
                pc.getHuntCount(),
                pc.getChaTra(),
                pc.getMarkCount(),
                0,
                pc.getDreamTimer(),
                pc.getIvoryTimer(),
                pc.getElfGrave(),
                0,
                0,
                0,
                0,
                0,
                pc.getBattleKillCount(),
                pc.getBattleDeathCount(),
                pc.getBirthDay(),
                pc.getLastSaveSlot(),
                pc.getId()
        );

        String name = pc.getName();

        if (!CharacterTable.charNameList.containsKey(name)) {
            L1CharName cn = new L1CharName();
            cn.setName(name);
            cn.setId(pc.getId());
            CharacterTable.charNameList.put(name, cn);
        }
    }

    public void clearOnlineStatus() {
        SqlUtils.update("UPDATE characters SET OnlineStatus=0");
    }

    public void updateOnlineStatus(L1PcInstance pc) {
        SqlUtils.update("UPDATE characters SET OnlineStatus=? WHERE objid=?", pc.getOnlineStatus(), pc.getId());
    }

    public boolean doesCharNameExist(String name) {
        return SqlUtils.selectInteger("SELECT count(*) FROM characters WHERE char_name=?", name) > 0;
    }

    public void clearTownFix() {
        SqlUtils.update("UPDATE town SET sales_money=0, town_fix_tax=0");
    }

    public void storeNewCharacter(L1PcInstance pc) {
        createCharacter(pc);
    }

    public L1PcInstance restoreCharacter(String charName) {
        return loadCharacter(charName);
    }

    public L1PcInstance restoreCharacter(int charId) {
        return loadCharacter(charId);
    }

    public void restoreInventory(L1PcInstance pc) {
        pc.getInventory().loadItems();

        PrivateWarehouse warehouse = WarehouseManager.getInstance().getPrivateWarehouse(pc.getAccountName());
        warehouse.loadItems();

        ElfWarehouse elfwarehouse = WarehouseManager.getInstance().getElfWarehouse(pc.getAccountName());
        elfwarehouse.loadItems();

        ExtraWarehouse extraWarehouse = WarehouseManager.getInstance().getExtraWarehouse(pc.getAccountName());
        extraWarehouse.loadItems();
    }

    public int selectPcLevel(int charId) {
        return SqlUtils.selectInteger("SELECT level FROM characters WHERE objid=?", charId);
    }

    public void updatePartnerId(int targetId) {
        updatePartnerId(targetId, 0);
    }

    public void updateLoc(int castleId, int a, int b, int c, int d, int f) {
        int[] loc = L1CastleLocation.getGetBackLoc(castleId);

        SqlUtils.update("UPDATE characters SET LocX=?, LocY=?, MapID=? WHERE OnlineStatus=0 AND MapID IN (?,?,?,?,?)",
                loc[1], loc[2], loc[3], a, b, c, d, f
        );
    }

    public void characterAccountCheck(L1PcInstance pc, String charName) {
        String sql = "SELECT login, password, phone FROM accounts WHERE ip = " +
                "(SELECT ip FROM accounts WHERE login = " +
                "(SELECT account_name FROM characters WHERE char_name = ?))";

        SqlUtils.query(sql, (rs, i) -> {
            SqlUtils.query("SELECT char_name, level, highlevel, clanname, onlinestatus FROM characters WHERE account_name = ?", (rs2, i1) -> {
                String onlineStatus = rs2.getInt("onlinestatus") == 0 ? "" : "(접속중)";
                pc.sendPackets(new S_SystemMessage("* " + rs2.getString("char_name") + " (Lv:" + rs2.getInt("level") + ") (HLv:" + rs2.getInt("highlevel") + ") " + "(혈맹:" + rs2.getString("clanname") + ") " + "\\fY" + onlineStatus));

                return null;
            }, rs.getString("login"));

            pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
            pc.sendPackets(new S_SystemMessage("\\fYAccounts : " + rs.getString("login") + ", PassWord : " + rs.getString("password") + ", " + rs.getString("phone")));

            return null;
        }, charName);
    }

    public void updatePartnerId(int targetId, int partnerId) {
        SqlUtils.update("UPDATE characters SET PartnerID=? WHERE objid=?", partnerId, targetId);
    }

    public void updateCharName(String newName, String oldName) {
        SqlUtils.update("UPDATE characters SET char_name =? WHERE char_name = ?", newName, oldName);
        SqlUtils.update("DELETE FROM character_buddys WHERE buddy_name=?", oldName);
    }

    public L1CharName selectCharNameByName(String name) {
        return SqlUtils.select("SELECT char_name name,objid id FROM characters where char_name=?", new BeanPropertyRowMapper<>(L1CharName.class), name);
    }

    public Integer selectCharIdByName(String charname) {
        return SqlUtils.selectInteger("SELECT objid FROM characters WHERE char_name = ?", charname);
    }

    public List<L1CharName> selectCharNameList() {
        return SqlUtils.query("SELECT * FROM characters", (rs, i) -> {
            L1CharName charName = new L1CharName();
            String name = rs.getString("char_name");
            charName.setName(name);
            charName.setId(rs.getInt("objid"));

            return charName;
        });
    }

    public void loadAllCharName() {
        charNameList.clear();

        List<L1CharName> charNames = selectCharNameList();

        for (L1CharName name : charNames) {
            charNameList.put(name.getName(), name);
        }
    }
}

