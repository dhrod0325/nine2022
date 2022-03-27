package ks.model.attack.utils;

import ks.constants.L1ItemId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillSound;
import ks.util.common.random.RandomUtils;

import static ks.constants.L1ItemId.*;

public class L1ArmorUtils {
    public static int addDamageByEaring(L1PcInstance pc) {
        int itemId = 0;

        if (pc.getInventory().checkEquipped(룸티스검은빛귀걸이)) {
            itemId = 룸티스검은빛귀걸이;
        } else if (pc.getInventory().checkEquipped(L1ItemId.축룸티스검은빛귀걸이)) {
            itemId = L1ItemId.축룸티스검은빛귀걸이;
        }

        if (itemId == 0) {
            return 1;
        }

        L1ItemInstance item = pc.getInventory().findEquippedItemId(itemId);

        EarRingInfo ringInfo = getBlackEarRingInfo(item.getBless(), item.getEnchantLevel());

        if (RandomUtils.isWinning(100, ringInfo.getPer())) {
            pc.sendPackets(new S_SkillSound(pc.getId(), 6319));
            Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 6319));
            return ringInfo.getDamage();
        }

        return 0;
    }

    public static EarRingInfo getBlackEarRingInfo(int bless, int enchantLevel) {
        int per = 0;
        int damage = 0;

        if (bless == 0) {
            switch (enchantLevel) {
                case 4:
                    per = 2;
                    damage = 20;
                    break;
                case 5:
                    per = 4;
                    damage = 20;
                    break;
                case 6:
                    per = 10;
                    damage = 30;
                    break;
                case 7:
                    per = 15;
                    damage = 40;
                    break;
                case 8:
                    per = 20;
                    damage = 50;
                    break;
                case 9:
                    per = 30;
                    damage = 60;
                    break;
            }
        } else {
            switch (enchantLevel) {
                case 5:
                    per = 2;
                    damage = 20;
                    break;
                case 6:
                    per = 4;
                    damage = 20;
                    break;
                case 7:
                    per = 10;
                    damage = 20;
                    break;
                case 8:
                    per = 15;
                    damage = 30;
                    break;
                case 9:
                    per = 20;
                    damage = 40;
                    break;
            }
        }

        return new EarRingInfo(per, damage);
    }

    public static EarRingInfo getRedEarRingInfo(int bless, int enchantLevel) {
        int per = 0;
        int damage = 0;

        if (bless == 0) {
            switch (enchantLevel) {
                case 4:
                    per = 2;
                    damage = 20;
                    break;
                case 5:
                    per = 4;
                    damage = 20;
                    break;
                case 6:
                    per = 10;
                    damage = 30;
                    break;
                case 7:
                    per = 15;
                    damage = 40;
                    break;
                case 8:
                    per = 20;
                    damage = 50;
                    break;
                case 9:
                    per = 30;
                    damage = 60;
                    break;
            }
        } else {
            switch (enchantLevel) {
                case 5:
                    per = 2;
                    damage = 20;
                    break;
                case 6:
                    per = 4;
                    damage = 20;
                    break;
                case 7:
                    per = 10;
                    damage = 30;
                    break;
                case 8:
                    per = 15;
                    damage = 40;
                    break;
                case 9:
                    per = 20;
                    damage = 50;
                    break;
            }
        }

        return new EarRingInfo(per, damage);
    }

    public static EarRingInfo getEarRingInfo(L1ItemInstance item) {
        if (item.getItemId() == 룸티스붉은빛귀걸이 || item.getItemId() == 축룸티스붉은빛귀걸이) {
            return getRedEarRingInfo(item.getBless(), item.getEnchantLevel());
        } else if (item.getItemId() == 룸티스검은빛귀걸이 || item.getItemId() == 축룸티스검은빛귀걸이) {
            return getBlackEarRingInfo(item.getBless(), item.getEnchantLevel());
        }

        return null;
    }

    public static int damageReduceByRedEaring(L1Character target) {
        if (target instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) target;

            int itemId = 0;

            if (pc.getInventory().checkEquipped(룸티스붉은빛귀걸이)) {
                itemId = 룸티스붉은빛귀걸이;
            } else if (pc.getInventory().checkEquipped(축룸티스붉은빛귀걸이)) {
                itemId = 축룸티스붉은빛귀걸이;
            }

            if (itemId == 0) {
                return 1;
            }

            L1ItemInstance item = pc.getInventory().findEquippedItemId(itemId);

            EarRingInfo earingInfo = getRedEarRingInfo(item.getBless(), item.getEnchantLevel());

            if (RandomUtils.isWinning(100, earingInfo.getPer())) {
                pc.sendPackets(new S_SkillSound(pc.getId(), 12118));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 12118));

                return earingInfo.getDamage();
            }
        }

        return 0;
    }

    public static class EarRingInfo {
        private int per;
        private int damage;

        public EarRingInfo(int per, int damage) {
            this.per = per;
            this.damage = damage;
        }

        public int getPer() {
            return per;
        }

        public void setPer(int per) {
            this.per = per;
        }

        public int getDamage() {
            return damage;
        }

        public void setDamage(int damage) {
            this.damage = damage;
        }
    }
}
