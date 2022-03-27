package ks.model;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1ItemId;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_HPMeter;
import ks.packets.serverpackets.S_Party;
import ks.packets.serverpackets.S_ServerMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class L1Party {
    private static final Logger logger = LogManager.getLogger(L1Party.class);

    private final List<L1PcInstance> memberList = new CopyOnWriteArrayList<>();

    private L1PcInstance leader = null;

    public void addMember(L1PcInstance pc) {
        try {
            if (memberList.size() >= CodeConfig.MAX_PARTY_NUMBER && memberList.contains(pc)) {
                return;
            }

            if (memberList.isEmpty()) {
                setLeader(pc);
            } else {
                createMiniHp(pc);
            }

            memberList.add(pc);
            pc.setParty(this);

            showAddPartyInfo(pc);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void removeMember(L1PcInstance pc) {
        try {
            if (!memberList.contains(pc)) {
                return;
            }

            pc.setParty(null);

            memberList.remove(pc);

            if (!memberList.isEmpty()) {
                deleteMiniHp(pc);
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }


    public boolean isVacancy() {
        return memberList.size() < CodeConfig.MAX_PARTY_NUMBER;
    }

    public boolean isMember(L1PcInstance pc) {
        return memberList.contains(pc);
    }

    public L1PcInstance getLeader() {
        return leader;
    }

    private void setLeader(L1PcInstance leader) {
        this.leader = leader;
    }

    public boolean isLeader(L1PcInstance pc) {
        if (pc == null) {
            return false;
        }

        return pc.getId() == leader.getId();
    }

    public String getMembersNameList() {
        StringBuilder result = new StringBuilder();

        for (L1PcInstance pc : memberList) {
            result.append(pc.getName()).append(" ");
        }

        return result.toString();
    }

    public void refresh(L1PcInstance pc) {// 파티추가

        if (pc == null) {
            return;
        }

        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                if (member == null) {
                    continue;
                }

                if (pc.getId() == member.getId()) {
                    continue;
                }

                L1Party party = member.getParty();

                if (party == null) {
                    continue;
                }

                if (pc.equals(party.getLeader())) {
                    member.sendPackets(new S_Party(0x6c2, pc));
                } else {
                    member.sendPackets(new S_Party(0x6c1, pc));
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void memberDie(L1PcInstance pc) {// 파티추가
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                if (pc.getId() == member.getId()) {
                    continue;
                }

                member.sendPackets(new S_Party(0x6c0, pc));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void showAddPartyInfo(L1PcInstance pc) {
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                if (pc.getId() == getLeader().getId() && memberList.size() == 1) {
                    continue;
                }

                if (pc.getId() == member.getId()) {
                    pc.sendPackets(new S_Party(0x68, pc));
                } else {
                    member.sendPackets(new S_Party(0x69, pc));
                }

                member.sendPackets(new S_Party(0x6e, member));
                createMiniHp(member);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void createMiniHp(L1PcInstance pc) {
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                member.sendPackets(new S_HPMeter(pc.getId(), 100 * pc.getCurrentHp() / pc.getMaxHp()));
                pc.sendPackets(new S_HPMeter(member.getId(), 100 * member.getCurrentHp() / member.getMaxHp()));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void deleteMiniHp(L1PcInstance pc) {
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                pc.sendPackets(new S_HPMeter(member.getId(), 0xff));
                member.sendPackets(new S_HPMeter(pc.getId(), 0xff));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void updateMiniHP(L1PcInstance pc) {
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                member.sendPackets(new S_HPMeter(pc.getId(), 100 * pc.getCurrentHp() / pc.getMaxHp()));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void breakup() {
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                member.sendPackets(new S_ServerMessage(418));
                removeMember(member);
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }
    }

    public void passLeader(L1PcInstance pc) { // 리더위임
        List<L1PcInstance> members = getMembers();

        for (L1PcInstance member : members) {
            try {
                L1Party party = member.getParty();

                if (party == null)
                    continue;

                party.setLeader(pc);
                member.sendPackets(new S_Party(0x6A, pc));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void leaveMember(L1PcInstance pc) {
        try {
            if (isLeader(pc) || memberList.size() == 2) {
                breakup();
            } else {
                List<L1PcInstance> members = getMembers();

                for (L1PcInstance member : members) {
                    member.sendPackets(new S_ServerMessage(420, pc.getName()));
                }

                removeMember(pc);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void kickMember(L1PcInstance pc) { // 리더추방
        if (memberList.size() == 2) {
            breakup();
        } else {
            List<L1PcInstance> members = getMembers();

            for (L1PcInstance member : members) {
                try {
                    member.sendPackets(new S_ServerMessage(420, pc.getName()));
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            pc.sendPackets(new S_ServerMessage(419));

            removeMember(pc);
        }

        pc.sendPackets(new S_ServerMessage(419)); // 파티로부터 추방되었습니다.
    }

    public List<L1PcInstance> getMembers() {
        return memberList;
    }

    public List<L1PcInstance> getVisiblePartyMembers(L1PcInstance pc) {
        List<L1PcInstance> partyMembers = getMembers();
        List<L1PcInstance> visibleList = new ArrayList<>();

        for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc)) {
            for (L1PcInstance member : partyMembers) {
                if (member.getLocation().isInScreen(pc.getLocation()) && member.equals(player)) {
                    if (!visibleList.contains(member))
                        visibleList.add(member);
                }
            }
        }

        visibleList.add(pc);

        return visibleList;
    }

    public boolean hasAllAinHasad(List<L1PcInstance> members) {
        for (L1PcInstance m : members) {
            if (m.getAinHasad() <= 0) {
                return false;
            }
        }

        return true;
    }

    public boolean hasAllDoll(List<L1PcInstance> members) {
        for (L1PcInstance m : members) {
            if (!m.isUsingDoll()) {
                return false;
            }
        }

        return true;
    }

    public boolean hasAllServerRune(List<L1PcInstance> members) {
        for (L1PcInstance m : members) {
            if (!m.isEquipServerRune()) {
                return false;
            }
        }

        return true;
    }

    public boolean hasAllServerGaho(List<L1PcInstance> members) {
        for (L1PcInstance m : members) {
            if (!m.getInventory().checkItem(L1ItemId.SERVER_GAHO, 1)) {
                return false;
            }
        }

        return true;
    }
}

