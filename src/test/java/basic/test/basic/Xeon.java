//package ks.test;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.apache.commons.io.FileUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.io.File;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Xeon {
//    public static void main(String[] args) throws Exception {
//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName("org.mariadb.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://148.66.138.152/linclassic?useUnicode=true&characterEncoding=utf8");
//        ds.setUsername("fv4smhqnw47b");
//        ds.setPassword("Rudtjr1216!");
//        JdbcTemplate template = new JdbcTemplate(ds);
//
//        String[] tList = new String[]{"군주마법서", "기사기술서", "정령의수정", "마법서", "흑정령의수정"};
//
//        for (String ss : tList) {
//            String u = "https://xeon310.com/bbs/board.php?bo_table=item_t&sca=" + ss;
//
//            Document doc = Jsoup.connect(u).get();
//            Elements lists = doc.select(".item_list_tr");
//
//            for (Element e : lists) {
//                int size = e.childrenSize();
//
//                if (size == 3) {
//                    String name = e.child(1).text();
//                    name = name.replace(":", "-");
//
////                    String ac = e.child(2).text().replace("-", "");
////                    String safe = e.child(3).text().replace("+", "");
//
//                    String ac = "";
//                    String safe = "";
//
//                    Elements types = e.child(2).select("img");
//
//                    List<String> t = new ArrayList<>();
//
//                    for (Element type : types) {
//                        String src = type.attr("src");
//                        if (src.contains("ic1.png")) {
//                            t.add("군");
//                        } else if (src.contains("ic2.png")) {
//                            t.add("기");
//                        } else if (src.contains("ic3.png")) {
//                            t.add("법");
//                        } else if (src.contains("ic4.png")) {
//                            t.add("요");
//                        } else if (src.contains("ica.png")) {
//                            t.add("다");
//                        }
//                    }
//
////                    String tt = StringUtils.join(t, ",");
//                    String tt = "";
//
//                    String opt = e.nextElementSibling().select("td[colspan='6']").text();
//
//                    String sql = "INSERT INTO g5_write_item_w (ca_name,wr_subject,wr_content,wr_1,wr_3,wr_4) values (?,?,?,?,?,?)";
//                    template.update(sql, ss, name, opt, ac, safe, tt);
//
//                    String fileName = name.replace(" ", "");
//
//                    sql = "INSERT INTO g5_board_file (bo_table,wr_id,bf_source,bf_file) values ('item_w',last_insert_id(),?,?)";
//                    template.update(sql, fileName + ".gif", fileName + ".gif");
//
//                    String src = e.select("img").attr("src");
//
//                    InputStream is = new URL(src).openStream();
//                    FileUtils.copyInputStreamToFile(is, new File("E:/works/lineage/lin380_v3/xeon/new/" + ss + "/" + fileName + ".gif"));
//                }
//            }
//        }
//
//        template.update("UPDATE g5_write_item_w set wr_num=-wr_id,wr_parent=wr_id,mb_id='admin',wr_name='메티스',wr_email='admin@domain.co.kr'");
//    }
//}
