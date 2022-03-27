package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.core.datatables.DungeonRandomTable;
import ks.core.datatables.DungeonTable;
import ks.core.network.L1Client;
import ks.model.*;
import ks.model.pc.L1PcInstance;
import ks.model.pc.hackCheck.speedHack.L1AcceleratorCheck;
import ks.model.trap.L1WorldTraps;
import ks.packets.serverpackets.S_MoveCharPacket;
import ks.packets.serverpackets.S_Party;

import java.util.List;

public class C_MoveChar extends ClientBasePacket {
    private static final byte[] HEADING_TABLE_X = CodeConfig.HEADING_TABLE_X;
    private static final byte[] HEADING_TABLE_Y = CodeConfig.HEADING_TABLE_Y;

    public C_MoveChar(byte[] decrypt, L1Client client) {
        super(decrypt);

        int locX = readH();
        int locY = readH();

        int heading = readC();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (heading < 0 || heading > 7)
            return;

        if (pc.isStatReturnCheck()) {
            return;
        }

        if (pc.isTeleport()) {
            return;
        }

        if (pc.getLevel() == 1) {
            if (pc.getExp() >= 10000) {
                pc.disconnect();
                return;
            }
        }

        String time = pc.getDataMap().get("damagedTime");

        if (time != null) {
            logger.debug("damagedTime:{}", System.currentTimeMillis() - Long.parseLong(time));
            pc.getDataMap().remove("motionTime");
        }

        if (pc.getTradeID() != 0) {
            L1Trade.cancel(pc);
        }

        if (pc.isAutoKingBuff()) {
            pc.setAutoKingBuff(false);
            pc.sendPackets("자동군업이 종료되었습니다");
        }

        if (pc.getStateMap().isAutoClan()) {
            pc.getStateMap().setAutoClan(false);
            pc.sendPackets("무인혈맹이 종료되었습니다");
        }

        if (!pc.getMap().ismPassable(locX, locY, heading)) {
            int castleId = L1CastleLocation.getCastleIdByArea(pc);

            if (castleId == 0 && pc.getMapId() != 4 && pc.getMapId() != 1) {
                L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
            }
        }

        if (pc.getAutoAttack().isAuto()) {
            pc.getAutoAttack().targetClear();
        }

        pc.endFishing();

        pc.startAutoUpdate();

        if (CodeConfig.SPEED_CHECK_MOVE_INTERVAL) {
            int result = pc.getAcceleratorChecker().checkInterval(L1AcceleratorCheck.ACT_TYPE.MOVE);
            if (result == L1AcceleratorCheck.R_DISCONNECTED) {
                logger.info("CHECK_MOVE_INTERVAL DISCONNECTED");
                return;
            }
        }

        if (pc.isInParty()) {
            List<L1PcInstance> members = pc.getParty().getMembers();

            if (pc.getParty() != null) {
                for (L1PcInstance member : members) {
                    member.sendPackets(new S_Party(0x6e, member));
                }
            }
        }

        pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
        pc.setCallClanId(0);

        if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
            pc.setRegenState(L1PcInstance.REGEN_STATE_MOVE);
        }

        pc.getMap().setPassable(pc.getLocation(), true);

        locX += HEADING_TABLE_X[heading];
        locY += HEADING_TABLE_Y[heading];

        if (DungeonTable.getInstance().dg(locX, locY, pc.getMap().getId(), pc)) {
            return;
        }

        if (DungeonRandomTable.getInstance().dg(locX, locY, pc.getMap().getId(), pc)) {
            return;
        }

        pc.getPierceCheck().setPrevLocation(new L1Location(pc.getX(), pc.getY(), pc.getMapId()));

        pc.getLocation().set(locX, locY);
        pc.setHeading(heading);

        Broadcaster.broadcastPacket(pc, new S_MoveCharPacket(pc));

        L1WorldTraps.getInstance().onPlayerMoved(pc);

        pc.getMap().setPassable(pc.getLocation(), false);
    }
}
