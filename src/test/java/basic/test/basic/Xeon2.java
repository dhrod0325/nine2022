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
//public class Xeon2 {
//    public static void main(String[] args) throws Exception {
//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName("org.mariadb.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://148.66.138.152/linclassic?useUnicode=true&characterEncoding=utf8");
//        ds.setUsername("fv4smhqnw47b");
//        ds.setPassword("Rudtjr1216!");
//        JdbcTemplate template = new JdbcTemplate(ds);
//
//        String[] cateList = new String[]{"다크1단계", "다크2단계", "다크3단계"};
//
//        for (String cate : cateList) {
//            String u = "https://xeon310.com/bbs/board.php?bo_table=job_t&sca=" + cate;
//
//            Document doc = Jsoup.connect(u).get();
//            Elements lists = doc.select(".item_list_tr");
//
//            for (Element e : lists) {
//                String name = e.child(1).text();
//                name = name.replace(":", "-");
//                String desc = e.child(6).text();
//
//                String skillLevel = e.child(2).text();
//                String hp = e.child(3).text();
//                String mp = e.child(4).text();
//                String consume = e.child(5).text();
//                String duration = e.child(7).text();
//
//                String sql = "INSERT INTO g5_write_skill (ca_name,wr_subject,wr_content,wr_1,wr_2,wr_3,wr_4,wr_5) values (?,?,?,?,?,?,?,?)";
//                template.update(sql, cate, name, desc, skillLevel, hp, mp, consume, duration);
//
//                String src = e.select("img").attr("src");
//                String fileName = name.replace(" ", "");
//                System.out.println(name + "," + src);
//
//                sql = "INSERT INTO g5_board_file (bo_table,wr_id,bf_source,bf_file) values ('skill',last_insert_id(),?,?)";
//                template.update(sql, fileName + ".gif", fileName + ".gif");
//
//                InputStream is = new URL(src).openStream();
//                FileUtils.copyInputStreamToFile(is, new File("E:/works/lineage/lin380_v3/xeon/skill/군주/" + fileName + ".gif"));
//            }
//        }
//
//        template.update("UPDATE g5_write_skill set wr_num=-wr_id,wr_parent=wr_id,mb_id='admin',wr_name='메티스',wr_email='admin@domain.co.kr'");
//    }
//}
