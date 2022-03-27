package bill.database;

import bill.database.model.BillGift;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

public class BillGiftTable {
    private final static BillGiftTable instance = new BillGiftTable();

    public static BillGiftTable getInstance() {
        return instance;
    }

    public void insert(BillGift vo) {
        String sql = "insert into bill_gift (" +
                "transRefKey,\n" +
                "transDT,\n" +
                "transRemark,\n" +
                "charId, \n" +
                "charName, \n" +
                "deposit, \n" +
                "gift, \n" +
                "giftDate, \n" +
                "regDate ) values \n" +
                " (" +
                ":transRefKey,\n" +
                ":transDT, \n" +
                ":transRemark, \n" +
                ":charId, \n" +
                ":charName, \n" +
                ":deposit, \n" +
                ":gift, \n" +
                ":giftDate, \n" +
                ":regDate )";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public int countByTransRefKey(String transRefKey) {
        return SqlUtils.selectInteger("select count(*) from bill_gift where transRefKey=? ", transRefKey);
    }

/*
    public BillGift findByTransRefKey(String transRefKey) {
        return SqlUtils.select("select * from bill_gift where transRefKey=? ", new BeanPropertyRowMapper<>(BillGift.class), transRefKey);
    }
    public List<BillGift> selectNoGiftList() {
        String sql = "select * from bill_gift where gift=0";

        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(BillGift.class));
    }

    public List<BillGift> findListByCharId(int charId) {
        String sql = "select * from bill_gift where charId=?";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(BillGift.class), charId);
    }

    public Integer findListCountByCharId(int charId) {
        String sql = "select count(*) from bill_gift where charId=? and gift=0";
        return SqlUtils.selectInteger(sql, charId);
    }*/
}
