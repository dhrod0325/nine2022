package ks.util;

import ks.app.config.prop.CodeConfig;
import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Character;
import ks.model.L1Location;
import ks.model.instance.L1CastleGuardInstance;
import ks.model.instance.L1DollInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.model.types.Point;
import ks.packets.serverpackets.S_MoveCharPacket;
import ks.system.dogFight.L1DogFightInstance;

import java.util.LinkedList;

public class L1CharPosUtils {
    private static final int COURSE_RANGE = 15;

    private static final byte[] HEADING_TABLE_X = CodeConfig.HEADING_TABLE_X;
    private static final byte[] HEADING_TABLE_Y = CodeConfig.HEADING_TABLE_Y;

    public static int[] getFrontLoc(int x, int y, int heading) {
        int[] loc = new int[2];

        x += HEADING_TABLE_X[heading];
        y += HEADING_TABLE_Y[heading];

        loc[0] = x;
        loc[1] = y;

        return loc;
    }

    public static L1Location getFrontLocation(L1Character cha) {
        int x = cha.getX();
        int y = cha.getY();

        int mapId = cha.getMapId();
        int heading = cha.getHeading();

        x += HEADING_TABLE_X[heading];
        y += HEADING_TABLE_Y[heading];

        return getFrontLocation(x, y, mapId, heading);
    }

    public static L1Location getFrontLocation(int x, int y, int mapId, int heading) {
        x += HEADING_TABLE_X[heading];
        y += HEADING_TABLE_Y[heading];

        return new L1Location(x, y, mapId);
    }

    public static int targetDirection(L1Character cha, int tx, int ty) {
        float dis_x = Math.abs(cha.getX() - tx); // X방향의 타겟까지의 거리
        float dis_y = Math.abs(cha.getY() - ty); // Y방향의 타겟까지의 거리
        float dis = Math.max(dis_x, dis_y); // 타겟까지의 거리

        if (dis == 0) {
            return cha.getHeading();
        }

        int avg_x = (int) Math.floor((dis_x / dis) + 0.59f);
        int avg_y = (int) Math.floor((dis_y / dis) + 0.59f);

        int dir_x = 0;
        int dir_y = 0;

        if (cha.getX() < tx)
            dir_x = 1;
        if (cha.getX() > tx)
            dir_x = -1;

        if (cha.getY() < ty)
            dir_y = 1;

        if (cha.getY() > ty)
            dir_y = -1;

        if (avg_x == 0)
            dir_x = 0;
        if (avg_y == 0)
            dir_y = 0;

        if (dir_x == 1 && dir_y == -1)
            return 1; // 상
        if (dir_x == 1 && dir_y == 0)
            return 2; // 우상
        if (dir_x == 1)
            return 3; // 오른쪽
        if (dir_x == 0 && dir_y == 1)
            return 4; // 우하
        if (dir_x == -1 && dir_y == 1)
            return 5; // 하
        if (dir_x == -1 && dir_y == 0)
            return 6; // 좌하
        if (dir_x == -1)
            return 7; // 왼쪽
        if (dir_y == -1)
            return 0; // 좌상

        return cha.getHeading();
    }

    public static boolean glanceCheck(L1Character attacker, L1Character target) {
        if (attacker.isLongAttack() && target.isLongAttack()) {
            boolean check1 = L1CharPosUtils.glanceCheck(attacker, target.getX(), target.getY());
            boolean check2 = L1CharPosUtils.glanceCheck(target, attacker.getX(), attacker.getY());

            if (check1 || check2) {
                return true;
            }
        }

        return glanceCheck(attacker, target.getX(), target.getY());
    }

    public static boolean glanceCheck(L1Character cha, int tx, int ty) {
        L1Map map = cha.getMap();

        int chx = cha.getX();
        int chy = cha.getY();

        for (int i = 0; i < 15; i++) {
            int cx = Math.abs(chx - tx);
            int cy = Math.abs(chy - ty);

            if (cx <= 1 && cy <= 1)
                break;

            if (!map.isAttackAble(chx, chy)) {
                return false;
            }

            int dir = targetDirection(cha, tx, ty);

            boolean passAble = map.isArrowPassable(chx, chy, dir);

            if (!passAble) {
                return false;
            }

            if (chx < tx)
                chx++;
            else if (chx > tx)
                chx--;
            if (chy < ty)
                chy++;
            else if (chy > ty)
                chy--;
        }

        return true;
    }

