package ks.core.datatables.exclude;

public class CharacterExclude {
    private int charId;
    private String targetName;

    public CharacterExclude() {
    }

    public CharacterExclude(int charId, String targetName) {
        this.charId = charId;
        this.targetName = targetName;
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
