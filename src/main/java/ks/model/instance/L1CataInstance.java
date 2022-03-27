package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ActionCodes;
import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.model.*;
import ks.model.attack.utils.L1AttackUtils;
import ks.model.pc.L1CheckTimer;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_ServerMessage;
import ks.scheduler.WarTimeScheduler;
import ks.util.L1CommonUtils;
import ks.util.common.random.RandomUtils;

import java.text.NumberFormat;

public class L1CataInstance extends L1NpcInstance {
    private final L1CheckTimer timer = new L1CheckTimer();

    public L1CataInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        int castleId = L1CastleLocation.getCastleIdByArea(pc);

        boolean isNowWar = WarTimeScheduler.getInstance().isNowWar(castleId);

        if (!isNowWar) {
            pc.sendPackets(new S_ServerMessage(3683));
            return;
        }

        if (!timer.isTimeOver("투석기사용")) {
            pc.sendPackets(new S_ServerMessage(3680));
            return;
        }

        int npcId = getNpcId();
        int locx = 0;
        int locy = 0;
        int gfxid = 0;

        switch (npcId) {
            //기란 5시
            case 460000130:
                locx = 33631;
                locy = 32678;
                gfxid = 12197;
                break;
            //기란 7시
            case 460000131:
                locx = 33631;
                locy = 32678;
                gfxid = 12193;
                break;
            //켄트 7시
            case 460000132:
                locx = 33170;
                locy = 32774;
                gfxid = 12193; // 좌측
                break;
            //켄트 5시
            case 460000133:
                locx = 33170;
                locy = 32774;
                gfxid = 12197; // 좌측
                break;
            //윈성 7시
            case 460000134:
                locx = 32675;
                locy = 33408;
                gfxid = 12193; // 좌측
                break;
            //윈성 5시
            case 460000135:
                locx = 32675;
                locy = 33408;
                gfxid = 12197; // 좌측
                break;
            //드워프성
            case 460000136:
                locx = 32827;
                locy = 32821;
                gfxid = 12193; // 좌측
                break;
        }

        int price = CodeConfig.CASTLE_WAR_BOMB_ADENA;

        if (pc.getInventory().consumeItem(L1ItemId.ADENA, price)) {
            Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Attack));

            getShellDmg(pc, locx, locy, gfxid);

            timer.setWaitTime("투석기사용", 1000 * 10);

            pc.sendPackets("투석기 이용금액 : " + NumberFormat.getInstance().format(price) + " 아데나가 사용되었습니다");
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$16785"));
        }
    }

    private void getShellDmg(L1PcInstance pc, int locx, int locy, int gfxid) {
        L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(81154, 1000, locx, locy, pc.getMapId());

        L1CommonUtils.locationEffect(effect, locx, locy, gfxid);

        for (L1Object object : L1World.getInstance().getVisibleObjects(effect, 3)) {
            if (object == null) {
                continue;
            }

            if (!(object instanceof L1Character)) {
                continue;
            }

            if (object.getId() == effect.getId()) {
                continue;
            }

            int damage = RandomUtils.nextInt(CodeConfig.CASTLE_WAR_BOMB_DMG_MIN, CodeConfig.CASTLE_WAR_BOMB_DMG_MAX);

            if (object instanceof L1PcInstance) {
                L1PcInstance target = (L1PcInstance) object;

                if (L1AttackUtils.isNotAttackAbleByTargetStatus(target)) {
                    continue;
                }

                if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COUNTER_MAGIC)) {
                    target.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.COUNTER_MAGIC);
                    continue;
                }

                if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IMMUNE_TO_HARM)) {
                    damage /= 2;
                }

                target.sendPackets(new S_DoActionGFX(target.getId(), L1ActionCodes.ACTION_Damage));
                Broadcaster.broadcastPacket(target, new S_DoActionGFX(target.getId(), L1ActionCodes.ACTION_Damage));
                target.receiveDamage(pc, damage);
            } else if (object instanceof L1SummonInstance || object instanceof L1PetInstance) {
                L1NpcInstance target = (L1NpcInstance) object;
                Broadcaster.broadcastPacket(target, new S_DoActionGFX(target.getId(), L1ActionCodes.ACTION_Damage));
                target.receiveDamage(pc, damage);
            }
        }
    }
}