    /**
     * 지정된 좌표에 공격 가능한가를 돌려준다.
     *
     * @param x     좌표의 X치.
     * @param y     좌표의 Y치.
     * @param range 공격 가능한 범위(타일수)
     * @return 공격 가능하면 true, 불가능하면 false
     */
    public static boolean isAttackPosition(L1Character cha, int x, int y, int range) {
        if (cha instanceof L1CastleGuardInstance) {
            L1CastleGuardInstance guard = (L1CastleGuardInstance) cha;

            if (guard.getNpcId() == 7000002 || guard.getNpcId() == 4707001) {
                return true;
            }
        }

        if (range >= 7) {
            if (cha.getLocation().getTileDistance(new Point(x, y)) > range) {
                return false;
            }

        } else {
            if (cha.getLocation().getTileLineDistance(new Point(x, y)) > range) {
                return false;
            }
        }

        return glanceCheck(cha, x, y);
    }

    public static boolean isAttackPosition(L1Character cha, L1Character target, int range) {
        if (cha instanceof L1CastleGuardInstance) {
            L1CastleGuardInstance guard = (L1CastleGuardInstance) cha;

            if (guard.getNpcId() == 7000002 || guard.getNpcId() == 4707001) {
                return true;
            }
        }

        int x = target.getX();
        int y = target.getY();

        if (range >= 7) {
            if (cha.getLocation().getTileDistance(new Point(x, y)) > range) {
                return false;
            }

        } else {
            if (cha.getLocation().getTileLineDistance(new Point(x, y)) > range) {
                return false;
            }
        }

        return glanceCheck(cha, target);
    }

    /**
     * 캐릭터가 존재하는 좌표가, 어느 존에 속하고 있을까를 돌려준다.
     *
     * @return 좌표의 존을 나타내는 값. 세이프티 존이면 1, 컴배트 존이면―1, 노멀 존이면 0.
     */
    private static int getZoneType(L1Character cha) {
        if (cha == null) {
            return 0;
        }

        if (cha.getMapId() == 2006) {
            return -1;
        }

        if (CodeConfig.getMapNormalChangeList().contains(cha.getMapId()) && !cha.getMap().isSafetyZone(cha.getLocation())) {
            return 0;
        }

        //펫매치 대련장
        if (cha.getMapId() == 5125) {
            return -1;
        }

        if (cha.getMap().isSafetyZone(cha.getLocation()))
            return 1;
        else if (cha.getMap().isCombatZone(cha.getLocation()))
            return -1;
        else
            return 0;
    }


    public static boolean isNormalZone(L1Character cha) {
        return getZoneType(cha) == 0;
    }

    public static boolean isCombatZone(L1Character cha) {
        return getZoneType(cha) == -1;
    }

    public static boolean isSafeZone(L1Character cha) {
        return getZoneType(cha) == 1;
    }

    public static int checkObject(int x, int y, short m, int dir) {
        L1Map map = L1WorldMap.getInstance().getMap(m);

        switch (dir) {
            case 1:
                if (map.isPassable(x, y, 1)) {
                    return 1;
                } else if (map.isPassable(x, y, 0)) {
                    return 0;
                } else if (map.isPassable(x, y, 2)) {
                    return 2;
                }
                break;
            case 2:
                if (map.isPassable(x, y, 2)) {
                    return 2;
                } else if (map.isPassable(x, y, 1)) {
                    return 1;
                } else if (map.isPassable(x, y, 3)) {
                    return 3;
                }
                break;
            case 3:
                if (map.isPassable(x, y, 3)) {
                    return 3;
                } else if (map.isPassable(x, y, 2)) {
                    return 2;
                } else if (map.isPassable(x, y, 4)) {
                    return 4;
                }
                break;
            case 4:
                if (map.isPassable(x, y, 4)) {
                    return 4;
                } else if (map.isPassable(x, y, 3)) {
                    return 3;
                } else if (map.isPassable(x, y, 5)) {
                    return 5;
                }
                break;
            case 5:
                if (map.isPassable(x, y, 5)) {
                    return 5;
                } else if (map.isPassable(x, y, 4)) {
                    return 4;
                } else if (map.isPassable(x, y, 6)) {
                    return 6;
                }
                break;
            case 6:
                if (map.isPassable(x, y, 6)) {
                    return 6;
                } else if (map.isPassable(x, y, 5)) {
                    return 5;
                } else if (map.isPassable(x, y, 7)) {
                    return 7;
                }
                break;
            case 7:
                if (map.isPassable(x, y, 7)) {
                    return 7;
                } else if (map.isPassable(x, y, 6)) {
                    return 6;
                } else if (map.isPassable(x, y, 0)) {
                    return 0;
                }
                break;
            case 0:
                if (map.isPassable(x, y, 0)) {
                    return 0;
                } else if (map.isPassable(x, y, 7)) {
                    return 7;
                } else if (map.isPassable(x, y, 1)) {
                    return 1;
                }
                break;
            default:
                break;
        }
        return -1;
    }

