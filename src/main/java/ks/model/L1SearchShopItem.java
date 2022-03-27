package ks.model;

import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.system.userShop.L1UserShopCommand;
import ks.system.userShop.table.L1UserShop;
import ks.util.L1CommonUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class L1SearchShopItem {
    private final L1PcInstance pc;
    private final List<L1UserShop> buyItems = new ArrayList<>();
    private String searchText;
    private int enchantLvl;
    private int bless;

    public L1SearchShopItem(L1PcInstance pc) {
        this.pc = pc;
    }

    public void init(String searchText, int enchantLvl, int bless) {
        this.searchText = searchText;
        this.enchantLvl = enchantLvl;
        this.bless = bless;
    }

    public void showHtml() {
        try {
            buyItems.clear();

            List<L1UserShop> sellItems = L1UserShopCommand.findItem(bless, searchText, enchantLvl, "sell");
            List<L1UserShop> sellPagingItems = L1CommonUtils.getPagingList(pc, sellItems);

            for (int i = sellPagingItems.size(); i < pc.getPagination().getRecordCountPerPage(); i++) {
                sellPagingItems.add(null);
            }

            List<String> params = new ArrayList<>();
            params.add(searchText);
            params.add(pc.getPagination().getPagingString());

            for (L1UserShop vo : sellPagingItems) {
                if (vo != null) {
                    params.add("-> +" + vo.getEnchantLvl() + " " + vo.getItemName());
                    params.add(String.format("금액 : %s원", NumberFormat.getInstance().format(vo.getPrice()) + ""));
                } else {
                    params.add("&nbsp;");
                    params.add("&nbsp;");
                }
            }

            buyItems.addAll(L1UserShopCommand.findItem(bless, searchText, enchantLvl, "buy"));

            for (L1UserShop vo : buyItems) {
                params.add("-> " + vo.getItemViewName());
                params.add(String.format("금액 : %s원", NumberFormat.getInstance().format(vo.getPrice()) + ""));
            }

            String html;

            if (sellItems.size() >= 5) {
                html = "cc_ntrade";
            } else {
                html = "cc_ntrade" + sellItems.size();
            }

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), html, params.toArray()));


        } catch (Exception e) {
            pc.sendPackets("일반검색 .일 아이템명 인첸트");
            pc.sendPackets("축복검색 .축 아이템명 인첸트");
            pc.sendPackets("저주검색 .저 아이템명 인첸트");
        }
    }

    public List<L1UserShop> getBuyItems() {
        return buyItems;
    }
}
