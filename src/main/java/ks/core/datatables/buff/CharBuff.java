package ks.core.datatables.buff;

public class CharBuff {
    private int char_obj_id;
    private int skill_id;
    private int remaining_time;
    private int poly_id;

    public int getChar_obj_id() {
        return char_obj_id;
    }

    public void setChar_obj_id(int char_obj_id) {
        this.char_obj_id = char_obj_id;
    }

    public int getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(int skill_id) {
        this.skill_id = skill_id;
    }

    public int getRemainingTime() {
        return remaining_time;
    }

    public void setRemaining_time(int remaining_time) {
        this.remaining_time = remaining_time;
    }

    public int getPoly_id() {
        return poly_id;
    }

    public void setPoly_id(int poly_id) {
        this.poly_id = poly_id;
    }
}
