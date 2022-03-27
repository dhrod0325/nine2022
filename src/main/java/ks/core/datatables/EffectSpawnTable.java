package ks.core.datatables;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.core.ObjectIdFactory;
import ks.core.datatables.npc.NpcTable;
import ks.model.Broadcaster;
import ks.model.L1Npc;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1EffectInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_EffectLocation;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class EffectSpawnTable {
    private static final Logger logger = LogManager.getLogger();

    private final List<L1EffectInstance> list = new ArrayList<>();

    public static EffectSpawnTable getInstance() {
        return LineageAppContext.getBean(EffectSpawnTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());
        //spawn();
    }

    public void spawn() {
        List<EffectEach> eachList = new ArrayList<>();
        eachList.add(new EffectEach(33412, 33432, 32794, 32803));
        eachList.add(new EffectEach(33421, 33439, 32822, 32834));

        LineageAppContext.spawnTaskScheduler().scheduleAtFixedRate(() -> {
            for (EffectEach each : eachList) {

                for (int x = each.minX; x <= each.maxX; x++) {
                    for (int y = each.minY; y <= each.maxY; y++) {
                        try {
                            L1Object e = new L1Object();

                            e.setX(x);
                            e.setY(y);
                            e.setMap((short) 4);

                            List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(e, 0);

                            if (!players.isEmpty()) {
                                for (L1PcInstance pc : players) {
                                    if (pc.getCurrentHp() < pc.getMaxHp()) {
                                        pc.healHp(RandomUtils.nextInt(1, 4));
                                    }

                                    if (pc.getCurrentMp() < pc.getMaxMp()) {
                                        pc.healMp(RandomUtils.nextInt(1, 4));
                                    }
                                }

                                Broadcaster.broadcastPacket(e, new S_EffectLocation(e.getX(), e.getY(), 3104));
                            }
                        } catch (Exception e) {
                            logger.error("오류", e);
                        }
                    }
                }


            }


        }, Duration.ofMillis(1250));
    }

    public List<L1EffectInstance> selectList() {
        return SqlUtils.query("SELECT * FROM spawnlist_effect", (rs, i) -> {
            L1Npc l1npc = NpcTable.getInstance().getTemplate(rs.getInt(2));
            L1EffectInstance field = null;

            if (l1npc != null) {
                try {
                    field = (L1EffectInstance) (NpcTable.getInstance().newNpcInstance(rs.getInt("npcid")));
                    field.setId(ObjectIdFactory.getInstance().nextId());
                    field.setX(rs.getInt("locx"));
                    field.setY(rs.getInt("locy"));
                    field.setMap((short) rs.getInt("mapid"));
                    field.setHomeX(field.getX());
                    field.setHomeY(field.getY());
                    field.setHeading(rs.getInt("heading"));
                    field.setLightSize(l1npc.getLightSize());
                    field.getLight().turnOnOffLight();
                } catch (Exception e) {
                    logger.error(e);
                    logger.error("오류", e);
                }
            }

            return field;
        });
    }

    private static class EffectEach {
        public int minX;
        public int maxX;
        public int minY;
        public int maxY;

        public EffectEach(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
    }
}