    public static void moveLocation(int[] pos, int dir) {
        switch (dir) {
            case 1:
                pos[0] = pos[0] + 1;
                pos[1] = pos[1] - 1;
                break;
            case 2:
                pos[0] = pos[0] + 1;
                break;
            case 3:
                pos[0] = pos[0] + 1;
                pos[1] = pos[1] + 1;
                break;
            case 4:
                pos[1] = pos[1] + 1;
                break;
            case 5:
                pos[0] = pos[0] - 1;
                pos[1] = pos[1] + 1;
                break;
            case 6:
                pos[0] = pos[0] - 1;
                break;
            case 7:
                pos[0] = pos[0] - 1;
                pos[1] = pos[1] - 1;
                break;
            case 0:
                pos[1] = pos[1] - 1;
                break;
            default:
                break;
        }
        pos[2] = dir;
    }

    public static void getFront(int[] pos, int dir) {
        switch (dir) {
            case 1:
                pos[4] = 2;
                pos[3] = 0;
                pos[2] = 1;
                pos[1] = 3;
                pos[0] = 7;
                break;
            case 2:
                pos[4] = 2;
                pos[3] = 4;
                pos[2] = 0;
                pos[1] = 1;
                pos[0] = 3;
                break;
            case 3:
                pos[4] = 2;
                pos[3] = 4;
                pos[2] = 1;
                pos[1] = 3;
                pos[0] = 5;
                break;
            case 4:
                pos[4] = 2;
                pos[3] = 4;
                pos[2] = 6;
                pos[1] = 3;
                pos[0] = 5;
                break;
            case 5:
                pos[4] = 4;
                pos[3] = 6;
                pos[2] = 3;
                pos[1] = 5;
                pos[0] = 7;
                break;
            case 6:
                pos[4] = 4;
                pos[3] = 6;
                pos[2] = 0;
                pos[1] = 5;
                pos[0] = 7;
                break;
            case 7:
                pos[4] = 6;
                pos[3] = 0;
                pos[2] = 1;
                pos[1] = 5;
                pos[0] = 7;
                break;
            case 0:
                pos[4] = 2;
                pos[3] = 6;
                pos[2] = 0;
                pos[1] = 1;
                pos[0] = 7;
                break;
            default:
                break;
        }
    }

    public static int searchCourse(L1Character character, int courseRange, int x, int y) {
        int i;

        int locCenter = courseRange + 1;
        int diff_x = x - locCenter;
        int diff_y = y - locCenter;

        int[] locBace = {character.getX() - diff_x, character.getY() - diff_y, 0, 0};
        int[] locNext = new int[4];
        int[] locCopy;

        int[] dirFront = new int[5];

        boolean[][] searchMap = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        LinkedList<int[]> queueSearch = new LinkedList<>();

        for (int j = courseRange * 2 + 1; j > 0; j--) {
            for (i = courseRange - Math.abs(locCenter - j); i >= 0; i--) {
                searchMap[j][locCenter + i] = true;
                searchMap[j][locCenter - i] = true;
            }
        }

        int[] firstCourse = {2, 4, 6, 0, 1, 3, 5, 7};

        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);

            moveLocation(locNext, firstCourse[i]);

