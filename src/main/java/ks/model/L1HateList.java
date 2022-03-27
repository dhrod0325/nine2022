package ks.model;

import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class L1HateList {
    private static final Logger logger = LogManager.getLogger(L1HateList.class);

    private final Map<L1Character, Integer> hateMap = new ConcurrentHashMap<>();

    public void add(L1Character cha, int hate) {
        if (cha == null) {
            return;
        }

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            if (pc.isGmInvis()) {
                return;
            }
        }

        if (hateMap.containsKey(cha)) {
            hateMap.put(cha, get(cha) + hate);
        } else {
            hateMap.put(cha, hate);
        }
    }

    public int get(L1Character cha) {
        Integer result = hateMap.get(cha);
        return result == null ? 0 : result;
    }

    public boolean containsKey(L1Character cha) {
        return hateMap.containsKey(cha);
    }

    public void remove(L1Character cha) {
        if (cha != null)
            hateMap.remove(cha);
    }

    public void clear() {
        hateMap.clear();
    }

    public boolean isEmpty() {
        return hateMap.isEmpty();
    }

    public L1Character getMaxHateCharacter() {
        L1Character maxHateCha = null;
        int hate = Integer.MIN_VALUE;

        for (L1Character e : hateMap.keySet()) {
            int value = get(e);

            if (hate < value) {
                maxHateCha = e;
                hate = value;
            }
        }

        return maxHateCha;
    }

    public int getTotalHate() {
        int totalHate = 0;

        for (int hate : hateMap.values()) {
            totalHate += hate;
        }

        return totalHate;
    }

    public List<L1Character> toTargetList() {
        return new ArrayList<>(hateMap.keySet());
    }

    public List<Integer> toHateList() {
        return new ArrayList<>(hateMap.values());
    }

    public Map<L1Character, Integer> getBossHateMap(L1NpcInstance npc) {
        Map<L1Character, Integer> dropHateMap = getDropHateMap(npc);
        Map<L1Character, Integer> bossHateMap = new ConcurrentHashMap<>();

        for (L1Character character : dropHateMap.keySet()) {
            if (!(character instanceof L1PcInstance)) {
                continue;
            }

            L1PcInstance attackerPc = (L1PcInstance) character;

            int hateCount = dropHateMap.get(character);

            if (attackerPc.isInParty()) {
                L1PcInstance leader = attackerPc.getParty().getLeader();

                List<L1PcInstance> members = attackerPc.getParty().getVisiblePartyMembers(attackerPc);

                for (L1PcInstance member : members) {
                    if (member.equals(attackerPc)) {
                        Integer partyHate = bossHateMap.getOrDefault(leader, 0);
                        bossHateMap.put(leader, partyHate + hateCount);
                        break;
                    }
                }
            } else {
                bossHateMap.put(character, hateCount);
            }
        }

        return bossHateMap;
    }

    public Integer calcHate(Map<L1Character, Integer> hateData) {
        int totalHate = 0;

        for (Integer value : hateData.values()) {
            totalHate += value;
        }

        return totalHate;
    }

    public Map<L1Character, Integer> getDropHateMap(L1NpcInstance npc) {
        Map<L1Character, Integer> dropHateMap = new HashMap<>();

        for (L1Character character : toTargetList()) {
            if (character == null)
                continue;

            if (character.getMapId() != npc.getMapId())
                continue;

            if (character.getLocation().isInScreen(npc.getLocation())) {
                try {
                    dropHateMap.put(character, get(character));
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }

        return dropHateMap;
    }
}
