package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Party;
import ks.model.pc.L1PcInstance;

import java.util.List;

public class S_Party extends ServerBasePacket {
    public S_Party(int type, L1PcInstance pc) {
        switch (type) {
            case 0x68:
                newMember(pc);
                break;
            case 0x69:
                oldMember(pc);
                break;
            case 0x6A:
                changeLeader(pc);
            case 0x6e:
                refreshParty(pc);
                break;
            case 0x6c0:
                nameColor(pc, 0);
                break;
            case 0x6c1:
                nameColor(pc, 1);
                break;
            case 0x6c2:
                nameColor(pc, 2);
                break;
            default:
                break;
        }
    }

    public S_Party(String htmlid, int objid, String partyname, String partymembers) {
        buildPacket(htmlid, objid, partyname, partymembers);
    }

    private void buildPacket(String htmlid, int objid, String partyname, String partymembers) {
        writeC(L1Opcodes.S_OPCODE_SHOWHTML);
        writeD(objid);
        writeS(htmlid);
        writeH(1);
        writeH(0x02);
        writeS(partyname);
        writeS(partymembers);
    }

    public void newMember(L1PcInstance pc) {
        L1Party party = pc.getParty();

        if (party == null)
            return;

        L1PcInstance leader = party.getLeader();
        List<L1PcInstance> members = party.getMembers();

        if (pc.getParty() == null) {
            return;
        }

        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x68);

        double nowhp = leader.getCurrentHp();
        double maxhp = leader.getMaxHp();

        writeC(members.size() - 1);
        writeD(leader.getId());
        writeS(leader.getName());
        writeC((int) (nowhp / maxhp) * 100);
        writeD(leader.getMapId());
        writeH(leader.getX());
        writeH(leader.getY());
        for (L1PcInstance member : members) {
            if (member.getId() == leader.getId())
                continue;

            nowhp = member.getCurrentHp();
            maxhp = member.getMaxHp();
            writeD(member.getId());
            writeS(member.getName());
            writeC((int) (nowhp / maxhp) * 100);
            writeD(member.getMapId());
            writeH(member.getX());
            writeH(member.getY());
        }
        writeC(0x00);
    }

    public void oldMember(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x69);
        writeD(pc.getId());
        writeS(pc.getName());
        writeD(pc.getMapId());
        writeH(pc.getX());
        writeH(pc.getY());
    }

    public void nameColor(L1PcInstance pc, int type) {//파티추가
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x6C);
        writeD(pc.getId());
        writeH(type);
    }

    public void changeLeader(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x6A);
        writeD(pc.getId());
        writeH(0x0000);
    }

    public void refreshParty(L1PcInstance pc) {
        L1Party party = pc.getParty();

        if (party == null) {
            return;
        }

        List<L1PcInstance> members = party.getMembers();

        writeC(L1Opcodes.S_OPCODE_PACKETBOX);
        writeC(0x6E);
        writeC(members.size());

        for (L1PcInstance member : members) {
            writeD(member.getId());
            writeD(member.getMapId());
            writeH(member.getX());
            writeH(member.getY());
        }

        writeC(0x00);
    }
}
