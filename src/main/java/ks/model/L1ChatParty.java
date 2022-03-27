package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

import java.util.ArrayList;
import java.util.List;

public class L1ChatParty {
    private final List<L1PcInstance> membersList = new ArrayList<>();

    private L1PcInstance leader = null;

    public void addMember(L1PcInstance pc) {
        if (pc == null) {
            throw new NullPointerException();
        }
        if (membersList.size() == CodeConfig.MAX_CHAT_PARTY_NUMBER && !leader.isGm() || membersList.contains(pc)) {
            return;
        }

        if (membersList.isEmpty()) {
            setLeader(pc);
        }

        membersList.add(pc);
        pc.setChatParty(this);
    }

    private void removeMember(L1PcInstance pc) {
        if (!membersList.contains(pc)) {
            return;
        }

        membersList.remove(pc);
        pc.setChatParty(null);
    }

    public boolean isVacancy() {
        return membersList.size() < CodeConfig.MAX_CHAT_PARTY_NUMBER;
    }

    public int getVacancy() {
        return CodeConfig.MAX_CHAT_PARTY_NUMBER - membersList.size();
    }

    public boolean isMember(L1PcInstance pc) {
        return membersList.contains(pc);
    }

    public L1PcInstance getLeader() {
        return leader;
    }

    private void setLeader(L1PcInstance pc) {
        leader = pc;
    }

    public boolean isLeader(L1PcInstance pc) {
        return pc.getId() == leader.getId();
    }

    public String getMembersNameList() {
        StringBuilder result = new StringBuilder();

        for (L1PcInstance pc : membersList) {
            result.append(pc.getName()).append(" ");
        }

        return result.toString();
    }

    private void breakup() {
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            removeMember(member);
            member.sendPackets(new S_ServerMessage(418));
        }
    }

    public void leaveMember(L1PcInstance pc) {
        if (isLeader(pc)) {
            breakup();
        } else {
            if (getNumOfMembers() == 2) {
                removeMember(pc);
                L1PcInstance leader = getLeader();
                removeMember(leader);

                sendLeftMessage(pc, pc);
                sendLeftMessage(leader, pc);
            } else {
                removeMember(pc);

                List<L1PcInstance> members = getMembers();

                for (L1PcInstance member : members) {
                    sendLeftMessage(member, pc);
                }
                sendLeftMessage(pc, pc);
            }
        }
    }

    public void kickMember(L1PcInstance pc) {
        if (getNumOfMembers() == 2) {
            removeMember(pc);
            L1PcInstance leader = getLeader();
            removeMember(leader);
        } else {
            removeMember(pc);
        }
        pc.sendPackets(new S_ServerMessage(419));
    }

    public List<L1PcInstance> getMembers() {
        return membersList;
    }

    public int getNumOfMembers() {
        return membersList.size();
    }

    private void sendLeftMessage(L1PcInstance sendTo, L1PcInstance left) {
        sendTo.sendPackets(new S_ServerMessage(420, left.getName()));
    }

}
