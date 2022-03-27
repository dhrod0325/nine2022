package ks.system.autoPotion;

import ks.constants.L1SkillId;
import ks.model.L1ItemDelay;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.system.event.TimePickupEvent;
import ks.util.L1CommonUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static ks.constants.L1SkillId.DECAY_POTION;

@Component
public class L1AutoPotionScheduler {
    @Scheduled(fixedDelay = 200)
    public void scheduled() {
        L1World.getInstance().getAllPlayers().forEach(pc -> {
            if (pc.isDead())
                return;

            if (pc.getMapId() != TimePickupEvent.MAP_ID) {
                pc.getAutoPotion().autoPotion();

                if (pc.isAutoDragonDiamond()) {
                    if (pc.getAinHasad() <= 10000) {
                        L1ItemInstance item = null;

                        if (pc.getInventory().findItemId(437010) != null) {
                            item = pc.getInventory().findItemId(437010);
                        }

                        if (item != null && item.getCount() > 0) {
                            if (L1ItemDelay.hasItemDelay(pc, item))
                                return;

                            item.clickItem(pc, null);

                            L1ItemDelay.onItemUse(pc, item);
                        }
                    }
                }

                if (pc.isAutoDragonPerl()) {
                    if (!pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
                        if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGON_PERL)) {
                            L1ItemInstance item = null;

                            if (pc.getInventory().findItemId(437011) != null) {
                                item = pc.getInventory().findItemId(437011);
                            }

                            if (item != null && item.getCount() > 0) {
                                if (L1ItemDelay.hasItemDelay(pc, item))
                                    return;

                                L1CommonUtils.useDragonPerl(pc);

                                pc.getInventory().consumeItem(437011, 1);// 해당아이템 삭제
                                pc.sendPackets(new S_ServerMessage(1065)); // 드진 멘트

                                L1ItemDelay.onItemUse(pc, item);
                            }
                        }
                    }
                }
            }
        });
    }
}
