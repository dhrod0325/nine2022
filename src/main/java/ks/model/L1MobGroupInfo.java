package ks.model;

import ks.model.instance.L1NpcInstance;

import java.util.ArrayList;
import java.util.List;

public class L1MobGroupInfo {
    private final List<L1NpcInstance> _membersList = new ArrayList<>();
    private L1NpcInstance _leader;
    private L1Spawn _spawn;
    private boolean _isRemoveGroup;

    public L1MobGroupInfo() {
    }

    public List<L1NpcInstance> get_membersList() {
        return _membersList;
    }

    public L1NpcInstance getLeader() {
        return _leader;
    }

    public void setLeader(L1NpcInstance npc) {
        _leader = npc;
    }

    public boolean isLeader(L1NpcInstance npc) {
        return npc.getId() == _leader.getId();
    }

    public L1Spawn getSpawn() {
        return _spawn;
    }

    public void setSpawn(L1Spawn spawn) {
        _spawn = spawn;
    }

    public void addMember(L1NpcInstance npc) {
        if (npc == null) {
            throw new NullPointerException();
        }

        if (_membersList.isEmpty()) {
            setLeader(npc);
            if (npc.isReSpawn()) {
                setSpawn(npc.getSpawn());
            }
        }

        if (!_membersList.contains(npc)) {
            _membersList.add(npc);
        }
        npc.setMobGroupInfo(this);
        npc.setMobGroupId(_leader.getId());
    }

    public synchronized int removeMember(L1NpcInstance npc) {
        if (npc == null) {
            throw new NullPointerException();
        }

        _membersList.remove(npc);

        npc.setMobGroupInfo(null);

        if (isLeader(npc)) {
            if (isRemoveGroup() && _membersList.size() != 0) {
                for (L1NpcInstance minion : _membersList) {
                    minion.setMobGroupInfo(null);
                    minion.setSpawn(null);
                    minion.setRespawn(false);
                }
                return 0;
            }
            if (_membersList.size() != 0) {
                setLeader(_membersList.get(0));
            }
        }

        return _membersList.size();
    }

    public int getNumOfMembers() {
        return _membersList.size();
    }

    public boolean isRemoveGroup() {
        return _isRemoveGroup;
    }

    public void setRemoveGroup(boolean flag) {
        _isRemoveGroup = flag;
    }

    public L1NpcInstance getIndexMember(int i) {
        return _membersList.get(i);
    }
}
