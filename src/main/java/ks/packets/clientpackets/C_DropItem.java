package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.constants.L1SkillId;
import ks.core.datatables.bugCheck.BugCheckTable;
import ks.core.network.L1Client;
import ks.listener.FieldItemDeleteListener;
import ks.model.L1GroundInventory;
import ks.model.L1World;
import ks.model.instance.L1DollInstance;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SystemMessage;
import ks.util.L1CommonUtils;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class C_DropItem extends ClientBasePacket {
    private final static Logger logger = LogManager.getLogger(C_DropItem.class);

    public C_DropItem(byte[] decrypt, L1Client client) {
        super(decrypt);

        try {
            int x = readH();
            int y = readH();

            int objectId = readD();
            int count = readD();

            L1PcInstance pc = client.getActiveChar();

            if (pc == null) {
                return;
            }


            if (pc.getOnlineStatus() != 1) {
                pc.disconnect();
                return;
            }

            if (L1CommonUtils.isTwoLogin(pc))
                return;

            L1ItemInstance item = pc.getInventory().getItem(objectId);

            if (item != null) {
                if (!pc.isGm() && !item.getItem().isTradeAble()) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }

                if (!BugCheckTable.getInstance().isDropAble(pc, item.getId(), count)) {
                    String msg = "땅떨 버그 사용자 : " + pc.getName() + "," + item.getName() + "," + item.getEnchantLevel() + "," + count;
                    L1LogUtils.bugLog(msg);
                    pc.sendPackets("드랍 불가능한 아이템입니다. 운영자에게 제보 바랍니다");

                    return;
                }

                int itemType = item.getItem().getType2();

                if ((itemType == 1 && count != 1) || (itemType == 2 && count != 1)) {
                    pc.disconnect(pc.getName() + " error1");
                    return;
                }

                if (item.getItemDelay().isDelay()) {
                    return;
                }

                if (item.getCount() <= 0) {
                    pc.disconnect(pc.getName() + " error2");
                    return;
                }

                if (!item.isStackable() && count != 1) {
                    pc.disconnect(pc.getName() + " error3");
                    return;
                }

                if (item.getCount() < count || count <= 0 || count > 2000000000) {
                    pc.disconnect(pc.getName() + " error4");
                    return;
                }

                if (pc.getLocation().getTileLineDistance(pc.getLocation()) > 1) {
                    pc.sendPackets(new S_SystemMessage("1칸 이내에만 아이템을 버릴수 있습니다."));
                    return;
                }

                if (item.isEquipped()) {
                    pc.sendPackets(new S_ServerMessage(125));
                    return;
                }

                if (item.getBless() >= 128) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }

                if (pc.getMapId() == 350) {
                    pc.sendPackets(new S_SystemMessage("시장에서는 아이템을 버릴 수 없습니다."));
                    return;
                }

                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CHANGGO_TIMER)) {
                    pc.sendPackets(new S_SystemMessage("RESTART 후 30초간 바닥에 아이템을 버릴 수 없습니다."));
                    return;
                }

                if (pc.getLevel() < CodeConfig.DROP_LEVEL_LIMIT) {
                    pc.sendPackets(new S_SystemMessage("레벨 " + CodeConfig.DROP_LEVEL_LIMIT + "부터 아이템을 버릴 수 있습니다."));
                    return;
                }

                if (FieldItemDeleteListener.getInstance().getRemainingSecondTime() <= 30 && FieldItemDeleteListener.getInstance().getRemainingSecondTime() > 0) {
                    pc.sendPackets("[알림] : 월드청소 " + FieldItemDeleteListener.getInstance().getRemainingSecondTime() + "초 전");
                    pc.sendPackets("[알림] : 월드청소가 종료된 이후에 아이템을 드랍하세요");

                    return;
                }

                for (L1DollInstance dollObject : pc.getDollList().values()) {
                    if (item.getId() == dollObject.getItemObjId()) {
                        pc.sendPackets(new S_ServerMessage(1181));
                        return;
                    }
                }

                for (Object petObject : pc.getPetList().values()) {
                    if (petObject instanceof L1PetInstance) {
                        L1PetInstance pet = (L1PetInstance) petObject;
                        if (item.getId() == pet.getItemObjId()) {
                            pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                            return;
                        }
                    }
                }

                if (item.getItemId() == L1ItemId.DOLL_초보) {
                    pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
                    return;
                }

                if (!item.getItem().isTradeAble()) {
                    pc.sendPackets(item.getName() + "은 땅에 버리거나 교환할수 없습니다");
                    return;
                }

                L1CommonUtils.clearMagicItem(pc, item);

                if (item.isStackable()) {
                    item.getItemDelay().start(2000);
                }

                L1LogUtils.debugLog("[드랍] - {} : {}", pc.getName(), L1LogUtils.logItemName(item, count));

                L1GroundInventory inv = L1World.getInstance().getInventory(x, y, pc.getMapId());
                pc.getInventory().tradeItem(item, count, inv);
                pc.getLight().turnOnOffLight();
                pc.saveInventory();
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
