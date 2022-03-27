package ks.util.common;

import ks.util.common.random.RandomUtils;

public class IntRange {
    private final int low;

    private final int high;

    public IntRange(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public IntRange(IntRange range) {
        this(range.low, range.high);
    }

    public static boolean includes(int i, int low, int high) {
        return (low <= i) && (i <= high);
    }

    public static int ensure(int n, int low, int high) {
        int r = n;

        r = Math.max(low, r);
        r = Math.min(r, high);

        return r;
    }

    /**
     * 수치 i가, 범위내에 있을까를 돌려준다.
     *
     * @param i 수치
     * @return 범위내이면 true
     */
    public boolean includes(int i) {
        return (low <= i) && (i <= high);
    }

    /**
     * 수치 i를, 이 범위내에 말다.
     *
     * @param i 수치
     * @return 말 수 있었던 값
     */
    public int ensure(int i) {
        int r = i;
        r = Math.max(low, r);
        r = Math.min(r, high);
        return r;
    }

    public int randomValue() {
        return RandomUtils.nextInt(getWidth() + 1) + low;
    }

    public int getWidth() {
        return high - low;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntRange)) {
            return false;
        }
        IntRange range = (IntRange) obj;
        return (this.low == range.low) && (this.high == range.high);
    }

    @Override
    public String toString() {
        return "low=" + low + ", high=" + high;
    }
}
