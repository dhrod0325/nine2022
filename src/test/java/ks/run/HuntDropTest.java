package ks.run;

import ks.app.config.prop.CodeConfig;
import ks.base.AbstractTest;
import ks.core.datatables.MapsTable;
import ks.core.datatables.ResolventTable;
import ks.core.datatables.exp.ExpTable;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Item;
import ks.system.huntCheck.HuntCheckDao;
import ks.system.huntCheck.vo.DropCheck;
import ks.system.huntCheck.vo.HuntCheck;
import ks.system.huntCheck.vo.HuntReport;
import ks.system.huntCheck.vo.HuntResult;
import ks.system.robot.L1RobotTable;
import ks.util.common.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HuntDropTest extends AbstractTest {
    public void 필드집계() {
        MapsTable.getInstance().load();
        ItemTable.getInstance().load();
        ResolventTable.getInstance().load();

        L1RobotTable robotTable = new L1RobotTable();
        HuntCheckDao huntCheckDao = new HuntCheckDao();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startTime = robotTable.selectLastHuntData().getStartTime();

        StringBuilder sb = new StringBuilder();
        sb.append("   SELECT   ");
        sb.append("   	charName,   ");
        sb.append("   	regDate,   ");
        sb.append("   	mapId,   ");
        sb.append("   	itemId,   ");
        sb.append("   	getResolvePrice ( itemid ) * count  resolvePriceSum ,   ");
        sb.append("   	exp ,   ");
        sb.append("   	id ,   ");
        sb.append("   	count   ");
        sb.append("   FROM   ");
        sb.append("   	huntcheck_item A   ");
        sb.append("   	LEFT JOIN huntcheck B ON A.huntId = B.id    ");
        sb.append("   WHERE   ");
        sb.append("   	1 = 1   ");
        sb.append("   	and date_format(regDate,'%Y-%m-%d %H:%i') >= ?  ");

        List<HuntResult> huntResults = SqlUtils.query(sb.toString(), new BeanPropertyRowMapper<>(HuntResult.class), format.format(startTime));

        Map<Integer, List<HuntResult>> dataMap = new HashMap<>();

        for (HuntResult result : huntResults) {
            List<HuntResult> map = dataMap.computeIfAbsent(result.getMapId(), k -> new ArrayList<>());
            map.add(result);
        }

        HuntCheck check = new HuntCheck();
        check.setSearchStartDate(format.format(startTime));

        List<HuntReport> dataResult = new ArrayList<>();

        List<HuntCheck> huntCheckList = huntCheckDao.selectHuntCheckList(check);

        int pcLevel = 60;

        Collection<L1Item> allItems = ItemTable.getInstance().getAllItems();

        for (Integer mapId : dataMap.keySet()) {
            Map<Integer, DropCheck> dropCheckMap = new LinkedHashMap<>();

            for (L1Item o : allItems) {
                dropCheckMap.put(o.getItemId(), new DropCheck(o.getItemId(), o.getName()));
            }

            List<HuntResult> data = dataMap.get(mapId);

            List<HuntResult> adenaList = new ArrayList<>();
            List<HuntResult> resolveList = new ArrayList<>();

            int resolventSum = 0;
            int adenaSum = 0;

            String charName = "";

            for (HuntResult huntResult : data) {
                if (huntResult.getItemId() == 40308) {
                    adenaSum += huntResult.getResolvePriceSum();
                    adenaList.add(huntResult);
                } else {
                    resolventSum += huntResult.getResolvePriceSum() * CodeConfig.RATE_CRISTAL;
                    resolveList.add(huntResult);
                }

                DropCheck dropCheck = dropCheckMap.get(huntResult.getItemId());

                if (dropCheck != null) {
                    dropCheck.sum += huntResult.getCount();
                }

                charName = huntResult.getCharName();
            }

            HuntReport report = new HuntReport(mapId, adenaSum, resolventSum);
            report.adenaList.addAll(adenaList);
            report.resolveList.addAll(resolveList);
            report.charName = charName;

            StringBuilder etc = new StringBuilder();

            for (Integer key : dropCheckMap.keySet()) {
                DropCheck dropCheck = dropCheckMap.get(key);
                L1Item item = ItemTable.getInstance().getTemplate(dropCheck.itemId);

                if (dropCheck.sum == 0) {
                    continue;
                }

                if (item == null) {
                    System.out.println("check null : " + dropCheck.itemId);
                    continue;
                }

                int bless = item.getBless();

                String type = "";

                if (bless == 0) {
                    type = "(축)";
                } else if (bless == 2) {
                    type = "(저)";
                }

                etc.append(type).append(item.getName()).append(":").append(dropCheck.sum).append(",");
            }

            report.etc = etc.toString();

            double expSum = 0;
            double expPerSum = 0;
            double ainBonus = 1.77;

            for (HuntCheck huntCheck : huntCheckList) {
                if (mapId == huntCheck.getMapId()) {
                    double per = ExpTable.getInstance().getPenaltyRate(pcLevel);

                    double exp = 1.0 * CodeConfig.RATE_XP * per * huntCheck.getExp() * ainBonus;
                    expSum += exp;
                    expPerSum += 0;
                }
            }

            report.expSum = expSum;
            report.expPerSum = expPerSum;

            dataResult.add(report);
        }

        dataResult.sort((o1, o2) -> o2.getTotal() - o1.getTotal());
        int k = (int) TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - startTime.getTime());
        System.out.println("사냥 시작 시간 : " + format.format(startTime) + "," + TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - startTime.getTime()) + "분 사냥 결과");
        NumberFormat numberFormat = NumberFormat.getInstance();
        int z = (k / 60);
        if (z == 0) {
            z = 1;
        }

        for (HuntReport report : dataResult) {
            double per1 = ExpTable.getInstance().getExpPercentage(pcLevel, (int) (ExpTable.getInstance().getExpByLevel(pcLevel) + report.expSum));

            String msg = String.format("[용해] : %s [아덴] : %s [합계] %s [경험치] %s [기타] %s - [시간평균] %s [%s] [%d] %s",
                    StringUtils.leftPad(numberFormat.format(report.resolventSum) + "", 15, " "),
                    StringUtils.leftPad(numberFormat.format(report.adenaSum) + "", 15, " "),
                    StringUtils.leftPad(numberFormat.format(report.getTotal()) + "", 15, " "),
                    StringUtils.leftPad(numberFormat.format((int) report.expSum) + "/" + per1, 15, " "),
                    StringUtils.rightPad(report.etc, 30, " "),
                    numberFormat.format(report.getTotal() == 0 ? 1 : report.getTotal() / z),
                    MapsTable.getInstance().getMapName(report.mapId),
                    report.mapId,
                    report.charName);

            System.out.println(msg);
        }
    }

}
