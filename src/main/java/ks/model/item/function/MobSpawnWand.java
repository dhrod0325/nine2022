package ks.model.item.function;

import ks.constants.L1ActionCodes;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_AttackPacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1SpawnUtils;
import ks.util.common.random.RandomUtils;

public class MobSpawnWand extends L1ItemInstance {
    public MobSpawnWand(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            int itemId = this.getItemId();
            if (pc.getMap().isCombatZone(pc.getLocation())) {
                pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
                Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
                pc.sendPackets(new S_ServerMessage(79));
                return;
            }

            if (pc.getMap().isUsePainWand()) {
                pc.sendPackets(new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));
                Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0, L1ActionCodes.ACTION_Wand));

                int chargeCount = useItem.getChargeCount();

                if (chargeCount <= 0 && itemId != 40412) {
                    pc.sendPackets(new S_ServerMessage(79));
                    return;
                }

                if (pc.getMap().isSafetyZone(pc.getLocation())) {
                    pc.sendPackets(new S_SystemMessage("마을안에서는 소나무 막대를 사용 할 수 없습니다."));
                    return;
                }

                int[] mobArray = {45005, 45009, 45019, 45041, 45043, 45060,
                        45065, 45068, 45157, 45082, 45024, 45107, 45161, 45126,
                        45136, 45184, 45215, 45223, 45021, 45008, 45016, 45025,
                        45033, 45046, 45064, 45040, 45147, 45140, 45092, 45155,
                        45192, 45122, 45130, 45138, 45213, 45173, 45171, 45143,
                        45149, 45098, 45127, 45144, 45079};

                int rnd = RandomUtils.nextInt(mobArray.length);

                L1SpawnUtils.spawn(pc, mobArray[rnd], 0, 300000);

                if (itemId == 40006 || itemId == 140006) {
                    useItem.setChargeCount(useItem.getChargeCount() - 1);
                    pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
                } else {
                    pc.getInventory().removeItem(useItem, 1);
                }
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        }
    }
}
