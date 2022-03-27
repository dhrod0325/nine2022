package ks.core.datatables.npc_making;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ShowCCHtml;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NpcMakingTable {
    private final List<NpcMaking> list = new ArrayList<>();
    private final List<NpcMakingInfo> npcMakingInfoList = new ArrayList<>();

    public static NpcMakingTable getInstance() {
        return LineageAppContext.getBean(NpcMakingTable.class);
    }

    @LogTime
    public void load() {
        list.clear();
        list.addAll(selectList());

        npcMakingInfoList.clear();
        npcMakingInfoList.addAll(selectInfoList());
    }

    public NpcMakingInfo findInfoByNpcId(int npcId) {
        for (NpcMakingInfo npcMakingInfo : npcMakingInfoList) {
            if (npcMakingInfo.getNpcId() == npcId) {
                return npcMakingInfo;
            }
        }

        return null;
    }

    public List<NpcMakingInfo> selectInfoList() {
        return SqlUtils.query("SELECT * FROM npc_making_info", new BeanPropertyRowMapper<>(NpcMakingInfo.class));
    }

    public List<NpcMaking> selectList() {
        List<NpcMaking> list = SqlUtils.query("SELECT * FROM npc_making order by ord,id", new BeanPropertyRowMapper<>(NpcMaking.class));

        for (NpcMaking making : list) {
            List<NpcMakingMaterial> materialList = SqlUtils.query("SELECT * FROM npc_making_material WHERE makingId=?",
                    new BeanPropertyRowMapper<>(NpcMakingMaterial.class),
                    making.getId()
            );

            making.getMakingMaterialList().addAll(materialList);
        }

        return list;
    }

    public List<NpcMaking> findByNpcId(int npcId) {
        List<NpcMaking> result = new ArrayList<>();

        for (NpcMaking npcMaking : list) {
            if (npcMaking.getNpcId() == npcId) {
                result.add(npcMaking);
            }
        }

        return result;
    }

    public List<NpcMaking> getList() {
        return list;
    }

    public List<NpcMaking> getMakingList(L1PcInstance pc, L1NpcInstance npc) {
        List<NpcMaking> list = findByNpcId(npc.getNpcId());

        return L1CommonUtils.getPagingList(pc, list);
    }

    public void showHtml(L1PcInstance pc, L1NpcInstance npc) {
        List<NpcMaking> makingList = getMakingList(pc, npc);

        if (!makingList.isEmpty()) {
            List<String> params = new ArrayList<>();

            params.add(pc.getPagination().getPagingString());

            for (NpcMaking m : makingList) {
                params.add(m.getMakingItemName());
            }

            for (int i = makingList.size(); i < pc.getPagination().getRecordCountPerPage(); i++) {
                params.add("&nbsp;");
            }

            NpcMakingInfo info = findInfoByNpcId(npc.getNpcId());

            if (info != null) {
                if (info.getMent1() != null) {
                    params.add(info.getMent1());
                }

                if (info.getMent2() != null) {
                    params.add(info.getMent2());
                }
            } else {
                params.add("무엇을 제작하려 하세요?");
                params.add("아래 아이템을 클릭하면 아이템을 제작할 수 있습니다");
            }

            pc.sendPackets(new S_ShowCCHtml(npc.getId(), "cc_making5", params));
        }
    }
}
