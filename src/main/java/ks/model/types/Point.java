package ks.model.types;

public class Point {
    protected int x = 0;
    protected int y = 0;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point pt) {
        x = pt.x;
        y = pt.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void set(Point pt) {
        x = pt.x;
        y = pt.y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getLineDistance(Point pt) {
        long diffX = pt.getX() - this.getX();
        long diffY = pt.getY() - this.getY();
        return Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    public int getTileLineDistance(Point pt) {
        return Math.max(Math.abs(pt.getX() - getX()), Math.abs(pt.getY() - getY()));
    }

    public int getTileDistance(Point pt) {
        return Math.abs(pt.getX() - getX()) + Math.abs(pt.getY() - getY());
    }

    public boolean isInScreen(Point pt) {
        int dist = getTileDistance(pt);

        if (dist > 22) {
            return false;
        } else if (dist <= 19) {
            return true;
        } else {
            int dist2 = Math.abs(pt.getX() - (this.getX() - 20)) + Math.abs(pt.getY() - (this.getY() - 20));
            return 23 <= dist2 && dist2 <= 56;
        }
    }

    public boolean isSamePoint(Point pt) {
        return (pt.getX() == getX() && pt.getY() == getY());
    }

    @Override
    public int hashCode() {
        return 7 * getX() + getY();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) {
            return false;
        }
        Point pt = (Point) obj;
        return (this.getX() == pt.getX()) && (this.getY() == pt.getY());
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
