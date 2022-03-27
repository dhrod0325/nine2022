package ks.model.item.function.potion;

import ks.constants.L1ItemId;
import ks.core.network.opcode.L1Opcodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillHaste;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.*;

public class GreenPotion extends L1ItemInstance {

    public GreenPotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            useGreenPotion(pc);
        }
    }

    private void useGreenPotion(L1PcInstance pc) {
        L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
        int itemId = useItem.getItemId();

        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.cancelAbsoluteBarrier();

        int time = 0;
        switch (itemId) {
            case L1ItemId.POTION_OF_HASTE_SELF:
            case 40030:
                time = 300;
                break;
            case 40018:
            case 41342:
                time = 1800;
                break;
            case 40039:
                time = 600;
                break;
            case 40040:
                time = 900;
                break;
            case 41261:
            case 41262:
            case 41268:
            case 41269:
            case 41271:
            case 41272:
            case 41273:
                time = 30;
                break;
            case 41338:
            case 140018:
                time = 2250;
                break;
            case 50018:
                time = 1200;
                break;
            case L1ItemId.B_POTION_OF_HASTE_SELF:
                time = 350;
                break;
        }

        pc.sendPackets(new S_SkillSound(pc.getId(), 191));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 191));

        if (pc.getHasteItemEquipped() > 0) {
            return;
        }

        int tempRemainingTime = 0;

        if (pc.getSkillEffectTimerSet().hasSkillEffect(HASTE)) {
            tempRemainingTime = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(HASTE);
            pc.getSkillEffectTimerSet().removeSkillEffect(HASTE);
            cancelHaste(pc);
            pc.getMoveState().setMoveSpeed(0);
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(GREATER_HASTE)) {
            tempRemainingTime = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(GREATER_HASTE);
            pc.getSkillEffectTimerSet().removeSkillEffect(GREATER_HASTE);
            cancelHaste(pc);
            pc.getMoveState().setMoveSpeed(0);
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HASTE)) {
            tempRemainingTime = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(STATUS_HASTE);
            pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_HASTE);
            cancelHaste(pc);
            pc.getMoveState().setMoveSpeed(0);
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(SLOW)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(SLOW);
            cancelHaste(pc);
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(GRATE_SLOW)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(GRATE_SLOW);
            cancelHaste(pc);
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(ENTANGLE)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(ENTANGLE);
            cancelHaste(pc);
        } else if (pc.getSkillEffectTimerSet().hasSkillEffect(WIND_SHACKLE)) {
            pc.getSkillEffectTimerSet().removeSkillEffect(WIND_SHACKLE);
            cancelHaste(pc);
        } else {
            time = tempRemainingTime + time;

            if (time > 3600) {
                time = 3600;
            }

            pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
            Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 1, 0));
            pc.getMoveState().setMoveSpeed(1);
            pc.sendPackets(new S_ChatPacket(pc, "이동 속도 및 공격 속도가 빨라집니다.", L1Opcodes.S_OPCODE_MSG, 20));
            pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HASTE, time * 1000);
        }

        pc.getInventory().removeItem(useItem, 1);
    }

    private void cancelHaste(L1PcInstance pc) {
        pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
        Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0));
    }
}
