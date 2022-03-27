package ks.system.autoHunt;

import ks.core.datatables.getback.GetBackTable;
import ks.model.Broadcaster;
import ks.model.ItemDelayTimer;
import ks.model.L1PolyMorph;
import ks.model.L1Teleport;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.L1TeleportUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1AutoHuntNormal extends L1AutoHunt {
    private static final Logger logger = LogManager.getLogger(L1AutoHuntNormal.class);

    public L1AutoHuntNormal(L1PcInstance robot) {
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

        if (hp <= 95) {
            doPotion();
        }

        if (hp < 20) {
            doHomeTeleport();
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
    }

    public void doDoll() {
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
        if (robot.hasItemDelay(50)) {
            return;
        }

        int heal = RandomUtils.nextInt(30, 60);
        Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 197));
        robot.setCurrentHp(robot.getCurrentHp() + heal);

        robot.addItemDelay(50, new ItemDelayTimer(robot, 1500));
    }

    @Override
    public void giveUp() {
        setAiStatus(AI_STATUS_WALK);
        attackList.clear();
        aiTryCount = 0;
    }

    @Override
    public void teleport() {
        L1TeleportUtils.randomTeleport(robot);
    }
}
