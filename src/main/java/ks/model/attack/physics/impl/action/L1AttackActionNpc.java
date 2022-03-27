package ks.model.attack.physics.impl.action;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.attack.physics.impl.L1AttackAction;
import ks.model.attack.physics.impl.vo.L1AttackParam;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.instance.L1NpcInstance;
import ks.model.types.Point;
import ks.packets.serverpackets.S_AttackMissPacket;
import ks.packets.serverpackets.S_AttackPacketForNpc;
import ks.packets.serverpackets.S_UseArrowSkill;
import ks.util.L1CharPosUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class L1AttackActionNpc implements L1AttackAction {
    private final Logger logger = LogManager.getLogger();
    private final L1NpcInstance attacker;
    private final L1Character target;

    public L1AttackActionNpc(L1NpcInstance attacker, L1Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public void action(L1AttackParam attackParam) {
        try {
            boolean isHit = attackParam.isHitUp();

            attacker.setHeading(L1CharPosUtils.targetDirection(attacker, target.getX(), target.getY()));

            try {
                int npcObjectId = attacker.getId();
                int bowActId;
                int actionActId;

                attacker.setHeading(L1CharPosUtils.targetDirection(attacker, target.getX(), target.getY()));

                boolean isLongRange = (attacker.getLocation().getTileLineDistance(new Point(target.getX(), target.getY())) > 1);
                bowActId = attacker.getTemplate().getBowActId();

                if (attackParam.getActId() > 0) {
                    actionActId = attackParam.getActId();
                } else {
                    actionActId = L1ActionCodes.ACTION_Attack;
                }

                if (isLongRange && bowActId > 0) {
                    Broadcaster.broadcastPacket(attacker, new S_UseArrowSkill(attacker, target.getId(), bowActId, target.getX(), target.getY(), isHit, actionActId));
                    if (!isHit) {
                        Broadcaster.broadcastPacket(attacker, new S_AttackMissPacket(attacker, target.getId(), actionActId));
                        L1AttackUtils.missAttack(attacker, target);
                    }
                } else {
                    if (isHit) {
                        Broadcaster.broadcastPacket(attacker, new S_AttackPacketForNpc(target, npcObjectId, actionActId));
                    } else {
                        Broadcaster.broadcastPacket(attacker, new S_AttackMissPacket(attacker, target.getId(), actionActId));
                        L1AttackUtils.missAttack(attacker, target);
                    }
                }
            } catch (Exception e) {
                logger.error("오류", e);
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
