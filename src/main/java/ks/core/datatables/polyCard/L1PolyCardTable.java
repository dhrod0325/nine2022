package ks.core.datatables.polyCard;

import ks.app.LineageAppContext;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class L1PolyCardTable {
    private final List<L1PolyCard> polyCards = new ArrayList<>();

    public static L1PolyCardTable getInstance() {
        return LineageAppContext.getBean(L1PolyCardTable.class);
    }

    public void load() {
        polyCards.clear();
        polyCards.addAll(selectPolyCardList());
    }

    public void masterPoly(int chardId, int itemId) {
        String sql = "INSERT INTO character_poly (char_id,poly_id,reg_date) VALUES (?,?,now())";
        SqlUtils.update(sql, chardId, getPolyId(itemId));
    }

    public boolean isMastedByItemId(int chardId, int itemId) {
        String sql = "SELECT COUNT(*) FROM character_poly WHERE poly_id=? and char_id=?";

        int polyId = getPolyId(itemId);

        int result = SqlUtils.selectInteger(sql, polyId, chardId);

        return result > 0;
    }

    public boolean isMastedByPolyId(int chardId, int polyId) {
        String sql = "SELECT COUNT(*) FROM character_poly WHERE poly_id=? and char_id=?";

        int result = SqlUtils.selectInteger(sql, polyId, chardId);

        return result > 0;
    }

    public List<L1PolyCard> getListByGrade(int grade) {
        List<L1PolyCard> result = new ArrayList<>();

        for (L1PolyCard o : polyCards) {
            if (o.getPolyGrade() == grade) {
                result.add(o);
            }
        }

        return result;
    }

    public List<L1PolyCard> selectPolyCardList() {
        String sql = "SELECT * FROM etcitem_poly";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1PolyCard.class));
    }

    public int getPolyId(int itemId) {
        L1PolyCard c = getPolyCard(itemId);

        if (c != null) {
            return c.getPolyId();
        }

        return -1;
    }

    public int getPolyGrade(int itemId) {
        L1PolyCard c = getPolyCard(itemId);

        if (c != null) {
            return c.getPolyGrade();
        }

        return -1;
    }

    public L1PolyCard getPolyCard(int itemId) {
        for (L1PolyCard o : polyCards) {
            if (o.getItemId() == itemId) {
                return o;
            }
        }

        return null;
    }

    public L1PolyCard getPolyCardByPolyId(int polyId) {
        for (L1PolyCard o : polyCards) {
            if (o.getPolyId() == polyId) {
                return o;
            }
        }

        return null;
    }

    public boolean isPolyCard(int itemId) {
        return getPolyCard(itemId) != null;
    }
}
