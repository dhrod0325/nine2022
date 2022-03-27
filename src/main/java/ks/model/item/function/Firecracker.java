package ks.model.item.function;

import ks.constants.L1ItemId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_SkillSound;

public class Firecracker extends L1ItemInstance {

    public Firecracker(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = useItem.getItemId();
            if (itemId >= 40136 && itemId <= 40161 || itemId == L1ItemId.GEMSTONE_POWDER) { // 불꽃
                int soundid = 3198;

                switch (itemId) {
                    case 40136:
                        soundid = 2046;
                        break;
                    case 40137:
                        soundid = 2047;
                        break;
                    case 40138:
                        soundid = 2048;
                        break;
                    case 40139:
                        soundid = 2040;
                        break;
                    case 40140:
                        soundid = 2051;
                        break;
                    case 40141:
                        soundid = 2028;
                        break;
                    case 40142:
                        soundid = 2036;
                        break;
                    case 40143:
                        soundid = 2041;
                        break;
                    case 40144:
                        soundid = 2053;
                        break;
                    case 40145:
                        soundid = 2029;
                        break;
                    case 40146:
                        soundid = 2039;
                        break;
                    case 40147:
                        soundid = 2045;
                        break;
                    case 40148:
                        soundid = 2043;
                        break;
                    case 40149:
                        soundid = 2034;
                        break;
                    case 40150:
                        soundid = 2055;
                        break;
                    case 40151:
                        soundid = 2032;
                        break;
                    case 40152:
                        soundid = 2031;
                        break;
                    case 40153:
                        soundid = 2038;
                        break;
                    case 40155:
                        soundid = 2044;
                        break;
                    case 40156:
                        soundid = 2042;
                        break;
                    case 40157:
                        soundid = 2035;
                        break;
                    case 40158:
                        soundid = 2049;
                        break;
                    case 40159:
                        soundid = 2033;
                        break;
                    case 40160:
                        soundid = 2030;
                        break;
                    case 40161:
                        soundid = 2037;
                        break;
                    default:
                        break;
                }

                pc.sendPackets(new S_SkillSound(pc.getId(), soundid));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), soundid));
                pc.getInventory().removeItem(useItem, 1);
            } else if (itemId >= 41357 && itemId <= 41382) { // 알파벳 불꽃
                int soundid = itemId - 34946;
                pc.sendPackets(new S_SkillSound(pc.getId(), soundid));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), soundid));
                pc.getInventory().removeItem(useItem, 1);
            }
        }
    }
}
