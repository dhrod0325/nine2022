package ks.util;

import ks.model.L1Character;
import ks.model.L1Location;
import ks.model.L1Teleport;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.util.common.random.RandomUtils;

public class L1TeleportUtils {
    public static void randomTeleport(L1PcInstance character) {
        L1Location newLocation = character.getLocation().randomLocation(200, true);
        int newX = newLocation.getX();
        int newY = newLocation.getY();
        short mapId = (short) newLocation.getMapId();
        L1Teleport.teleport(character, newX, newY, mapId, character.getHeading(), true);
    }

    public static boolean teleportToTargetFront(L1Character cha, L1Character target, int distance) {
        int locX = target.getX();
        int locY = target.getY();

        int heading = target.getHeading();
        L1Map map = target.getMap();
        short mapId = target.getMapId();

        switch (heading) {
            case 1:
                locX += distance;
                locY -= distance;
                break;
            case 2:
                locX += distance;
                break;
            case 3:
                locX += distance;
                locY += distance;
                break;
            case 4:
                locY += distance;
                break;
            case 5:
                locX -= distance;
                locY += distance;
                break;
            case 6:
                locX -= distance;
                break;
            case 7:
                locX -= distance;
                locY -= distance;
                break;
            case 0:
                locY -= distance;
                break;
            default:
                break;
        }

        if (map.isPassable(locX, locY)) {
            if (cha instanceof L1PcInstance) {
                L1Teleport.teleport((L1PcInstance) cha, locX, locY, mapId, cha.getHeading(), true);
                return true;
            }
        }

        return false;
    }

    public static void teleportToGiran(L1PcInstance pc) {
        int ran = RandomUtils.nextInt(5);

        L1Location[] locations = new L1Location[]{
                new L1Location(33439, 32801, (short) 4),
                new L1Location(33439, 32796, (short) 4),
                new L1Location(33444, 32805, (short) 4),
                new L1Location(33431, 32822, (short) 4),
                new L1Location(33439, 32814, (short) 4)
        };

        L1Location loc = locations[ran];
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) 4, pc.getHeading(), true);
    }

    public static void teleportToSilverTown(L1PcInstance pc) {
        int ran = RandomUtils.nextInt(4);

        L1Location[] locations = new L1Location[]{
                new L1Location(33080, 33392, (short) 4),
                new L1Location(33071, 33402, (short) 4),
                new L1Location(33072, 33392, (short) 4),
                new L1Location(33097, 33366, (short) 4)
        };

        L1Location loc = locations[ran];
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) 4, pc.getHeading(), true);
    }

    public static boolean omanAmuletTeleportAble(L1PcInstance pc) {
        if (pc.getMapId() >= 101 && pc.getMapId() <= 110) {
            int checkItem = -1;

            switch (pc.getMapId()) {
                case 101:
                    checkItem = 60001215;
                    break;
                case 102:
                    checkItem = 60001216;
                    break;
                case 103:
                    checkItem = 60001217;
                    break;
                case 104:
                    checkItem = 60001218;
                    break;
                case 105:
                    checkItem = 60001219;
                    break;
                case 106:
                    checkItem = 60001220;
                    break;
                case 107:
                    checkItem = 60001221;
                    break;
                case 108:
                    checkItem = 60001222;
                    break;
                case 109:
                    checkItem = 60001223;
                    break;
                case 110:
                    checkItem = 60001224;
                    break;
            }

            if (checkItem != -1) {
                return pc.getInventory().checkItem(checkItem);
            }
        }

        return false;
    }
}
