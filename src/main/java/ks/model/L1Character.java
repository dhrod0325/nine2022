package ks.model;

import ks.constants.L1SkillId;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.extend.ReceiveDamageAble;
import ks.model.pc.L1CheckTimer;
import ks.model.pc.L1PcInstance;
import ks.model.poison.L1Poison;
import ks.packets.serverpackets.S_Poison;
import ks.packets.serverpackets.S_RemoveObject;
import ks.packets.serverpackets.ServerBasePacket;
import ks.util.common.IntRange;

import java.util.HashMap;
import java.util.Map;

public abstract class L1Character extends L1Object implements ReceiveDamageAble {
    private final L1CheckTimer timer = new L1CheckTimer();
    private final SkillEffectTimerSet skillEffectTimerSet = new SkillEffectTimerSet(this);

    private final Map<Integer, ItemDelayTimer> itemDelay = new HashMap<>();

    private final L1MoveState moveState = new L1MoveState();
    private final L1NearObjects nearObjects = new L1NearObjects();

    protected GfxId gfx = new GfxId();
    protected L1Light light = new L1Light(this);
    protected Ability ability = new Ability(this);
    protected Resistance resistance = new Resistance(this);
    protected AC ac = new AC();
    protected int dmgUp;
    protected int addDmgUp;
    protected int bowDmgUp;
    protected int addBowDmgUp;
    protected int hitUp;
    protected int addHitUp;
    protected int bowHitUp;
    protected int addBowHitUp;
    protected int trueMaxHp;
    private L1Poison poison = null;
    private L1Paralysis paralysis;
    private String name;
    private String title;
    private int level = 1;
    private int exp;
    private int lawful;
    private int karma;
    private int currentHp;
    private short maxHp;
    private int currentMp;
    private int trueMaxMp;
    private short maxMp;
    private boolean paralyzed;
    private boolean sleeped;
    private boolean dead;
    private int addAttrKind;
    private int actionStatus;
    private int killCount;
    private int deathCount;
    private int oldLawful;
    private int oldExp;
    private int eventDamage = 0;
    private int optionHp = 0;
    private L1ItemInstance weapon;

    public L1ItemInstance getWeapon() {
        return weapon;
    }

    public void setWeapon(L1ItemInstance weapon) {
        this.weapon = weapon;
    }

    public int getHeading() {
        return moveState.getHeading();
    }

    public void setHeading(int heading) {
        moveState.setHeading(heading);
    }

