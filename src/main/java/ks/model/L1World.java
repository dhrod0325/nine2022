package ks.model;

import ks.app.LineageAppContext;
import ks.app.aop.LogTime;
import ks.app.config.prop.CodeConfig;
import ks.app.config.prop.ServerConfig;
import ks.constants.L1PacketBoxType;
import ks.model.instance.*;
import ks.model.pc.L1PcInstance;
import ks.model.types.Point;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_SystemMessage;
import ks.packets.serverpackets.ServerBasePacket;
import ks.system.robot.is.L1RobotInstance;
import ks.system.userShop.L1UserShopNpcInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class L1World {
    private static final Logger logger = LogManager.getLogger();

    private static final int MAX_MAP_ID = 25088;

    private final Map<Integer, L1Object> allObjects = new ConcurrentHashMap<>();
    private final Map<String, L1PcInstance> pcAndRobotPlayers = new ConcurrentHashMap<>();
    private final Map<Integer, L1MonsterInstance> allMonsters = new ConcurrentHashMap<>();
    private final Map<String, L1RobotInstance> robotPlayers = new ConcurrentHashMap<>();
    private final Map<String, L1PcInstance> allPlayers = new ConcurrentHashMap<>();
    private final Map<Integer, L1SummonInstance> allSummons = new ConcurrentHashMap<>();
    private final Map<Short, Map<Integer, L1Object>> visibleObjects = new ConcurrentHashMap<>();
    private final Map<Integer, L1ItemInstance> allItem = new ConcurrentHashMap<>();
    private final Map<String, L1NpcShopInstance> allNpcShop = new ConcurrentHashMap<>();
    private final Map<String, L1Clan> allClans = new ConcurrentHashMap<>();
    private final Map<Integer, L1War> allWars = new ConcurrentHashMap<>();
    private final Map<String, L1PcInstance> gmPlayers = new ConcurrentHashMap<>();
    private final Map<Integer, L1DoorInstance> doors = new ConcurrentHashMap<>();

    private final Map<String, L1UserShopNpcInstance> allAutoNpcShops = new ConcurrentHashMap<>();

    private int weather = 4;
    private boolean worldChatEnable = true;

    public static L1World getInstance() {
        return LineageAppContext.getBean(L1World.class);
    }

    @LogTime
    public void load() {
        Stream.iterate(0, n -> n + 1)
                .limit(MAX_MAP_ID)
                .forEach(integer -> visibleObjects.put(Short.parseShort(integer.toString()), new ConcurrentHashMap<>()));
    }

    public void storeObject(Collection<?> list) {
        list.forEach((o) -> {
            if (o instanceof L1Object) {
                L1Object obj = (L1Object) o;
                storeObject(obj);
            }
        });
    }

    public void storeObject(L1Object object) {
        if (object == null) {
            throw new NullPointerException();
        }

        synchronized (this) {
            allObjects.put(object.getId(), object);

            if (object instanceof L1RobotInstance) {
                String name = ((L1RobotInstance) object).getName().toUpperCase();
                robotPlayers.put(name, (L1RobotInstance) object);
                pcAndRobotPlayers.put(name, (L1RobotInstance) object);
            } else if (object instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) object;
                String name = pc.getName().toUpperCase();

                allPlayers.put(name, pc);
                pcAndRobotPlayers.put(name, pc);

                if (pc.isGm()) {
                    gmPlayers.put(name, pc);
                }
            } else if (object instanceof L1UserShopNpcInstance) {
                allAutoNpcShops.put(((L1UserShopNpcInstance) object).getName(), (L1UserShopNpcInstance) object);
            } else if (object instanceof L1DoorInstance) {
                doors.put(((L1DoorInstance) object).getDoorId(), (L1DoorInstance) object);
            } else if (object instanceof L1NpcShopInstance) {
                allNpcShop.put(((L1NpcShopInstance) object).getName(), (L1NpcShopInstance) object);
            } else if (object instanceof L1ItemInstance) {
                allItem.put(object.getId(), (L1ItemInstance) object);
            } else if (object instanceof L1SummonInstance) {
                allSummons.put(object.getId(), (L1SummonInstance) object);
            } else if (object instanceof L1MonsterInstance) {
                L1MonsterInstance mi = (L1MonsterInstance) object;
                allMonsters.put(object.getId(), mi);
            }
        }
    }

    public void removeObject(L1Object object) {
        if (object == null) {
            throw new NullPointerException();
        }

        allObjects.remove(object.getId());

        if (object instanceof L1RobotInstance) {
            String name = ((L1RobotInstance) object).getName().toUpperCase();
            robotPlayers.remove(name);
            pcAndRobotPlayers.remove(name);
        } else if (object instanceof L1PcInstance) {
            String name = ((L1PcInstance) object).getName().toUpperCase();

            allPlayers.remove(name);
            pcAndRobotPlayers.remove(name);
            gmPlayers.remove(name);
        } else if (object instanceof L1UserShopNpcInstance) {
            String name = ((L1UserShopNpcInstance) object).getName().toUpperCase();
            allAutoNpcShops.remove(name);
        } else if (object instanceof L1DoorInstance) {
            doors.remove(((L1DoorInstance) object).getDoorId());
        } else if (object instanceof L1NpcShopInstance) {
            allNpcShop.remove(((L1NpcShopInstance) object).getName());
        } else if (object instanceof L1ItemInstance) {
            allItem.remove(object.getId());
        } else if (object instanceof L1SummonInstance) {
            allSummons.remove(object.getId());
        } else if (object instanceof L1MonsterInstance) {
            allMonsters.remove(object.getId());
        }
    }

    public L1Object findObject(int oID) {
        return allObjects.get(oID);
    }

    public L1NpcInstance findNpcBySpawnId(int spawnId) {
        return (L1NpcInstance) allObjects.values()
                .stream()
                .filter((o) -> {
                    if (o instanceof L1NpcInstance) {
                        L1NpcInstance npc = (L1NpcInstance) o;

                        if (npc.getSpawn() != null) {
                            return npc.getSpawn().getId() == spawnId;
                        }
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    public L1GroundInventory getInventory(int x, int y, short map) {
        int inventoryKey = ((x - 30000) * 10000 + (y - 30000)) * -1;

        Object object = visibleObjects.get(map).get(inventoryKey);

        if (object == null) {
            return new L1GroundInventory(inventoryKey, x, y, map);
        } else {
            return (L1GroundInventory) object;
        }
    }

    public L1DoorInstance findDoor(int doorId) {
        return doors.get(doorId);
    }

    public L1GroundInventory getInventory(L1Location loc) {
        return getInventory(loc.getX(), loc.getY(), (short) loc.getMap().getId());
    }

    public void addVisibleObject(L1Object object) {
        if (object == null)
            return;

        if (object.getMapId() <= MAX_MAP_ID) {
            visibleObjects.get(object.getMapId()).put(object.getId(), object);
        }
    }

    public void removeVisibleObject(L1Object object) {
        if (object == null)
            return;

        if (object.getMapId() <= MAX_MAP_ID) {
            visibleObjects.get(object.getMapId()).remove(object.getId());
        }
    }

    public void moveVisibleObject(L1Object object, int newMap) {
        if (object == null)
            return;

        if (object.getMapId() != newMap) {
            if (object.getMapId() <= MAX_MAP_ID) {
                visibleObjects.get(object.getMapId()).remove(object.getId());
            }

            if (newMap <= MAX_MAP_ID) {
                visibleObjects.get((short) newMap).put(object.getId(), object);
            }
        }
    }

    public List<L1Object> getVisibleBoxObjects(L1Object object, int heading, int width, int height) {
        int x = object.getX();
        int y = object.getY();
        int map = object.getMapId();

        L1Location location = object.getLocation();

        int[] headingRotate = {6, 7, 0, 1, 2, 3, 4, 5};

        double cosSita = Math.cos(headingRotate[heading] * Math.PI / 4);
        double sinSita = Math.sin(headingRotate[heading] * Math.PI / 4);

        if (map <= MAX_MAP_ID) {
            return getVisibleObjects(map)
                    .stream()
                    .filter((element) -> {
                        if (element == null || element.equals(object)) {
                            return false;
                        }
                        if (map != element.getMapId()) {
                            return false;
                        }
                        if (location.isSamePoint(element.getLocation())) {
                            return true;
                        }

                        int distance = location.getTileLineDistance(element.getLocation());

                        if (distance > height && distance > width) {
                            return false;
                        }

                        int x1 = element.getX() - x;
                        int y1 = element.getY() - y;

                        int rotX = (int) Math.round(x1 * cosSita + y1 * sinSita);
                        int rotY = (int) Math.round(-x1 * sinSita + y1 * cosSita);

                        int xMin = 0;
                        int yMin = -width;

                        return rotX > xMin && distance <= height && rotY >= yMin && rotY <= width;
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public List<L1MonsterInstance> getVisibleMonsters(L1Object object, int radius) {
        return getVisibleObjects(object, radius)
                .stream()
                .filter(L1MonsterInstance.class::isInstance)
                .map(L1MonsterInstance.class::cast)
                .collect(Collectors.toList());
    }

    public List<L1Object> getVisibleObjects(L1Object object) {
        return getVisibleObjects(object, -1);
    }

    public List<L1Object> getVisibleObjects(L1Object object, int radius) {
        short mapId = object.getMapId();
        Point pt = object.getLocation();

        if (mapId <= MAX_MAP_ID) {
            return visibleObjects.get(mapId)
                    .values()
                    .stream()
                    .filter((pc) -> {
                        if (pc.equals(object)) {
                            return false;
                        }
                        if (mapId != pc.getMapId()) {
                            return false;
                        }

                        if (radius == -1) {
                            return pt.isInScreen(pc.getLocation());
                        } else if (radius == 0) {
                            return pt.isSamePoint(pc.getLocation());
                        } else {
                            return pt.getTileLineDistance(pc.getLocation()) <= radius;
                        }
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public List<L1PcInstance> getVisiblePlayer(L1Object object) {
        return getVisiblePlayer(object, -1);
    }

    public List<L1PcInstance> getVisiblePlayer(L1Object object, int radius) {
        int map = object.getMapId();
        Point pt = object.getLocation();

        Collection<L1PcInstance> players = pcAndRobotPlayers.values();

        return players.stream().filter((pc) -> {
            if (pc == null || pc.equals(object) || map != pc.getMapId()) {
                return false;
            }
            if (radius == -1) {
                return pt.isInScreen(pc.getLocation());
            } else if (radius == 0) {
                return pt.isSamePoint(pc.getLocation());
            } else {
                return pt.getTileLineDistance(pc.getLocation()) <= radius;
            }
        }).collect(Collectors.toList());
    }

    public List<L1PcInstance> getVisiblePlayerExceptTargetSight(L1Object object, L1Object target) {
        int mapId = object.getMapId();
        Point objectPt = object.getLocation();
        Point targetPt = target.getLocation();

        return visibleObjects
                .get((short) mapId)
                .values()
                .stream()
                .filter(L1PcInstance.class::isInstance)
                .map(L1PcInstance.class::cast)
                .filter(pc -> !pc.equals(object))
                .filter(pc -> {
                    if (CodeConfig.PC_RECOGNIZE_RANGE == -1) {
                        return (objectPt.isInScreen(pc.getLocation())) && (!targetPt.isInScreen(pc.getLocation()));
                    } else {
                        return (objectPt.getTileLineDistance(pc.getLocation()) <= CodeConfig.PC_RECOGNIZE_RANGE)
                                && (targetPt.getTileLineDistance(pc.getLocation()) > CodeConfig.PC_RECOGNIZE_RANGE);
                    }
                })
                .collect(Collectors.toList());

    }

    public L1PcInstance getPlayer(String name) {
        return pcAndRobotPlayers
                .values()
                .stream()
                .filter(pc -> pc.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public L1PcInstance getPlayer(int x, int y, short mapId) {
        return pcAndRobotPlayers
                .values()
                .stream()
                .filter(pc -> pc.getX() == x && pc.getY() == y && pc.getMapId() == mapId)
                .findFirst()
                .orElse(null);
    }

    public L1PcInstance getPlayer(int charId) {
        return pcAndRobotPlayers.values().stream().filter((p) -> p.getId() == charId).findFirst().orElse(null);
    }

    public L1NpcInstance getNpc(int npcId, int spawnId) {
        return getAllNpc().stream().filter((ni) -> ni.getNpcId() == npcId && ni.getSpawn().getId() == spawnId).findFirst().orElse(null);
    }

    public boolean getNpcShop(String name) {
        return getAllNpcShop().stream().allMatch(each -> each.getName().equalsIgnoreCase(name));
    }

    public final Collection<L1Object> getVisibleObjects(int mapId) {
        Map<Integer, L1Object> map = visibleObjects.get((short) mapId);

        if (map == null) {
            return Collections.emptyList();
        }

        return map.values();
    }

    public void addWar(L1War war) {
        allWars.put(war.getCastleId(), war);
    }

    public void removeWar(L1War war) {
        allWars.remove(war.getCastleId());
    }

    public Collection<L1War> getWarList() {
        return Collections.unmodifiableCollection(allWars.values()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void storeClan(L1Clan clan) {
        removeClan(clan);

        allClans.put(clan.getClanName(), clan);
    }

    public void removeClan(L1Clan clan) {
        allClans.remove(clan.getClanName());
    }

    public L1Clan getClan(String clanName) {
        if (clanName == null)
            return null;

        return allClans.get(clanName);
    }

    public L1Clan getClan(int clanId) {
        return allClans.values()
                .stream()
                .filter((clan) -> clan.getClanId() == clanId)
                .findFirst()
                .orElse(null);
    }


    public Collection<L1UserShopNpcInstance> getAllAutoNpcShops() {
        return Collections.unmodifiableCollection(allAutoNpcShops.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1Clan> getAllClans() {
        return Collections.unmodifiableCollection(allClans.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public boolean isWorldChatEnable() {
        return worldChatEnable;
    }

    public void setWorldChatEnable(boolean flag) {
        worldChatEnable = flag;
    }

    /**
     * 월드상에 존재하는 모든 플레이어에 패킷을 송신한다.
     *
     * @param packet 송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
     */
    public void broadcastPacketToAll(ServerBasePacket packet) {
        getAllPlayers().forEach(pc -> pc.sendPackets(packet, false));
        packet.close();
    }

    public void broadcastServerMessage(String message) {
        broadcastPacketToAll(new S_SystemMessage(message));
    }

    public void broadcastPacketGreenMessage(String msg) {
        broadcastPacketToAll(new S_PacketBox(L1PacketBoxType.GREEN_MESSAGE, "[" + ServerConfig.SERVER_NAME + "] " + msg));
    }

    public L1RobotInstance getRobot(String s) {
        return getRobotPlayers()
                .stream()
                .filter((robot) -> robot.getName().equalsIgnoreCase(s))
                .findFirst()
                .orElse(null);
    }

    /**
     * object를 인식할 수 있는 범위에 있는 플레이어를 취득한다
     */
    public List<L1PcInstance> getRecognizePlayer(L1Object object) {
        return getVisiblePlayer(object, CodeConfig.PC_RECOGNIZE_RANGE)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1PcInstance> getAllPlayers() {
        return Collections.unmodifiableCollection(allPlayers.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1PcInstance> getAllPlayersByMap(int mapId) {
        return getAllPlayers().stream()
                .filter(Objects::nonNull)
                .filter(pc -> pc.getMapId() == mapId)
                .collect(Collectors.toList());
    }

    public Collection<L1RobotInstance> getRobotPlayers() {
        return Collections.unmodifiableCollection(robotPlayers.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1Object> getAllObject() {
        return Collections.unmodifiableCollection(allObjects.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1MonsterInstance> getAllMonsters() {
        return Collections.unmodifiableCollection(allMonsters.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1NpcShopInstance> getAllNpcShop() {
        return Collections.unmodifiableCollection(allNpcShop.values())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Collection<L1NpcInstance> getAllNpc() {
        Collection<L1NpcInstance> list = new ArrayList<>();

        getAllObject().forEach((o) -> {
            if (o instanceof L1NpcInstance) {
                list.add((L1NpcInstance) o);
            }
        });

        return list;
    }

    public Collection<L1SummonInstance> getAllSummons() {
        return Collections.unmodifiableCollection(allSummons.values()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Collection<L1ItemInstance> getAllItem() {
        return Collections.unmodifiableCollection(allItem.values()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Collection<L1PcInstance> getGmPlayers() {
        return gmPlayers.values();
    }

    public Map<Integer, L1DoorInstance> getDoors() {
        return doors;
    }
}
