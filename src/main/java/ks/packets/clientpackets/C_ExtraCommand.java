package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.Broadcaster;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;

import static ks.constants.L1SkillId.SHAPE_CHANGE;

public class C_ExtraCommand extends ClientBasePacket {
    public C_ExtraCommand(byte[] data, L1Client client) {
        super(data);

        int actionId = readC();

        logger.debug("actionId : {}", actionId);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (actionId < 66 || actionId > 69) return;

        if (pc.isInvisible()) {
            return;
        }

        if (pc.isTeleport()) {
            return;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(SHAPE_CHANGE)) {
            int gfxId = pc.getGfxId().getTempCharGfx();
            if (gfxId != 6080 && gfxId != 6094) {
                return;
            }
        }

        Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), actionId)); // 주위의 플레이어에 송신
    }
}
