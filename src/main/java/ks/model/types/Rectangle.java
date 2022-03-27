package ks.model.types;

public class Rectangle {
    private int left;

    private int top;

    private int right;

    private int bottom;

    public Rectangle(int left, int top, int right, int bottom) {
        set(left, top, right, bottom);
    }

    public void set(Rectangle rect) {
        set(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight());
    }

    public void set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }

    public boolean contains(int x, int y) {
        return (left <= x && x <= right) && (top <= y && y <= bottom);
    }

    public boolean contains(Point pt) {
        return contains(pt.getX(), pt.getY());
    }
}