            if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                return firstCourse[i];
            }

            if (searchMap[locNext[0]][locNext[1]]) {
                int tmpX = locNext[0] + diff_x;
                int tmpY = locNext[1] + diff_y;

                boolean found = false;

                switch (i) {
                    case 0:
                        found = character.getMap().isPassable(tmpX, tmpY + 1, i);
                        break;
                    case 1:
                        found = character.getMap().isPassable(tmpX - 1, tmpY + 1, i);
                        break;
                    case 2:
                        found = character.getMap().isPassable(tmpX - 1, tmpY, i);
                        break;
                    case 3:
                        found = character.getMap().isPassable(tmpX - 1, tmpY - 1, i);
                        break;
                    case 4:
                        found = character.getMap().isPassable(tmpX, tmpY - 1, i);
                        break;
                    case 5:
                        found = character.getMap().isPassable(tmpX + 1, tmpY - 1, i);
                        break;
                    case 6:
                        found = character.getMap().isPassable(tmpX + 1, tmpY, i);
                        break;
                    case 7:
                        found = character.getMap().isPassable(tmpX + 1, tmpY + 1, i);
                        break;
                    default:
                        break;
                }

                if (found) {
                    locCopy = new int[4];
                    System.arraycopy(locNext, 0, locCopy, 0, 4);
                    locCopy[2] = firstCourse[i];
                    locCopy[3] = firstCourse[i];

                    queueSearch.add(locCopy);
                }

                searchMap[locNext[0]][locNext[1]] = false;
            }
        }

        while (queueSearch.size() > 0) {
            locBace = queueSearch.removeFirst();
            getFront(dirFront, locBace[2]);

            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                moveLocation(locNext, dirFront[i]);
                if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                    return locNext[3];
                }
                if (searchMap[locNext[0]][locNext[1]]) {
                    int tmpX = locNext[0] + diff_x;
                    int tmpY = locNext[1] + diff_y;

                    boolean found = false;

                    switch (i) {
                        case 0:
                            found = character.getMap().isPassable(tmpX, tmpY + 1, i);
                            break;
                        case 1:
                            found = character.getMap().isPassable(tmpX - 1, tmpY + 1, i);
                            break;
                        case 2:
                            found = character.getMap().isPassable(tmpX - 1, tmpY, i);
                            break;
                        case 3:
                            found = character.getMap().isPassable(tmpX - 1, tmpY - 1, i);
                            break;
                        case 4:
                            found = character.getMap().isPassable(tmpX, tmpY - 1, i);
                            break;
                        default:
                            break;
                    }

                    if (found) {
                        locCopy = new int[4];
                        System.arraycopy(locNext, 0, locCopy, 0, 4);
                        locCopy[2] = dirFront[i];
                        queueSearch.add(locCopy);
                    }

                    searchMap[locNext[0]][locNext[1]] = false;
                }
            }
        }

        return -1;
    }

    public static void setDirectionMove(L1Character character, int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;

            switch (dir) {
                case 1:
                    nx = 1;
                    ny = -1;
                    character.setHeading(1);
                    break;
                case 2:
                    nx = 1;
                    ny = 0;
                    character.setHeading(2);
                    break;
                case 3:
                    nx = 1;
                    ny = 1;
                    character.setHeading(3);
                    break;
                case 4:
                    nx = 0;
                    ny = 1;
                    character.setHeading(4);
                    break;
                case 5:
                    nx = -1;
                    ny = 1;
                    character.setHeading(5);
                    break;
                case 6:
                    nx = -1;
                    ny = 0;
                    character.setHeading(6);
                    break;
                case 7:
                    nx = -1;
                    ny = -1;
                    character.setHeading(7);
                    break;
                case 0:
                    nx = 0;
                    ny = -1;
                    character.setHeading(0);
                    break;
                default:
                    break;
            }

            int nnx = character.getX() + nx;
            int nny = character.getY() + ny;

            boolean moveCheck = character instanceof L1DollInstance || character instanceof L1DogFightInstance;

            if (!moveCheck) {
                character.getMap().setPassable(character.getLocation(), true);
            }

            character.setX(nnx);
            character.setY(nny);

            Broadcaster.broadcastPacket(character, new S_MoveCharPacket(character));

            if (!moveCheck) {
                character.getMap().setPassable(character.getLocation(), false);
            }
        }
    }

    public static int calcMoveDirection(L1Character character, int x, int y) {
        return calcMoveDirection(character, x, y, character.getLocation().getLineDistance(new Point(x, y)));
    }

    public static int calcMoveDirection(L1Character character, int x, int y, double distance) {
        int dir;

        if (character.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DARKNESS) && distance >= 2D) {
            return -1;
        } else if (distance > 30D) {
            return -1;
        } else if (distance > COURSE_RANGE) {
            dir = targetDirection(character, x, y);
            dir = checkObject(character.getX(), character.getY(), character.getMapId(), dir);
        } else {
            dir = searchCourse(character, COURSE_RANGE, x, y);

            if (dir == -1) {
                dir = targetDirection(character, x, y);

                if (character instanceof L1NpcInstance) {
                    L1NpcInstance npc = (L1NpcInstance) character;

                    if (!npc.isExistCharacterBetweenTarget(dir)) {
                        dir = checkObject(character.getX(), character.getY(), character.getMapId(), dir);
                    }
                }
            }
        }

        return dir;
    }
}
