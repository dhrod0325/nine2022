package ks.system.boss.model;

import ks.model.instance.L1MonsterInstance;
import ks.util.common.random.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class L1Boss {
    private final Logger logger = LogManager.getLogger();

    private int id;
    private int npcId;
    private String mapName;

    private int spawnGroup;
    private int spawnX1;
    private int spawnY1;
    private int spawnX2;
    private int spawnY2;
    private String spawnYoil;
    private int spawnMap;
    private int randomRange;
    private String monName;
    private String ment;
    private boolean yn;
    private String spawnTime;
    private int bossGrade;
    private L1MonsterInstance npc;
    private int deleteMin;
    private Date regDate;
    private int randomSpawnMinute;

    private final List<L1BossTime> timeList = new ArrayList<>();

    public L1MonsterInstance getNpc() {
        return npc;
    }

    public void setNpc(L1MonsterInstance npc) {
        this.npc = npc;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public int getDeleteMin() {
        return deleteMin;
    }

    public void setDeleteMin(int deleteMin) {
        this.deleteMin = deleteMin;
    }

    public void setSpawnTime(String spawnTime) {
        this.spawnTime = spawnTime;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int id) {
        this.npcId = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String name) {
        this.mapName = name;
    }

    public int getSpawnX1() {
        return spawnX1;
    }

    public void setSpawnX1(int spawnX1) {
        this.spawnX1 = spawnX1;
    }

    public int getSpawnY1() {
        return spawnY1;
    }

    public void setSpawnY1(int spawnY1) {
        this.spawnY1 = spawnY1;
    }

    public int getSpawnMap() {
        return spawnMap;
    }

    public void setSpawnMap(int mapid) {
        this.spawnMap = mapid;
    }

    public String getMonName() {
        return monName;
    }

    public void setMonName(String mon) {
        this.monName = mon;
    }

    public List<String> getYoilList() {
        List<String> result = new ArrayList<>();

        if (!StringUtils.isEmpty(spawnYoil)) {
            result.addAll(Arrays.asList(spawnYoil.split(",")));
        }

        return result;
    }

    public boolean isSpawnTime(int h, int m, long currentTime) {
        if (!isSpawnYoil(currentTime)) {
            return false;
        }

        for (L1BossTime t : timeList) {
            if (t.isRandom()) {
                if (t.getRandomHour() == h && t.getRandomMinute() == m) {
                    return true;
                }
            } else {
                if (t.getHour() == h && t.getMinute() == m) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getYoil(long time) {
        Date date = new Date(0);

        String[] weekDay = {"일", "월", "화", "수", "목", "금", "토"};

        date.setTime(time);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return weekDay[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        L1Boss boss = (L1Boss) o;
        return id == boss.id;
    }

    public int getBossGrade() {
        return bossGrade;
    }

    public void setBossGrade(int bossGrade) {
        this.bossGrade = bossGrade;
    }

    public int getSpawnX2() {
        return spawnX2;
    }

    public void setSpawnX2(int spawnX2) {
        this.spawnX2 = spawnX2;
    }

    public int getSpawnY2() {
        return spawnY2;
    }

    public void setSpawnY2(int spawnY2) {
        this.spawnY2 = spawnY2;
    }

    public void buildTimeList() {
        timeList.clear();

        if ("정각".equalsIgnoreCase(spawnTime)) {
            spawnTime = "00:00,01:00,02:00,03:00,04:00,05:00,06:00,07:00,08:00,09:00,10:00,11:00,12:00,13:00,14:00,15:00,16:00,17:00,18:00,19:00,20:00,21:00,22:00,23:00";
        } else if ("삼십분마다".equalsIgnoreCase(spawnTime)) {
            spawnTime = "00:00,00:30,01:00,01:30,02:00,02:30,03:00,03:30,04:00,04:30,05:00,05:30,06:00,06:30,07:00,07:30,08:00,08:30,09:00,09:30,10:00,10:30,11:00,11:30,12:00,12:30,13:00,13:30,14:00,14:30,15:00,15:30,16:00,16:30,17:00,17:30,18:00,18:30,19:00,19:30,20:00,20:30,21:00,21:30,22:00,22:30,23:00,23:30";
        } else if ("삼십분되면".equalsIgnoreCase(spawnTime)) {
            spawnTime = "00:30,01:30,02:30,03:30,04:30,05:30,06:30,07:30,08:30,09:30,10:30,11:30,12:30,13:30,14:30,15:30,16:30,17:30,18:30,19:30,20:30,21:30,22:30,23:30";
        }

        if (!StringUtils.isEmpty(spawnTime)) {
            for (String s : spawnTime.split(",")) {
                String[] t = s.split(":");

                L1BossTime o = new L1BossTime();

                int hour = Integer.parseInt(t[0]);
                int minute = Integer.parseInt(t[1]);

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);

                o.setHour(c.get(Calendar.HOUR_OF_DAY));
                o.setMinute(c.get(Calendar.MINUTE));
                o.setRandom(randomSpawnMinute > 0);

                buildRandomTime(o);

                timeList.add(o);
            }
        }
    }

    public void buildNextTime() {
        L1BossTime nextTime = nextTime();
        buildRandomTime(nextTime);

        logger.debug("nextTime : {}", nextTime);
    }

    public void buildRandomTime(L1BossTime time) {
        if (!time.isRandom()) {
            return;
        }

        Calendar c = time.toCalendar();
        c.add(Calendar.MINUTE, RandomUtils.nextInt(randomSpawnMinute));

        time.setRandomHour(c.get(Calendar.HOUR_OF_DAY));
        time.setRandomMinute(c.get(Calendar.MINUTE));

        logger.trace("buildNextTime - time:{}", time);
    }

    public L1BossTime nextTime() {
        if (timeList.isEmpty()) {
            return null;
        }

        SortedSet<L1BossTime> set = new TreeSet<>(timeList);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 1);

        SortedSet<L1BossTime> sortedSet = set.tailSet(new L1BossTime(c.getTime()));

        if (sortedSet.isEmpty()) {
            sortedSet = set;
        }

        return sortedSet.first();
    }

    public void setSpawnYoil(String spawnYoil) {
        this.spawnYoil = spawnYoil;
    }

    public String getMent() {
        return ment;
    }

    public void setMent(String ment) {
        this.ment = ment;
    }

    public String getSpawnYoil() {
        return spawnYoil;
    }

    public int getRandomRange() {
        return randomRange;
    }

    public void setRandomRange(int randomRange) {
        this.randomRange = randomRange;
    }

    public boolean isYn() {
        return yn;
    }

    public void setYn(boolean yn) {
        this.yn = yn;
    }

    public String getSpawnTime() {
        return spawnTime;
    }

    public boolean isSpawnYoil(long currentTime) {
        String nowYoil = getYoil(currentTime);

        for (String yoil : getYoilList()) {
            if (StringUtils.isEmpty(yoil)) {
                continue;
            }

            if (yoil.equalsIgnoreCase("전체") || yoil.equalsIgnoreCase(nowYoil)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAreaSpawn() {
        return getSpawnX1() > 0 && getSpawnX2() > 0 && getSpawnY1() > 0 && getSpawnY2() > 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRandomSpawnMinute() {
        return randomSpawnMinute;
    }

    public void setRandomSpawnMinute(int randomSpawnMinute) {
        this.randomSpawnMinute = randomSpawnMinute;
    }

    public int getSpawnGroup() {
        return spawnGroup;
    }

    public void setSpawnGroup(int spawnGroup) {
        this.spawnGroup = spawnGroup;
    }
}