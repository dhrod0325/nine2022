package ks.system.boss.table;

import ks.system.boss.model.L1BossDieHistory;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1BossDieHistoryTable {
    private static final L1BossDieHistoryTable instance = new L1BossDieHistoryTable();
    private final List<L1BossDieHistory> list = new CopyOnWriteArrayList<>();

    public static L1BossDieHistoryTable getInstance() {
        return instance;
    }

    public void load() {
        list.clear();
        list.addAll(selectList());
    }

    public List<L1BossDieHistory> selectList() {
        String sql = "SELECT * FROM spawnlist_boss_die_history";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1BossDieHistory.class));
    }

    public List<L1BossDieHistory> selectByDate(int bossSpawnId, int npcId, Date date) {
        String sql = "SELECT * FROM spawnlist_boss_die_history WHERE npcId=? AND date_format(deathTime,'%Y-%m-%d %H:%i')=? and bossSpawnId=?";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1BossDieHistory.class), npcId, format.format(date), bossSpawnId);
    }

    public List<L1BossDieHistory> selectListByPortalInfo(int npcId, int portalId, String portalStartTime) {
        String sql = "SELECT * FROM spawnlist_boss_die_history WHERE npcId=? AND portalId=? AND portalStartTime=?";
        return SqlUtils.query(sql, new BeanPropertyRowMapper<>(L1BossDieHistory.class), npcId, portalId, portalStartTime);
    }

    public List<L1BossDieHistory> getList() {
        return list;
    }

    public void insertOrUpdate(int bossSpawnId, int npcId, Date deathTime, String attacker, int portalId, Date portalStartTime) {
        Integer exists = SqlUtils.selectInteger("SELECT COUNT(*) FROM spawnlist_boss_die_history WHERE npcId=? and deathTime=? ", npcId, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(deathTime));

        if (exists <= 0) {
            String sql = "INSERT INTO spawnlist_boss_die_history (bossSpawnId,npcId, deathTime, attacker, portalId,portalStartTime) VALUES (?,?,?,?,?,?)";
            SqlUtils.update(sql, bossSpawnId, npcId, deathTime, attacker, portalId, portalStartTime);
        }
    }

    public void insertOrUpdate(int bossSpawnId, int npcId, Date deathTime, String attacker) {
        insertOrUpdate(bossSpawnId, npcId, deathTime, attacker, 0, null);
    }
}
