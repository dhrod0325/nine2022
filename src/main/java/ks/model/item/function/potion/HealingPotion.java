package ks.model.item.function.potion;

import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1ItemDelay;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1SkillId.DECAY_POTION;
import static ks.constants.L1SkillId.POLLUTE_WATER;

public class HealingPotion extends L1ItemInstance {
    public HealingPotion(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(getId());

            useItem(pc, useItem);
        }
    }

    public void useItem(L1PcInstance pc, L1ItemInstance item) {
        int delayId = item.getItem().getDelayId();

        if (delayId != 0) {
            if (pc.hasItemDelay(delayId)) {
                return;
            }
        }

        int itemId = item.getItemId();
        int effect = 0;

        int min = Integer.parseInt(item.getItem().getEtc1());
        int max = Integer.parseInt(item.getItem().getEtc2());

        int heal = RandomUtils.nextInt(min, max);

        switch (itemId) {
            case 40010: //체력 회복제
            case 40019: //농축 체력 회복제
            case 40022: //고대의 체력 회복제
            case 40043: //토끼의 간
            case 50006:
            case 40026: //바나나 주스
            case 40027: //오렌지 주스
            case 40028: //사과 주스
                effect = 189;
                break;
            case 40011://고급 체력 회복제
            case 40020://고급 농축 체력 회복제
            case 40023://고대의 고급 체력 회복제
            case 40029: //상아탑의 체력 회복제
                if (itemId == 40029) {
                    effect = 189;
                } else {
                    effect = 194;
                }
                break;
            case 40012://강력 체력 회복제
            case 40021://농축 강력 체력 회복제
            case 40024://고대의 강력 체력 회복제
            case 40506: //엔트의 열매
            case 60001302:
                effect = 197;
                break;
        }

        if (pc.getPoison() != null) {
            if (pc.getPoison().getEffectId() == 2) {
                return;
            }
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.CURSE_SKILLS)
                || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.STUN_SKILLS)
                || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.SLEEP_SKILLS)
                || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)
        ) {
            return;
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
            pc.sendPackets(new S_ServerMessage(698));
            return;
        }

        pc.cancelAbsoluteBarrier();

        pc.sendPackets(new S_SkillSound(pc.getId(), effect));

        Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), effect));

        if (pc.getMent().isPotion()) {
            pc.sendPackets(new S_ServerMessage(77));
        }

        if (pc.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) {
            heal /= 4;
        }

        if (pc.getAddPotionPer() > 0) {
            double per = (pc.getAddPotionPer() / 100) + 1;
            heal *= per;
        }

        pc.setCurrentHp(pc.getCurrentHp() + heal);

        pc.getInventory().removeItem(item, 1);
        L1ItemDelay.onItemUse(pc, item);
    }
}
