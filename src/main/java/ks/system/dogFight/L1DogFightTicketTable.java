package ks.system.dogFight;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class L1DogFightTicketTable {
    private final HashMap<Integer, L1DogFightTicket> tickets = new HashMap<>();

    public static L1DogFightTicketTable getInstance() {
        return LineageAppContext.getBean(L1DogFightTicketTable.class);
    }

    public void load() {
        tickets.clear();

        for (L1DogFightTicket ticket : selectList()) {
            tickets.put(ticket.getItemObjId(), ticket);
        }
    }

    public List<L1DogFightTicket> selectList() {
        return SqlUtils.query("SELECT * FROM dog_fight_ticket", new BeanPropertyRowMapper<>(L1DogFightTicket.class));
    }

    public void storeNewTiket(L1DogFightTicket ticket) {
        if (ticket.getItemObjId() != 0) {
            tickets.put(ticket.getItemObjId(), ticket);
        }

        SqlUtils.update("INSERT INTO dog_fight_ticket SET itemObjId=?,round=?,allotmentPercentage=?,victory=?,runnerNum=?,runnerNpcId=?",
                ticket.getItemObjId(),
                ticket.getRound(),
                ticket.getAllotmentPercentage(),
                ticket.getVictory(),
                ticket.getRunnerNum(),
                ticket.getRunnerNpcId()
        );
    }

    public void deleteTicket(int itemObjId) {
        tickets.remove(itemObjId);

        SqlUtils.update("delete from dog_fight_ticket WHERE itemObjId=?", itemObjId);
    }

    public void oldTicketDelete(int round) {
        SqlUtils.update("delete from dog_fight_ticket WHERE itemObjId=0 and round!=?", round);
    }

    public void updateTicket(int round, int num, double allotmentPercentage) {
        for (L1DogFightTicket ticket : tickets.values()) {
            if (ticket.getRound() == round && ticket.getRunnerNum() == num) {
                ticket.setVictory(1);
                ticket.setAllotmentPercentage(allotmentPercentage);
            }
        }

        SqlUtils.update("UPDATE dog_fight_ticket SET victory=? ,allotmentPercentage=? WHERE round=? and runnerNum=?", 1, allotmentPercentage, round, num);
    }

    public L1DogFightTicket getTemplate(int itemObjId) {
        if (tickets.containsKey(itemObjId)) {
            return tickets.get(itemObjId);
        }
        return null;
    }

    public int getRoundNumOfMax() {
        return SqlUtils.selectInteger("select ifnull(max(round),0) + 1 from dog_fight_ticket");
    }

    public void restoreTickets() {
        SqlUtils.update("UPDATE character_items \n" +
                "SET item_id = ? \n" +
                "WHERE\n" +
                "	id IN (\n" +
                "	SELECT\n" +
                "		id \n" +
                "	FROM\n" +
                "		(\n" +
                "		SELECT\n" +
                "			id,\n" +
                "			SUBSTRING_INDEX( SUBSTRING_INDEX( REPLACE ( item_name, ' ', '-' ), '-',- 2 ), '-', 1 ) round \n" +
                "		FROM\n" +
                "			character_items \n" +
                "		WHERE\n" +
                "			item_id = ? \n" +
                "		) a \n" +
                "	WHERE\n" +
                "	( SELECT count(*) FROM dog_fight_result WHERE round = a.round )= 0 \n" +
                "	)", L1DogFight.RESTORE_TICKET_ID, L1DogFight.SHOP_ITEM_ID);
    }
}
