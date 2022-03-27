package ks.model;

import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_TradeAddItem;
import ks.packets.serverpackets.S_TradeStatus;
import ks.util.log.L1LogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class L1Trade {
    private static final Logger logger = LogManager.getLogger(L1Trade.class);

    public static void addItem(L1PcInstance player, int itemid, int itemcount) {
        L1Object tradingPartner = L1World.getInstance().findObject(player.getTradeID());
        L1ItemInstance item = player.getInventory().getItem(itemid);

        if (item != null && tradingPartner != null) {
            if (tradingPartner instanceof L1PcInstance) {
                L1PcInstance tradepc = (L1PcInstance) tradingPartner;

                if (!item.isEquipped()) {
                    if (item.getCount() < itemcount || 0 >= itemcount) {
                        player.sendPackets(new S_TradeStatus(1));
                        tradepc.sendPackets(new S_TradeStatus(1));
                        player.setTradeOk(false);
                        tradepc.setTradeOk(false);
                        player.setTradeID(0);
                        tradepc.setTradeID(0);
                        return;
                    }

                    player.getInventory().tradeItem(item, itemcount, player.getTradeWindowInventory());
                    player.sendPackets(new S_TradeAddItem(item, itemcount, 0));
                    tradepc.sendPackets(new S_TradeAddItem(item, itemcount, 1));
                }
            }
        }
    }

    public static void trade(L1PcInstance pc) {
        try {
            L1Object tradingPartner = L1World.getInstance().findObject(pc.getTradeID());

            if (tradingPartner != null) {
                if (tradingPartner instanceof L1PcInstance) {
                    L1PcInstance tradepc = (L1PcInstance) tradingPartner;

                    List<L1ItemInstance> playerTradelist = pc.getTradeWindowInventory().getItems();
                    List<L1ItemInstance> tradingPartnerTradelist = tradepc.getTradeWindowInventory().getItems();

                    tradeItems(pc, tradepc, playerTradelist);
                    tradeItems(tradepc, pc, tradingPartnerTradelist);

                    pc.sendPackets(new S_TradeStatus(0));
                    tradepc.sendPackets(new S_TradeStatus(0));
                    pc.setTradeOk(false);
                    tradepc.setTradeOk(false);
                    pc.setTradeID(0);
                    tradepc.setTradeID(0);
                    pc.getLight().turnOnOffLight();
                    tradepc.getLight().turnOnOffLight();
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void tradeItems(L1PcInstance pc, L1PcInstance target, List<L1ItemInstance> playerTradeList) {
        for (L1ItemInstance item : new ArrayList<>(playerTradeList)) {
            pc.getTradeWindowInventory().tradeItem(item, item.getCount(), target.getInventory());

            L1LogUtils.tradeLog("[교환] {} -> {} / {}", pc.getName(), target.getName(), L1LogUtils.logItemName(item));
        }
    }

    public static void cancel(L1PcInstance pc) {
        L1Object tradingPartner = L1World.getInstance().findObject(pc.getTradeID());

        if (tradingPartner != null) {
            if (tradingPartner instanceof L1PcInstance) {
                L1PcInstance tradePc = (L1PcInstance) tradingPartner;

                List<L1ItemInstance> playerTradeList = pc.getTradeWindowInventory().getItems();
                List<L1ItemInstance> tradingPartnerTradeList = tradePc.getTradeWindowInventory().getItems();

                tradeItems(pc, pc, playerTradeList);
                tradeItems(tradePc, tradePc, tradingPartnerTradeList);

                pc.sendPackets(new S_TradeStatus(1));
                tradePc.sendPackets(new S_TradeStatus(1));

                pc.setTradeOk(false);
                tradePc.setTradeOk(false);

                pc.setTradeID(0);
                tradePc.setTradeID(0);
            }
        }
    }

    public static boolean checkTradeAble(L1PcInstance player, L1PcInstance target) {
        if (player.getAccountName().equalsIgnoreCase(target.getAccountName())) {
            player.disconnect();
            target.disconnect();
            return false;
        }

        if (player.getTradeID() != 0 || target.getTradeID() != 0) {
            String msg = "교환 : 자신 또는 대상은 거래 중인 상태입니다.";
            target.sendPackets(msg);
            player.sendPackets(msg);

            return false;
        }

        if (player.isAutoKingBuff() || target.isAutoKingBuff()) {
            String msg = "교환 : 자신 또는 대상이 자동군업 상태입니다";
            player.sendPackets(msg);
            target.sendPackets(msg);

            return false;
        }

        int maxCount = 180 - 16;

        int pcSize = player.getInventory().getSize();
        int targetSize = target.getInventory().getSize();

        if (pcSize > maxCount || targetSize > maxCount) {
            String msg1 = "교환 : 자신 또는 상대방이 아이템을 너무 많이 소지중입니다";
            String msg2 = "교환 : 교환 가능 최대 인벤토리 수량 : " + maxCount;

            player.sendPackets(msg1);
            player.sendPackets(msg2);

            target.sendPackets(msg1);
            target.sendPackets(msg2);

            return false;
        }

        return true;
    }
}