package ks.model.instance;

import ks.constants.L1ActionCodes;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.item.L1TreasureBox;
import ks.model.map.L1WorldMap;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_Door;
import ks.packets.serverpackets.S_DoorPack;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.npc.NpcRestScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class L1DoorInstance extends L1NpcInstance {
    private final Logger logger = LogManager.getLogger();

    public final int PASS = 0;
    public final int NOT_PASS = 1;

    public long closeTime = 0;
    private int doorId = 0;
    private int direction = 0;
    private int leftEdgeLocation = 0;
    private int rightEdgeLocation = 0;
    private int openStatus = L1ActionCodes.ACTION_Close;
    private int passable = NOT_PASS;
    private int keeperId = 0;
    private int autoStatus = 0;
    private int crackStatus;
    private int autoCloseTime;
    private int autoCloseNotOpenAble;
    private int giveItemId;

    public L1DoorInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (getMaxHp() == 0 || getMaxHp() == 1) {
            return;
        }

        if (getCurrentHp() > 0 && !isDead()) {
            L1AttackRun attack = new L1AttackRun(pc, this);
            attack.action();
            attack.commit();
            castleDoorAction(pc);
        }
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.getNearObjects().addKnownObject(this);
        perceivedFrom.sendPackets(new S_DoorPack(this));
        sendDoorPacket(perceivedFrom);

        if (getDoorId() >= 5000 && getDoorId() <= 5009) {
            if (getOpenStatus() == L1ActionCodes.ACTION_Close) {
                isPassAbleDoor(false);
                setPassable(NOT_PASS);
                sendDoorPacket(null);
            }

            if (getOpenStatus() == L1ActionCodes.ACTION_Open) {
                isPassAbleDoor(true);
                setPassable(PASS);
                sendDoorPacket(null);
            }
        }
    }

    @Override
    public void deleteMe() {
        setPassable(PASS);

        List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : players) {
            sendDoorPacket(pc);
        }

        super.deleteMe();
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (getMaxHp() == 0 || getMaxHp() == 1) {
            return;
        }

        int GfxId = getGfxId().getGfxId();

        if (getCurrentHp() > 0 && !isDead()) {
            int newHp = getCurrentHp() - damage;
            if (newHp <= 0 && !isDead()) {
                setCurrentHp(0);
                setDead(true);
                setActionStatus(doorAction(GfxId, L1ActionCodes.ACTION_DoorDie));
                death();
            }

            if (newHp > 0) {
                setCurrentHp(newHp);
                if ((getMaxHp() / 6) > getCurrentHp()) {
                    if (crackStatus != 5) {
                        Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), doorAction(GfxId, L1ActionCodes.ACTION_DoorAction5)));
                        setActionStatus(doorAction(GfxId, L1ActionCodes.ACTION_DoorAction5));
                        crackStatus = 5;
                    }
                } else if ((getMaxHp() * 2 / 6) > getCurrentHp()) {
                    if (crackStatus != 4) {
                        Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_DoorAction4));
                        setActionStatus(L1ActionCodes.ACTION_DoorAction4);
                        crackStatus = 4;
                    }
                } else if ((getMaxHp() * 3 / 6) > getCurrentHp()) {
                    if (crackStatus != 3) {
                        Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_DoorAction3));
                        setActionStatus(L1ActionCodes.ACTION_DoorAction3);
                        crackStatus = 3;
                    }
                } else if ((getMaxHp() * 4 / 6) > getCurrentHp()) {
                    if (getAutoStatus() == 1) {// 이 상태에서 자동 리페어가 되는지 확실하지는 않다;
                        repairGate();
                    } else if (crackStatus != 2) {
                        Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_DoorAction2));
                        setActionStatus(L1ActionCodes.ACTION_DoorAction2);
                        crackStatus = 2;
                    }
                } else if ((getMaxHp() * 5 / 6) > getCurrentHp()) {
                    if (crackStatus != 1) {
                        Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_DoorAction1));
                        setActionStatus(L1ActionCodes.ACTION_DoorAction1);
                        crackStatus = 1;
                    }
                }
            }
        } else if (!isDead()) {
            setDead(true);
            setActionStatus(doorAction(GfxId, L1ActionCodes.ACTION_DoorDie));
            death();
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);
    }

    public void death() {
        try {
            int gfxId = getGfxId().getGfxId();

            setCurrentHp(0);
            setDead(true);
            isPassAbleDoor(true);
            setActionStatus(doorAction(gfxId, L1ActionCodes.ACTION_DoorDie));

            getMap().setPassable(getLocation(), true);

            Broadcaster.broadcastPacket(L1DoorInstance.this, new S_DoActionGFX(getId(), doorAction(gfxId, L1ActionCodes.ACTION_DoorDie)));
            setPassable(PASS);
            sendDoorPacket(null);
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }

    private void sendDoorPacket(L1PcInstance pc) {
        int entranceX = getEntranceX();
        int entranceY = getEntranceY();

        int leftEdgeLocation = getLeftEdgeLocation();
        int rightEdgeLocation = getRightEdgeLocation();

        int size = rightEdgeLocation - leftEdgeLocation;

        if (size == 0) {
            sendPacket(pc, entranceX, entranceY);
        } else {
            if (direction == 0) {
                for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
                    sendPacket(pc, x, entranceY);
                }
            } else {
                if (doorId == 8020 || doorId == 8022) {
                    entranceX += 1;
                }

                for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
                    sendPacket(pc, entranceX, y);
                }
            }
        }
    }

    private void sendPacket(L1PcInstance pc, int x, int y) {
        if (pc != null) {
            pc.sendPackets(new S_Door(x, y, direction, getPassable()));
        } else {
            Broadcaster.broadcastPacket(this, new S_Door(x, y, direction, getPassable()));
        }
    }

    public void open() {
        open(null);
    }

    public void open(L1PcInstance pc) {
        if (isDead()) {
            return;
        }

        if (isClose()) {
            if (pc != null) {
                if (gfx.getGfxId() == 6345 || gfx.getGfxId() == 6348) {
                    if (!pc.getInventory().checkItem(433200, 1)) {
                        pc.sendPackets(new S_ServerMessage(329, "개미 부화 촉매제"));
                        return;
                    }

                    pc.getInventory().consumeItem(433200, 1);

                    if (giveItemId > 0) {
                        L1TreasureBox box = L1TreasureBox.get(giveItemId);

                        if (box != null) {
                            box.open(pc);
                        }
                    }
                }
            }

            isPassAbleDoor(true);

            if (autoCloseTime > 0) {
                setCloseTime(autoCloseTime);
                logger.debug("next open : {}", new SimpleDateFormat("HH:mm:ss").format(new Date(closeTime)));
                NpcRestScheduler.getInstance().addDoor(this);
            }

            Broadcaster.broadcastPacket(this, new S_DoActionGFX(this.getId(), L1ActionCodes.ACTION_Open));
            setOpenStatus(L1ActionCodes.ACTION_Open);
            setPassable(PASS);
            sendDoorPacket(null);
        }
    }

    public void setCloseTime(long closeTime) {
        NpcRestScheduler.getInstance().removeDoor(this);

        this.closeTime = System.currentTimeMillis() + (closeTime * 1000L);

        NpcRestScheduler.getInstance().addDoor(this);
    }

    public void close() {
        close(null);
    }

    public void close(L1PcInstance pc) {
        if (isDead()) {
            return;
        }

        if (isOpen()) {
            if (autoCloseNotOpenAble == 1) {
                if (closeTime > System.currentTimeMillis()) {
                    return;
                }
            }

            isPassAbleDoor(false);
            Broadcaster.broadcastPacket(this, new S_DoActionGFX(this.getId(), L1ActionCodes.ACTION_Close));
            setOpenStatus(L1ActionCodes.ACTION_Close);
            setPassable(NOT_PASS);
            sendDoorPacket(null);
        }
    }

    public int doorAction(int GfxId, int doorAction) {
        int action = doorAction;

        boolean doorCheck = GfxId == 11987 || GfxId == 11989 || GfxId == 11991 //켄트성문
                || GfxId == 12127 || GfxId == 12129 || GfxId == 12133//기란성문
                || GfxId == 12163 || GfxId == 12164 || GfxId == 12167 || GfxId == 12170;

        if (action == L1ActionCodes.ACTION_DoorDie) {
            if (doorCheck) {
                action = 36;
            }
        } else if (action == L1ActionCodes.ACTION_DoorAction5) {
            if (doorCheck) {
                action = 35;
            }
        }

        return action;
    }

    public void isPassAbleDoor(boolean flag) {
        int leftEdgeLocation = getLeftEdgeLocation();
        int rightEdgeLocation = getRightEdgeLocation();
        int size = rightEdgeLocation - leftEdgeLocation;

        if (size == 0) {
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX(), this.getY() - 1, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX(), this.getY() + 1, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() + 1, this.getY(), flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() + 1, this.getY() - 1, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() + 1, this.getY() + 1, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() - 1, this.getY(), flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() - 1, this.getY() - 1, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() - 1, this.getY() + 1, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX(), this.getY() - 2, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX(), this.getY() + 2, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() + 2, this.getY(), flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() + 2, this.getY() - 2, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() + 2, this.getY() + 2, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() - 2, this.getY(), flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() - 2, this.getY() - 2, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX() - 2, this.getY() + 2, flag);
            L1WorldMap.getInstance().getMap(getMapId()).setPassable(this.getX(), this.getY(), flag);
        } else {
            if (direction == 0) {
                for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY(), flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() - 1, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() + 1, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() - 2, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(doorX, this.getY() + 2, flag);
                }
            } else {
                for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX(), doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 1, doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 1, doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() + 2, doorY, flag);
                    L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(this.getX() - 2, doorY, flag);
                }
            }
        }
    }

    public void repairGate() {
        if (getMaxHp() > 1) {
            setDead(false);
            setCurrentHp(getMaxHp());
            setActionStatus(0);
            setCrackStatus(0);
            setOpenStatus(L1ActionCodes.ACTION_Open);
            close();
        }
    }

    private void castleDoorAction(L1PcInstance pc) {
        List<L1Object> list = L1World.getInstance().getVisibleObjects(this, 13);

        for (L1Object obj : list) {
            if (obj == null)
                continue;
            if (obj instanceof L1CastleGuardInstance) {
                L1CastleGuardInstance guard = (L1CastleGuardInstance) obj;
                guard.setTarget(pc);
            }
        }
    }

    public int getDoorId() {
        return doorId;
    }

    public void setDoorId(int i) {
        doorId = i;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int i) {
        if (i == 0 || i == 1 || i == 2) {
            direction = i;
        }
    }

    public int getEntranceX() {
        int entranceX;

        if (direction == 0) {
            entranceX = getX();
        } else {
            entranceX = getX() - 1;
        }

        return entranceX;
    }

    public int getEntranceY() {
        int entranceY;

        if (direction == 0) {
            entranceY = getY() + 1;
        } else {
            entranceY = getY();
        }

        return entranceY;
    }

    public int getLeftEdgeLocation() {
        return leftEdgeLocation;
    }

    public void setLeftEdgeLocation(int i) {
        leftEdgeLocation = i;
    }

    public int getRightEdgeLocation() {
        return rightEdgeLocation;
    }

    public void setRightEdgeLocation(int i) {
        rightEdgeLocation = i;
    }

    public int getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(int i) {
        if (i == L1ActionCodes.ACTION_Open || i == L1ActionCodes.ACTION_Close) {
            openStatus = i;
        }
    }

    public boolean isOpen() {
        return getOpenStatus() == L1ActionCodes.ACTION_Open;
    }

    public boolean isClose() {
        return getOpenStatus() == L1ActionCodes.ACTION_Close;
    }

    public int getPassable() {
        return passable;
    }

    public void setPassable(int i) {
        if (i == PASS || i == NOT_PASS) {
            passable = i;
        }
    }

    public int getKeeperId() {
        return keeperId;
    }

    public void setKeeperId(int i) {
        keeperId = i;
    }

    public int getCrackStatus() {
        return crackStatus;
    }

    public void setCrackStatus(int i) {
        crackStatus = i;
    }

    public int getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(int i) {
        autoStatus = i;
    }

    public int getAutoCloseTime() {
        return autoCloseTime;
    }

    public void setAutoCloseTime(int autoCloseTime) {
        this.autoCloseTime = autoCloseTime;
    }

    public int getAutoCloseNotOpenAble() {
        return autoCloseNotOpenAble;
    }

    public void setAutoCloseNotOpenAble(int autoCloseNotOpenAble) {
        this.autoCloseNotOpenAble = autoCloseNotOpenAble;
    }

    public int getGiveItemId() {
        return giveItemId;
    }

    public void setGiveItemId(int giveItemId) {
        this.giveItemId = giveItemId;
    }


}


