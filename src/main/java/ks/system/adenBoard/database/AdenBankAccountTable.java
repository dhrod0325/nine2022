package ks.system.adenBoard.database;

import ks.app.LineageAppContext;
import ks.system.adenBoard.model.AdenBankAccount;
import ks.system.adenBoard.model.AdenBuy;
import ks.system.adenBoard.model.AdenSell;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AdenBankAccountTable {
    public static AdenBankAccountTable getInstance() {
        return LineageAppContext.getBean(AdenBankAccountTable.class);
    }

    public void insertOrUpdateBankAccount(AdenBankAccount vo) {
        String sql = "INSERT INTO aden_bank_account (account_id,\n" +
                "                                       bank_no,\n" +
                "                                       bank_owner_name,\n" +
                "                                       bank_name,\n" +
                "                                       phone,\n" +
                "                                       reg_date)\n" +
                "        VALUES (:account_id,\n" +
                "                :bank_no,\n" +
                "                :bank_owner_name,\n" +
                "                :bank_name,\n" +
                "                :phone,\n" +
                "                now())\n" +
                "        ON DUPLICATE KEY UPDATE account_id      =:account_id,\n" +
                "                                bank_no         =:bank_no,\n" +
                "                                bank_owner_name =:bank_owner_name,\n" +
                "                                bank_name       =:bank_name,\n" +
                "                                phone           =:phone";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void insertAdenSell(AdenSell vo) {
        String sql = "INSERT aden_sell_list (account_id, name, aden, cash, status, reg_date)\n" +
                "        VALUES (:account_id, :name, :aden, :cash, :status, now())";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void insertAdenBuy(AdenBuy vo) {
        String sql = "INSERT INTO aden_buy (aden_sell_id, buyer_id, buyer_name, reg_date)\n" +
                "        values (:aden_sell_id, :buyer_id, :buyer_name, now())";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public AdenBankAccount getBankAccount(String account_id) {
        String sql = "SELECT * FROM aden_bank_account WHERE account_id = ?";
        return SqlUtils.select(sql, new BeanPropertyRowMapper<>(AdenBankAccount.class), account_id);
    }

    public AdenSell getAdenSell(int id) {
        String sql = "select * from aden_sell_list where id = ?";
        return SqlUtils.select(sql, new BeanPropertyRowMapper<>(AdenSell.class), id);
    }

    public boolean isAlreadyRegisted(String accountId) {
        String sql = "select count(*) from aden_sell_list where account_id = ? and status = 1";
        return SqlUtils.selectInteger(sql, accountId) > 0;
    }

    public AdenBuy getAdenBuy(int id) {
        String sql = "SELECT * FROM aden_buy where aden_sell_id = ?";

        return SqlUtils.select(sql, new BeanPropertyRowMapper<>(AdenBuy.class), id);
    }

    public void updateAdenSellStatus(String status, int id) {
        SqlUtils.update("UPDATE aden_sell_list SET status=? WHERE ID = ?", status, id);
    }

    public void deleteAdenSell(int id) {
        SqlUtils.update("delete FROM aden_sell_list where id = ?", id);
    }

    public List<Map<String, Object>> getAdenDataList(int number, int countPerPage) {
        Map<String, Object> data = new HashMap<>();
        data.put("number", number);
        data.put("countPerPage", countPerPage);

        String sql = "SELECT *\n" +
                "        FROM aden_sell_list A,\n" +
                "        aden_bank_account B\n" +
                "        WHERE A.account_id = B.account_id\n";

        if (number > 0) {
            sql += "     AND id < :number \n";
        }

        sql += "        order by id desc\n" +
                "        limit :countPerPage";

        return SqlUtils.queryForListParamMap(sql, data);
    }

    public Map<String, Object> getAdenData(int id) {
        String sql = "SELECT *\n" +
                "        FROM aden_sell_list A,\n" +
                "             aden_bank_account B\n" +
                "        WHERE A.account_id = B.account_id\n" +
                "          and id = ?";

        return SqlUtils.queryForMap(sql, id);
    }


}
