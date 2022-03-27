package bill.database;

import com.baroservice.ws.BankAccountLogEx;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.util.List;

public class BillTable {
    private static final BillTable instance = new BillTable();

    public static BillTable getInstance() {
        return instance;
    }

    public void insertOrUpdate(BankAccountLogEx vo) {
        if (selectCount(vo) == 0) {
            insert(vo);
        }
    }

    public int selectCount(BankAccountLogEx vo) {
        String sql = "select count(*) from bill where transRefKey=?";

        return SqlUtils.selectInteger(sql, vo.getTransRefKey());
    }

    public List<BankAccountLogEx> selectUnRegisteredInGiftList() {
        String sql = "select * from bill where transRefKey not in (\n" +
                "select transRefKey from bill_gift\n" +
                ")";

        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(BankAccountLogEx.class));
    }

    public List<BankAccountLogEx> selectList() {
        String sql = "select * from bill";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(BankAccountLogEx.class));
    }

    public void insert(BankAccountLogEx vo) {
        String sql = "insert into bill (" +
                "                transRefKey ," +
                "                transDT," +
                "                transRemark," +
                "                corpNum ," +
                "                bankAccountNum," +
                "                withdraw ," +
                "                deposit ," +
                "                balance ," +
                "                transType," +
                "                transOffice ," +
                "                mgtRemark1 ," +
                "                mgtRemark2) VALUES (" +
                "                :transRefKey ," +
                "                :transDT," +
                "                :transRemark," +
                "                :corpNum ," +
                "                :bankAccountNum," +
                "                :withdraw ," +
                "                :deposit ," +
                "                :balance ," +
                "                :transType," +
                "                :transOffice ," +
                "                :mgtRemark1 ," +
                "                :mgtRemark2) ";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    /*

    public Bill findByTransDtAndRemark(String transDt, String remark) {
        String sql = "select * from bill where date_format(transDT,'%Y%m%d%H%i')=? and transRemark=?";

        BankAccountLogEx ex = SqlUtils.select(sql, new BeanPropertyRowMapper<>(BankAccountLogEx.class), transDt, remark);

        if (ex == null) {
            return null;
        }

        return new Bill(ex);
    }

    * */
}
