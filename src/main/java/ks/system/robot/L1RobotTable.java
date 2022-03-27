package ks.system.robot;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.item.ItemTable;
import ks.model.instance.L1ItemInstance;
import ks.system.robot.ai.L1RobotAiFactory;
import ks.system.robot.is.L1RobotInstance;
import ks.system.robot.model.L1RobotHuntData;
import ks.system.robot.model.L1RobotHuntLocation;
import ks.system.robot.model.L1RobotHuntLocationWay;
import ks.system.robot.model.L1RobotTpl;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class L1RobotTable {
    private final List<String> cachedNameList = new CopyOnWriteArrayList<>();
    private final List<L1RobotTpl> cachedRobotCharacters = new CopyOnWriteArrayList<>();

    public static L1RobotTable getInstance() {
        return LineageAppContext.getBean(L1RobotTable.class);
    }

    public List<L1RobotTpl> getCachedRobotCharacters() {
        return cachedRobotCharacters;
    }

    public void endHuntTime() {
        L1RobotHuntData last = selectLastHuntData();

        if (last != null && last.getEndCheck() == 0) {
            last.setEndCheck(1);
            last.setEndTime(new Date());
            updateHuntData(last);
        }
    }

    @LogTime
    public void load() {
        cachedNameList.clear();
        cachedRobotCharacters.clear();

        cachedNameList.addAll(selectNameList());
        cachedRobotCharacters.addAll(selectRobotCharacterList());
    }

    public List<L1RobotTpl> selectRobotCharacterList() {
        String sql = "SELECT * FROM robot_characters WHERE useYn=1";

        List<L1RobotTpl> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1RobotTpl.class));

        for (L1RobotTpl tpl : list) {
            if (tpl.getHuntId() != 0) {
                List<L1RobotHuntLocation> huntList = getHuntList(tpl.getHuntId());

                if (!huntList.isEmpty()) {
                    L1RobotHuntLocation loc = huntList.get(0);

                    tpl.setHuntLocation(loc);
                    tpl.setLocX(loc.getLocX());
                    tpl.setLocY(loc.getLocY());
                    tpl.setLocMap(loc.getLocMap());

//                    List<L1RobotHuntLocationWay> wayList = getHuntWayList(loc.getId());
//                    loc.setWayList(wayList);
//
//                    if (!wayList.isEmpty()) {
//                        tpl.setLocX(wayList.get(0).getLocX());
//                        tpl.setLocY(wayList.get(0).getLocY());
//                        tpl.setLocMap(wayList.get(0).getLocMap());
//                    }
                }
            }
        }

        return list;
    }

    public int insertHuntWayList(int huntLocId, int locX, int locY, int locMap, int ord) {
        String sql = "INSERT INTO robot_hunt_way (huntLocId,locX,locY,locMap,ord) values (?,?,?,?,?)";
        return SqlUtils.update(sql, huntLocId, locX, locY, locMap, ord);
    }

    public List<L1RobotHuntLocationWay> getHuntWayList(int locId) {
        String sql = "SELECT * FROM robot_hunt_way WHERE huntLocId=? order by ord";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1RobotHuntLocationWay.class), locId);
    }

    public List<L1RobotHuntLocation> getHuntList(int huntId) {
        String sql = "SELECT * FROM robot_hunt where id=?";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1RobotHuntLocation.class), huntId);
    }

    public List<String> selectNameList() {
        String sql = "select name from robot_name";
        return SqlUtils.queryForList(sql, String.class);
    }

    public String findFirstRobotName() {
        for (String name : cachedNameList) {
            String sql = "SELECT * FROM characters WHERE char_name=?";
            List<Map<String, Object>> result = SqlUtils.queryForList(sql, name);

            if (result.isEmpty()) {
                cachedNameList.remove(name);
                return name;
            }
        }

        return null;
    }

    public L1RobotTpl findCachedRobotCharacter(String name) {
        for (L1RobotTpl o : cachedRobotCharacters) {
            if (name.equalsIgnoreCase(o.getName())) {
                return o;
            }
        }

        return null;
    }

    public L1RobotInstance createRobot(String name) {
        L1RobotInstance robot = new L1RobotInstance();

        L1RobotTpl tpl = findCachedRobotCharacter(name);

        String title = "";
        int lvl = 1;
        int clan = 0;
        int weaponId = 0;
        int enchant = 0;
        int bless = 1;
        int con = 18;
        int str = 16;
        int dex = 12;
        int wis = 10;
        int cha = 10;
        int int1 = 10;
        int baseCon = 10;
        int baseStr = 10;
        int baseDex = 10;
        int baseWis = 10;
        int baseInt = 10;
        int baseCha = 10;
        int hp = 100;
        int mp = 100;

        int mr = 10;
        int ac = 10;
        int er = 10;
        int sp = 10;

        int classType;

        int locX = 0;
        int locY = 0;
        int locMap = 0;
        int lawful = 0;
        int dollItemId = 0;

        L1RobotType robotType;
        int id = 0;

        if (tpl == null) {
            robotType = L1RobotType.STAND_BY;
            classType = L1CommonUtils.randomClassId();
            robot.setClassId(classType);

            if (robot.isKnight()) {
                weaponId = 30;
            } else if (robot.isDarkElf()) {
                weaponId = 69;
            } else if (robot.isElf()) {
                weaponId = 172;
            }

            if (RandomUtils.isWinning(100, 50)) {
                weaponId = 0;
            }
        } else {
            id = tpl.getId();
            title = tpl.getTitle();

            lvl = tpl.getLvl();
            clan = tpl.getClan();

            weaponId = tpl.getWeaponId();
            enchant = tpl.getEnchant();
            bless = tpl.getBless();

            con = tpl.getCon();
            str = tpl.getStr();
            dex = tpl.getDex();
            wis = tpl.getWis();
            cha = tpl.getCha();
            int1 = tpl.getInt1();

            baseCon = tpl.getBaseCon();
            baseStr = tpl.getBaseStr();
            baseDex = tpl.getBaseDex();
            baseWis = tpl.getBaseWis();
            baseInt = tpl.getBaseInt();
            baseCha = tpl.getBaseCha();

            hp = tpl.getHp();
            mp = tpl.getMp();

            mr = tpl.getMr();
            ac = tpl.getAc();
            er = tpl.getEr();
            sp = tpl.getSp();

            classType = tpl.getClassType();

            locX = tpl.getLocX();
            locY = tpl.getLocY();
            locMap = tpl.getLocMap();
            lawful = tpl.getLawful();
            dollItemId = tpl.getDollItemId();

            robotType = L1RobotType.by(tpl.getRobotType());
            robot.setClassId(classType);
            robot.setTpl(tpl);
            robot.addDmgUp(tpl.getAddDmg());
            robot.addHitUp(tpl.getAddHitUp());
        }

        if (weaponId != 0) {
            robot.setCurrentWeapon(weaponId);
        }

        robot.setId(id);
        robot.getInventory().loadItems();

        robot.setDead(false);
        robot.setAccountName("");
        robot.setName(name);
        robot.setHighLevel(lvl);
        robot.setLevel(lvl);
        robot.setExp(ExpTable.getInstance().getExpByLevel(lvl));
        robot.setLawful(lawful);
        robot.setClanId(clan);
        robot.setClanName(title);

        robot.addBaseMaxHp((short) hp);
        robot.addBaseMaxMp((short) mp);

        robot.setCurrentHp(hp);
        robot.setCurrentMp(mp);

        robot.getResistance().addMr(mr);
        robot.getResistance().addSp(sp);

        robot.getAC().setAc(ac);

        robot.getAbility().setBaseStr(baseStr);
        robot.getAbility().setStr(str);
        robot.getAbility().setBaseCon(baseCon);
        robot.getAbility().setCon(con);
        robot.getAbility().setBaseDex(baseDex);
        robot.getAbility().setDex(dex);
        robot.getAbility().setBaseCha(baseCha);
        robot.getAbility().setCha(cha);
        robot.getAbility().setBaseInt(baseInt);
        robot.getAbility().setInt(int1);
        robot.getAbility().setBaseWis(baseWis);
        robot.getAbility().setWis(wis);

        robot.getGfxId().setTempCharGfx(classType);
        robot.getGfxId().setGfxId(classType);
        robot.setSex(0);
        robot.setHeading(RandomUtils.nextInt(0, 7));
        robot.setFood(100);
        robot.setElfAttr(0);
        robot.setPkCount(0);
        robot.setExpRes(0);
        robot.setPartnerId(0);
        robot.setAccessLevel((short) 0);
        robot.setGm(false);
        robot.setHellTime(0);
        robot.setBanned(false);
        robot.setKarma(0);
        robot.setReturnStat(0);
        robot.setGmInvis(false);

        robot.setX(locX);
        robot.setY(locY);
        robot.setMap((short) locMap);

        if (weaponId != 0) {
            robot.getEquipSlot().set(ItemTable.getInstance().createItem(weaponId));
            robot.getWeapon().setEnchantLevel(enchant);
            robot.getWeapon().setBless(bless);
        }

        robot.setRobotType(robotType);
        robot.setAi(L1RobotAiFactory.createAi(robot));

        if (robot.getRobotType() == L1RobotType.HUNT) {
            robot.setHuntCount(1);
            robot.setHuntPrice(0);
        }

        if (dollItemId != 0) {
            if (!robot.getInventory().checkItem(dollItemId)) {
                L1ItemInstance item = ItemTable.getInstance().createItem(dollItemId);
                item.setItemOwner(robot);
                item.setCount(1);
                robot.getInventory().storeItem(item);
            }
        }

        return robot;
    }

    public List<String> getCachedNameList() {
        return cachedNameList;
    }

    public void insertHuntData(L1RobotHuntData data) {
        String sql = "insert into robot_hunt_data (startTime,endTime,endCheck) values (?,?,?)";
        SqlUtils.update(sql, data.getStartTime(), data.getEndTime(), data.getEndCheck());
    }

    public void updateHuntData(L1RobotHuntData data) {
        String sql = "update robot_hunt_data set startTime=?,endTime=?,endCheck=? where id = ?";
        SqlUtils.update(sql, data.getStartTime(), data.getEndTime(), data.getEndCheck(), data.getId());
    }

    public Integer selectLastHuntDataId() {
        String sql = "select max(id) from robot_hunt_data";
        return SqlUtils.select(sql, Integer.class);
    }

    public L1RobotHuntData selectLastHuntData() {
        Integer id = selectLastHuntDataId();

        if (id == null)
            return null;

        String sql = "select * from robot_hunt_data where id=?";
        List<L1RobotHuntData> list = SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1RobotHuntData.class), id);
        return list.isEmpty() ? null : list.get(0);
    }
}
