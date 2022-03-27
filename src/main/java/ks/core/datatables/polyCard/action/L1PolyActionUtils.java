package ks.core.datatables.polyCard.action;

import ks.constants.L1DataMapKey;
import ks.constants.L1SkillId;
import ks.core.datatables.PolyTable;
import ks.core.datatables.favPoly.FavPoly;
import ks.core.datatables.polyCard.L1PolyCard;
import ks.core.datatables.polyCard.L1PolyCardTable;
import ks.model.L1PolyMorph;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_CloseList;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_ShowCCHtml;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class L1PolyActionUtils {
    private static final Logger logger = LogManager.getLogger();

    public static boolean usePolyScroll(L1PcInstance pc, int itemId, String polyName) {

        if (StringUtils.isEmpty(polyName)) {

        }

        int time = 0;

        if (itemId == 40088 || itemId == 40096 || itemId == 60001213) {
            time = 1800;
        } else if (itemId == 140088) {
            time = 2100;
        }

        L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyName);

        L1ItemInstance item = pc.getInventory().findItemId(itemId);
        boolean isUseItem = false;

        if (polyName.equals("doppel")) {
            if (!pc.getInventory().checkItem(60001386)) {
                return true;
            }
        }

        if (polyName.equals("none") || StringUtils.isEmpty(polyName)) {
            if (pc.getGfxId().getTempCharGfx() != 6034 && pc.getGfxId().getTempCharGfx() != 6035) {
                pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.SHAPE_CHANGE);
            }

            isUseItem = true;
        } else if (poly.getMinLevel() == 100) {
            isUseItem = true;
        } else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
            L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC);
            isUseItem = true;
        }

        if (!polyName.equals("none") && poly != null) {
            if (pc.getFavPolyList().size() > 0) {
                int last = pc.getFavPolyList().get(pc.getFavPolyList().size() - 1).getPolyId();

                if (last != poly.getPolyId()) {
                    FavPoly favPoly = new FavPoly();
                    favPoly.setPolyId(poly.getPolyId());
                    favPoly.setCharId(pc.getId());

                    pc.addFavPoly(favPoly);
                }
            } else {
                FavPoly favPoly = new FavPoly();
                favPoly.setPolyId(poly.getPolyId());
                favPoly.setCharId(pc.getId());

                pc.addFavPoly(favPoly);
            }
        }

        if (poly != null) {
            logger.debug(ReflectionToStringBuilder.toString(poly));
        }

        if (isUseItem) {
            pc.getInventory().removeItem(item, 1);
            pc.sendPackets(new S_CloseList(pc.getId()));
        } else {
            pc.sendPackets(new S_ServerMessage(181));
        }

        return isUseItem;
    }

    public static boolean action(L1PcInstance pc, String action) {
        if (action.startsWith("cc_monreg")) {
            String idx = action.replace("cc_monreg", "");
            pc.getDataMap().put(L1DataMapKey.MON_FAV_IDX, idx);
            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_monfav"));

            return true;
        }

        //빠른변신 일반,고급,희귀 선택
        if (action.startsWith("cc_monfreg")) {
            int step = Integer.parseInt(action.replace("cc_monfreg", ""));

            List<L1PolyCard> polyList = L1PolyCardTable.getInstance().getListByGrade(step + 1);

            List<Integer> params = new ArrayList<>();

            for (L1PolyCard card : polyList) {
                if (card.getImg() == 0) {
                    continue;
                }

                if (pc.getMasterPolyIdList().contains(card.getPolyId())) {
                    params.add(card.getImg());
                } else {
                    params.add(card.getImgOff());
                }
            }

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_monfav" + step, params));

            return true;
        }

        if (action.startsWith("cc_monchange")) {
            int idx = Integer.parseInt(action.replace("cc_monchange", ""));

            if (pc.getFavPolyImgList().size() > idx) {
                List<FavPoly> changeList = new ArrayList<>(pc.getFavPolyList());

                if (changeList.size() == 1) {
                    idx = 0;
                } else if (changeList.size() == 2 && idx == 2) {
                    idx = 1;
                }

                Collections.reverse(changeList);

                FavPoly fav = changeList.get(idx);

                L1PolyMorph poly = PolyTable.getInstance().getTemplate(fav.getPolyId());

                if (pc.getInventory().checkItem(40088)) {
                    usePolyScroll(pc, 40088, poly.getName());
                } else if (pc.getInventory().checkItem(40096)) {
                    usePolyScroll(pc, 40096, poly.getName());
                } else if (pc.getInventory().checkItem(140088)) {
                    usePolyScroll(pc, 140088, poly.getName());
                } else if (pc.getInventory().checkItem(60001213)) {
                    usePolyScroll(pc, 60001213, poly.getName());
                }
            }

            return true;
        }

        if (action.equals("cc_gomonlist")) {
            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_monlist", pc.getFavPolyImgList()));
            return true;
        }

        if (action.startsWith("cc_monlist")) {
            int step = Integer.parseInt(action.replace("cc_monlist", ""));

            List<Integer> params = new ArrayList<>();

            pc.sendPackets(new S_ShowCCHtml(pc.getId(), "cc_monlist" + step, params));

            return true;
        }

        if (action.startsWith("cc_polyreg")) {
            return true;
        }

        if (action.equals("cc_polyring1")) {
            if (pc.getInventory().checkItem(60001386)) {
                String poly = "doppel";

                if (pc.getInventory().checkItem(40088)) {
                    usePolyScroll(pc, 40088, poly);
                } else if (pc.getInventory().checkItem(40096)) {
                    usePolyScroll(pc, 40096, poly);
                } else if (pc.getInventory().checkItem(140088)) {
                    usePolyScroll(pc, 140088, poly);
                } else if (pc.getInventory().checkItem(60001213)) {
                    usePolyScroll(pc, 60001213, poly);
                }
            } else {
                pc.sendPackets("잘못된 호출입니다");
            }

            return true;
        } else if (action.startsWith("cc_poly")) {
            String tt = action.replace("cc_poly_", "");
            String[] ss = tt.split("_");

            int step = Integer.parseInt(ss[0]);
            int idx = Integer.parseInt(ss[1]);

            List<L1PolyCard> polyList = L1PolyCardTable.getInstance().getListByGrade(step + 1);

            if (!polyList.isEmpty()) {
                L1PolyCard poly = polyList.get(idx);

                if (pc.isMasterPoly(poly.getPolyId())) {
                    L1PolyMorph.doPoly(pc, poly.getPolyId(), 1800, L1PolyMorph.MORPH_BY_GM);
                    pc.sendPackets(new S_CloseList(pc.getId()));
                } else {
                    pc.sendPackets("습득하지 않은 변신입니다");
                }
            }

            return true;
        }

        return false;
    }
}
