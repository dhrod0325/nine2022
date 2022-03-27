/**
 * License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE").
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR
 * COPYRIGHT LAW IS PROHIBITED.
 * <p>
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 */
package ks.system.race.datatable;

import ks.app.LineageAppContext;
import ks.system.race.L1RaceManager;
import ks.system.race.model.L1RaceTicket;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Component
public class L1RaceTicketTable {
    private final HashMap<Integer, L1RaceTicket> tickets = new HashMap<>();

    private int maxRoundNumber;

    public static L1RaceTicketTable getInstance() {
        return LineageAppContext.getBean(L1RaceTicketTable.class);
    }

    public void load() {
        tickets.clear();

        for (L1RaceTicket ticket : selectList()) {
            tickets.put(ticket.getItemObjId(), ticket);
        }
    }

    public List<L1RaceTicket> selectList() {
        return SqlUtils.query("SELECT * FROM race_ticket", new RowMapper<L1RaceTicket>() {
            int temp = 0;

            @Override
            public L1RaceTicket mapRow(ResultSet rs, int rowNum) throws SQLException {
                L1RaceTicket ticket = new L1RaceTicket();
                int itemObjId = rs.getInt("item_obj_id");

                ticket.setItemObjId(itemObjId);
                ticket.setRound(rs.getInt("round"));
                ticket.setAllotmentPercentage(rs.getInt("allotment_percentage"));
                ticket.setVictory(rs.getInt("victory"));
                ticket.setRunnerNum(rs.getInt("runner_num"));
                ticket.setRunnerNpcId(rs.getInt("runner_npc_id"));

                if (ticket.getRound() > temp) {
                    temp = ticket.getRound();
                }

                maxRoundNumber = temp;

                return ticket;
            }
        });
    }

    public void storeNewTiket(L1RaceTicket ticket) {
        if (ticket.getItemObjId() != 0) {
            tickets.put(ticket.getItemObjId(), ticket);
        }

        SqlUtils.update("INSERT INTO race_ticket SET item_obj_id=?,round=?,allotment_percentage=?,victory=?,runner_num=?,runner_npc_id=?",
                ticket.getItemObjId(),
                ticket.getRound(),
                ticket.getAllotmentPercentage(),
                ticket.getVictory(),
                ticket.getRunnerNum(),
                ticket.getRunnerNpcId()
        );
    }

    public void deleteTicket(int itemobjid) {
        tickets.remove(itemobjid);

        SqlUtils.update("delete from race_ticket WHERE item_obj_id=?", itemobjid);
    }

    public void oldTicketDelete(int round) {
        SqlUtils.update("delete from race_ticket WHERE item_obj_id=0 and round!=?", round);
    }

    public void updateTicket(int round, int num, double allotment_percentage) {
        for (L1RaceTicket ticket : tickets.values()) {
            if (ticket.getRound() == round && ticket.getRunnerNum() == num) {
                ticket.setVictory(1);
                ticket.setAllotmentPercentage(allotment_percentage);
            }
        }

        SqlUtils.update("UPDATE race_ticket SET victory=? ,allotment_percentage=? WHERE round=? and runner_num=?", 1, allotment_percentage, round, num);
    }

    public L1RaceTicket getTemplate(int itemobjid) {
        if (tickets.containsKey(itemobjid)) {
            return tickets.get(itemobjid);
        }
        return null;
    }

    public int getRoundNumOfMax() {
        return maxRoundNumber;
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
                "	( SELECT count(*) FROM race_result WHERE round = a.round )= 0 \n" +
                "	)", L1RaceManager.RESTORE_TICKET_ID, L1RaceManager.SHOP_ITEM_ID);
    }
}
