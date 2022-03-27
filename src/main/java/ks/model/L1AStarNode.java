package ks.model;

public class L1AStarNode {
    public int f;

    public int h;

    public int g;

    public int x, y;

    public L1AStarNode prev;

    public L1AStarNode[] direct = new L1AStarNode[8];

    public L1AStarNode next;

    public L1AStarNode() {
        for (int i = 0; i < 8; i++) {
            direct[i] = null;
        }
    }

    @Override
    public String toString() {
        return "L1AStarNode{" +
                "f=" + f +
                ", h=" + h +
                ", g=" + g +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
