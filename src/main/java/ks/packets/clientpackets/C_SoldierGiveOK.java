package ks.packets.clientpackets;

import ks.core.datatables.CharSoldierTable;
import ks.core.datatables.SoldierTable;
import ks.core.network.L1Client;
import ks.model.L1CharSoldier;
import ks.model.L1Object;
import ks.model.L1Soldier;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.scheduler.timer.BaseTime;
import ks.scheduler.timer.realTime.RealTimeScheduler;

public class C_SoldierGiveOK extends ClientBasePacket {
    public C_SoldierGiveOK(byte[] abyte0, L1Client clientthread) {
        super(abyte0);

        int objid = readD(); // 말 건 npc id
        int index = readH(); // 선택목록 순번
        int unknow = readH(); // ????
        int t_obj = readD(); // pc.getId
        int count = readH(); // 선택갯수

        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }
        L1Object obj = L1World.getInstance().findObject(t_obj);
        L1PcInstance target = (L1PcInstance) obj;

        int npc_id = 0;
        int getCount = 0;

        int castle_id = pc.getClan().getCastleId();
        L1Soldier soldier = SoldierTable.getInstance().getSoldierTable(
                castle_id);

        if (pc.getClanId() != target.getClanId())
            return;

        switch (index) {
            case 1:
                npc_id = soldier.getSoldier1NpcId();
                getCount = soldier.getSoldier1();
                break;
            case 2:
                npc_id = soldier.getSoldier2NpcId();
                getCount = soldier.getSoldier2();
                break;
            case 3:
                npc_id = soldier.getSoldier3NpcId();
                getCount = soldier.getSoldier3();
                break;
            case 4:
                npc_id = soldier.getSoldier4NpcId();
                getCount = soldier.getSoldier4();
                break;
            default:
                break;
        }

        if (getCount < count)
            return;

        BaseTime r = RealTimeScheduler.getInstance().getTime();
        int time = r.getSeconds();

        L1CharSoldier newCharSoldier = new L1CharSoldier(pc.getId());
        newCharSoldier.setSoldierNpc(npc_id);
        newCharSoldier.setSoldierCount(count);
        newCharSoldier.setSoldierCastleId(target.getClan().getCastleId());
        newCharSoldier.setSoldierTime(time);

        CharSoldierTable.getInstance().storeCharSoldier(newCharSoldier);

        int sum = getCount - count;
        switch (index) {
            case 1:
                soldier.setSoldier1(sum);
                break;
            case 2:
                soldier.setSoldier2(sum);
                break;
            case 3:
                soldier.setSoldier3(sum);
                break;
            case 4:
                soldier.setSoldier4(sum);
                break;
            default:
                break;
        }

        SoldierTable.getInstance().updateSoldier(soldier);

        target.sendPackets(new S_NPCTalkReturn(objid, "orville8"));
    }
}
