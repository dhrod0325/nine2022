package ks.system.portalsystem;

import ks.constants.L1ItemId;
import ks.model.L1Spawn;
import ks.model.L1Teleport;
import ks.model.L1World;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.scheduler.timer.BaseTime;
import ks.system.portalsystem.model.L1PortalData;
import ks.system.portalsystem.model.L1PortalLocation;
import ks.system.portalsystem.table.PortalSpawnTable;
import ks.system.portalsystem.table.PortalSystemTable;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1TeleportUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public abstract class L1AbstractPortalSystem implements L1PortalSystem {
    private static final Logger logger = LogManager.getLogger(L1AbstractPortalSystem.class);

    private L1PortalData portalData;

    private L1NpcInstance portalNpc;

    @Override
    public L1PortalData getPortalData() {
        return portalData;
    }

    @Override
    public void setPortalData(L1PortalData portalData) {
        this.portalData = portalData;
    }

    @Override
    public boolean isOpen(BaseTime time) {
        return portalData.isOpen(time);
    }

    public String getOpenMent() {
        String msg = portalData.getName() + " 오픈되었습니다";

        if (portalData.getFee() > 0) {
            msg += "(입장료:" + NumberFormat.getInstance().format(getPortalData().getFee()) + ")";
        }

        return msg;
    }

    public String getCloseMent() {
        return portalData.getName() + " 종료되었습니다";
    }

    @Override
    public void open(BaseTime time) {
        logger.info(portalData.getName() + " open");

        L1World.getInstance().broadcastPacketGreenMessage(getOpenMent());
        L1World.getInstance().broadcastServerMessage(getOpenMent());

        if (getPortalSpawnId() != 0) {
            L1Spawn sp = PortalSpawnTable.getInstance().getTemplate(getPortalSpawnId());

            portalNpc = sp.doSpawn(sp.getSpawnNum() + 1, 0, false);
            portalNpc.setRespawn(false);
            portalNpc.startDeleteTimer(getRemainingSecond());
        }
    }

    @Override
    public void run(BaseTime time) {//
        logger.trace(portalData.getName() + " run");
    }

    @Override
    public void waiting(BaseTime time) {
        for (L1PcInstance pc : getInnerPlayers()) {
            if (pc instanceof L1RobotInstance)
                continue;

            if (pc.isGm()) {
                continue;
            }

            if (isOpen(time))
                continue;

            pc.sendPackets(getCloseMent());

            L1TeleportUtils.teleportToGiran(pc);
        }
    }

    @Override
    public void shutdown() {
        if (portalNpc != null) {
            portalNpc.setDeleteTime(0);
        }
    }

    @Override
    public void close(BaseTime time) {
        shutdown();
        next();
        update();

        L1World.getInstance().broadcastPacketGreenMessage(getCloseMent());
        L1World.getInstance().broadcastServerMessage(getCloseMent());
    }

    protected abstract List<L1PcInstance> getInnerPlayers();

    public boolean isOpen() {
        if (portalData != null) {
            return portalData.isOpen();
        } else {
            return true;
        }
    }

    public Date getStartTime() {
        if (portalData != null)
            return portalData.getStartTime();

        return null;
    }

    public Date getCloseTime() {
        if (portalData != null)
            return portalData.getCloseTime();

        return null;
    }

    public long getRemainingSecond() {
        return portalData.getRemainingSecond();
    }

    public int getPortalSpawnId() {
        return portalData.getPortalSpawnId();
    }

    public void next() {
        portalData.next();
    }

    public void update() {
        PortalSystemTable.getInstance().update(portalData);
    }

    @Override
    public void teleportToStartLocation(L1PcInstance pc, int idx) {
        if (isOpen()) {
            if (getPortalData().getFee() > 0) {
                if (!pc.getInventory().checkItem(L1ItemId.ADENA, getPortalData().getFee())) {
                    pc.sendPackets(getPortalData().getName() + " 입장료는 " + NumberFormat.getInstance().format(getPortalData().getFee()) + "아데나 입니다");
                    return;
                }

                pc.getInventory().consumeItem(L1ItemId.ADENA, getPortalData().getFee());
            }

            L1PortalLocation portalLocation = null;

            if (idx == 1) {
                portalLocation = getPortalData().getTeleportLocation1();
            } else if (idx == 2) {
                portalLocation = getPortalData().getTeleportLocation2();
            } else if (idx == 3) {
                portalLocation = getPortalData().getTeleportLocation3();
            } else if (idx == 4) {
                portalLocation = getPortalData().getTeleportLocation4();
            } else if (idx == 5) {
                portalLocation = getPortalData().getTeleportLocation5();
            }

            if (portalLocation != null) {
                L1Teleport.teleport(pc, portalLocation.getX(), portalLocation.getY(), (short) portalLocation.getMap(), pc.getHeading(), true, 2);
            }
        } else {
            pc.sendPackets(getPortalData().getName() + " : 입장 가능한 시간이 아닙니다");
        }

    }
}
