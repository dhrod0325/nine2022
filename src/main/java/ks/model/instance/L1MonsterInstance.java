package ks.model.instance;

import ks.app.config.prop.CodeConfig;
import ks.commands.gm.GMCommandsUtils;
import ks.constants.L1ActionCodes;
import ks.constants.L1ItemId;
import ks.constants.L1NpcConstants;
import ks.constants.L1SkillId;
import ks.core.datatables.MapsTable;
import ks.core.datatables.balance.MapBalance;
import ks.core.datatables.balance.MapBalanceTable;
import ks.core.datatables.boss.BossGradeDrop;
import ks.core.datatables.boss.BossGradeDropTable;
import ks.core.datatables.drop.DropTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.mapEvent.MapEventDropTable;
import ks.core.datatables.transform.NpcTransform;
import ks.core.datatables.transform.NpcTransformTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.map.L1Map;
import ks.model.pc.L1DamageCheck;
import ks.model.pc.L1PcInstance;
import ks.model.skill.utils.L1SkillUtils;
import ks.model.types.Point;
import ks.packets.serverpackets.S_DoActionGFX;
import ks.packets.serverpackets.S_NPCPack;
import ks.packets.serverpackets.S_RemoveObject;
import ks.packets.serverpackets.S_SkillSound;
import ks.system.boss.model.L1Boss;
import ks.system.boss.table.L1BossSpawnListHotTable;
import ks.util.L1CharPosUtils;
import ks.util.common.NumberUtils;
import ks.util.common.random.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class L1MonsterInstance extends L1NpcInstance {
    private final Logger logger = LogManager.getLogger();
    private final L1DamageCheck damageCheck = new L1DamageCheck();
    private L1Boss boss;
    private L1MonsterDeath death = new L1MonsterDeath(this);
    private boolean riper = false;

    public L1MonsterInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void settingTemplate(L1Npc template) {
        try {
            super.settingTemplate(template);

            boss = L1BossSpawnListHotTable.getInstance().findByNpcId(template.getNpcId());

            if (boss != null) {
                double per = 0;

                if (boss.getBossGrade() == 1) {
                    per = CodeConfig.BOSS_HP_VALANCE1;
                } else if (boss.getBossGrade() == 2) {
                    per = CodeConfig.BOSS_HP_VALANCE2;
                } else if (boss.getBossGrade() == 3) {
                    per = CodeConfig.BOSS_HP_VALANCE3;
                }

                int hp = template.getHp();

                addHp((int) (hp * (per - 1)));
            }

            if (getHp() >= 32767) {
                int hp = getHp() - 32767;

                if (isRiper()) {
                    setOptionHp(hp / 2);
                } else {
                    setOptionHp(hp);
                }
            }
        } catch (Exception e) {
            logger.error("몬스터 세팅중 오류가 발생했습니다 {}", getName());
        }
    }

    public void setDrop() {
        setDrop(null);
    }

    public void reSetting() {
        setDrop();

        setBalance();
    }

    public void setBalance() {
        MapBalance d = MapBalanceTable.getInstance().getData(getMapId());

        if (d != null) {
            int maxHp = getMaxHp();

            maxHp *= d.getHpLeverage();

            setCurrentHp(maxHp);
            setMaxHp(maxHp);
        }
    }

    public void setDrop(L1PcInstance pc) {
        inventory.clearItems();

        List<L1Drop> dropList = getDropList();

        if (!isDropAble()) {
            return;
        }

        double dropRate = CodeConfig.RATE_DROP_ITEMS;
        double adenaRate = CodeConfig.RATE_DROP_ADENA;

        double adenaRateOfMapId = MapsTable.getInstance().getAdenaDropRate(getMapId());
        double itemRateOfMapId = MapsTable.getInstance().getDropRate(getMapId());

        for (L1Drop drop : dropList) {
            int itemId = drop.getItemId();

            if (adenaRate == 0 && itemId == L1ItemId.ADENA) {
                continue;
            }

            if (CodeConfig.nonDropList().contains(itemId)) {
                continue;
            }

            double rateOfItem = 1;
            double resultDropRate = drop.getChance() * dropRate;
            double chance = resultDropRate * rateOfItem;

            if (pc != null) {
                //버닝가호를 착용한 상태에서 확률 증가
                if (itemId == 60001314) {
                    if (pc.getInventory().checkItem(L1ItemId.SERVER_GAHO)) {
                        chance *= 2;
                    }
                }
            }

            if (isRiper()) {
                chance /= 2;
            }

            if (dropRate == 0) {
                continue;
            }

            int ran = RandomUtils.nextInt(1000000);

            if (getTemplate().getRandomLevel() > 0 && itemId != 40308) {
                chance += (int) ((chance * 0.1 * getLevel() / 3d));
            }

            if (chance <= ran) {
                continue;
            }

            // 드롭 개수를 설정
            double amount = 1;

            int min = drop.getMin();
            int max = drop.getMax();

            if (getTemplate().getRandomLevel() > 0) {
                if (itemId == 40308) {
                    int t = getLevel() * (getMaxHp() / 100 + 1) * (-getAC().getAc() / 100 + 1);

                    min = t * 9;
                    max = t * 10;
                }
            }

            min = (int) (min * amount);
            max = (int) (max * amount);

            int itemCount = RandomUtils.nextInt(min, max);

            if (itemId == L1ItemId.ADENA) {
                itemCount *= adenaRate;
                itemCount *= adenaRateOfMapId;
            } else {
                itemCount *= itemRateOfMapId;
            }

            if (itemCount < 0) {
                itemCount = 0;
            }

            if (itemCount > 2000000000) {
                itemCount = 2000000000;
            }

            try {
                L1ItemInstance item = ItemTable.getInstance().createItem(itemId);

                if (item == null) {
                    logger.warn("null item : " + itemId);
                    continue;
                }

                item.setCount(itemCount);

                inventory.storeItem(item);
            } catch (Exception e) {
                logger.error("오류", e);
            }
        }

        settingBossDrop();
    }

    private void settingBossDrop() {
        if (isBoss() && !isRiper()) {
            List<BossGradeDrop> list = BossGradeDropTable.getInstance().findByGrade(boss.getBossGrade());

            for (BossGradeDrop drop : list) {
                if (!RandomUtils.isWinning(1000000, drop.getDropChance())) {
                    continue;
                }

                L1ItemInstance bossDropItem = ItemTable.getInstance().createItem(drop.getDropItemId());
                bossDropItem.setCount(drop.getDropCount());
                inventory.storeItem(bossDropItem);
            }
        }
    }

    @Override
    public void setLocation(L1Location loc) {
        int oldId = getMapId();

        if (oldId != loc.getMapId()) {
            super.setLocation(loc);
            reSetting();
        } else {
            super.setLocation(loc);
        }


    }

    @Override
    public void setLocation(int x, int y, int mapid) {
        int oldId = getMapId();

        if (oldId != mapid) {
            super.setLocation(x, y, mapid);
            reSetting();
        } else {
            super.setLocation(x, y, mapid);
        }
    }

    @Override
    public void setMap(L1Map map) {
        int oldId = getMapId();

        if (oldId != map.getId()) {
            super.setMap(map);
            reSetting();
        } else {
            super.setMap(map);
        }
    }

    @Override
    public void setMap(short mapId) {
        int oldId = getMapId();

        if (oldId != mapId) {
            super.setMap(mapId);
            reSetting();
        } else {
            super.setMap(mapId);
        }
    }

    public L1MonsterDeath getDeath() {
        return death;
    }

    public void setDeath(L1MonsterDeath death) {
        this.death = death;
    }

    @Override
    public void onItemUse() {
        L1Character target = getTarget();

        if (!isActivated() && target != null) {
            if (getLevel() <= 45) {
                useItem(L1NpcConstants.USEITEM_HASTE, 40);
            }

            if (getTemplate().isDoppel() && target instanceof L1PcInstance) {
                L1PcInstance targetPc = (L1PcInstance) target;

                changeDoppel(targetPc);

                List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

                for (L1PcInstance pc : list) {
                    if (pc == null)
                        continue;

                    pc.sendPackets(new S_RemoveObject(this));
                    pc.getNearObjects().removeKnownObject(this);
                    pc.updateObject();
                }
            }
        }

        if (getCurrentHp() * 100 / getMaxHp() < 40) {
            useItem(L1NpcConstants.USEITEM_HEAL, 50);
        }
    }

    private void changeDoppel(L1PcInstance pc) {
        if (pc == null) {
            return;
        }

        setName(pc.getName());
        setNameId(pc.getName());
        setTitle(pc.getTitle());
        setTempLawful(pc.getLawful());

        setPassiSpeed(640);
        setAtkSpeed(900);

        getGfxId().setTempCharGfx(pc.getClassId());
        getGfxId().setGfxId(pc.getClassId());
    }

    @Override
    public void deleteMe() {
        super.deleteMe();

        L1MobGroupInfo mobGroupInfo = getMobGroupInfo();

        if (mobGroupInfo == null) {
            if (isReSpawn()) {
                respawn(true);
            }
        } else {
            if (mobGroupInfo.removeMember(this) == 0) {
                setMobGroupInfo(null);

                if (isReSpawn()) {
                    respawn(false);
                }
            }
        }
    }

    @Override
    public void onPerceive(L1PcInstance pc) {
        pc.getNearObjects().addKnownObject(this);

        if (0 < getCurrentHp()) {
            if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_SINK) {
                pc.sendPackets(new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Hide));
            } else if (getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_FLY) {
                pc.sendPackets(new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Moveup));
            }

            onNpcAI();
        }

        pc.sendPackets(new S_NPCPack(this));

        if (getTemplate().isAgrocoi()) {
            hateList.add(pc, 0);
        }
    }

    @Override
    public void searchTarget() {
        super.searchTarget();

        L1PcInstance targetPlayer = null;

        List<L1PcInstance> list = L1World.getInstance().getVisiblePlayer(this);

        for (L1PcInstance pc : list) {
            if (pc == null)
                continue;

            if (pc.getCurrentHp() <= 0 || pc.isDead()) {
                continue;
            }

            int mapId = getMapId();

            //콜로세움
            if (NumberUtils.contains(mapId, 88, 98, 92, 91, 95)) {
                if (!pc.isInvisible() || getTemplate().isAgrocoi()) {
                    targetPlayer = pc;
                    break;
                }
            }

            //우호도 체크
            if ((getTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
                    || (getTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
                continue;
            }

            if (!getTemplate().isAgro() && !getTemplate().isAgrososc()
                    && getTemplate().isAgroGfxId1() < 0
                    && getTemplate().isAgroGfxId2() < 0) {
                if (pc.getLawful() < -1000) {
                    targetPlayer = pc;
                    break;
                }

                continue;
            }

            if (!pc.isInvisible() || getTemplate().isAgrocoi()) {
                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
                    if (getTemplate().isAgrososc()) {
                        targetPlayer = pc;
                        break;
                    }
                } else if (getTemplate().isAgro()) {
                    targetPlayer = pc;
                    break;
                }

                if (getTemplate().isAgroGfxId1() >= 0 && getTemplate().isAgroGfxId1() <= 4) {
                    if (L1NpcConstants.classGfxId[getTemplate().isAgroGfxId1()][0] == pc.getGfxId().getTempCharGfx() || L1NpcConstants.classGfxId[getTemplate().isAgroGfxId1()][1] == pc.getGfxId().getTempCharGfx()) {
                        targetPlayer = pc;
                        break;
                    }
                } else if (pc.getGfxId().getTempCharGfx() == getTemplate().isAgroGfxId1()) {
                    targetPlayer = pc;
                    break;
                }

                if (getTemplate().isAgroGfxId2() >= 0 && getTemplate().isAgroGfxId2() <= 4) {
                    if (L1NpcConstants.classGfxId[getTemplate().isAgroGfxId2()][0] == pc.getGfxId().getTempCharGfx() || L1NpcConstants.classGfxId[getTemplate().isAgroGfxId2()][1] == pc.getGfxId().getTempCharGfx()) {
                        targetPlayer = pc;
                        break;
                    }
                } else if (pc.getGfxId().getTempCharGfx() == getTemplate().isAgroGfxId2()) {
                    targetPlayer = pc;
                    break;
                }
            }
        }

        if (targetPlayer != null) {
            setTarget(targetPlayer);
            hateList.add(targetPlayer, 0);
        }
    }

    @Override
    public void setLink(L1Character cha) {
        if (cha != null) {
            if (hateList.isEmpty()) {
                hateList.add(cha, 0);
                checkTarget();
            }
        }
    }

    @Override
    public void onNpcAI() {
        if (isAiRunning()) {
            return;
        }

        setActivated(false);
        startAI();
    }

    @Override
    public void onTarget() {
        int distance = getLocation().getTileDistance(new Point(getHomeX(), getHomeY()));

        if (getMapId() == 70) {
            if (L1CharPosUtils.isSafeZone(this)) {
                L1Teleport.npcTeleport(this, getHomeX(), getHomeY(), getMapId(), 5, true);
                return;
            }
        }

        if (distance > CodeConfig.MONSTER_MAX_MOVE_DISTANCE_FROM_HOMEPOINT) {
            L1Teleport.npcTeleport(this, getHomeX(), getHomeY(), getMapId(), 5, true);
            return;
        }

        if (isBoss()) {
            if (distance > CodeConfig.BOSS_MAX_MOVE_DISTANCE_FROM_HOMEPOINT) {
                L1Teleport.npcTeleport(this, getHomeX(), getHomeY(), getMapId(), 5, true);
                return;
            }
        }

        super.onTarget();
    }

    public boolean isBoss() {
        return L1BossSpawnListHotTable.getInstance().isBoss(getNpcId());
    }

    @Override
    public void onAction(L1PcInstance pc) {
        if (getCurrentHp() > 0 && !isDead()) {
            L1AttackRun attack = new L1AttackRun(pc, this);
            attack.action();
            attack.commit();
        }
    }

    @Override
    public void receiveManaDamage(L1Character attacker, int mpDamage) {
        if (attacker == null)
            return;

        if (mpDamage > 0 && !isDead()) {
            onNpcAI();

            setHate(attacker, mpDamage);

            if (attacker instanceof L1PcInstance) {
                searchLink((L1PcInstance) attacker, getTemplate().getFamily());
            }

            int newMp = getCurrentMp() - mpDamage;
            if (newMp < 0) {
                newMp = 0;
            }

            setCurrentMp(newMp);
        }
    }

    private int transRiperId(int mapid) {
        int id = (mapid - 100) / 10;
        int mobid = 0;

        List<Integer> riperBossIds50 = new ArrayList<>(Arrays.asList(
                45513,
                45547,
                45606,
                45650,
                45652)
        );

        List<Integer> riperBossIds8090 = new ArrayList<>(Arrays.asList(
                45618,
                45672,
                45653)
        );

        List<Integer> riperBossIds100 = new ArrayList<>(Collections.singletonList(
                81047)
        );

        switch (id - 1) {
            case 4:
                mobid = riperBossIds50.get(RandomUtils.nextInt(riperBossIds50.size()));
                break; // 50층대 - 60층보스
            case 7:
            case 8:
                mobid = riperBossIds8090.get(RandomUtils.nextInt(riperBossIds8090.size()));
                break;
            case 9:
                mobid = riperBossIds100.get(RandomUtils.nextInt(riperBossIds100.size()));
                break;
        }

        return mobid;
    }

    public void dead(L1Character attacker) {
        getSkillEffectTimerSet().removeSkillEffect(L1SkillUtils.ICE_SKILLS);

        setCurrentHp(0);
        setDead(true);
        setActionStatus(L1ActionCodes.ACTION_Die);

        death.setAttacker(attacker);
        death.run();
    }

    public L1DamageCheck getDamageCheck() {
        return damageCheck;
    }

    @Override
    public void receiveDamage(L1Character attacker, int damage) {
        if (attacker == null)
            return;

        if (getCurrentHp() > 0 && !isDead()) {
            if (getHiddenStatus() != L1NpcConstants.HIDDEN_STATUS_NONE || getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_FLY) {
                return;
            }

            onNpcAI();

            if (attacker instanceof L1EffectInstance) {
                L1EffectInstance o = (L1EffectInstance) attacker;

                if (o.getSpawner() != null) {
                    setHate(o.getSpawner(), damage);
                } else {
                    setHate(attacker, damage);
                }
            } else {
                setHate(attacker, damage);
            }

            if (damage > 0) {
                L1SkillUtils.removeSleep(this);

                if (attacker instanceof L1PcInstance) {
                    L1PcInstance pc = (L1PcInstance) attacker;

                    if (GMCommandsUtils.isDebug(pc)) {
                        damageCheck.damageCheck(pc, damage);
                    }
                }
            }

            if (attacker instanceof L1PcInstance) {
                searchLink((L1PcInstance) attacker, getTemplate().getFamily());
            }

            if (attacker instanceof L1PcInstance && damage > 0) {
                L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }

            int newHp;

            if (getOptionHp() > 0) {
                newHp = getOptionHp() - damage;
            } else {
                newHp = getCurrentHp() - damage;
            }

            boolean transForm = false;

            if (newHp <= 0 && !isDead() && getOptionHp() <= 0) {
                int npcid = getTemplate().getNpcId();

                int transformId = getTemplate().getTransformId();
                int mapid = getMapId();

                List<NpcTransform> transList = NpcTransformTable.getInstance().findList(npcid);

                if (!transList.isEmpty()) {
                    int randomIdx = RandomUtils.getChanceIdx(transList);

                    NpcTransform npcTransform;

                    if (randomIdx != -1) {
                        npcTransform = transList.get(randomIdx);
                    } else {
                        npcTransform = transList.get(RandomUtils.nextInt(transList.size()));
                    }

                    transForm = true;

                    if (npcTransform.getGfxId() != 0) {
                        Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), npcTransform.getGfxId()));
                    }

                    transform(npcTransform.getTransformId());
                } else if (
                        NumberUtils.contains(mapid, 150, 180, 190, 200)
                ) {
                    if (transformId == 45590) {
                        transform(45590);
                    } else if (getTemplate().getNpcId() == 45590) {
                        if (RandomUtils.isWinning(100, CodeConfig.RND_RIPER)) {
                            int rid = transRiperId(mapid);
                            Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
                            transForm = true;
                            transformRiper(rid);
                        } else {
                            dead(attacker);
                        }
                    } else {
                        dead(attacker);
                    }
                } else if (transformId == -1) {
                    dead(attacker);
                } else {
                    transForm = true;
                    transform(transformId);
                }
            }

            if (getOptionHp() > 0) {
                if (!transForm) {
                    setOptionHp(newHp);
                }
            } else {
                if (newHp > 0) {
                    setCurrentHp(newHp);
                    hide();
                }
            }
        } else if (!isDead()) {
            dead(attacker);
        }
    }

    @Override
    public void setCurrentHp(int i) {
        super.setCurrentHp(i);
        if (getMaxHp() > getCurrentHp()) {
            startHpRegeneration();
        }
    }

    @Override
    public void setCurrentMp(int i) {
        super.setCurrentMp(i);
        if (getMaxMp() > getCurrentMp()) {
            startMpRegeneration();
        }
    }

    @Override
    public short getMaxHp() {
        return super.getMaxHp();
    }

    private void hide() {
        int npcId = getTemplate().getNpcId();

        if (npcId == 45061 || npcId == 45161 || npcId == 45181 || npcId == 45455) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = RandomUtils.nextInt(10);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_SINK);
                    Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Hide));
                    setActionStatus(13);
                    Broadcaster.broadcastPacket(this, new S_NPCPack(this));
                }
            }
        } else if (npcId == 45682) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = RandomUtils.nextInt(50);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_SINK);
                    Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_AntharasHide));
                    setActionStatus(20);
                    Broadcaster.broadcastPacket(this, new S_NPCPack(this));
                }
            }
        } else if (npcId == 45681) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = RandomUtils.nextInt(50);

                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_FLY);
                    Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Moveup));
                    setActionStatus(11);
                    Broadcaster.broadcastPacket(this, new S_NPCPack(this));
                }
            }
        } else if (npcId == 45067 || npcId == 45264 || npcId == 45452 || npcId == 45090 || npcId == 45321 || npcId == 45445) {
            if (getMaxHp() / 3 > getCurrentHp()) {
                int rnd = RandomUtils.nextInt(10);
                if (1 > rnd) {
                    allTargetClear();
                    setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_FLY);
                    Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), L1ActionCodes.ACTION_Moveup));
                    setActionStatus(4);
                    Broadcaster.broadcastPacket(this, new S_NPCPack(this));
                }
            }
        }
    }

    public void initHide() {
        int npcId = getTemplate().getNpcId();

        if (npcId == 45061 || npcId == 45161 || npcId == 45181 || npcId == 45455 || npcId == 400000 || npcId == 400001) {
            int rnd = RandomUtils.nextInt(3);

            if (1 > rnd) {
                setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_SINK);
                setActionStatus(13);
            }
        } else if (npcId == 45045 || npcId == 45126 || npcId == 45134 || npcId == 45281) {
            int rnd = RandomUtils.nextInt(3);

            if (1 > rnd) {
                setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_SINK);
                setActionStatus(4);
            }
        } else hiddenStatusFly(npcId);
    }

    public void initHideForMinion(L1NpcInstance leader) {
        int npcId = getTemplate().getNpcId();

        if (leader.getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_SINK) {
            if (npcId == 45061 || npcId == 45161 || npcId == 45181 || npcId == 45455 || npcId == 400000 || npcId == 400001) {
                setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_SINK);
                setActionStatus(13);
            } else if (npcId == 45045 || npcId == 45126 || npcId == 45134 || npcId == 45281) {
                setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_SINK);
                setActionStatus(4);
            }
        } else if (leader.getHiddenStatus() == L1NpcConstants.HIDDEN_STATUS_FLY) {
            hiddenStatusFly(npcId);
        }
    }

    private void hiddenStatusFly(int npcId) {
        if (npcId == 45067 || npcId == 45264 || npcId == 45452 || npcId == 45090 || npcId == 45321 || npcId == 45445) {
            setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_FLY);
            setActionStatus(4);
        } else if (npcId == 45681) {
            setHiddenStatus(L1NpcConstants.HIDDEN_STATUS_FLY);
            setActionStatus(11);
        }
    }

    @Override
    protected void transform(int transformId) {
        setTransformPrevNpcId(getNpcId());
        super.transform(transformId);
        reSetting();
    }

    protected void transformRiper(int transformId) {
        setTransformPrevNpcId(getNpcId());

        setRiper(true);
        setOptionHp(getOptionHp() / 4);
        transform(transformId);

        addHpr(-getHpr());
        addHprInterval(-getHprInterval());

        setTempLawful(0);
        setLawful(0);

        List<L1PcInstance> list = L1World.getInstance().getRecognizePlayer(this);

        for (L1PcInstance pc : list) {
            onPerceive(pc);
        }
    }

    public boolean isRiper() {
        return riper;
    }

    public void setRiper(boolean riper) {
        this.riper = riper;
    }

    private boolean isDropAble() {
        double dropRate = CodeConfig.RATE_DROP_ITEMS;
        double adenaRate = CodeConfig.RATE_DROP_ADENA;

        if (dropRate <= 0) {
            dropRate = 0;
        }

        if (adenaRate <= 0) {
            adenaRate = 0;
        }

        return !(dropRate <= 0) || !(adenaRate <= 0);
    }

    private List<L1Drop> getDropList() {
        int mobId = getTemplate().getNpcId();

        List<L1Drop> dropList = DropTable.getInstance().findDropList(mobId);

        List<L1Drop> eventDropList = MapEventDropTable.getInstance().findItemsToDropList(this);

        for (L1Drop d : eventDropList) {
            if (!dropList.contains(d)) {
                dropList.add(d);
            }
        }

        return dropList;
    }
}
