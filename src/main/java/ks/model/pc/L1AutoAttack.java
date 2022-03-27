package ks.model.pc;

import ks.app.LineageAppContext;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.core.datatables.SkillsTable;
import ks.model.L1Character;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.hackCheck.speedHack.L1AcceleratorCheck;
import ks.packets.serverpackets.S_AddItem;
import ks.packets.serverpackets.S_DeleteInventoryItem;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public class L1AutoAttack implements Runnable {
    private static final int DELAY_DIFF = 5;

    public static final int ITEM_ID = 60001164;

    private final L1PcInstance pc;
    private int targetId;

    private ScheduledFuture<?> attackFuture;

    private boolean auto = false;

    public L1AutoAttack(L1PcInstance pc) {
        this.pc = pc;
    }

    public boolean isAuto() {
        return auto;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void onVisualUpdate() {
        if (!isAuto()) {
            return;
        }

        long time = System.currentTimeMillis();

        pc.getAcceleratorChecker().put(L1AcceleratorCheck.ACT_TYPE.SPELL_NO_DELAY, time);
        pc.getAcceleratorChecker().put(L1AcceleratorCheck.ACT_TYPE.SPELL_DELAY, time);
        pc.getAcceleratorChecker().put(L1AcceleratorCheck.ACT_TYPE.ATTACK, time);
    }

    public void start() {
        if (!isAuto())
            return;

        attackFuture = LineageAppContext.commonTaskScheduler().scheduleAtFixedRate(this, Instant.now(), Duration.ofMillis(20));
    }

    public void stop() {
        if (attackFuture != null) {
            attackFuture.cancel(true);
            attackFuture = null;
        }
    }

    @Override
    public void run() {
        if (!isAuto()) {
            return;
        }

        if (targetId == 0) {
            return;
        }

        boolean result = pc.getAcceleratorChecker().isRightInterval(L1AcceleratorCheck.ACT_TYPE.ATTACK, DELAY_DIFF);

        if (!result) {
            return;
        }

        L1Object target = L1World.getInstance().findObject(targetId);

        if (target instanceof L1NpcInstance) {
            L1NpcInstance targetCharacter = (L1NpcInstance) target;

            if (targetCharacter.isDead()) {
                targetClear();
                return;
            }

            if (L1AttackUtils.isNotHitAble(pc, targetCharacter)) {
                targetClear();
            } else {
                if (pc.getAttack().isAttack(targetCharacter)) {
                    if (!targetCharacter.isDead()) {
                        target.onAction(pc);
                        pc.getAcceleratorChecker().put(L1AcceleratorCheck.ACT_TYPE.ATTACK, System.currentTimeMillis());
                    }
                }
            }
        } else if (target instanceof L1PcInstance) {
            off();
        }
    }

    public void targetClear() {
        setTargetId(0);
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public void useItem() {
        if (pc.getWeapon() == null) {
            pc.sendPackets("무기를 착용하셔야 사용 가능합니다");
            return;
        }

        if (isAuto()) {
            off();
        } else {
            on();
        }

        reloadItem();
    }

    public void reloadItem() {
        L1ItemInstance item = getItem();

        if (item != null) {
            if (isAuto()) {
                item.setGfxId(8804);
            } else {
                item.setGfxId(8803);
            }

            pc.sendPackets(new S_DeleteInventoryItem(item));
            pc.sendPackets(new S_AddItem(item));
        }
    }

    public L1ItemInstance getItem() {
        return pc.getInventory().findItemId(ITEM_ID);
    }

    public void damaged(L1Character attacker) {
        if (attacker instanceof L1PcInstance) {
            if (isAuto()) {
                off();
            }
        }
    }

    public void death() {
        if (isAuto()) {
            off();
        }
    }

    public void off() {
        pc.sendPackets("자동칼질이 종료되었습니다");

        stop();
        setAuto(false);
        targetClear();

        reloadItem();
    }

    public void quit() {
        if (attackFuture != null) {
            attackFuture.cancel(true);
            attackFuture = null;
        }
    }

    public void on() {
        setAuto(true);
        start();

        pc.sendPackets("자동칼질이 시작되었습니다");
        pc.sendGreenMessageAndSystemMessage("자동칼질중 캐릭 이동이 빠르게 반응하지 않을수 있습니다");
    }

    public boolean onSkill(int skillId) {
        stop();

        L1AcceleratorCheck.ACT_TYPE actType;

        if (SkillsTable.getInstance().getTemplate(skillId).getActionId() == L1ActionCodes.ACTION_SkillAttack) {
            actType = L1AcceleratorCheck.ACT_TYPE.SPELL_DELAY;
        } else {
            actType = L1AcceleratorCheck.ACT_TYPE.SPELL_NO_DELAY;
        }

        boolean isRight = pc.getAcceleratorChecker().isRightInterval(actType, DELAY_DIFF);

        if (isRight) {
            pc.getAcceleratorChecker().put(actType, System.currentTimeMillis());

            int skillInterval = pc.getAcceleratorChecker().getRightInterval(actType);

            skillInterval /= CodeConfig.AUTO_ATTACK_DELAY;

            pc.getAcceleratorChecker().put(L1AcceleratorCheck.ACT_TYPE.ATTACK, System.currentTimeMillis() + skillInterval);

            start();
        }

        return isRight;
    }
}
