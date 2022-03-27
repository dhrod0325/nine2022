package ks.system.robot.ai;

import ks.core.datatables.getback.GetBackTable;
import ks.model.L1PolyMorph;
import ks.model.L1Teleport;
import ks.model.instance.L1ItemInstance;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1TeleportUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1RobotNormalAi extends L1RobotAiAbstractTemplate {
    protected Logger logger = LogManager.getLogger(getClass());

    public L1RobotNormalAi(L1RobotInstance robot) {
        super(robot);
    }

    @Override
    public void executeBuff() {
        if (robot.isDead()) {
            return;
        }

        doLocationCheck();

        doDoll();

        doBrave();

        doPoly();

        doRepairWeapon();

        int hp = calcHpPer();

        if (hp <= 90) {
            doPotion();
        }

        if (hp < 20) {
            doHomeTeleport();
        }

        if (!robot.getInventory().checkItem(40024, 10)) {
            setAiStatus(AI_STATUS_SHOP);
        }
    }

    @Override
    public void toAiShop() {
        super.toAiShop();

        L1ItemInstance item = robot.getInventory().findItemId(40024);

        if (item == null) {
            robot.getInventory().storeItem(40024, 200);
        }
    }

    public void doHomeTeleport() {
        resetCounts();
        giveUp();

        int[] loc = GetBackTable.getInstance().getBackLocation(robot);

        L1Teleport.teleport(robot, loc[0], loc[1], (short) loc[2], 5, true);

        setAiStatus(AI_STATUS_SETTING);
    }

    public void doLocationCheck() {
        if (robot.getTpl() != null) {
            if (robot.getTpl().getLocMap() != robot.getMapId()) {
                int hp = calcHpPer();

                if (hp > 90) {
                    L1Teleport.teleport(robot, robot.getTpl().getLocX(), robot.getTpl().getLocY(), (short) robot.getTpl().getLocMap(), 0, true);
                    setAiStatus(AI_STATUS_WALK);
                }
            }
        }
    }

    public void doDoll() {
        if (!robot.isUsingDoll() && robot.getTpl() != null && robot.getTpl().getDollItemId() != 0) {
            robot.getInventory().storeItem(41246, 50);
            robot.getInventory().findItemId(robot.getTpl().getDollItemId()).clickItem(robot, null);
        }

        robot.setAinHasad(2000000);
    }

    public void doBrave() {
        if (robot.getMoveState().getMoveSpeed() != 1) {
            robot.getMoveState().setMoveSpeed(1);
        }

        if (robot.isKnight() || robot.isCrown() || robot.isElf()) {
            robot.getMoveState().setBraveSpeed(1);
        }
    }

    public void doPoly() {
        int polyId;
        int time = 7200;

        if (robot.getGfxId().getGfxId() == robot.getGfxId().getTempCharGfx()) {
            if (robot.getWeapon() != null) {
                switch (robot.getWeapon().getItem().getType()) {
                    case 4:
                    case 13:
                        polyId = 2284;
                        break;
                    default:
                        polyId = 6142;
                        break;
                }

                L1PolyMorph.doPoly(robot, polyId, time, 1);
            }
        }
    }

    public void doRepairWeapon() {
        if (robot.getWeapon() != null && robot.getWeapon().getDurability() > 0) {
            robot.getInventory().recoveryDamage(robot.getWeapon());
        }
    }

    public void doPotion() {
        L1ItemInstance item = robot.getInventory().findItemId(40024);

        if (item != null) {
            item.clickItem(robot, null);
        }
    }

    @Override
    public void giveUp() {
        setAiStatus(AI_STATUS_WALK);
        hateList.clear();
        aiTryCount = 0;
    }

    @Override
    public void teleport() {
        L1TeleportUtils.randomTeleport(robot);
    }
}