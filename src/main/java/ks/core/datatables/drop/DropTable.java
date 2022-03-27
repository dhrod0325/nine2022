package ks.core.datatables.drop;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.constants.L1DataMapKey;
import ks.constants.L1ItemId;
import ks.model.*;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_Sound;
import ks.packets.serverpackets.S_SystemMessage;
import ks.system.huntCheck.HuntCheckDao;
import ks.system.huntCheck.vo.HuntCheck;
import ks.system.huntCheck.vo.HuntCheckItem;
import ks.system.robot.L1RobotType;
import ks.system.robot.is.L1RobotInstance;
import ks.util.L1CommonUtils;
import ks.util.common.SqlUtils;
import ks.util.common.random.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DropTable {
    private final Map<Integer, List<L1Drop>> droplists = new HashMap<>();

    public static DropTable getInstance() {
        return LineageAppContext.getBean(DropTable.class);
    }

    public Map<Integer, List<L1Drop>> getDropList() {
        return droplists;
    }

    public List<L1Drop> findDropList(int npcId) {
        return droplists.getOrDefault(npcId, new ArrayList<>());
    }

    @LogTime
    public void load() {
        droplists.clear();

        List<L1Drop> list = selectList();

        for (L1Drop drop : list) {
            List<L1Drop> dropList = droplists.getOrDefault(drop.getMobId(), new ArrayList<>());
            dropList.add(drop);

            droplists.put(drop.getMobId(), dropList);
        }
    }

    public List<L1Drop> selectList() {
        return SqlUtils.query("select * from droplist", (rs, i) -> {
            int mobId = rs.getInt("mobId");
            int itemId = rs.getInt("itemId");
            int min = rs.getInt("min");
            int max = rs.getInt("max");
            int chance = rs.getInt("chance");

            L1Drop drop = new L1Drop(mobId, itemId, min, max, chance);
            drop.setItemName(rs.getString("itemname"));

            return drop;
        });
    }

    public void dropShare(L1PcInstance pc, L1MonsterInstance npc) {
        if (npc == null || pc == null)
            return;

        HuntCheck huntCheck = huntCheck(pc, npc);

        L1HateList hate = npc.getHateList();
        L1Inventory inventory = npc.getInventory();

        if (inventory.isEmpty()) {
            return;
        }

        Map<L1Character, Integer> dropHateMap = hate.getDropHateMap(npc);
        int totalHate = hate.calcHate(dropHateMap);

        if (npc.isBoss()) {
            if (!npc.isRiper()) {
                for (L1Character cha : dropHateMap.keySet()) {
                    int hateCount = dropHateMap.get(cha);

                    if (cha instanceof L1PcInstance) {
                        if (hateCount <= 0) {
                            continue;
                        }

                        L1PcInstance attacker = (L1PcInstance) cha;

                        boolean isInScreen = attacker.getLocation().isInScreen(npc.getLocation());

                        if (npc.getLevel() >= 65 && isInScreen && attacker.getLevel() >= CodeConfig.BOSS_HANIP) {
                            attacker.getInventory().storeItem(6000088, 1);
                            attacker.sendPackets("보스한입상자를 획득하였습니다");
                        }
                    }
                }
            }
        }

        List<L1ItemInstance> items = inventory.getItems();

        boolean serverGahoCheck = false;

        for (L1ItemInstance item : items) {
            int itemId = item.getItemId();

            if (npc.getNpcId() == 45915 || npc.getNpcId() == 45914) {
                if (itemId == 41327) {
                    int cnt = 0;
                    for (Integer h : hate.toHateList()) {
                        if (h > 0) {
                            cnt++;
                        }
                    }

                    if (cnt > 1) {
                        continue;
                    }
                }
            }

            if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
                item.setNowLighting(false);
            }

            if (pc instanceof L1RobotInstance) {
                dropSharePrivate(npc, item, pc, totalHate, dropHateMap);
            } else if (npc.isBoss()) {
                dropShareBoss(npc, item, hate);
            } else if (CodeConfig.CLASSIC_DROP_LIST().contains(itemId)) {
                dropShareClassic(npc, item, pc);
            } else if (pc.isInParty()) {
                List<L1PcInstance> members = pc.getParty().getVisiblePartyMembers(pc);

                boolean hasAllServerGaho = pc.getParty().hasAllServerGaho(members);

                for (L1PcInstance member : members) {
                    if (member.getLevel() >= CodeConfig.SERVER_GAHO_LEVEL) {
                        if (!hasAllServerGaho) {
                            serverGahoCheck = true;
                            item.setCount(item.getCount() / 2);
                        }
                    }
                }

                dropShareParty(npc, item, pc);
            } else {
                if (pc.getLevel() >= CodeConfig.SERVER_GAHO_LEVEL) {
                    if (!pc.getInventory().checkItem(L1ItemId.SERVER_GAHO, 1)) {
                        serverGahoCheck = true;
                        item.setCount(item.getCount() / 2);
                    }
                }

                dropSharePrivate(npc, item, pc, totalHate, dropHateMap);
            }

            huntCheckItem(pc, huntCheck, item);
        }

        if (serverGahoCheck) {
            String msg = String.format("%d레벨 이상은 가호를 소지하지않으면 아데나,아이템 드랍 패널티가 적용됩니다", CodeConfig.SERVER_GAHO_LEVEL);

            if (pc.isInParty()) {
                List<L1PcInstance> members = pc.getParty().getVisiblePartyMembers(pc);

                for (L1PcInstance member : members) {
                    member.sendPackets(msg);
                }
            } else {
                pc.sendPackets(msg);
            }
        }
    }

    private void dropSharePrivate(L1MonsterInstance npc, L1ItemInstance item, L1PcInstance pc, int totalHate, Map<L1Character, Integer> dropHateMap) {
        int itemId = item.getItemId();
        L1Inventory inventory = npc.getInventory();

        boolean isRobot = pc instanceof L1RobotInstance;

        if (itemId == L1ItemId.ADENA) {
            if (pc.isEquipServerRune() || isRobot) {
                item.setCount(item.getCount() * 2);
            }
        }

        if (totalHate > 0 && !isRobot) {
            int randomInt = RandomUtils.nextInt(totalHate);
            int chanceHate = 0;

            for (L1Character character : dropHateMap.keySet()) {
                chanceHate += dropHateMap.get(character);

                if (chanceHate > randomInt) {
                    if (character instanceof L1SummonInstance) {
                        L1SummonInstance o = (L1SummonInstance) character;
                        character = o.getMaster();
                    }

                    if (character instanceof L1PetInstance) {
                        L1PetInstance o = (L1PetInstance) character;
                        character = o.getMaster();
                    }

                    if (character instanceof L1PcInstance) {
                        L1PcInstance player = (L1PcInstance) character;

                        if (player.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                            settingDropItem(player, npc, item, inventory);
                        } else {
                            L1Inventory targetInventory = groundInventory(item, player, npc);
                            inventory.tradeItem(item, item.getCount(), targetInventory);
                        }
                    }

                    break;
                }
            }
        } else {
            settingDropItem(pc, npc, item, inventory);
        }
    }

    private void settingDropItem(L1PcInstance pc, L1MonsterInstance npc, L1ItemInstance item, L1Inventory inventory) {
        boolean autoLoot;

        if (item.getItemId() == L1ItemId.ADENA) {
            autoLoot = CodeConfig.AUTO_LOOT_ADENA;
        } else {
            autoLoot = CodeConfig.AUTO_LOOT_ITEM;
        }

        if (pc instanceof L1RobotInstance) {
            autoLoot = true;
            pc.setEquipServerRune(true);
        }

        if (pc.isGm() && !"false".equals(pc.getDataMap().get(L1DataMapKey.AUTO_LOOT))) {
            autoLoot = true;
        }

        L1Inventory targetInventory;

        if (autoLoot) {
            if (pc.isEquipServerRune()) {
                L1ItemInstance adena = pc.getInventory().findItemId(L1ItemId.ADENA);

                if (adena != null && adena.getCount() > 2000000000) {
                    targetInventory = groundInventory(item, pc, npc);
                    inventory.tradeItem(item, item.getCount(), targetInventory);
                    pc.sendPackets(new S_ServerMessage(166, "소지하고 있는 아데나", "2,000,000,000을 초과하고 있습니다."));
                } else {
                    targetInventory = pcInventory(item, pc, npc);
                }
            } else {
                targetInventory = groundInventory(item, pc, npc);
            }
        } else {
            targetInventory = groundInventory(item, pc, npc);
        }

        inventory.tradeItem(item, item.getCount(), targetInventory);
    }

    private void dropShareParty(L1MonsterInstance npc, L1ItemInstance item, L1PcInstance pc) {
        int itemId = item.getItemId();
        L1Inventory inventory = npc.getInventory();

        List<L1PcInstance> members = pc.getParty().getVisiblePartyMembers(pc);
        boolean hasAllServerRune = pc.getParty().hasAllServerRune(members);

        boolean autoLoot;

        if (item.getItemId() == L1ItemId.ADENA) {
            if (hasAllServerRune) {
                item.setCount(item.getCount() * 2);
            }
        }

        if (itemId == L1ItemId.ADENA) {
            autoLoot = CodeConfig.AUTO_LOOT_ADENA;
        } else {
            autoLoot = CodeConfig.AUTO_LOOT_ITEM;
        }

        if (!hasAllServerRune) {
            autoLoot = false;
        }

        if (pc instanceof L1RobotInstance) {
            autoLoot = true;
        }

        if (autoLoot) {
            if (itemId == L1ItemId.ADENA) {
                int adenCount = item.getCount() / members.size();

                for (L1PcInstance member : members) {
                    if (member.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                        L1Inventory v = member.getInventory();
                        inventory.tradeItem(item, adenCount, v);

                        if (member.getMent().isDrop()) {
                            member.sendPackets(new S_SystemMessage(npc.getName() + " -> " + item.getNumberedName(adenCount) + " [ " + member.getName() + " ]" + " 획득"));
                        }
                    } else {
                        L1Inventory v = groundInventory(item, member, npc);
                        inventory.tradeItem(item, adenCount, v);
                    }
                }
            } else {
                L1PcInstance luckyMember = members.get(RandomUtils.nextInt(members.size()));

                if (luckyMember.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                    L1Inventory targetInventory = luckyMember.getInventory();
                    inventory.tradeItem(item, item.getCount(), targetInventory);

                    for (L1PcInstance member : members) {
                        if (member.getMent().isDrop()) {
                            member.sendPackets(new S_SystemMessage(npc.getName() + " -> " + item.getLogName() + " [ " + luckyMember.getName() + " ]" + " 획득"));
                        }
                    }
                } else {
                    L1Inventory targetInventory = groundInventory(item, luckyMember, npc);
                    inventory.tradeItem(item, item.getCount(), targetInventory);
                }
            }
        } else {
            L1Inventory targetInventory = groundInventory(item, pc, npc);
            inventory.tradeItem(item, item.getCount(), targetInventory);
        }
    }

    private void dropShareBoss(L1NpcInstance npc, L1ItemInstance item, L1HateList hate) {
        L1Inventory inventory = npc.getInventory();
        Map<L1Character, Integer> bossHateMap = hate.getBossHateMap(npc);
        L1Character owner = L1CommonUtils.maxHateCharacter(bossHateMap);
        inventory.tradeItem(item, item.getCount(), groundInventory(item, owner, npc));
    }

    private void dropShareClassic(L1NpcInstance npc, L1ItemInstance item, L1PcInstance pc) {
        L1Inventory inventory = npc.getInventory();
        pc.sendPackets(new S_Sound(CodeConfig.CLASSIC_BOX_SOUND));
        Broadcaster.broadcastPacket(pc, new S_Sound(CodeConfig.CLASSIC_BOX_SOUND));
        inventory.tradeItem(item, item.getCount(), groundInventory(item, pc, npc));
    }

    public HuntCheck huntCheck(L1PcInstance pc, L1MonsterInstance npc) {
        HuntCheck huntCheck = new HuntCheck();
        huntCheck.setCharName(pc.getName());
        huntCheck.setWeaponId(pc.getWeaponInfo().getWeaponId());
        huntCheck.setWeaponEnchant(pc.getWeaponInfo().getWeaponEnchant());
        huntCheck.setAc(pc.getAC().getAc());
        huntCheck.setMr(pc.getResistance().getMr());
        huntCheck.setLocX(pc.getX());
        huntCheck.setLocY(pc.getY());
        huntCheck.setMapId(pc.getMapId());
        huntCheck.setMobId(npc.getNpcId());
        huntCheck.setRegDate(new Date());
        huntCheck.setExp(npc.getExp());
        huntCheck.setCharId(pc.getId());

        if (pc instanceof L1RobotInstance) {
            if (((L1RobotInstance) pc).getRobotType() == L1RobotType.HUNT) {
                HuntCheckDao.getInstance().insertHunt(huntCheck);
            }
        }

        return huntCheck;
    }

    public void huntCheckItem(L1PcInstance pc, HuntCheck huntCheck, L1ItemInstance item) {
        HuntCheckItem huntCheckItem = new HuntCheckItem();
        huntCheckItem.setHuntId(huntCheck.getId());
        huntCheckItem.setItemId(item.getItemId());
        huntCheckItem.setCount(item.getCount());

        if (pc instanceof L1RobotInstance) {
            if (((L1RobotInstance) pc).getRobotType() == L1RobotType.HUNT) {
                HuntCheckDao.getInstance().insertHuntItem(huntCheckItem);
            }
        } else {
            huntCheck.getHuntCheckItemList().add(huntCheckItem);
        }
    }

    public L1Inventory pcInventory(L1ItemInstance item, L1PcInstance owner, L1NpcInstance npc) {
        if (owner.getMent().isDrop()) {
            owner.sendPackets(new S_SystemMessage(npc.getName() + " -> " + item.getLogName()));
        }

        return owner.getInventory();
    }

    public L1Inventory groundInventory(L1ItemInstance item, L1Character owner, L1NpcInstance npc) {
        L1Location randomDropLocation = randomDropLocation(npc);

        int randomX = randomDropLocation.getX();
        int randomY = randomDropLocation.getY();

        L1Inventory v = L1World.getInstance().getInventory(randomX, randomY, npc.getMapId());

        item.setDropMobId(npc.getNpcId());

        if (owner instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) owner;
            item.startItemOwnerTimer(pc);

            if (item.getItem().getDropSound() > 0) {
                Broadcaster.broadcastPacket(npc, new S_Sound(item.getItem().getDropSound()));
            }
        }

        return v;
    }

    public L1Location randomDropLocation(L1NpcInstance npc) {
        List<Integer> dirList = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));

        Collections.shuffle(dirList);

        int x;
        int y;
        int map = npc.getMapId();

        for (Integer dir : dirList) {
            x = CodeConfig.HEADING_TABLE_X[dir];
            y = CodeConfig.HEADING_TABLE_Y[dir];

            int x1 = npc.getX() + x;
            int y1 = npc.getY() + y;

            List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(npc);

            boolean passAble1 = npc.getMap().isPassable(x1, y1);
            boolean passAble2 = false;

            for (L1PcInstance pc : players) {
                if (pc.getLocation().equals(new L1Location(x1, y1, map))) {
                    passAble2 = true;
                    break;
                }
            }

            if (passAble1 || passAble2) {
                return new L1Location(x1, y1, map);
            }
        }

        return npc.getLocation();
    }
}
