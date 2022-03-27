import basic.test.BaseTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameAboutParser extends BaseTest {

    public static void main(String[] args) throws IOException {
        String u = "http://lineage.gameabout.com/monster/?area_id=671";
        String mapId = "해적섬";

        Document document = Jsoup.connect(u).get();

        Elements elements = document.select(".greyscale table tr");

        int i = 0;

        for (Element element : elements) {
            if (i == 0) {
                i++;
                continue;
            }

            Elements tdList = element.select("td");

            String e1 = tdList.get(0).text();

            Element titleTd = tdList.get(1);

            Elements kk = titleTd.select("a");

            String exp = "";

            List<String> dropList = new ArrayList<>();

            if (!kk.isEmpty()) {
                String href = kk.get(0).attr("href").replace("javascript:mon_view('", "").replace("')", "");

                Document dd = Jsoup.connect(u + "&kf=1&mon_id=" + href).get();

                Elements infoTr = dd.select(".mon_info_tbl tr");
                Element tt = infoTr.get(1).select("td").get(3);
                exp = tt.text();

                Elements dropTd = infoTr.get(infoTr.size() - 1).select("img");

                for (Element drop : dropTd) {
                    String title = drop.attr("title");
                    title = title.substring(title.indexOf("[") + 1, title.indexOf("]"));
                    dropList.add(title);
                }
            }

            String e2 = titleTd.text();

            String e3 = tdList.get(2).text().replace(",", "");
            String e4 = tdList.get(3).text().replace(",", "");
            String e5 = tdList.get(4).text();
            String e6 = tdList.get(5).text().replace("%", "").replace("-", "");
            exp = exp.replace(",", "");

            jdbcTemplate.update("insert into 임시_게임어바웃_몬스터정보 (mapName,name,lvl,hp,ac,mr,exp) values (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=? ",
                    mapId, e2, e3, e4, e5, e6, exp,e2);
        }
    }
}
