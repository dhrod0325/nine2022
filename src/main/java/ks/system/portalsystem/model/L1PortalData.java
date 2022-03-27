package ks.system.portalsystem.model;

import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.scheduler.timer.BaseTime;
import ks.scheduler.timer.realTime.RealTimeScheduler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.*;

public class L1PortalData {

    private Logger logger = LogManager.getLogger(getClass());

    private int id;
    private String name;
    private String portalClassName;
    private Date startTime;
    private int portalSpawnId;
    private int durationMinute;
    private int periodMinute;
    private String impl;
    private int isUse;
    private String startTimes;
    private int fee;
    private int npcId;
    private int mapId;

    private String teleport1;
    private String teleport2;
    private String teleport3;
    private String teleport4;
    private String teleport5;

    private int recommendLevel;

    public L1PortalLocation getTeleportLocation5() {
        return teleportLocation(teleport5);
    }

    public L1PortalLocation getTeleportLocation1() {
        return teleportLocation(teleport1);
    }

    public L1PortalLocation getTeleportLocation2() {
        return teleportLocation(teleport2);
    }

    public L1PortalLocation getTeleportLocation3() {
        return teleportLocation(teleport3);
    }

    public L1PortalLocation getTeleportLocation4() {
        return teleportLocation(teleport4);
    }

    private L1PortalLocation teleportLocation(String v) {
        if (!StringUtils.isEmpty(v)) {
            String[] s = v.split(",");

            int x = Integer.parseInt(s[0].trim());
            int y = Integer.parseInt(s[1].trim());
            int m = Integer.parseInt(s[2].trim());
            String str = s[3];

            return new L1PortalLocation(x, y, m, str);
        }

        return null;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(String startTimes) {
        this.startTimes = startTimes;
    }

    public List<L1Time> getStartTimeList() {
        List<L1Time> result = new ArrayList<>();

        if (!StringUtils.isEmpty(startTimes)) {
            String[] list = startTimes.split(",");

            for (String o : list) {
                String[] tt = o.split(":");
                result.add(new L1Time(Integer.parseInt(tt[0]), Integer.parseInt(tt[1])));
            }
        }

        return result;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getPortalSpawnId() {
        return portalSpawnId;
    }

    public void setPortalSpawnId(int portalSpawnId) {
        this.portalSpawnId = portalSpawnId;
    }

    public int getDurationMinute() {
        return durationMinute;
    }

    public void setDurationMinute(int durationMinute) {
        this.durationMinute = durationMinute;
    }

    public int getPeriodMinute() {
        return periodMinute;
    }

    public void setPeriodMinute(int periodMinute) {
        this.periodMinute = periodMinute;
    }

    public boolean isOpen(BaseTime time) {
        Date current = time.toDate();

        if (getStartTimeList().isEmpty()) {
            return startTime.before(current) && getCloseTime().after(current);
        } else {
            if (getStartTime().before(current)) {
                for (L1Time runTime : getStartTimeList()) {
                    Calendar closeTime = Calendar.getInstance();
                    closeTime.setTime(runTime.getTime());
                    closeTime.add(Calendar.MINUTE, durationMinute);

                    //logger.debug("{} 열리는 시간 : {}", getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(a.getTime()));

                    if (current.after(runTime.getTime()) && current.before(closeTime.getTime())) {
                        setStartTime(runTime.getTime());
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void next() {
        if (getStartTimeList().isEmpty()) {
            setStartTime(DateUtils.addMinutes(getStartTime(), periodMinute));
        }
    }

    public boolean isOpen() {
        return isOpen(RealTimeScheduler.getInstance().getTime());
    }

    public Date getCloseTime() {
        Calendar result = Calendar.getInstance();
        result.setTime(startTime);
        result.add(Calendar.MINUTE, durationMinute);

        return result.getTime();
    }

    public long getRemainingSecond() {
        return getCloseTime().getTime() - System.currentTimeMillis();
    }

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public String getTeleport1() {
        return teleport1;
    }

    public void setTeleport1(String teleport1) {
        this.teleport1 = teleport1;
    }

    public String getTeleport2() {
        return teleport2;
    }

    public void setTeleport2(String teleport2) {
        this.teleport2 = teleport2;
    }

    public String getTeleport3() {
        return teleport3;
    }

    public void setTeleport3(String teleport3) {
        this.teleport3 = teleport3;
    }

    public String getTeleport4() {
        return teleport4;
    }

    public void setTeleport4(String teleport4) {
        this.teleport4 = teleport4;
    }

    public String getTeleport5() {
        return teleport5;
    }

    public void setTeleport5(String teleport5) {
        this.teleport5 = teleport5;
    }

    public int getRecommendLevel() {
        return recommendLevel;
    }

    public void setRecommendLevel(int recommendLevel) {
        this.recommendLevel = recommendLevel;
    }

    public String getPortalClassName() {
        return portalClassName;
    }

    public void setPortalClassName(String portalClassName) {
        this.portalClassName = portalClassName;
    }

    public void showHtml(L1PcInstance pc, L1NpcInstance npc) {
        String name = getName();
        String time = getStartTimes();
        String fee;
        String duration = getDurationMinute() + "";

        if (getFee() == 0) {
            fee = "무료";
        } else {
            fee = NumberFormat.getInstance().format(getFee());
        }

        L1PortalLocation t1 = getTeleportLocation1();
        L1PortalLocation t2 = getTeleportLocation2();
        L1PortalLocation t3 = getTeleportLocation3();
        L1PortalLocation t4 = getTeleportLocation4();
        L1PortalLocation t5 = getTeleportLocation5();

        pc.sendPackets(new S_ShowCCHtml(npc.getId(), "cc_rotation", Arrays.asList(
                name,
                time,
                fee,
                duration,
                t1 == null ? "&nbsp;" : t1.getName(),
                t2 == null ? "&nbsp;" : t2.getName(),
                t3 == null ? "&nbsp;" : t3.getName(),
                t4 == null ? "&nbsp;" : t4.getName(),
                t5 == null ? "&nbsp;" : t5.getName(),
                getRecommendLevel()
        )));
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
