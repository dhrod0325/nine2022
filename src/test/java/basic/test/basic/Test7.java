package basic.test.basic;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

public class Test7 {
        public static void main(String[] args) throws ParseException, IOException {
            String sql = "SELECT\n" +
                    "	RANKING \n" +
                    "FROM\n" +
                    "	( SELECT char_name, dense_rank() over ( ORDER BY exp DESC ) RANKING FROM characters WHERE AccessLevel = 0) T \n" +
                    "WHERE\n" +
                    "	CHAR_NAME = ?";

            System.out.println(sql);

//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName("org.mariadb.jdbc.Driver");
//        ds.setUrl("jdbc:mysql://localhost:3306/cccc?useUnicode=true&characterEncoding=euckr&serverTimezone=Asia/Seoul");
//        ds.setUsername("root");
//        ds.setPassword("rudtjr1216!");
//        ds.setValidationQuery("SELECT 1");
//        ds.setMaxIdle(100);
//
//
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
//        List<Map<String, Object>> s = jdbcTemplate.queryForList("select * from mapids");
//
//        List<Long[]> o2 = new ArrayList<>();
//
//        for (Map<String, Object> m : s) {
//            Long[] a = new Long[5];
//
//            a[0] = Long.valueOf(m.get("mapid").toString());
//            a[1] = (Long) m.get("startX");
//            a[2] = (Long) m.get("endX");
//            a[3] = (Long) m.get("startY");
//            a[4] = (Long) m.get("endY");
//
//            System.out.println(Arrays.toString(a));
//        }


//        List<String> a = Files.readAllLines(Paths.get("mapt.txt"));
//
//        for (String m : a) {
//            System.out.println(m.replace("[", "{").replace("]", "}") + ",");
//        }

        }

        private static class MagicDollEventKey {
            private int itemId;
            private int npcId;

            public MagicDollEventKey(int itemId, int npcId) {
                this.itemId = itemId;
                this.npcId = npcId;
            }

            public int getItemId() {
                return itemId;
            }

            public void setItemId(int itemId) {
                this.itemId = itemId;
            }

            public int getNpcId() {
                return npcId;
            }

            public void setNpcId(int npcId) {
                this.npcId = npcId;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                MagicDollEventKey that = (MagicDollEventKey) o;
                return itemId == that.itemId && npcId == that.npcId;
            }

            @Override
            public int hashCode() {
                return Objects.hash(itemId, npcId);
            }
        }
    }
