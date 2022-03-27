package ks.core.datatables.getback;

public class GetBack {
    public int areaX1;
    public int areaY1;
    public int areaX2;
    public int areaY2;
    public int areaMapId;
    public int getbackX1;
    public int getbackY1;
    public int getbackX2;
    public int getbackY2;
    public int getbackX3;
    public int getbackY3;
    public int getbackMapId;
    public int getbackTownId;
    public int getbackTownIdForElf;
    public int getbackTownIdForDarkelf;

    public boolean isSpecifyArea() {
        return (areaX1 != 0 && areaY1 != 0 && areaX2 != 0 && areaY2 != 0);
    }
}
