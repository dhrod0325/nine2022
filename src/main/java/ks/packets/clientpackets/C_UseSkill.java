package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.core.datatables.MapsTable;
import ks.core.datatables.SkillsTable;
import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.attack.utils.L1MagicUtils;
import ks.model.instance.L1ScarecrowInstance;
import ks.model.pc.L1PcInstance;
import ks.model.pc.hackCheck.speedHack.L1AcceleratorCheck;
import ks.model.skill.L1SkillUse;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.L1StatusUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static ks.constants.L1SkillId.*;

public class C_UseSkill extends ClientBasePacket {
    private static final Logger logger = LogManager.getLogger(C_UseSkill.class);

    public C_UseSkill(byte[] data, L1Client client) {
        super(data);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        try {
            int row = readC();
            int column = readC();
            int skillId = (row * 8) + column + 1;

            String charName = "";

            int targetId = 0;
            int targetX = 0;
            int targetY = 0;

            if (pc.isTeleport() || pc.isDead()) {
                return;
            }

            if (!pc.isSkillMastery(skillId)) {
                return;
            }

            if (!pc.isUsableSkill()) {
                pc.sendPackets(new S_ServerMessage(563));
                pc.tell();
                return;
            }

            if (pc.getMapId() == 350) {
                pc.sendPackets(new S_SystemMessage("시장 안에서는 마법이 불가능합니다."));
                return;
            }

            if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_INVISI_OFF)) {
                return;
            }

            if (pc.getAutoAttack().isAuto()) {
                if (!pc.getAutoAttack().onSkill(skillId)) {
                    return;
                }
            }

            if (CodeConfig.SPEED_CHECK_SPELL_INTERVAL) {
                int result;

                if (SkillsTable.getInstance().getTemplate(skillId).getActionId() == L1ActionCodes.ACTION_SkillAttack) {
                    result = pc.getAcceleratorChecker().checkInterval(L1AcceleratorCheck.ACT_TYPE.SPELL_DELAY);
                } else {
                    result = pc.getAcceleratorChecker().checkInterval(L1AcceleratorCheck.ACT_TYPE.SPELL_NO_DELAY);
                }

                if (result == L1AcceleratorCheck.R_DISCONNECTED) {
                    return;
                }
            }

            if (data.length > 4) {
                try {
                    switch (skillId) {
                        case SUMMON_MONSTER:
                            targetX = readC();
                            targetY = readC();
                            break;
                        case CALL_CLAN:
                        case RUN_CLAN:
                            charName = readS();
                            break;
                        case TRUE_TARGET:
                            targetId = readD();
                            targetX = readH();
                            targetY = readH();
                            break;
                        case TELEPORT:
                        case MASS_TELEPORT:
                            targetId = readH();
                            targetX = readH();
                            targetY = readH();
                            break;
                        case FIRE_WALL:
                        case LIFE_STREAM:
                            targetX = readH();
                            targetY = readH();
                            break;
                        default:
                            if (data.length >= 12) {
                                targetId = readD();
                                targetX = readH();
                                targetY = readH();
                            } else {
                                targetId = readD();
                            }
                            break;
                    }

                } catch (Exception e) {
                    logger.error(e);
                }
            }

            L1Object o = L1World.getInstance().findObject(targetId);

            if (!(o instanceof L1ScarecrowInstance)) {
                if (L1CommonUtils.isStandByServer(pc)) {
                    return;
                }
            }

            L1MagicUtils.stopAbsoluteBarrier(pc);

            pc.getSkillEffectTimerSet().killSkillEffectTimer(MEDITATION);

            if (skillId == CALL_CLAN || skillId == RUN_CLAN) {
                if (charName.isEmpty()) {
                    return;
                }

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < charName.length(); i++) {
                    if (charName.charAt(i) == '[') {
                        break;
                    }

                    sb.append(charName.charAt(i));
                }

                L1PcInstance target = L1World.getInstance().getPlayer(sb.toString());

                if (target == null) {
                    pc.sendPackets(new S_ServerMessage(73, charName));
                    return;
                }

                if (pc.getClanId() != target.getClanId()) {
                    pc.sendPackets(new S_ServerMessage(414));
                    return;
                }

                List<Integer> huntMapList = MapsTable.getInstance().huntMapList();

                if (huntMapList.contains((int) pc.getMapId()) || huntMapList.contains((int) target.getMapId()) || pc.getMapId() != 4 || pc.isAutoKingBuff()) {
                    pc.sendPackets("대상 또는 자신이 스킬을 사용할 수 없는 지역입니다");
                    return;
                }

                if (L1StatusUtils.isStatusLock(target) || L1StatusUtils.isStatusLock(pc)) {
                    pc.sendPackets("대상 또는 자신이 스킬을 사용할 수 없는 상태입니다");
                    return;
                }

                targetId = target.getId();

                if (skillId == CALL_CLAN) {
                    int callClanId = pc.getCallClanId();
                    if (callClanId == 0 || callClanId != targetId) {
                        pc.setCallClanId(targetId);
                        pc.setCallClanHeading(pc.getHeading());
                    }
                }
            }

            L1SkillUse skillUse = new L1SkillUse(pc, skillId, targetId, targetX, targetY, 0, L1SkillUse.TYPE_NORMAL);
            skillUse.run();

        } catch (Exception e) {
            logger.error(e);
            logger.error("오류", e);
        }
    }
}
