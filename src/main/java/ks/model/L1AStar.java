package ks.model;


import ks.app.config.prop.CodeConfig;
import ks.model.map.L1Map;
import ks.model.map.L1WorldMap;
import ks.util.L1CharPosUtils;

public class L1AStar {
    private L1AStarNode openNode, closedNode;

    private int totalCheckCount = 0;

    public void cleanTail() {
        L1AStarNode tmp;

        while (openNode != null) {
            tmp = openNode.next;
            openNode = tmp;
        }

        while (closedNode != null) {
            tmp = closedNode.next;
            closedNode = tmp;
        }

        totalCheckCount = 0;
    }

    public int getTotalCheckCount() {
        return totalCheckCount;
    }

    public int calcDir(L1Character character, int tx, int ty) {
        cleanTail();

        int[] iPath = new int[2];

        L1AStarNode tail = searchTail(character, tx, ty);

        if (tail != null) {
            int count;

            for (count = 0; count <= CodeConfig.ASTAR_LOOP_COUNT; count++) {
                if (tail != null) {
                    if (tail.x == character.getX() && tail.y == character.getY()) {
                        break;
                    }

                    iPath[0] = tail.x;
                    iPath[1] = tail.y;
                    tail = tail.prev;
                } else {
                    break;
                }

                totalCheckCount += count;
            }

//            L1AStarNode node = openNode;
//
//            for (int i = 0; i < 5; i++) {
//                if (node != null) {
//                    node = node.next;
//
//                    if (node != null) {
//                        System.out.println(node.x + "," + node.y);
//                    }
//                }
//            }

            return L1CharPosUtils.calcMoveDirection(character, iPath[0], iPath[1]);
        }

        return -1;
    }

    public L1AStarNode searchTail(L1Character character, int tx, int ty) {
        L1AStarNode src = new L1AStarNode();

        src.g = 0;
        src.h = (tx - character.getX()) * (tx - character.getX()) + (ty - character.getY()) * (ty - character.getY());
        src.f = src.h;
        src.x = character.getX();
        src.y = character.getY();

        openNode = src;

        L1AStarNode best = null;

        int count;

        for (count = 0; count <= CodeConfig.ASTAR_SEARCH_TAIL_COUNT; count++) {
            if (openNode == null) {
                break;
            }

            best = openNode;
            openNode = best.next;
            best.next = closedNode;
            closedNode = best;

            if (Math.max(Math.abs(tx - best.x), Math.abs(ty - best.y)) == 1) {
                break;
            }

            if (makeChild(best, tx, ty, character.getMapId()) == 0 && count == 0) {
                break;
            }
        }

        totalCheckCount += count;

        return best;
    }

    public char makeChild(L1AStarNode node, int tx, int ty, short m) {
        int x, y;
        char flag = 0;
        char[] cc = {0, 0, 0, 0, 0, 0, 0, 0};

        x = node.x;
        y = node.y;

        // 인접한 노드로 이동가능한지 검사
        cc[0] = isMoveAble(x, y + 1, m);
        cc[1] = isMoveAble(x - 1, y + 1, m);
        cc[2] = isMoveAble(x - 1, y, m);
        cc[3] = isMoveAble(x - 1, y - 1, m);
        cc[4] = isMoveAble(x, y - 1, m);
        cc[5] = isMoveAble(x + 1, y - 1, m);
        cc[6] = isMoveAble(x + 1, y, m);
        cc[7] = isMoveAble(x + 1, y + 1, m);

        // 이동가능한 방향이라면 노드를 생성하고 평가값 계산
        if (cc[2] == 1) {
            makeChildSub(node, x - 1, y, tx, ty);
            flag = 1;
        }
        if (cc[6] == 1) {
            makeChildSub(node, x + 1, y, tx, ty);
            flag = 1;
        }
        if (cc[4] == 1) {
            makeChildSub(node, x, y - 1, tx, ty);
            flag = 1;
        }

        if (cc[0] == 1) {
            makeChildSub(node, x, y + 1, tx, ty);
            flag = 1;
        }

        if (cc[7] == 1 && cc[6] == 1 && cc[0] == 1) {
            makeChildSub(node, x + 1, y + 1, tx, ty);
            flag = 1;
        }

        if (cc[3] == 1 && cc[2] == 1 && cc[4] == 1) {
            makeChildSub(node, x - 1, y - 1, tx, ty);
            flag = 1;
        }

        if (cc[5] == 1 && cc[4] == 1 && cc[6] == 1) {
            makeChildSub(node, x + 1, y - 1, tx, ty);
            flag = 1;
        }

        if (cc[1] == 1 && cc[0] == 1 && cc[2] == 1) {
            makeChildSub(node, x - 1, y + 1, tx, ty);
            flag = 1;
        }

        return flag;
    }

    public char isMoveAble(int x, int y, short mapId) {
        L1Map map = L1WorldMap.getInstance().getMap(mapId);

        if (!map.isPassable(x, y)) {
            return 0;
        }
        if (map.isExistDoor(x, y)) {
            return 0;
        }

        return 1;
    }

    public void makeChildSub(L1AStarNode node, int x, int y, int tx, int ty) {
        L1AStarNode old;
        int g = node.g + 1;

        if ((old = isOpen(x, y)) != null) {
            search8way(node, old, g);
        } else if ((old = isClosed(x, y)) != null) {
            search8way(node, old, g);
        } else {
            L1AStarNode child = new L1AStarNode();
            child.prev = node;
            child.g = g;
            child.h = (x - tx) * (x - tx) + (y - ty) * (y - ty);
            child.f = child.h + child.g;
            child.x = x;
            child.y = y;

            insertNode(child);

            for (int i = 0; i < 8; i++) {
                if (node.direct[i] == null) {
                    node.direct[i] = child;
                    break;
                }
            }
        }
    }

    private void search8way(L1AStarNode node, L1AStarNode old, int g) {
        int i;

        for (i = 0; i < 8; i++) {
            if (node.direct[i] == null) {
                node.direct[i] = old;
                break;
            }
        }

        if (g < old.g) {
            old.prev = node;
            old.g = g;
            old.f = old.h + old.g;
        }
    }

    public L1AStarNode isOpen(int x, int y) {
        return nodeCheck(x, y, openNode);
    }

    public L1AStarNode isClosed(int x, int y) {
        return nodeCheck(x, y, closedNode);
    }

    private L1AStarNode nodeCheck(int x, int y, L1AStarNode closedNode) {
        L1AStarNode tmp = closedNode;

        int count;

        for (count = 0; count <= CodeConfig.ASTAR_NODE_CHECK_COUNT; count++) {
            if (tmp == null) {
                break;
            }

            if (tmp.x == x && tmp.y == y) {
                break;
            }

            tmp = tmp.next;
        }

        totalCheckCount += count;

        return tmp;
    }

    public void insertNode(L1AStarNode src) {
        if (openNode == null) {
            openNode = src;
            return;
        }

        L1AStarNode old = null;
        L1AStarNode tmp = openNode;

        int count;

        for (count = 0; count <= 6; count++) {
            if (tmp != null && (tmp.f < src.f)) {
                old = tmp;
                tmp = tmp.next;
            } else {
                break;
            }
        }

        totalCheckCount += count;

        src.next = tmp;

        if (old != null) {
            old.next = src;
        } else {
            openNode = src;
        }
    }
}
