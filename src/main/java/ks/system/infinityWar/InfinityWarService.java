package ks.system.infinityWar;

import ks.app.LineageAppContext;
import ks.app.event.NpcActionEvent;
import ks.model.L1Object;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.system.infinityWar.model.InfinityWar;
import ks.system.infinityWar.table.InfinityWarTable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class InfinityWarService {
    @Resource
    private InfinityWarTable warTable;

    public static InfinityWarService getInstance() {
        return LineageAppContext.getBean(InfinityWarService.class);
    }

    public List<Integer> infinityWarMapIds() {
        List<Integer> result = new ArrayList<>();

        for (InfinityWar war : warTable.getList()) {
            result.add(war.getMapId());
        }

        return result;
    }

    @EventListener
    public void event(NpcActionEvent e) {
        NpcActionEvent.NpcActionEventSource source = e.getSource();

        String action = source.getAction();
        L1Object obj = source.getObj();
        L1PcInstance pc = source.getPc();

        if (obj instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) obj;

            warTable.getList().forEach(war -> {
                if (war.getNpc().contains(npc.getNpcId())) {
                    war.getWarSystem().npcTalk(action, pc, npc);
                }
            });
        }
    }
}
