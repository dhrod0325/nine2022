package ks.model.pc.hackCheck.speedHack.spr;

import ks.model.pc.L1PcInstance;
import ks.model.pc.hackCheck.speedHack.L1AcceleratorCheck;
import ks.util.common.SqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SprChecker {
    private static final Logger logger = LogManager.getLogger(SprChecker.class);
    private final L1PcInstance pc;
    private final Map<SprCheck, List<SprCheck>> sprCheckMap = new ConcurrentHashMap<>();

    public SprChecker(L1PcInstance pc) {
        this.pc = pc;
    }

    public void check(SprCheck vo) {
        int rightInterval = new L1AcceleratorCheck(pc).getRightInterval(vo.getAct());

        if (vo.getInterval() > rightInterval + 50) {
            return;
        }

        sprCheckMap.putIfAbsent(vo, new ArrayList<>());
        List<SprCheck> a = sprCheckMap.get(vo);
        a.add(vo);
        sprCheckMap.put(vo, a);
    }

    public List<SprCheck> getActList(SprCheck vo, int act) {
        List<SprCheck> result = new CopyOnWriteArrayList<>();

        try {
            synchronized (sprCheckMap) {
                List<SprCheck> list = sprCheckMap.get(vo);

                if (list == null) {
                    return Collections.emptyList();
                }

                for (SprCheck o : list) {
                    if (o.getAct() == act) {
                        result.add(o);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            logger.error("speed hack check error ", e);
        }

        return result;
    }

    public int getAvgInterval(SprCheck vo) {
        try {
            List<SprCheck> list = getActList(vo, vo.getAct());

            if (list.isEmpty()) {
                return 0;
            }

            int sum = 0;

            for (SprCheck c : list) {
                sum += c.getInterval();
            }

            return sum / list.size();
        } catch (Exception e) {
            logger.error("speed hack check error ", e);
        }

        return 0;
    }

    public void save() {
        save(0);
    }

    public void save(long rInterval) {
        for (SprCheck spr : sprCheckMap.keySet()) {
            int[] actList = new int[]{0, 1, 2, 3};

            for (int act : actList) {
                List<SprCheck> list = getActList(spr, act);

                if (list.size() < 10) {
                    continue;
                }

                int sum = 0;

                for (SprCheck c : list) {
                    sum += c.getInterval();
                }

                int avg = sum / list.size();

                int rightInterval = new L1AcceleratorCheck(pc).getRightInterval(act);

                SprCheck c = new SprCheck(pc, act, avg);
                c.setRightInterval(rightInterval);
                c.setRinterval(rInterval);

                if (c.getRegDate() == null) {
                    c.setRegDate(new Date());
                }

                insert(c);

                sprCheckMap.get(spr).clear();
            }
        }
    }

    public void insert(SprCheck vo) {
        String sql = "insert into characters_spr " +
                "(charId, spr, act, avgInterval,weaponId,haste, brave, fastmove, elfbrave, thirdspeed, dancing,rightInterval,rinterval,regDate) " +
                "values " +
                "(:charId,:spr,:act,:interval,:weaponId,:haste, :brave, :fastmove, :elfbrave, :thirdspeed, :dancing,:rightInterval,:rinterval,:regDate) " +
                "on duplicate key update " +
                "avgInterval=((:interval+avgInterval)/2),rightInterval=:rightInterval,rinterval=:rinterval,regDate=:regDate";

        SqlUtils.update(sql, new BeanPropertySqlParameterSource(vo));
    }

    public void clear() {
        sprCheckMap.clear();
    }
}
