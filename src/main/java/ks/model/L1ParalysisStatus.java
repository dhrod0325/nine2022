package ks.model;

public class L1ParalysisStatus {
    private int type;
    private boolean flag;

    public L1ParalysisStatus(int type, boolean flag) {
        this.type = type;
        this.flag = flag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isOn(int pType) {
        if (type == pType) {
            return flag;
        }

        return false;
    }
}
