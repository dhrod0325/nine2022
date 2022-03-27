package ks.model.item.function.potion;

import ks.constants.L1ItemId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillBrave;
import ks.packets.serverpackets.S_SkillSound;

import static ks.constants.L1SkillId.*;

public class BravePotion extends L1ItemInstance {

    public BravePotion(L1Item item) {
        super(item);
    }

    public static void removeBraveStatus(L1PcInstance pc, int statusBrave) {
        if (pc.getSkillEffectTimerSet().hasSkillEffect(statusBrave)) {
            clearTimer(pc, statusBrave);

            Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
            pc.getMoveState().setBraveSpeed(0);
        }
    }

    private static int clearTimer(L1PcInstance pc, int statusBrave) {
        int remainingTime = 0;

        if (pc.getSkillEffectTimerSet().hasSkillEffect(statusBrave)) {
            remainingTime = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(statusBrave);
            pc.getSkillEffectTimerSet().killSkillEffectTimer(statusBrave);
            pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
        }

        return remainingTime;
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
                pc.sendPackets(new S_ServerMessage(698));
                return;
            }

            pc.cancelAbsoluteBarrier();

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            int itemId = this.getItemId();

            if ((itemId == L1ItemId.POTION_OF_EMOTION_BRAVERY || itemId == L1ItemId.B_POTION_OF_EMOTION_BRAVERY || itemId == 41415 || itemId == 50014) && pc.isKnight()) {
                useBravePotion(pc, itemId);
            } else if ((itemId == 40068 || itemId == 140068 || itemId == 50015) && pc.isElf()) {
                useBravePotion(pc, itemId);
            } else if ((itemId == 40031 || itemId == 60001428) && pc.isCrown()) {
                useBravePotion(pc, itemId);
            } else if (itemId == 40733) {
                useBraveCoin(pc);
            } else {
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            pc.getInventory().removeItem(useItem, 1);
        }
    }

    private void useBraveCoin(L1PcInstance pc) {
        int time = 600;

        removeBraveStatus(pc, STATUS_ELFBRAVE);
        removeBraveStatus(pc, HOLY_WALK);
        removeBraveStatus(pc, MOVING_ACCELERATION);
        removeBraveStatus(pc, WIND_WALK);

        pc.sendPackets(new S_SkillSound(pc.getId(), 7110));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7110));
        pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, time * 1000);
        pc.getMoveState().setBraveSpeed(1);
    }

    private void useBravePotion(L1PcInstance pc, int itemId) {
        int time = 0;

        switch (itemId) {
            case L1ItemId.POTION_OF_EMOTION_BRAVERY:
                time = 300;
                break;
            case 40031:
            case 40068:
                time = 600;
                break;
            case 41415:
                time = 1800;
                break;
            case 50014:
            case 60001428:
                time = 1200;
                break;
            case L1ItemId.B_POTION_OF_EMOTION_BRAVERY:
                time = 350;
                break;
            case 50015:
                time = 1920;
                break;
            case 140068:
                time = 700;
                break;
        }

        if (itemId == 40068 || itemId == 50015 || itemId == 140068) {
            duplicateSkillCheck(pc);

            int remainingTime = clearTimer(pc, STATUS_ELFBRAVE);

            time += remainingTime;

            if (time > 3600) {
                time = 3600;
            }

            pc.sendPackets(new S_SkillBrave(pc.getId(), 3, time));
            Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 3, 0));
            pc.getSkillEffectTimerSet().setSkillEffect(STATUS_ELFBRAVE, time * 1000);
        } else {
            int remainingTime = clearTimer(pc, STATUS_BRAVE);

            time += remainingTime;

            if (time > 3600) {
                time = 3600;
            }

            pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
            Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 1, 0));
            pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, time * 1000);
        }

        pc.sendPackets(new S_SkillSound(pc.getId(), 751));
        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 751));
        pc.getMoveState().setBraveSpeed(1);
    }

    private void duplicateSkillCheck(L1PcInstance pc) {
        removeBraveStatus(pc, STATUS_BRAVE);
        removeBraveStatus(pc, DANCING_BLADES);
        removeBraveStatus(pc, WIND_WALK);
    }
}
