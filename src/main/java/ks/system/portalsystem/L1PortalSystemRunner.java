package ks.system.portalsystem;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.listener.TimeListener;
import ks.scheduler.timer.realTime.RealTimeScheduler;
import ks.system.portalsystem.model.L1PortalData;
import ks.system.portalsystem.table.PortalSpawnTable;
import ks.system.portalsystem.table.PortalSystemTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class L1PortalSystemRunner {
    private final Logger logger = LogManager.getLogger();

    private final Map<String, L1PortalSystem> portalSystemMap = new HashMap<>();

    public static L1PortalSystemRunner getInstance() {
        return LineageAppContext.getBean(L1PortalSystemRunner.class);
    }

    public void loadPortalSystem() {
        portalSystemMap.clear();
    }

    public L1PortalSystem findByNpcId(int npcId) {
        for (L1PortalSystem portalSystem : portalSystemMap.values()) {
            L1PortalData data = portalSystem.getPortalData();

            if (data.getNpcId() == npcId) {
                return portalSystem;
            }
        }

        return null;
    }

    public void putPortalSystem(L1PortalSystem portalSystem) {
        if (!portalSystemMap.containsKey(portalSystem.getName()))
            portalSystemMap.put(portalSystem.getName(), portalSystem);
    }

    @LogTime
    public void load() {
        loadPortalSystem();

        List<L1PortalData> list = PortalSystemTable.getInstance().getList();

        for (L1PortalData data : list) {
            try {
                L1PortalSystem system = portalSystemMap.get(data.getPortalClassName());

                if (system == null) {
                    system = new L1DefaultPortalSystem();
                }

                system.setPortalData(data);

                putPortalSystem(system);

                L1PortalSystemListener listener = new L1PortalSystemListener();
                listener.setPortalSystem(system);

                RealTimeScheduler.getInstance().addListener(listener);
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    public void clear() {
        List<TimeListener> list = RealTimeScheduler.getInstance().getListeners();

        for (TimeListener listener : list) {
            if (listener instanceof L1PortalSystemListener) {
                ((L1PortalSystemListener) listener).shutDown();

                RealTimeScheduler.getInstance().removeListener(listener);
            }
        }
    }

    public void reLoad() {
        clear();

        PortalSpawnTable.getInstance().load();
        PortalSystemTable.getInstance().load();

        load();
    }
}
