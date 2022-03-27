package ks.model;

public class L1Pet {
    private int itemobjId;
    private int objId;
    private int npcId;
    private String name;
    private int level;
    private int hp;
    private int mp;
    private int exp;
    private int lawful;
    private int food;
    private int foodTime;

    public L1Pet() {
    }

    public int getItemobjId() {
        return itemobjId;
    }

    public void setItemobjId(int i) {
        itemobjId = i;
    }

    public int getObjId() {
        return objId;
    }

    public void setObjId(int i) {
        objId = i;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int i) {
        npcId = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int i) {
        level = i;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int i) {
        hp = i;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int i) {
        mp = i;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int i) {
        exp = i;
    }

    public int getLawful() {
        return lawful;
    }

    public void setLawful(int i) {
        lawful = i;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int i) {
        food = i;
    }

    public int getFoodTime() {
        return foodTime;
    }

    public void setFoodTime(int i) {
        foodTime = i;
    }

    public Type getType() {
        int a = 0;
        int b = 0;

        switch (npcId) {
            case 45043:
                a = 12;
                b = 1;
                break;
            case 45040:
                a = 215;
                b = 5;
                break;
            case 45047:
                a = 136;
                b = 3;
                break;
            case 45042:
                a = 137;
                b = 3;
                break;
            case 45054:
                a = 138;
                b = 3;
                break;
            case 45034:
                a = 139;
                b = 3;
                break;
            case 45046:
                a = 140;
                b = 3;
                break;
            case 45048:
                a = 117;
                b = 5;
                break;
            case 45053:
                a = 252;
                b = 6;
                break;
            case 45049:
                a = 10;
                b = 3;
                break;
            case 45039:
                a = 141;
                b = 10;
                break;
            case 45044:
                a = 180;
                b = 13;
                break;
            case 45711:
                a = 232;
                b = 15;
                break;
            case 45313:
                a = 234;
                b = 15;
                break;
            case 46044:
                a = 237;
                b = 15;
                break;
            case 46042:
                a = 239;
                b = 15;
                break;
            case 45688:
                a = 132;
                b = 10;
                break;
            case 45690:
                a = 133;
                b = 10;
                break;
            case 45687:
                a = 134;
                b = 10;
                break;
            case 45693:
                a = 135;
                b = 10;
                break;
            case 45694:
                a = 136;
                b = 10;
                break;
            case 45695:
                a = 137;
                b = 10;
                break;
            case 45692:
                a = 138;
                b = 10;
                break;
            case 45689:
                a = 139;
                b = 10;
                break;
            case 45696:
                a = 142;
                b = 10;
                break;
            case 45686:
                a = 143;
                b = 10;
                break;
            case 45691:
                a = 144;
                b = 10;
                break;
            case 45697:
                a = 182;
                b = 13;
                break;
            case 45712:
                a = 233;
                b = 15;
                break;
            case 45710:
                a = 235;
                b = 15;
                break;
            case 46046:
                a = 236;
                b = 15;
                break;
            case 46045:
                a = 238;
                b = 15;
                break;
            case 46043:
                a = 240;
                b = 15;
                break;
            default:
                break;
        }

        return new Type(a, b);
    }

    public static class Type {
        private int type1;
        private int type2;

        public Type(int type1, int type2) {
            this.type1 = type1;
            this.type2 = type2;
        }

        public int getType1() {
            return type1;
        }

        public void setType1(int type1) {
            this.type1 = type1;
        }

        public int getType2() {
            return type2;
        }

        public void setType2(int type2) {
            this.type2 = type2;
        }
    }
}