    public void resurrect(int hp) {
        if (!isDead()) {
            return;
        }

        if (hp <= 0) {
            hp = 1;
        }

        setCurrentHp(hp);
        setDead(false);
        setActionStatus(0);

        L1PolyMorph.undoPoly(this);

        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            pc.sendPackets(new S_RemoveObject(this));
            pc.getNearObjects().removeKnownObject(this);
            pc.updateObject();
        }
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int i) {
        currentHp = i;

        if (currentHp >= getMaxHp()) {
            currentHp = getMaxHp();
        }
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(int i) {
        currentMp = i;

        if (currentMp >= getMaxMp()) {
            currentMp = getMaxMp();
        }
    }

    public boolean isSleeped() {
        return sleeped;
    }

    public void setSleeped(boolean sleeped) {
        this.sleeped = sleeped;
    }

    public boolean isParalyzed() {
        return paralyzed;
    }

    public void setParalyzed(boolean paralyzed) {
        this.paralyzed = paralyzed;
    }

    public L1Paralysis getParalysis() {
        return paralysis;
    }

    public void setParalaysis(L1Paralysis p) {
        paralysis = p;
    }

    public void cureParalaysis() {
        if (paralysis != null) {
            paralysis.cure();
        }
    }

    public L1Inventory getInventory() {
        return null;
    }

    public void addItemDelay(int delayId, ItemDelayTimer timer) {
        itemDelay.put(delayId, timer);
    }

    public void removeItemDelay(int delayId) {
        itemDelay.remove(delayId);
    }

    public boolean hasItemDelay(int delayId) {
        ItemDelayTimer delayTimer = itemDelay.get(delayId);

        if (delayTimer == null)
            return false;

        if (delayTimer.hasItemDelay()) {
            return true;
        } else {
            removeItemDelay(delayId);
            return false;
        }
    }

    public void curePoison() {
        if (poison == null) {
            return;
        }

        poison.cure();
    }

    public boolean isLongAttack() {
        return false;
    }

    public int getEr() {
        return ability.getEr();
    }

    public L1Poison getPoison() {
        return poison;
    }

    public void setPoison(L1Poison poison) {
        this.poison = poison;
    }

    public abstract void sendPackets(ServerBasePacket serverbasepacket);

    public void setPoisonEffect(int effectId) {
        Broadcaster.broadcastPacket(this, new S_Poison(getId(), effectId));
    }

    public int getEventDamage() {
        return eventDamage;
    }

    public void setEventDamage(int eventDamage) {
        this.eventDamage = eventDamage;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        title = s;
    }

    public synchronized int getLevel() {
        return level;
    }

    public synchronized void setLevel(long level) {
        this.level = (int) level;
    }

    public short getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int hp) {
        trueMaxHp = hp;
        maxHp = (short) IntRange.ensure(trueMaxHp, 1, 32767);
        currentHp = Math.min(currentHp, maxHp);
    }

    public void addMaxHp(int i) {
        setMaxHp(trueMaxHp + i);
    }

    public int getOptionHp() {
        return IntRange.ensure(optionHp, 0, optionHp);
    }

    public void setOptionHp(int optionHp) {
        this.optionHp = optionHp;
    }

    public short getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(int mp) {
        trueMaxMp = mp;
        maxMp = (short) IntRange.ensure(trueMaxMp, 0, 32767);
        currentMp = Math.min(currentMp, maxMp);
    }

    public void addMaxMp(int i) {
        setMaxMp(trueMaxMp + i);
    }

    public void healHp(int pt) {
        setCurrentHp(getCurrentHp() + pt);
    }

    public int getAddAttrKind() {
        return addAttrKind;
    }

    public void setAddAttrKind(int i) {
        addAttrKind = i;
    }

    public int getDmgUp() {
        return dmgUp;
    }

    public void setDmgUp(int dmgUp) {
        this.dmgUp = dmgUp;
    }

    public void addDmgUp(int i) {
        addDmgUp += i;

        if (addDmgUp >= 127) {
            dmgUp = 127;
        } else {
            dmgUp = Math.max(addDmgUp, -128);
        }
    }

    public int getBowDmgUp() {
        return bowDmgUp;
    }

    public void addBowDmgUp(int i) {
        addBowDmgUp += i;

        if (addBowDmgUp >= 127) {
            bowDmgUp = 127;
        } else {
            bowDmgUp = Math.max(addBowDmgUp, -128);
        }
    }

    public int getHitUp() {
        return hitUp;
    }

    public void addHitUp(int i) {
        addHitUp += i;

        if (addHitUp >= 127) {
            hitUp = 127;
        } else {
            hitUp = Math.max(addHitUp, -128);
        }
    }

    public int getBowHitUp() {
        return bowHitUp;
    }

    public void addBowHitup(int i) {
        addBowHitUp += i;

        if (addBowHitUp >= 127) {
            bowHitUp = 127;
        } else {
            bowHitUp = Math.max(addBowHitUp, -128);
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(int i) {
        actionStatus = i;
    }

    public int getLawful() {
        return lawful;
    }

    public void setLawful(int i) {
        lawful = i;
    }

    public int getOldLawful() {
        return oldLawful;
    }

    public void setOldLawful(int i) {
        oldLawful = i;
    }

    public int getOldExp() {
        return oldExp;
    }

    public void setOldExp(int i) {
        oldExp = i;
    }

    public synchronized void addLawful(int i) {
        lawful += i;
        lawful = IntRange.ensure(lawful, -32768, 32767);
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int Kills) {
        killCount = Kills;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int Deaths) {
        deathCount = Deaths;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public GfxId getGfxId() {
        return gfx;
    }

    public L1NearObjects getNearObjects() {
        return nearObjects;
    }

    public L1Light getLight() {
        return light;
    }

    public Ability getAbility() {
        return ability;
    }

    public Resistance getResistance() {
        return resistance;
    }

    public AC getAC() {
        return ac;
    }

    public L1MoveState getMoveState() {
        return moveState;
    }

    public SkillEffectTimerSet getSkillEffectTimerSet() {
        return skillEffectTimerSet;
    }

    public boolean isInvisible() {
        return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY) || getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING));
    }

    public void setTempCharGfx(int i) {

    }

    public void onPerceive(L1PcInstance l1PcInstance) {

    }

    public void onPerceive() {

    }

    public int getTotalHitUp() {
        return 0;
    }

    public int getTotalBowHitUp() {
        return 0;
    }

    public boolean isOverlapLocation() {
        if (L1World.getInstance().getVisiblePlayer(this, 0).size() > 0) {
            for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(this, 0)) {
                if (!visiblePc.isDead()) {
                    return true;
                }
            }
        }

        return false;
    }

    public synchronized L1CheckTimer getTimer() {
        return timer;
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {

    }

    @Override
    public void receiveManaDamage(L1Character attacker, int damageMp) {

    }

    @Override
    public String toString() {
        return getName();
    }
}
